package com.basic.eyflutter_core.nets.requests;

import android.text.TextUtils;

import com.basic.eyflutter_core.nets.OkRx;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.callback.FileCallback;
import com.basic.eyflutter_core.nets.enums.RequestState;
import com.basic.eyflutter_core.nets.enums.RequestType;
import com.cloud.eyutils.events.Action1;
import com.cloud.eyutils.utils.ObjectJudge;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2017/11/15
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class OkRxDownloadFileRequest extends BaseRequest {

    public void call(String url,
                     HashMap<String, String> headers,
                     TreeMap<String, Object> params,
                     File downFile,
                     Action1<Float> progressAction,
                     Action1<File> successAction,
                     Action1<RequestState> completeAction) {
        if (TextUtils.isEmpty(url) || downFile == null || !downFile.exists()) {
            if (completeAction != null) {
                completeAction.call(RequestState.Completed);
            }
            return;
        }
        RetrofitParams retrofitParams = new RetrofitParams();
        retrofitParams.setRequestType(RequestType.GET);
        if (!ObjectJudge.isNullOrEmpty(params)) {
            retrofitParams.getParams().putAll(params);
        }
        Request.Builder builder = getBuilder(url, headers, retrofitParams).get();
        Request request = builder.build();
        OkHttpClient client = OkRx.getInstance().getOkHttpClient();
        //绑定cookies
        bindCookies(client, request.url());
        //请求网络
        client.newCall(request).enqueue(new FileCallback(downFile, progressAction, successAction, completeAction));
    }
}
