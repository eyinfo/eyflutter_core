package com.cloud.basic.mq.cloud_basic_mq.enums;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/9/16
 * Description:
 * Modifier:
 * ModifyContent:
 */
public enum ChannelMode {
    method("0eff8bd070f64d1890193686196f5a31");

    private String channelName;

    private ChannelMode(String value) {
        this.channelName = value;
    }

    public String getChannelName() {
        return this.channelName;
    }
}
