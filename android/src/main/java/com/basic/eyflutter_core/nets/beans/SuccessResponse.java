package com.basic.eyflutter_core.nets.beans;

import com.basic.eyflutter_core.nets.enums.DataType;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-16
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class SuccessResponse {

    private ResponseData responseData;

    private DataType dataType;

    private RetrofitParams retrofitParams;

    private int code;

    public SuccessResponse(ResponseData responseData, DataType dataType) {
        this.responseData = responseData;
        this.dataType = dataType;
    }

    public ResponseData getResponseData() {
        if (responseData == null) {
            responseData = new ResponseData();
        }
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public RetrofitParams getRetrofitParams() {
        if (retrofitParams == null) {
            retrofitParams = new RetrofitParams();
        }
        return retrofitParams;
    }

    public void setRetrofitParams(RetrofitParams retrofitParams) {
        this.retrofitParams = retrofitParams;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
