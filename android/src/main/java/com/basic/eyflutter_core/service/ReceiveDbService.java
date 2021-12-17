package com.basic.eyflutter_core.service;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.basic.eyflutter_core.ChannelConstants;
import com.basic.eyflutter_core.beans.ChannelMessage;
import com.basic.eyflutter_core.beans.ColumnProperty;
import com.basic.eyflutter_core.beans.FieldItem;
import com.basic.eyflutter_core.beans.SchemaData;
import com.basic.eyflutter_core.channel.ChannelPlugin;
import com.basic.eyflutter_core.dao.BaseCacheDao;
import com.basic.eyflutter_core.enums.ChannelMode;
import com.basic.eyflutter_core.greens.DBManager;
import com.basic.eyflutter_core.greens.DBOpenHelper;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.JsonUtils;
import com.cloud.eyutils.utils.ObjectJudge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import io.flutter.plugin.common.MethodChannel;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/9/23
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class ReceiveDbService {

    public void receive(Context applicationContext, MethodChannel.Result result, HashMap<String, Object> arguments) {
        if (ObjectJudge.isNullOrEmpty(arguments)) {
            return;
        }
        ChannelMessage message = ChannelMessage.getInstance();
        String oType = ConvertUtils.toString(arguments.get("operationType"));
        if (TextUtils.equals(oType, "insertOrReplace")) {
            insertOrReplace(applicationContext, arguments);
        } else if (TextUtils.equals(oType, "deleteInTx")) {
            deleteInTx(applicationContext, arguments);
        } else if (TextUtils.equals(oType, "query")) {
            query(applicationContext, message, arguments, false, result);
        } else if (TextUtils.equals(oType, "queryList")) {
            query(applicationContext, message, arguments, true, result);
        } else if (TextUtils.equals(oType, "count")) {
            count(applicationContext, message, arguments, result);
        } else if (TextUtils.equals(oType, "exists")) {
            exists(applicationContext, message, arguments, result);
        }
    }

    private class ResultEntry {
        SchemaData schemaData;
        boolean success;
        List<FieldItem> fields;
        String where;
    }

    private SchemaData getSchema(HashMap<String, Object> arguments) {
        if (ObjectJudge.isNullOrEmpty(arguments) || !arguments.containsKey("schema")) {
            return new SchemaData();
        }
        Object schema = arguments.get("schema");
        if (schema == null) {
            return new SchemaData();
        }
        SchemaData entry = JsonUtils.parseT(String.valueOf(schema), SchemaData.class);
        return entry == null ? new SchemaData() : entry;
    }

    private List<FieldItem> getFields(HashMap<String, Object> arguments) {
        if (ObjectJudge.isNullOrEmpty(arguments) || !arguments.containsKey("fields")) {
            return new LinkedList<>();
        }
        Object fields = arguments.get("fields");
        if (fields == null) {
            return new LinkedList<>();
        }
        List<FieldItem> fieldItems = JsonUtils.parseArray(String.valueOf(fields), FieldItem.class);
        return fieldItems == null ? new LinkedList<FieldItem>() : fieldItems;
    }

    private ResultEntry getResult(HashMap<String, Object> arguments) {
        ResultEntry resultEntry = new ResultEntry();
        resultEntry.schemaData = getSchema(arguments);
        resultEntry.fields = getFields(arguments);
        if (ObjectJudge.isNullOrEmpty(resultEntry.fields)) {
            return resultEntry;
        }
        Object where = arguments.get("where");
        resultEntry.where = where == null ? "" : String.valueOf(where);
        resultEntry.success = true;
        return resultEntry;
    }

    private String getSqlType(String dartType) {
        switch (dartType) {
            case "num":
            case "int":
            case "bool":
            case "date":
                return "integer";
            case "double":
            case "float":
                return "REAL";
            default:
                return "TEXT";
        }
    }

    private String getCreateTableSql(ResultEntry result) {
        SchemaData schemaData = result.schemaData;
        List<FieldItem> fields = result.fields;
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ").append(schemaData.getTableName()).append("(");
        for (int i = 0; i < fields.size(); i++) {
            FieldItem field = fields.get(i);
            builder.append(field.getName()).append(" ").append(getSqlType(field.getType()));
            if (field.isPrimary()) {
                builder.append(" PRIMARY KEY ");
            }
            if (field.isAutoincrement()) {
                builder.append(" AUTOINCREMENT");
            }
            if (field.isUnique()) {
                builder.append(" UNIQUE");
            }
            if ((i + 1) < fields.size()) {
                builder.append(",");
            }
        }
        builder.append(");");
        return builder.toString();
    }

    private ColumnProperty[] getColumns(ResultEntry result) {
        List<FieldItem> fields = result.fields;
        ColumnProperty[] properties = new ColumnProperty[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            FieldItem field = fields.get(i);
            ColumnProperty columnProperty = new ColumnProperty();
            columnProperty.setColumnName(field.getName());
            columnProperty.setColumnType(field.getType());
            properties[i] = columnProperty;
        }
        return properties;
    }

    @SuppressWarnings("unchecked")
    private List<HashMap<String, Object>> getDataMap(HashMap<String, Object> arguments) {
        List<HashMap<String, Object>> dataMap = new LinkedList<>();
        Object data = arguments.get("data");
        if (data instanceof HashMap) {
            dataMap.add((HashMap<String, Object>) data);
        } else if (data instanceof List) {
            dataMap.addAll((List<HashMap<String, Object>>) data);
        }
        return dataMap;
    }

    private String getSqlValue(String dartType, Object value) {
        switch (dartType) {
            case "num":
            case "int":
            case "double":
            case "float":
                return value == null ? "0" : ConvertUtils.toString(value);
            case "bool":
                return ConvertUtils.toString(value == null ? false : ObjectJudge.isTrue(value));
            case "date":
                return value == null ? ConvertUtils.toString(System.currentTimeMillis()) : ConvertUtils.toString(value);
            default:
                return value == null ? "''" : String.format("'%s'", value);
        }
    }

    private void buildInserOrReplaceSql(StringBuilder sqlBuilder, SchemaData schemaData, StringBuilder valueBuilder, List<FieldItem> fields, HashMap<String, Object> map) {
        sqlBuilder.append("INSERT or replace INTO ").append(schemaData.getTableName()).append("(");
        for (int i = 0; i < fields.size(); i++) {
            FieldItem field = fields.get(i);
            sqlBuilder.append("`").append(field.getName()).append("`");
            valueBuilder.append(getSqlValue(field.getType(), map.get(field.getName())));
            if ((i + 1) < fields.size()) {
                sqlBuilder.append(",");
                valueBuilder.append(",");
            }
        }
        sqlBuilder.append(")VALUES(").append(valueBuilder.toString()).append(");");
    }

    private Object getCursorValue(Cursor query, String dartType, int columnIndex) {
        switch (dartType) {
            case "num":
            case "int":
            case "bool":
            case "date":
                return query.getInt(columnIndex);
            case "double":
            case "float":
                return query.getDouble(columnIndex);
            default:
                return query.getString(columnIndex);
        }
    }

    private List<HashMap<String, Object>> getQueryList(Cursor query, List<FieldItem> fields) {
        List<HashMap<String, Object>> lst = new LinkedList<>();
        if (query == null) {
            return lst;
        }
        while (query.moveToNext()) {
            HashMap<String, Object> map = new HashMap<>();
            for (FieldItem field : fields) {
                int columnIndex = query.getColumnIndex(field.getName());
                if (columnIndex < 0) {
                    continue;
                }
                map.put(field.getName(), getCursorValue(query, field.getType(), columnIndex));
            }
            lst.add(map);
        }
        if (!query.isClosed()) {
            query.close();
        }
        return lst;
    }

    //INSERT or replace INTO user(`id`,`name`,`value`)VALUES(1,324242,544);
    private void insertOrReplace(Context applicationContext, HashMap<String, Object> arguments) {
        ResultEntry result = getResult(arguments);
        if (!result.success) {
            return;
        }
        try {
            String databaseKey = result.schemaData.getDatabaseKey();
            String createTableSql = getCreateTableSql(result);
            ColumnProperty[] columns = getColumns(result);

            SchemaData schemaData = result.schemaData;
            List<FieldItem> fields = result.fields;
            List<HashMap<String, Object>> dataMap = getDataMap(arguments);
            StringBuilder sqlBuilder = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();

            //批量插入或更新数据
            for (HashMap<String, Object> map : dataMap) {
                buildInserOrReplaceSql(sqlBuilder, schemaData, valueBuilder, fields, map);
                valueBuilder.delete(0, valueBuilder.length());
            }
            BaseCacheDao cacheDao = new BaseCacheDao();
            cacheDao.execSql(applicationContext, databaseKey, schemaData.getTableName(), createTableSql, columns, sqlBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteInTx(Context applicationContext, HashMap<String, Object> arguments) {
        ResultEntry result = getResult(arguments);
        if (!result.success) {
            return;
        }
        try {
            String createTableSql = getCreateTableSql(result);
            ColumnProperty[] columns = getColumns(result);

            SchemaData schemaData = result.schemaData;
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("DELETE  FROM ").append(schemaData.getTableName());
            if (!TextUtils.isEmpty(result.where)) {
                sqlBuilder.append(" WHERE ").append(result.where);
            }
            String databaseKey = result.schemaData.getDatabaseKey();
            BaseCacheDao cacheDao = new BaseCacheDao();
            cacheDao.execSql(applicationContext, databaseKey, schemaData.getTableName(), createTableSql, columns, sqlBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void query(Context applicationContext, ChannelMessage message, HashMap<String, Object> arguments, boolean isList, Object channelResult) {
        ResultEntry result = getResult(arguments);
        if (!result.success) {
            return;
        }
        try {
            String createTableSql = getCreateTableSql(result);
            ColumnProperty[] columns = getColumns(result);

            SchemaData schemaData = result.schemaData;
            List<FieldItem> fields = result.fields;
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT ");
            for (int i = 0; i < fields.size(); i++) {
                FieldItem field = fields.get(i);
                sqlBuilder.append("`").append(field.getName()).append("`");
                if ((i + 1) < fields.size()) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(" FROM ").append(schemaData.getTableName());
            sqlBuilder.append(" WHERE ").append(result.where).append(";");
            String databaseKey = result.schemaData.getDatabaseKey();
            HashMap<String, Object> map = new HashMap<>();
            BaseCacheDao cacheDao = new BaseCacheDao();
            DBOpenHelper helper = DBManager.getInstance().getHelper(applicationContext, databaseKey);
            Cursor query = cacheDao.query(helper, schemaData.getTableName(), createTableSql, columns, sqlBuilder.toString());
            List<HashMap<String, Object>> queryList = getQueryList(query, fields);
            DBManager.getInstance().close(helper);
            if (isList) {
                message.setData(JsonUtils.toJson(queryList));
            } else {
                message.setData(queryList.size() > 0 ? JsonUtils.toJson(queryList.get(0)) : "{}");
            }
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.storageMethodName, message, channelResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void count(Context applicationContext, ChannelMessage message, HashMap<String, Object> arguments, Object channelResult) {
        ResultEntry result = getResult(arguments);
        if (!result.success) {
            return;
        }
        try {
            SchemaData schemaData = result.schemaData;
            String createTableSql = getCreateTableSql(result);
            ColumnProperty[] columns = getColumns(result);
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT count(1) as `count` FROM ").append(schemaData.getTableName());
            sqlBuilder.append(" WHERE ").append(result.where).append(";");
            String databaseKey = result.schemaData.getDatabaseKey();
            int count = 0;
            BaseCacheDao cacheDao = new BaseCacheDao();
            DBOpenHelper helper = DBManager.getInstance().getHelper(applicationContext, databaseKey);
            Cursor query = cacheDao.query(helper, schemaData.getTableName(), createTableSql, columns, sqlBuilder.toString());
            if (query != null) {
                if (query.moveToFirst()) {
                    int columnIndex = query.getColumnIndex("count");
                    count = query.getInt(columnIndex);
                }
                if (!query.isClosed()) {
                    query.close();
                }
            }
            DBManager.getInstance().close(helper);
            message.setData(count);
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.storageMethodName, message, channelResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exists(Context applicationContext, ChannelMessage message, HashMap<String, Object> arguments, Object channelResult) {
        ResultEntry result = getResult(arguments);
        if (!result.success) {
            return;
        }
        try {
            SchemaData schemaData = result.schemaData;
            String createTableSql = getCreateTableSql(result);
            ColumnProperty[] columns = getColumns(result);
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT count(1) as `count` FROM ").append(schemaData.getTableName());
            sqlBuilder.append(" WHERE ").append(result.where).append(";");
            String databaseKey = result.schemaData.getDatabaseKey();
            boolean isExist = false;
            BaseCacheDao cacheDao = new BaseCacheDao();
            DBOpenHelper helper = DBManager.getInstance().getHelper(applicationContext, databaseKey);
            Cursor query = cacheDao.query(helper, schemaData.getTableName(), createTableSql, columns, sqlBuilder.toString());
            if (query != null) {
                if (query.moveToFirst()) {
                    int columnIndex = query.getColumnIndex("count");
                    int count = query.getInt(columnIndex);
                    isExist = count > 0;
                }
                if (!query.isClosed()) {
                    query.close();
                }
            }
            DBManager.getInstance().close(helper);
            message.setData(isExist);
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.storageMethodName, message, channelResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
