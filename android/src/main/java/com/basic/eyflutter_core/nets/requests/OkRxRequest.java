package com.basic.eyflutter_core.nets.requests;

import android.text.TextUtils;

import com.basic.eyflutter_core.nets.OkRx;
import com.basic.eyflutter_core.nets.beans.CompleteResponse;
import com.basic.eyflutter_core.nets.beans.ResponseData;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.beans.SuccessResponse;
import com.basic.eyflutter_core.nets.beans.TransParams;
import com.basic.eyflutter_core.nets.callback.StringCallback;
import com.basic.eyflutter_core.nets.enums.CallStatus;
import com.basic.eyflutter_core.nets.enums.ErrorType;
import com.basic.eyflutter_core.nets.enums.RequestState;
import com.basic.eyflutter_core.nets.enums.ResponseDataType;
import com.basic.eyflutter_core.nets.events.OnNetworkConnectListener;
import com.basic.eyflutter_core.utils.EffectiveMMkvUtils;
import com.cloud.eyutils.CdLibConfig;
import com.cloud.eyutils.constants.LibConfigKeys;
import com.cloud.eyutils.events.Action1;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.Timeout;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2017/11/15
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class OkRxRequest extends BaseRequest {

    public void call(TransParams transParams, Action1<SuccessResponse> successAction, Action1<CompleteResponse> completeAction) {
        if (TextUtils.isEmpty(transParams.getUrl())) {
            if (completeAction != null) {
                completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, 0));
            }
            return;
        }
        setCancelIntervalCacheCall(false);
        RetrofitParams retrofitParams = transParams.getRetrofitParams();
        CallStatus callStatus = retrofitParams.getCallStatus();
        String ckey = String.format("%s%s", retrofitParams.getCacheKey(), getAllParamsJoin(transParams.getHeaders(), retrofitParams));
        if (!cacheDealWith(callStatus, successAction, ckey, retrofitParams)) {
            //此时结束处理
            return;
        }
        //如果网络未连接则不作请求
        Object netListenerObj = CdLibConfig.getInstance().getConfigValue(LibConfigKeys.netStatusConfigKey);
        if (netListenerObj instanceof OnNetworkConnectListener) {
            OnNetworkConnectListener connectListener = (OnNetworkConnectListener) netListenerObj;
            if (completeAction != null && !connectListener.isConnected()) {
                completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.netRequest, 0));

                completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, 0));
                return;
            }
        }
        Request.Builder builder = getBuilder(transParams.getUrl(), transParams.getHeaders(), retrofitParams);
        if (builder == null) {
            completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.businessProcess, 0));
            return;
        }
        Request request = builder.build();
        OkHttpClient client = OkRx.getInstance().getOkHttpClient();
        StringCallback callback = new StringCallback(successAction, completeAction) {
            @Override
            protected void onSuccessCall(ResponseData responseData, RetrofitParams retrofitParams, HashMap<String, String> headers) {
                ResponseDataType responseDataType = responseData.getResponseDataType();
                if (responseDataType != ResponseDataType.object) {
                    return;
                }
                CallStatus callStatus = retrofitParams.getCallStatus();
                if (callStatus != CallStatus.OnlyNet && !TextUtils.isEmpty(retrofitParams.getCacheKey())) {
                    String ckey = String.format("%s%s", retrofitParams.getCacheKey(), getAllParamsJoin(headers, retrofitParams));
                    String mkey = String.format("%s_%s", String.valueOf(ckey.hashCode()), retrofitParams.getCacheKey());
                    EffectiveMMkvUtils.getInstance().putString(mkey, responseData.getResponse(), retrofitParams.getCacheTime());
                }
            }
        };
        callback.setRequestKey(ckey);
        callback.setHeaders(retrofitParams.getHeadParams());
        callback.setRetrofitParams(retrofitParams);
        callback.setCancelIntervalCacheCall(isCancelIntervalCacheCall());
        //数据类型
        callback.setDataClass(retrofitParams.getDataClass());
        callback.setCallStatus(callStatus);
        callback.setResponseDataType(retrofitParams.getResponseDataType());
        //请求失败后是否重试
        callback.setFailureRetry(retrofitParams.isFailureRetry());
        callback.setFailureRetryCount(retrofitParams.getFailureRetryCount());
        //设置请求开始时间
        callback.setRequestStartTime(System.currentTimeMillis());
        //绑定cookies
        bindCookies(client, request.url());
        Call call = client.newCall(request);
        //设置当前请求超时时间
        long timeoutMillis = retrofitParams.getTimeoutMillis();
        if (timeoutMillis > 0) {
            Timeout timeout = call.timeout();
            timeout.timeout(timeoutMillis, TimeUnit.MILLISECONDS);
        }
        //请求网络
        call.enqueue(callback);
    }
}
