package com.basic.eyflutter_core.beans;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-12-09
 * Description:native->flutter message
 * Modifier:
 * ModifyContent:
 */
public class ChannelMessage {
    /**
     * 发送数据
     */
    private Object data;
    /**
     * 错误码
     */
    private String errorCode = "200";
    /**
     * 错误消息
     */
    private String errorMessage;
    /**
     * 错误详细描述
     */
    private String errorDetails;
    /**
     * 发送消息后是否结束通道或流
     */
    private boolean isEndChannelOrStream;

    private ChannelMessage() {
        //init
    }

    public static ChannelMessage getInstance() {
        return new ChannelMessage();
    }

    public Object getData() {
        if (data == null) {
            return new Object();
        }
        return data;
    }

    public ChannelMessage setData(Object data) {
        this.data = data;
        return this;
    }

    public String getErrorCode() {
        return errorCode == null ? "" : errorCode;
    }

    public ChannelMessage setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage == null ? "" : errorMessage;
    }

    public ChannelMessage setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getErrorDetails() {
        return errorDetails == null ? "" : errorDetails;
    }

    public ChannelMessage setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
        return this;
    }

    public boolean isEndChannelOrStream() {
        return isEndChannelOrStream;
    }

    public ChannelMessage setEndChannelOrStream(boolean endChannelOrStream) {
        isEndChannelOrStream = endChannelOrStream;
        return this;
    }
}
