package com.basic.eyflutter_core.greens;

import android.content.Context;

import com.basic.eyflutter_core.annotations.ProjectDBPath;
import com.basic.eyflutter_core.enums.PathType;
import com.cloud.eyutils.launchs.LauncherState;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-10
 * Description:数据配置模型
 * Modifier:
 * ModifyContent:
 */
public abstract class AndroidDaoModel {

    private Context applicationContext;
    /**
     * 内部路径
     */
    @ProjectDBPath(pathType = PathType.internal)
    private String internal;

    /**
     * 私有路径
     */
    @ProjectDBPath(pathType = PathType.privacy)
    private String privacy;

    public AndroidDaoModel() {
        applicationContext = LauncherState.getApplicationContext();
        onCreate(applicationContext);
    }

    public void onCreate(Context applicationContext) {

    }

    public Context getApplicationContext() {
        return this.applicationContext;
    }

    public abstract String getInternal();

    public abstract String getPrivacy();
}
