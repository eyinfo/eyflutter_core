package com.basic.eyflutter_core.utils;

import android.content.Context;

import com.basic.eyflutter_core.service.MMKVService;
import com.cloud.eyutils.storage.files.StorageUtils;
import com.tencent.mmkv.MMKV;

import java.util.Set;

public class MmkvUtils {
    private static MmkvUtils mmkvUtils;

    private MMKVService mmkvService;

    public static MmkvUtils getInstance(Context applicationContext) {
        if (mmkvUtils == null) {
            synchronized (MmkvUtils.class) {
                mmkvUtils = new MmkvUtils(applicationContext);
            }
        }
        return mmkvUtils;
    }

    private MmkvUtils(Context applicationContext) {
        String rootDir = StorageUtils.getInternalRootDir(applicationContext);
        MMKV.initialize(rootDir);
        mmkvService = new MMKVService();
        mmkvService.checkMmkvs();
    }

    public void putString(String withId, String key, String type, Object value) {
        mmkvService.put(withId, key, type, value);
    }

    public long getLong(String withId, String key) {
        return mmkvService.getLong(withId, key);
    }

    public double getDouble(String withId, String key) {
        return mmkvService.getDouble(withId, key);
    }

    public boolean getBoolean(String withId, String key) {
        return mmkvService.getBoolean(withId, key);
    }

    public String getString(String withId, String key) {
        return mmkvService.getString(withId, key);
    }

    public Set<String> getSets(String withId, String key) {
        return mmkvService.getSets(withId, key);
    }

    public void remove(String withId, String key) {
        mmkvService.remove(withId, key);
    }
}
