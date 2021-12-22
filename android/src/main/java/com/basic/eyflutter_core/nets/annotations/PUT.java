package com.basic.eyflutter_core.nets.annotations;

import com.basic.eyflutter_core.nets.enums.RequestContentType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2017/6/6
 * Description:
 * Modifier:
 * ModifyContent:
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PUT {
    /**
     * 相对地址
     * <p>
     * return
     */
    String value() default "";

    /**
     * 相对地址库
     * <p>
     * return
     */
    UrlItem[] values() default {};

    /**
     * 若为true,则默认不添加此请求下的所有字段
     * <p>
     * return
     */
    boolean isRemoveEmptyValueField() default false;

    /**
     * 是否完整url
     * <p>
     * return
     */
    boolean isFullUrl() default false;

    /**
     * 数据提交方式
     * <p>
     * return
     */
    RequestContentType contentType() default RequestContentType.None;

    /**
     * 请求失败时是否重试(默认false)
     * 若为true则在{@link OkRxConfigParams}.retryCount之后，
     * 每次请求后延时间增加5秒；最多重试failureRetryCount次;
     *
     * @return true-请求失败时自动重新请求直到成功为止,false-只请求一次;
     */
    boolean isFailureRetry() default false;

    /**
     * isFailureRetry()==true时请求失败重试次数
     *
     * @return 重试次数(默认100)
     */
    int failureRetryCount() default 100;

    /**
     * 超时毫秒数(connection write request <=0不处理)
     *
     * @return 超时毫秒数
     */
    long timeoutMillis() default 0;
}
