package com.basic.eyflutter_core.channel;

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
