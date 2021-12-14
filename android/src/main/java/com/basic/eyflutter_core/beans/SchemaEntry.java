package com.basic.eyflutter_core.beans;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-17
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class SchemaEntry {
    //数据表结构
    private String schema;
    //sql字段
    private String fields;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}
