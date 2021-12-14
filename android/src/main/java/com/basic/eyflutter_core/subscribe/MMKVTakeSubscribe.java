package com.basic.eyflutter_core.subscribe;

import android.content.Context;
import android.text.TextUtils;

import com.basic.eyflutter_core.ChannelConstants;
import com.basic.eyflutter_core.beans.ChannelMessage;
import com.basic.eyflutter_core.channel.ChannelPlugin;
import com.basic.eyflutter_core.channel.OnDistributionSubscribe;
import com.basic.eyflutter_core.enums.ChannelMode;
import com.basic.eyflutter_core.utils.MmkvUtils;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.utils.ConvertUtils;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

public class MMKVTakeSubscribe extends OnDistributionSubscribe {
    @Override
    public void onSubscribe(MethodChannel.Result result, HashMap<String, Object> arguments) {
        String withId = ConvertUtils.toString(arguments.get("withId"));
        String key = ConvertUtils.toString(arguments.get("key"));
        String type = ConvertUtils.toString(arguments.get("type"));
        ChannelMessage message = ChannelMessage.getInstance();
        Context applicationContext = LauncherState.getApplicationContext();
        if (TextUtils.equals(type, "int")) {
            message.setData(MmkvUtils.getInstance(applicationContext).getLong(withId, key));
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.mmkvTakeMethodName, message, result);
        } else if (TextUtils.equals(type, "double")) {
            message.setData(MmkvUtils.getInstance(applicationContext).getDouble(withId, key));
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.mmkvTakeMethodName, message, result);
        } else if (TextUtils.equals(type, "bool")) {
            message.setData(MmkvUtils.getInstance(applicationContext).getBoolean(withId, key));
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.mmkvTakeMethodName, message, result);
        } else if (TextUtils.equals(type, "string")) {
            message.setData(MmkvUtils.getInstance(applicationContext).getString(withId, key));
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.mmkvTakeMethodName, message, result);
        } else if (TextUtils.equals(type, "set")) {
            message.setData(MmkvUtils.getInstance(applicationContext).getSets(withId, key));
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.mmkvTakeMethodName, message, result);
        }
    }
}
