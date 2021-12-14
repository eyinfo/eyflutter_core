package com.basic.eyflutter_core.events;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/4/19
 * Description:数据链
 * Modifier:
 * ModifyContent:
 */
public abstract class OnDataChainRunnable<R, T, Extra> {

    public abstract R run(T t);

    public void complete(R r, Extra extras) {

    }
}
