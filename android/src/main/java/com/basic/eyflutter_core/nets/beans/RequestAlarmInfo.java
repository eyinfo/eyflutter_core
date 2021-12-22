package com.basic.eyflutter_core.nets.beans;

import com.basic.eyflutter_core.nets.enums.RequestType;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/10/10
 * Description:请求警报信息
 * Modifier:
 * ModifyContent:
 */
public class RequestAlarmInfo {
    /**
     * 请求类型
     */
    private RequestType requestType = RequestType.GET;
    /**
     * 请求url
     */
    private String url;
    /**
     * 请求头信息(包含全局配置头信息)
     */
    private HashMap<String, String> headers;
    /**
     * 请求参数
     */
    private TreeMap<String, Object> params;
    /**
     * 请求错误消息
     */
    private String message = "";
    /**
     * 请求返回码
     */
    private int code;
    /**
     * 请求开始时间
     */
    private long requestStartTime;
    /**
     * 请求结束时间
     */
    private long requestEndTime;

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getHeaders() {
        if (headers == null) {
            return new HashMap<>();
        }
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public TreeMap<String, Object> getParams() {
        if (params == null) {
            params = new TreeMap<>();
        }
        return params;
    }

    public void setParams(TreeMap<String, Object> params) {
        this.params = params;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public long getRequestEndTime() {
        return requestEndTime;
    }

    public void setRequestEndTime(long requestEndTime) {
        this.requestEndTime = requestEndTime;
    }
}
