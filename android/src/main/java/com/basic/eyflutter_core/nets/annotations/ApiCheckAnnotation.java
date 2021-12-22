package com.basic.eyflutter_core.nets.annotations;

import com.basic.eyflutter_core.nets.enums.CallStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2016/8/19
 * Description:接口请求验证
 * Modifier:
 * ModifyContent:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiCheckAnnotation {
    /**
     * token验证,默认为false[有token则带,空值则不带];
     *
     * @return true:自动取token值并传入;false:不传;
     */
    boolean isTokenValid() default false;

    /**
     * 数据回调类型默认{@link CallStatus}.OnlyNet
     *
     * @return CallStatus
     */
    CallStatus callStatus() default CallStatus.OnlyNet;

    /**
     * 缓存时间
     * <p>
     * return
     */
    long cacheTime() default 0;

    /**
     * 缓存时间单位
     * <p>
     * return
     */
    TimeUnit cacheTimeUnit() default TimeUnit.SECONDS;

    /**
     * 缓存key
     * <p>
     * return
     */
    String cacheKey() default "859800517";

    /**
     * 明细缓存key
     *
     * @return 用户存在明细数据key
     */
    String detailCacheKey() default "";
}
