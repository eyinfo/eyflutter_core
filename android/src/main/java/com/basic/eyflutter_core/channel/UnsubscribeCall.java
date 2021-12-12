package com.basic.eyflutter_core.channel;

import com.cloud.eyutils.HandlerManager;
import com.cloud.eyutils.events.Action0;
import com.cloud.eyutils.events.RunnableParamsN;
import com.cloud.eyutils.utils.ObjectJudge;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/6/4
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class UnsubscribeCall<Result> implements Action0 {

    private ImplementedCallHandle<Result> callHandle;

    public UnsubscribeCall(ImplementedCallHandle<Result> callHandle) {
        this.callHandle = callHandle;
    }

    @Override
    public void call() {
        if (ObjectJudge.isMainThread()) {
            callHandle.notImplemented();
        }
        else {
            HandlerManager.getInstance().post(new RunnableParamsN<Object>() {
                @Override
                public void run(Object... objects) {
                    callHandle.notImplemented();
                }
            });
        }
    }
}
