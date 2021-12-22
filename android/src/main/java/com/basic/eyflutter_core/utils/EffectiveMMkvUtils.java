package com.basic.eyflutter_core.utils;

import android.content.Context;
import android.text.TextUtils;

import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.JsonUtils;
import com.cloud.eyutils.utils.ObjectJudge;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class EffectiveMMkvUtils {

    private static EffectiveMMkvUtils mMkvUtils;

    public static EffectiveMMkvUtils getInstance() {
        if (mMkvUtils == null) {
            synchronized (EffectiveMMkvUtils.class) {
                mMkvUtils = new EffectiveMMkvUtils();
            }
        }
        return mMkvUtils;
    }

    private String withId = "f3a4a408a860c3a4";

    public void putString(String cacheKey, String value, Duration duration) {
        if (TextUtils.isEmpty(value) || duration == null) {
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("startTime", System.currentTimeMillis());
            put("duration", duration.toMillis());
            put("value", value);
        }};
        String json = JsonUtils.toJson(map);
        Context applicationContext = LauncherState.getApplicationContext();
        MmkvUtils.getInstance(applicationContext).putString(withId, cacheKey, "string", json);
    }

    public void clean(String cacheKey) {
        Context applicationContext = LauncherState.getApplicationContext();
        MmkvUtils.getInstance(applicationContext).remove(withId, cacheKey);
    }

    @SuppressWarnings("unchecked")
    public String getString(String cacheKey) {
        Context applicationContext = LauncherState.getApplicationContext();
        String value = MmkvUtils.getInstance(applicationContext).getString(withId, cacheKey);
        if (ObjectJudge.isEmptyJson(value)) {
            return "";
        }
        Map<String, Object> map = JsonUtils.parseT(value, HashMap.class);
        if (!map.containsKey("startTime") || !map.containsKey("duration") || !map.containsKey("value")) {
            return "";
        }
        Object obj = map.get("value");
        if (!(obj instanceof String)) {
            return "";
        }
        String content = ConvertUtils.toString(obj);
        int startTime = ConvertUtils.toInt(map.get("startTime"));
        int duration = ConvertUtils.toInt(map.get("duration"));
        if (startTime == 0 || duration == 0) {
            return content;
        }
        long diff = System.currentTimeMillis() - startTime;
        if (diff > duration) {
            MmkvUtils.getInstance(applicationContext).remove(withId, cacheKey);
            return "";
        }
        return content;
    }
}
