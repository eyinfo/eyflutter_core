package com.basic.eyflutter_core.nets.beans;

import com.basic.eyflutter_core.nets.enums.DataType;
import com.basic.eyflutter_core.nets.enums.ResultState;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-16
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class ResultParams<T> {
    private T data;
    private DataType dataType;
    private long requestStartTime;
    private long requestTotalTime;
    private ResultState state;
    private int code;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public long getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public long getRequestTotalTime() {
        return requestTotalTime;
    }

    public void setRequestTotalTime(long requestTotalTime) {
        this.requestTotalTime = requestTotalTime;
    }

    public ResultState getState() {
        return state;
    }

    public void setState(ResultState state) {
        this.state = state;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
