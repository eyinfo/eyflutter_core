package com.basic.eyflutter_core.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.basic.eyflutter_core.beans.ColumnProperty;
import com.cloud.eyutils.CdLibConfig;
import com.cloud.eyutils.beans.BasicConfigItem;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.GlobalUtils;
import com.cloud.eyutils.utils.ObjectJudge;
import com.cloud.eyutils.utils.SharedPrefUtils;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;

import java.util.HashSet;
import java.util.Set;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-11
 * Description:sqlite列检测;只在首次使用或版本更新后检测一次
 * Modifier:
 * ModifyContent:
 */
public class ColumnsCheck {

    private String columnCachePrefixKey = "adc662e7734648ce9f111462adec9e61";

    private String getSqlText(Database database, String tableName) {
        Cursor query = null;
        try {
            String sql = "select [sql] from sqlite_master where `type`='table' and tbl_name = ?";
            query = database.rawQuery(sql, new String[]{tableName});
            if (query != null && query.moveToFirst()) {
                int columnIndex = query.getColumnIndex("sql");
                String text = query.getString(columnIndex);
                return text;
            }
        } catch (Exception e) {
            //query sql error
        } finally {
            if (query != null && !query.isClosed()) {
                query.close();
            }
        }
        return "";
    }

    private String getSqlText(SQLiteDatabase sqLiteDatabase, String tableName) {
        Cursor query = null;
        try {
            String sql = "select [sql] from sqlite_master where `type`='table' and tbl_name = ?";
            query = sqLiteDatabase.rawQuery(sql, new String[]{tableName});
            if (query != null && query.moveToFirst()) {
                int columnIndex = query.getColumnIndex("sql");
                String text = query.getString(columnIndex);
                return text;
            }
        } catch (Exception e) {
            //query sql error
        } finally {
            if (query != null && !query.isClosed()) {
                query.close();
            }
        }
        return "";
    }

    private String getKey(String databaseTagKey, String tableName) {
        String key = String.format("%s_%s_%s", columnCachePrefixKey, databaseTagKey, tableName);
        return key;
    }

    private int getVersionCode() {
        BasicConfigItem basicConfigItem = CdLibConfig.getInstance().getBasicConfigItem();
        String configPath = String.format("%s.BuildConfig", basicConfigItem.getProjectPackgeName());
        Object code = GlobalUtils.getBuildConfigValue(configPath, "VERSION_CODE");
        return ConvertUtils.toInt(code);
    }

    private boolean isNeedCheckFields(Context applicationContext, String databaseTagKey, String tableName) {
        String key = getKey(databaseTagKey, tableName);
        int value = SharedPrefUtils.getPrefInt(applicationContext, key);
        if (value <= 0) {
            return true;
        }
        int code = getVersionCode();
        if (code > value) {
            return true;
        }
        return false;
    }

    //检测成功后保存当前版本
    private void setVersionCodeForDB(String databaseTagKey, String tableName) {
        int code = getVersionCode();
        Context applicationContext = LauncherState.getApplicationContext();
        String key = getKey(databaseTagKey, tableName);
        SharedPrefUtils.setPrefInt(applicationContext, key, code);
    }

    private Set<ColumnProperty> filterAddColumns(String sqlText, ColumnProperty[] columns) {
        Set<ColumnProperty> addColumns = new HashSet<>();
        for (ColumnProperty property : columns) {
            if (sqlText.contains(String.format("\"%s\"", property.getColumnName()))) {
                continue;
            }
            addColumns.add(property);
        }
        return addColumns;
    }

    private Set<ColumnProperty> filterAddColumns(String sqlText, Property[] properties) {
        Set<ColumnProperty> addColumns = new HashSet<>();
        for (Property property : properties) {
            if (sqlText.contains(String.format("\"%s\"", property.columnName))) {
                continue;
            }
            ColumnProperty columnProperty = new ColumnProperty();
            columnProperty.setColumnName(property.columnName);
            columnProperty.setColumnType(property.type.getSimpleName());
            addColumns.add(columnProperty);
        }
        return addColumns;
    }

    private String getSqlColumnType(String propertyType) {
        String typeName = "";
        switch (propertyType.toLowerCase()) {
            case "long":
            case "int":
            case "boolean":
            case "date":
                typeName = "INTEGER";
                break;
            case "double":
            case "float":
                typeName = "REAL";
                break;
            default:
                typeName = "TEXT";
                break;
        }
        return typeName;
    }

    private synchronized void addColumnToDb(Database database, String databaseTagKey, String tableName, Set<ColumnProperty> addColumns) {
        try {
            database.beginTransaction();
            for (ColumnProperty property : addColumns) {
                StringBuilder builder = new StringBuilder();
                builder.append("alter table ")
                        .append(tableName)
                        .append(" add ")
                        .append(property.getColumnName());
                builder.append(" ").append(getSqlColumnType(property.getColumnType()))
                        .append(";");
                database.execSQL(builder.toString());
            }
            database.setTransactionSuccessful();
            database.endTransaction();
            //保存版本避免重复检测
            setVersionCodeForDB(databaseTagKey, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void addColumnToDb(SQLiteDatabase sqLiteDatabase, String databaseTagKey, String tableName, Set<ColumnProperty> addColumns) {
        try {
            for (ColumnProperty property : addColumns) {
                StringBuilder builder = new StringBuilder();
                builder.append("alter table ")
                        .append(tableName)
                        .append(" add ")
                        .append(property.getColumnName());
                builder.append(" ").append(getSqlColumnType(property.getColumnType()))
                        .append(";");
                sqLiteDatabase.execSQL(builder.toString());
            }
            //保存版本避免重复检测
            setVersionCodeForDB(databaseTagKey, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果不存在则添加列
     *
     * @param database       数据库对象
     * @param databaseTagKey 数据库唯一标识
     * @param tableName      表名
     * @param properties     当前版本表所有列
     */
    public void addColumnIfNotExist(Context applicationContext, Database database, String databaseTagKey, String tableName, Property[] properties) {
        if (TextUtils.isEmpty(databaseTagKey) || TextUtils.isEmpty(tableName) || ObjectJudge.isNullOrEmpty(properties)) {
            return;
        }
        if (!isNeedCheckFields(applicationContext, databaseTagKey, tableName)) {
            return;
        }
        String sqlText = getSqlText(database, tableName);
        if (TextUtils.isEmpty(sqlText)) {
            return;
        }
        Set<ColumnProperty> addColumns = filterAddColumns(sqlText, properties);
        if (ObjectJudge.isNullOrEmpty(addColumns)) {
            //如果没有字段匹配视为成功
            //保存版本避免重复检测
            setVersionCodeForDB(databaseTagKey, tableName);
            return;
        }
        addColumnToDb(database, databaseTagKey, tableName, addColumns);
    }

    public void addColumnIfNotExist(Database database, String databaseTagKey, String tableName, Property[] properties) {
        addColumnIfNotExist(LauncherState.getApplicationContext(), database, databaseTagKey, tableName, properties);
    }

    public void addColumnIfNotExistNonTransaction(Context applicationContext, SQLiteDatabase sqLiteDatabase, String databaseTagKey, String tableName, ColumnProperty[] columns) {
        if (TextUtils.isEmpty(databaseTagKey) || TextUtils.isEmpty(tableName) || ObjectJudge.isNullOrEmpty(columns)) {
            return;
        }
        if (!isNeedCheckFields(applicationContext, databaseTagKey, tableName)) {
            return;
        }
        String sqlText = getSqlText(sqLiteDatabase, tableName);
        if (TextUtils.isEmpty(sqlText)) {
            return;
        }
        Set<ColumnProperty> addColumns = filterAddColumns(sqlText, columns);
        if (ObjectJudge.isNullOrEmpty(addColumns)) {
            //如果没有字段匹配视为成功
            //保存版本避免重复检测
            setVersionCodeForDB(databaseTagKey, tableName);
            return;
        }
        addColumnToDb(sqLiteDatabase, databaseTagKey, tableName, addColumns);
    }

    public void addColumnIfNotExistNonTransaction(SQLiteDatabase sqLiteDatabase, String databaseTagKey, String tableName, ColumnProperty[] columns) {
        addColumnIfNotExistNonTransaction(LauncherState.getApplicationContext(), sqLiteDatabase, databaseTagKey, tableName, columns);
    }
}
