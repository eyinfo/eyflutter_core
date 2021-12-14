package com.basic.eyflutter_core.enums;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-10
 * Description:路径类型
 * Modifier:
 * ModifyContent:
 */
public enum PathType {
    /**
     * 应用程序内部数据库{context.getDatabasePath()}
     */
    internal,
    /**
     * 私有目录数据库(/data/data/package name/cache/...)
     */
    privacy,
    /**
     * 除internal和privacy外，其它都需标注此类型
     */
    custom
}
