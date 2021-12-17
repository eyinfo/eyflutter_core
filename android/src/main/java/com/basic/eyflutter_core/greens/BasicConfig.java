package com.basic.eyflutter_core.greens;

import android.content.Context;
import android.os.Environment;

import com.cloud.eyutils.beans.BasicConfigItem;
import com.cloud.eyutils.events.OnEntryCall;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.utils.PathsUtils;

import java.io.File;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-11
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class BasicConfig implements OnEntryCall {

    private String packageName;

    public BasicConfig(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public Object onEntryResult() {
        BasicConfigItem configItem = new BasicConfigItem();
        configItem.setDebug(false);
        configItem.setProjectPackgeName(packageName);
        Context applicationContext = LauncherState.getApplicationContext();
        File dir = applicationContext.getExternalFilesDir("");
        configItem.setCacheRootDir(dir);
        return configItem;
    }
}
