package com.basic.eyflutter_core.channel;

import android.text.TextUtils;

import com.basic.eyflutter_core.enums.ChannelMode;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-12-06
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class MethodChannelPlugin {

    private HashMap<String, MethodChannel> channelMap = new HashMap<>();

    /**
     * 注册method channel
     *
     * @param messenger 消息解码器
     */
    public void register(BinaryMessenger messenger, String channelName) {
        if (messenger == null) {
            return;
        }
        MethodChannel channel = new MethodChannel(messenger, channelName);
        channel.setMethodCallHandler(new ChannelMethodCallHandler(channelName));
        channelMap.put(channelName, channel);
    }

    public MethodChannel getChannel(ChannelMode mode) {
        return channelMap.get(mode.getChannelName());
    }

    public MethodChannel getChannel() {
        return getChannel(ChannelMode.method);
    }

    private class ChannelMethodCallHandler implements MethodChannel.MethodCallHandler {

        private String channelName;

        public ChannelMethodCallHandler(String channelName) {
            this.channelName = channelName;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
            String method = methodCall.method;
            if (TextUtils.isEmpty(method)) {
                return;
            }
            Object arguments = methodCall.arguments();
            if (arguments != null && !(arguments instanceof Map)) {
                return;
            }
            OnDistributionSubscribe subscribe = ChannelPlugin.getInstance().getSubscribe(method);
            if (subscribe != null) {
                subscribe.onSubscribe(result, (HashMap<String, Object>) arguments);
                return;
            }
//            ChannelHandle handle = new ChannelHandle();
//            handle.distribution(channelName, method, arguments, new ImplementedCallHandle<MethodChannel.Result>(result, method, arguments) {
//                @Override
//                protected void onNotImplemented(MethodChannel.Result result, String action, Object arguments) {
//                    result.notImplemented();
//                    ToastUtils.show(String.format("action=%s,arguments=%s方法未实现", action, JsonUtils.toJson(arguments)));
//                }
//            });
        }
    }
}
