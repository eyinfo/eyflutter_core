package com.basic.eyflutter_core.nets.events;

import com.basic.eyflutter_core.nets.beans.RequestAlarmInfo;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-10-10
 * Description:接口请求总时间超过警报最大时间(OkRxConfigParams->requestAlarmMaxTime)时回调
 * Modifier:
 * ModifyContent:
 */
public interface OnRequestAlarmApiListener {
    /**
     * 请求时间超过OkRxConfigParams->requestAlarmMaxTime时回调
     *
     * @param alarmInfo 接口请求及响应信息
     */
    public void onRequestAlarmApi(RequestAlarmInfo alarmInfo);
}
