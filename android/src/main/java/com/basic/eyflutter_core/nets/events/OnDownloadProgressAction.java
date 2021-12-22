package com.basic.eyflutter_core.nets.events;

import com.cloud.eyutils.events.Action1;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-06-11
 * Description:下载进度回调(避免重载构建参数时无法自动提示)
 * Modifier:
 * ModifyContent:
 */
public interface OnDownloadProgressAction extends Action1<Float> {

}
