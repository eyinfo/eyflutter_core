package com.basic.eyflutter_core.subscribe;

import android.content.Context;

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

public class MMKVReceiveSubscribe extends OnDistributionSubscribe {

    @Override
    public void onSubscribe(MethodChannel.Result result, HashMap<String, Object> arguments) {
        String withId = ConvertUtils.toString(arguments.get("withId"));
        String key = ConvertUtils.toString(arguments.get("key"));
        String type = ConvertUtils.toString(arguments.get("type"));
        Object value = arguments.get("value");
        Context applicationContext = LauncherState.getApplicationContext();
        MmkvUtils.getInstance(applicationContext).putString(withId, key, type, value);
        ChannelMessage message = ChannelMessage.getInstance();
        message.setData(value);
        ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.mmkvMethodName, message, result);
    }
}
