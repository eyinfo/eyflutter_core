package com.basic.eyflutter_core.beans;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-17
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class FieldItem {

    /**
     * 字段名
     */
    private String name;
    /**
     * 定义名称
     */
    private String defineName;
    /**
     * 字段类型
     */
    private String type;
    /**
     * 是否主键
     */
    private boolean primary;
    /**
     * 是否唯一
     */
    private boolean unique;

    /**
     * 是否自增
     */
    private boolean autoincrement;
    /**
     * 字段长度
     */
    private int length;

    public String getName() {
        return name == null ? "" : name;
    }

    public FieldItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getDefineName() {
        return defineName == null ? "" : defineName;
    }

    public FieldItem setDefineName(String defineName) {
        this.defineName = defineName;
        return this;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public FieldItem setType(String type) {
        this.type = type;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public FieldItem setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public boolean isUnique() {
        return unique;
    }

    public FieldItem setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public boolean isAutoincrement() {
        return autoincrement;
    }

    public FieldItem setAutoincrement(boolean autoincrement) {
        this.autoincrement = autoincrement;
        return this;
    }

    public int getLength() {
        return length;
    }

    public FieldItem setLength(int length) {
        this.length = length;
        return this;
    }
}
