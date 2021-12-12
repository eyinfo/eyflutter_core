package com.basic.eyflutter_core.beans;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-02-27
 * Description:channel注册状态
 * Modifier:
 * ModifyContent:
 */
public class ChannelRegisterStatus {

    /**
     * 是否已初始化
     */
    private boolean isInitialized;
    /**
     * 是否正在注册
     */
    private boolean isRegisting;

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public boolean isRegisting() {
        return isRegisting;
    }

    public void setRegisting(boolean registing) {
        isRegisting = registing;
    }
}
