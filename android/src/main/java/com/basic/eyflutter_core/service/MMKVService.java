package com.basic.eyflutter_core.service;

import android.text.TextUtils;

import com.basic.eyflutter_core.ChannelConstants;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.ObjectJudge;
import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.Set;

public class MMKVService {

    private MMKV mmkvOrdinary;
    private MMKV mmkvUser;
    private MMKV mmkvState;

    public MMKVService() {
        checkMmkvs();
    }

    public void checkMmkvs() {
        if (mmkvOrdinary == null) {
            mmkvOrdinary = MMKV.defaultMMKV();
        }
        if (mmkvState == null) {
            mmkvState = MMKV.mmkvWithID(ChannelConstants.mmkvStateKey);
        }
        if (mmkvUser == null) {
            mmkvUser = MMKV.mmkvWithID(ChannelConstants.mmkvUserKey);
        }
    }

    private MMKV getMMKVInstance(String withId) {
        if (TextUtils.equals(withId, ChannelConstants.mmkvUserKey)) {
            return mmkvUser;
        } else if (TextUtils.equals(withId, ChannelConstants.mmkvStateKey)) {
            return mmkvState;
        } else {
            return mmkvOrdinary;
        }
    }

    @SuppressWarnings("unchecked")
    public void put(String withId, String key, String type, Object value) {
        if (ObjectJudge.isEmptyString(key) || value == null) {
            return;
        }
        try {
            MMKV mmkv = getMMKVInstance(withId);
            if ((value instanceof Set) || TextUtils.equals(type, "set")) {
                mmkv.encode(key, (Set<String>) value);
            } else {
                String cvalue = ConvertUtils.toString(value);
                if ((value instanceof Integer) || (value instanceof Long) || TextUtils.equals(type, "int")) {
                    mmkv.encode(key, ConvertUtils.toLong(cvalue));
                } else if ((value instanceof Double) || TextUtils.equals(type, "double")) {
                    mmkv.encode(key, ConvertUtils.toDouble(cvalue));
                } else if ((value instanceof String) || TextUtils.equals(type, "string")) {
                    mmkv.encode(key, cvalue);
                } else if ((value instanceof Boolean) || TextUtils.equals(type, "bool")) {
                    mmkv.encode(key, ObjectJudge.isTrue(cvalue));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getLong(String withId, String key) {
        if (ObjectJudge.isEmptyString(key)) {
            return 0;
        }
        MMKV mmkv = getMMKVInstance(withId);
        return mmkv.decodeLong(key, 0);
    }

    public double getDouble(String withId, String key) {
        if (ObjectJudge.isEmptyString(key)) {
            return 0;
        }
        MMKV mmkv = getMMKVInstance(withId);
        return mmkv.decodeDouble(key, 0);
    }

    public String getString(String withId, String key) {
        if (ObjectJudge.isEmptyString(key)) {
            return "";
        }
        MMKV mmkv = getMMKVInstance(withId);
        return mmkv.decodeString(key, "");
    }

    public boolean getBoolean(String withId, String key) {
        if (ObjectJudge.isEmptyString(key)) {
            return false;
        }
        MMKV mmkv = getMMKVInstance(withId);
        return mmkv.decodeBool(key, false);
    }

    public Set<String> getSets(String withId, String key) {
        if (ObjectJudge.isEmptyString(key)) {
            return new HashSet<>();
        }
        MMKV mmkv = getMMKVInstance(withId);
        Set<String> sets = mmkv.decodeStringSet(key);
        return sets == null ? new HashSet<String>() : sets;
    }

    public void remove(String withId, String key) {
        if (ObjectJudge.isEmptyString(key)) {
            return;
        }
        MMKV mmkv = getMMKVInstance(withId);
        mmkv.removeValueForKey(key);
    }

    public void removeContains(String withId, String fuzzyKey) {
        if (ObjectJudge.isEmptyString(fuzzyKey)) {
            return;
        }
        MMKV mmkv = getMMKVInstance(withId);
        String[] keys = mmkv.allKeys();
        if (ObjectJudge.isNullOrEmpty(keys)) {
            return;
        }
        Set<String> removeKeys = new HashSet<String>();
        for (String key : keys) {
            if (key.startsWith(fuzzyKey)) {
                removeKeys.add(key);
            }
        }
        mmkv.removeValuesForKeys(ConvertUtils.toArray(removeKeys));
    }
}
