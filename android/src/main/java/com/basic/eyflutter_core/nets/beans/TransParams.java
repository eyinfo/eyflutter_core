package com.basic.eyflutter_core.nets.beans;

import java.util.HashMap;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-16
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class TransParams {

    private RetrofitParams retrofitParams;

    private HashMap<String, String> headers;

    private String url;

    private String useClass;

    public RetrofitParams getRetrofitParams() {
        if (retrofitParams == null) {
            retrofitParams = new RetrofitParams();
        }
        return retrofitParams;
    }

    public void setRetrofitParams(RetrofitParams retrofitParams) {
        this.retrofitParams = retrofitParams;
    }

    public HashMap<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUseClass() {
        return useClass == null ? "" : useClass;
    }

    public void setUseClass(String useClass) {
        this.useClass = useClass;
    }
}
