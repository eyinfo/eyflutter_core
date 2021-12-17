package com.basic.eyflutter_core.channel;

import android.text.TextUtils;

import com.basic.eyflutter_core.beans.ChannelMessage;
import com.basic.eyflutter_core.beans.ChannelRegisterStatus;
import com.basic.eyflutter_core.enums.ChannelMode;
import com.cloud.eyutils.HandlerManager;
import com.cloud.eyutils.events.RunnableParamsN;
import com.cloud.eyutils.storage.MemoryCache;
import com.cloud.eyutils.utils.ObjectJudge;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.reactivex.rxjava3.internal.queue.MpscLinkedQueue;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-12-06
 * Description:method basic event
 * Modifier:
 * ModifyContent:
 */
public class ChannelPlugin {

    private static ChannelPlugin channelPlugin;
    private MethodChannelPlugin methodChannelPlugin = new MethodChannelPlugin();
    private Object _decoder;
    private String registerStatusKey = "4ac921ae710b48f18fe3c09cfa959b9d";
    private MpscLinkedQueue<ChannelMessageEntry> messageEntries = new MpscLinkedQueue<>();
    private HashSet<Integer> messageCodes = new HashSet<>();
    private boolean isSending = false;
    private Map<String, OnDistributionSubscribe> subscribeMap = new LinkedHashMap<>();

    private ChannelPlugin() {
        //init
    }

    public static ChannelPlugin getInstance() {
        if (channelPlugin == null) {
            synchronized (ChannelPlugin.class) {
                if (channelPlugin == null) {
                    channelPlugin = new ChannelPlugin();
                }
            }
        }
        return channelPlugin;
    }

    public OnDistributionSubscribe getSubscribe(String action) {
        if (!subscribeMap.containsKey(action)) {
            return null;
        }
        return subscribeMap.get(action);
    }

    public void putSubscribe(String action, OnDistributionSubscribe subscribe) {
        if (TextUtils.isEmpty(action) || subscribe == null) {
            return;
        }
        subscribeMap.put(action, subscribe);
    }

    public void removeSubScribe(String action) {
        if (TextUtils.isEmpty(action)) {
            return;
        }
        subscribeMap.remove(action);
    }

    private ChannelRegisterStatus getRegisterStatus() {
        Object o = MemoryCache.getInstance().get(registerStatusKey);
        if (o instanceof ChannelRegisterStatus) {
            return (ChannelRegisterStatus) o;
        }
        return new ChannelRegisterStatus();
    }

    /**
     * 注册channel通信
     *
     * @param decoder 解码器对象
     * @param modes   通信模式
     */
    public void register(Object decoder, ChannelMode... modes) {
        if (ObjectJudge.isNullOrEmpty(modes)) {
            return;
        }
        this._decoder = decoder;
        ChannelRegisterStatus registerStatus = getRegisterStatus();
        if (registerStatus.isInitialized()) {
            registerStatus.setRegisting(false);
            return;
        }
        if (registerStatus.isRegisting()) {
            return;
        }
        String canonicalName = decoder.getClass().getCanonicalName();
        if (decoder instanceof BinaryMessenger) {
            BinaryMessenger messenger = (BinaryMessenger) decoder;
            for (ChannelMode mode : modes) {
                registerMode(messenger, mode);
            }
        } else if (decoder instanceof FlutterEngine) {
            FlutterEngine flutterEngine = (FlutterEngine) decoder;
            DartExecutor dartExecutor = flutterEngine.getDartExecutor();
            BinaryMessenger messenger = dartExecutor.getBinaryMessenger();
            for (ChannelMode mode : modes) {
                registerMode(messenger, mode);
            }
        } else if (decoder instanceof PluginRegistry) {
            PluginRegistry registry = (PluginRegistry) decoder;
            for (ChannelMode mode : modes) {
                registerMode(registry, canonicalName, mode);
            }
        }
        registerStatus.setInitialized(true);
        registerStatus.setRegisting(false);
        MemoryCache.getInstance().set(registerStatusKey, registerStatus);
    }

    private void registerMode(BinaryMessenger messenger, ChannelMode mode) {
        if (messenger == null) {
            return;
        }
        isSending = false;
        if (mode == ChannelMode.method) {
            methodChannelPlugin.register(messenger, mode.getChannelName());
        }
    }

    private void registerMode(PluginRegistry registry, String canonicalName, ChannelMode mode) {
        BinaryMessenger messenger = getMessenger(registry, canonicalName, mode);
        this.registerMode(messenger, mode);
    }

    @SuppressWarnings("deprecation")
    private BinaryMessenger getMessenger(PluginRegistry registry, String canonicalName, ChannelMode mode) {
        String key = String.format("%s_%s", canonicalName, mode.getChannelName());
        if (registry.hasPlugin(key)) {
            return null;
        }
        registry.registrarFor(key);
        BinaryMessenger messenger = null;
        if (mode == ChannelMode.method) {
            PluginRegistry.Registrar registrar = registry.registrarFor(ChannelPlugin.class.getName());
            messenger = registrar.messenger();
        }
        return messenger;
    }

    class ChannelMessageEntry {
        ChannelMode mode;
        String action;
        ChannelMessage message;
        SendCompleteHandle handle;
        Object channelResult;
        int resultCode;
    }

    /**
     * 发送channel消息
     *
     * @param mode    channel
     * @param action  用于区分channel的不同操作
     * @param message 发送消息
     * @param handle  发送结束处理
     */
    public void sendMessage(ChannelMode mode, String action, ChannelMessage message, SendCompleteHandle handle, Object channelResult) {
        if (channelResult == null) {
            return;
        }
        int resultCode = channelResult.hashCode();
        if (messageCodes.contains(resultCode)) {
            return;
        }
        ChannelMessageEntry entry = new ChannelMessageEntry();
        entry.mode = mode;
        entry.action = action;
        entry.message = message;
        entry.handle = handle;
        entry.channelResult = channelResult;
        entry.resultCode = resultCode;
        messageEntries.offer(entry);
        messageCodes.add(resultCode);
        if (!isSending) {
            performSend();
        }
    }

    private void performSend() {
        if (messageEntries.isEmpty()) {
            isSending = false;
            return;
        }
        isSending = true;
        ChannelMessageEntry entry = messageEntries.poll();
        if (entry == null) {
            performSend();
            return;
        }
        messageCodes.remove(entry.resultCode);
        SendCompleteHandle handle = new SendCompleteHandle() {
            @Override
            protected void onSendComplete(ChannelPlugin channelPlugin) {
                isSending = false;
                performSend();
            }
        };
        if (ObjectJudge.isMainThread()) {
            sendInternalMessage(entry, handle);
        } else {
            HandlerManager.getInstance().post(new RunnableParamsN<Object>() {
                @Override
                public void run(Object... params) {
                    if (!(params[0] instanceof ChannelMessageEntry)) {
                        return;
                    }
                    ChannelMessageEntry messageEntry = (ChannelMessageEntry) params[0];
                    SendCompleteHandle handle = (SendCompleteHandle) params[1];
                    sendInternalMessage(messageEntry, handle);
                }
            }, entry, handle);
        }
    }

    /**
     * 发送channel消息
     *
     * @param mode    channel
     * @param action  用于区分channel的不同操作
     * @param message 发送消息
     */
    public void sendMessage(ChannelMode mode, String action, ChannelMessage message, Object channelResult) {
        sendMessage(mode, action, message, new SendCompleteHandle(), channelResult);
    }

    private void sendInternalMessage(ChannelMessageEntry entry, SendCompleteHandle handle) {
        if (entry.mode == null || TextUtils.isEmpty(entry.action) || entry.message == null) {
            return;
        }
        if (entry.mode == ChannelMode.method) {
            sendMethodMessage(entry, handle);
        }
    }

    private void sendMethodMessage(ChannelMessageEntry entry, SendCompleteHandle handle) {
        if (!(entry.channelResult instanceof MethodChannel.Result)) {
            if (handle != null) {
                handle.onSendComplete(this);
            }
            return;
        }
        try {
            MethodChannel.Result result = (MethodChannel.Result) entry.channelResult;
            if (TextUtils.equals(entry.message.getErrorCode(), "200")) {
                Object data = entry.message.getData();
                result.success(data);
            } else {
                result.error(entry.message.getErrorCode(), entry.message.getErrorMessage(), entry.message.getErrorDetails());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (handle != null) {
            handle.onSendComplete(this);
        }
    }

    private void sendEventMessage(ChannelMessageEntry entry, SendCompleteHandle handle) {
        if (!(entry.channelResult instanceof EventChannel.EventSink)) {
            return;
        }
        EventChannel.EventSink eventSink = (EventChannel.EventSink) entry.channelResult;
        if (TextUtils.equals(entry.message.getErrorCode(), "200")) {
            Object data = entry.message.getData();
            eventSink.success(data);
        } else {
            eventSink.error(entry.message.getErrorCode(), entry.message.getErrorMessage(), entry.message.getErrorDetails());
        }
        if (entry.message.isEndChannelOrStream()) {
            eventSink.endOfStream();
        }
        if (handle != null) {
            handle.onSendComplete(this);
        }
    }

    public void invokeMethod(String method, Object arguments, OnMethodResultCall<Object> resultCall) {
        MethodChannel channel = methodChannelPlugin.getChannel();
        if (channel == null) {
            return;
        }
        channel.invokeMethod(method, arguments, new MCResult(resultCall, _decoder));
    }

    private class MCResult implements MethodChannel.Result {

        private OnMethodResultCall<Object> call;
        private Object decoder;

        MCResult(OnMethodResultCall<Object> call, Object decoder) {
            this.call = call;
            this.decoder = decoder;
        }

        @Override
        public void success(Object result) {
            if (call == null) {
                return;
            }
            call.onMethodSuccess(result, decoder);
        }

        @Override
        public void error(String errorCode, String errorMessage, Object errorDetails) {

        }

        @Override
        public void notImplemented() {

        }
    }

    /**
     * 未实现时调用此方法，结束回调
     *
     * @param mode   通道类型
     * @param action 发送或接收消息key
     */
    public void notImplemented(ChannelMode mode, String action, Object channelResult) {
        if (mode == null || TextUtils.isEmpty(action)) {
            return;
        }
        if (channelResult instanceof MethodChannel.Result) {
            MethodChannel.Result result = (MethodChannel.Result) channelResult;
            result.notImplemented();
        } else if (channelResult instanceof EventChannel.EventSink) {
            EventChannel.EventSink eventSink = (EventChannel.EventSink) channelResult;
            eventSink.endOfStream();
        }
    }

    public void destroy() {
        _decoder = null;
        MemoryCache.getInstance().remove(registerStatusKey);
    }
}
