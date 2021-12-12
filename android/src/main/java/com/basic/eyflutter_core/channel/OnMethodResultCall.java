package com.cloud.basic.mq.cloud_basic_mq.channel;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-01-15
 * Description:method channel回调
 * Modifier:
 * ModifyContent:
 */
public interface OnMethodResultCall<T> {

    void onMethodSuccess(T arguments, Object decoder);
}
