package com.cloud.basic.mq.cloud_basic_mq.channel;

import com.cloud.libsdk.ebus.EBus;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-02-14
 * Description:
 * Modifier:
 * ModifyContent:
 */
class ChannelHandle {

    //分发消息
    public <Result> void distribution(String channelName, String action, Object arguments, ImplementedCallHandle<Result> callHandle) {
        Result result = callHandle.getResult();
        EBus.getInstance().postTarget(action, new UnsubscribeCall<>(callHandle), result, arguments);
    }
}
