package com.basic.eyflutter_core.nets.beans;

import com.basic.eyflutter_core.nets.enums.ErrorType;
import com.basic.eyflutter_core.nets.enums.RequestState;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-16
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class CompleteResponse {

    private RequestState requestState;

    private ErrorType errorType;

    private Integer code;

    public CompleteResponse(RequestState requestState, ErrorType errorType, Integer code) {
        this.requestState = requestState;
        this.errorType = errorType;
        this.code = code;
    }

    public RequestState getRequestState() {
        return requestState;
    }

    public void setRequestState(RequestState requestState) {
        this.requestState = requestState;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public Integer getCode() {
        return code == null ? 0 : code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
