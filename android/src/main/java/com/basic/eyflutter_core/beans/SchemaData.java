package com.basic.eyflutter_core.beans;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-17
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class SchemaData {

    /**
     * 数据库表名
     */
    private String tableName;
    /**
     * internal
     * privacy
     */
    private String databaseKey;

    public String getTableName() {
        return tableName == null ? "" : tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabaseKey() {
        return databaseKey == null ? "" : databaseKey;
    }

    public void setDatabaseKey(String databaseKey) {
        this.databaseKey = databaseKey;
    }
}
