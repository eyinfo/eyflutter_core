package com.basic.eyflutter_core.nets.events;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/7/16
 * Description:api提交监听
 * Modifier:
 * ModifyContent:
 */
public interface OnApiSubmitListener<T> {

    void onApiStart();

    void onApiSuccess(T t);

    void onApiFinished();
}
