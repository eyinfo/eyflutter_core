package com.basic.eyflutter_core.nets.requests;

import android.text.TextUtils;

import com.basic.eyflutter_core.nets.OkRx;
import com.basic.eyflutter_core.nets.beans.CompleteBitmapResponse;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.beans.SuccessBitmapResponse;
import com.basic.eyflutter_core.nets.callback.BitmapCallback;
import com.basic.eyflutter_core.nets.enums.RequestState;
import com.basic.eyflutter_core.nets.enums.RequestType;
import com.basic.eyflutter_core.nets.events.OnNetworkConnectListener;
import com.cloud.eyutils.CdLibConfig;
import com.cloud.eyutils.constants.LibConfigKeys;
import com.cloud.eyutils.events.Action1;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-29
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class OkRxBitmapRequest extends BaseRequest {

    public void call(String url, Action1<SuccessBitmapResponse> successAction, Action1<CompleteBitmapResponse> completeAction) {
        if (TextUtils.isEmpty(url)) {
            if (completeAction != null) {
                completeAction.call(new CompleteBitmapResponse(RequestState.Completed));
            }
            return;
        }
        if (successAction == null) {
            if (completeAction != null) {
                completeAction.call(new CompleteBitmapResponse(RequestState.Completed));
            }
            return;
        }
        //如果网络未连接则不作请求
        Object netListenerObj = CdLibConfig.getInstance().getConfigValue(LibConfigKeys.netStatusConfigKey);
        if (netListenerObj instanceof OnNetworkConnectListener) {
            OnNetworkConnectListener connectListener = (OnNetworkConnectListener) netListenerObj;
            if (completeAction != null && !connectListener.isConnected()) {
                completeAction.call(new CompleteBitmapResponse(RequestState.Completed));
            }
            return;
        }
        RetrofitParams retrofitParams = new RetrofitParams();
        retrofitParams.setRequestType(RequestType.GET);
        Request.Builder builder = getBuilder(url, null, retrofitParams);
        if (builder == null) {
            completeAction.call(new CompleteBitmapResponse(RequestState.Completed));
            return;
        }
        Request request = builder.build();
        OkHttpClient client = OkRx.getInstance().getOkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new BitmapCallback(successAction, completeAction));
    }
}
