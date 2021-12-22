package com.basic.eyflutter_core.nets.enums;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2017/11/17
 * Description:请求状态
 * Modifier:
 * ModifyContent:
 */
public enum RequestState {
    /**
     * 成功
     */
    Success,
    /**
     * 失败
     */
    Error,
    /**
     * 完成
     */
    Completed,
    /**
     * 空列表
     */
    Empty,
    /**
     * 没有更多数据
     */
    Nothing,

    /**
     * 刷新列表数据
     */
    Refresh,

    /**
     * 加载更多数据异常
     */
    MoreDataError
}
