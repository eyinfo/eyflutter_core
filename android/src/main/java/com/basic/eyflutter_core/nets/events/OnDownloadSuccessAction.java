package com.basic.eyflutter_core.nets.events;

import com.cloud.eyutils.events.Action1;

import java.io.File;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-06-11
 * Description:下载成功回调(避免重载构建参数时无法自动提示)
 * Modifier:
 * ModifyContent:
 */
public abstract class OnDownloadSuccessAction implements Action1<File> {

    private Object extras;

    public Object getExtras() {
        return extras;
    }

    public void setExtras(Object extras) {
        this.extras = extras;
    }

    public void failure(File file) {
        //请求或处理失败回调
    }
}
