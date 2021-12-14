package com.basic.eyflutter_core.annotations;

import com.basic.eyflutter_core.enums.PathType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-10
 * Description:项目数据路径
 * Modifier:
 * ModifyContent:
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectDBPath {
    /**
     * 访问目录类型
     *
     * @return PathType
     */
    PathType pathType() default PathType.internal;
}
