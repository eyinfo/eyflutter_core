package com.basic.eyflutter_core.service;

import android.content.Context;
import android.text.TextUtils;

import com.basic.eyflutter_core.ChannelConstants;
import com.basic.eyflutter_core.beans.ChannelMessage;
import com.basic.eyflutter_core.channel.ChannelPlugin;
import com.basic.eyflutter_core.enums.ChannelMode;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.ObjectJudge;
import com.cloud.eyutils.utils.SharedPrefUtils;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/9/23
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class ReceiveSharedPreferencesService {

    private class PreferenceEntry {
        public String key;
        public String type;
        public Object value;
        //取值类型
        public String getType;
    }

    public void receive(Context applicationContext, MethodChannel.Result result, HashMap<String, Object> arguments) {
        if (ObjectJudge.isNullOrEmpty(arguments)) {
            return;
        }
        PreferenceEntry entry = getPreferenceEntry(arguments);
        if (TextUtils.isEmpty(entry.key)) {
            return;
        }
        if (TextUtils.equals(entry.type, "set")) {
            savePreference(applicationContext, entry);
            ChannelMessage message = ChannelMessage.getInstance();
            message.setData("success");
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.sharedPreferencesMethodName, message, result);
        } else if (TextUtils.equals(entry.type, "get")) {
            getPreference(applicationContext, entry, result);
        } else if (TextUtils.equals(entry.type, "clear")) {
            SharedPrefUtils.remove(applicationContext, entry.key);
            ChannelMessage message = ChannelMessage.getInstance();
            message.setData("success");
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.sharedPreferencesMethodName, message, result);
        }
    }

    private void getPreference(Context applicationContext, PreferenceEntry entry, MethodChannel.Result channelResult) {
        if (applicationContext == null) {
            return;
        }
        ChannelMessage message = ChannelMessage.getInstance();
        if (TextUtils.equals(entry.getType, "bool")) {
            boolean value = SharedPrefUtils.getPrefBoolean(applicationContext, entry.key);
            message.setData(value);
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.sharedPreferencesMethodName, message, channelResult);
        } else if (TextUtils.equals(entry.getType, "int")) {
            int value = SharedPrefUtils.getPrefInt(applicationContext, entry.key);
            message.setData(value);
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.sharedPreferencesMethodName, message, channelResult);
        } else if (TextUtils.equals(entry.getType, "double")) {
            double value = SharedPrefUtils.getPrefDouble(applicationContext, entry.key);
            message.setData(value);
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.sharedPreferencesMethodName, message, channelResult);
        } else if (TextUtils.equals(entry.getType, "string")) {
            String value = SharedPrefUtils.getPrefString(applicationContext, entry.key);
            message.setData(value);
            ChannelPlugin.getInstance().sendMessage(ChannelMode.method, ChannelConstants.sharedPreferencesMethodName, message, channelResult);
        }
    }

    private void savePreference(Context applicationContext, PreferenceEntry entry) {
        if (applicationContext == null) {
            return;
        }
        if (entry.value instanceof Boolean) {
            SharedPrefUtils.setPrefBoolean(applicationContext, entry.key, ObjectJudge.isTrue(entry.value));
        } else if (entry.value instanceof Integer) {
            SharedPrefUtils.setPrefInt(applicationContext, entry.key, ConvertUtils.toInt(entry.value));
        } else if (entry.value instanceof Double) {
            SharedPrefUtils.setPrefDouble(applicationContext, entry.key, ConvertUtils.toDouble(entry.value));
        } else if (entry.value instanceof String) {
            SharedPrefUtils.setPrefString(applicationContext, entry.key, ConvertUtils.toString(entry.value));
        }
    }

    private PreferenceEntry getPreferenceEntry(HashMap<String, Object> arguments) {
        PreferenceEntry entry = new PreferenceEntry();
        if (arguments.containsKey("key")) {
            entry.key = ConvertUtils.toString(arguments.get("key"));
        }
        if (arguments.containsKey("type")) {
            entry.type = ConvertUtils.toString(arguments.get("type"));
        }
        if (arguments.containsKey("value")) {
            entry.value = arguments.get("value");
        }
        if (arguments.containsKey("getType")) {
            entry.getType = ConvertUtils.toString(arguments.get("getType"));
        }
        return entry;
    }
}
