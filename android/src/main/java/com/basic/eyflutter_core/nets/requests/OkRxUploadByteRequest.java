package com.basic.eyflutter_core.nets.requests;

import android.text.TextUtils;

import com.basic.eyflutter_core.nets.OkRx;
import com.basic.eyflutter_core.nets.beans.CompleteResponse;
import com.basic.eyflutter_core.nets.beans.ResponseData;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.beans.SuccessResponse;
import com.basic.eyflutter_core.nets.enums.DataType;
import com.basic.eyflutter_core.nets.enums.ErrorType;
import com.basic.eyflutter_core.nets.enums.RequestState;
import com.basic.eyflutter_core.nets.enums.ResponseDataType;
import com.basic.eyflutter_core.nets.events.OnGlobalRequestParamsListener;
import com.basic.eyflutter_core.nets.properties.ByteRequestItem;
import com.cloud.eyutils.events.Action1;
import com.cloud.eyutils.logs.Logger;
import com.cloud.eyutils.utils.GlobalUtils;
import com.cloud.eyutils.utils.JsonUtils;
import com.cloud.eyutils.utils.ObjectJudge;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2017/11/15
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class OkRxUploadByteRequest {

    private String responseString = "";
    private RetrofitParams retrofitParams;

    public void setRetrofitParams(RetrofitParams retrofitParams) {
        this.retrofitParams = retrofitParams;
    }

    public void call(String url,
                     HashMap<String, String> headers,
                     HashMap<String, Object> params,
                     List<ByteRequestItem> byteRequestItems,
                     final Action1<SuccessResponse> successAction,
                     final Action1<CompleteResponse> completeAction) {
        try {
            if (TextUtils.isEmpty(url)) {
                if (completeAction != null) {
                    completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, 0));
                }
                return;
            }
            OkHttpClient client = OkRx.getInstance().getOkHttpClient();
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (!ObjectJudge.isNullOrEmpty(byteRequestItems)) {
                for (ByteRequestItem byteRequestItem : byteRequestItems) {
                    if (byteRequestItem.getBs() == null) {
                        continue;
                    }
                    RequestBody body = RequestBody.create(MediaType.parse(byteRequestItem.getMediaTypeValue()), byteRequestItem.getBs());
                    String filename = String.format("%s.rxtiny", GlobalUtils.getGuidNoConnect());
                    requestBody.addFormDataPart(byteRequestItem.getFieldName(), filename, body);
                }
            }
            //绑定全局请求参数
            OnGlobalRequestParamsListener globalRequestParamsListener = OkRx.getInstance().getGlobalRequestParamsListener();
            if (globalRequestParamsListener != null) {
                HashMap<String, Object> globalParams = globalRequestParamsListener.onGlobalParams();
                if (!ObjectJudge.isNullOrEmpty(globalParams)) {
                    for (Map.Entry<String, Object> entry : globalParams.entrySet()) {
                        if (!params.containsKey(entry.getKey())) {
                            params.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
            if (!ObjectJudge.isNullOrEmpty(params)) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    if (entry.getValue() instanceof Byte[]) {
                        continue;
                    }
                    if (entry.getValue() instanceof List) {
                        requestBody.addFormDataPart(entry.getKey(), JsonUtils.toJson(entry.getValue()));
                    } else {
                        requestBody.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }
            }
            Request.Builder builder = new Request.Builder().url(url).post(requestBody.build());
            if (!ObjectJudge.isNullOrEmpty(headers)) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    builder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            Request request = builder.build();
            client.newBuilder().readTimeout(60000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (completeAction != null) {
                        completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.netRequest, 0));
                    }
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) {
                    int code = response.code();
                    try {
                        if (successAction != null) {
                            ResponseBody body = response.body();
                            if (body != null) {
                                responseString = body.string();
                                ResponseData responseData = new ResponseData();
                                responseData.setResponseDataType(ResponseDataType.object);
                                responseData.setResponse(responseString);
                                SuccessResponse successResponse = new SuccessResponse(responseData, DataType.NetData);
                                successResponse.setCode(code);
                                successResponse.setRetrofitParams(retrofitParams);
                                successAction.call(successResponse);
                            }
                        }
                    } catch (Exception e) {
                        Logger.error(e);
                    } finally {
                        if (completeAction != null) {
                            completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, code));
                        }
                    }
                }
            });
        } catch (Exception e) {
            completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, 0));
            Logger.error(e);
        }
    }
}
