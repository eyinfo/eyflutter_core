package com.basic.eyflutter_core.nets.beans;

import com.basic.eyflutter_core.nets.enums.RequestState;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-29
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class CompleteBitmapResponse {

    /**
     * success、fail、complete
     */
    private RequestState requestState;
    /**
     * net request code
     */
    private int code;

    public CompleteBitmapResponse(RequestState requestState) {
        this.requestState = requestState;
    }

    public CompleteBitmapResponse(RequestState requestState, int code) {
        this.requestState = requestState;
        this.code = code;
    }

    public RequestState getRequestState() {
        return requestState;
    }

    public void setRequestState(RequestState requestState) {
        this.requestState = requestState;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
