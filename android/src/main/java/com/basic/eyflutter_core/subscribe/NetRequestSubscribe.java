package com.basic.eyflutter_core.subscribe;

import android.text.TextUtils;

import com.basic.eyflutter_core.ChannelConstants;
import com.basic.eyflutter_core.channel.ChannelPlugin;
import com.basic.eyflutter_core.channel.OnDistributionSubscribe;
import com.basic.eyflutter_core.nets.OkRxManager;
import com.basic.eyflutter_core.nets.beans.CompleteResponse;
import com.basic.eyflutter_core.nets.beans.FlutterDataResponse;
import com.basic.eyflutter_core.nets.beans.ResponseData;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.beans.SuccessResponse;
import com.basic.eyflutter_core.nets.beans.TransParams;
import com.basic.eyflutter_core.nets.enums.RequestContentType;
import com.basic.eyflutter_core.nets.enums.RequestState;
import com.basic.eyflutter_core.nets.enums.RequestType;
import com.basic.eyflutter_core.nets.enums.ResponseDataType;
import com.cloud.eyutils.HandlerManager;
import com.cloud.eyutils.events.Action1;
import com.cloud.eyutils.events.RunnableParamsN;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.JsonUtils;

import java.util.HashMap;
import java.util.TreeMap;

import io.flutter.plugin.common.MethodChannel;

public class NetRequestSubscribe extends OnDistributionSubscribe {

    private MethodChannel.Result result;

    @Override
    public void onSubscribe(MethodChannel.Result result, HashMap<String, Object> arguments) {
        this.result = result;
        String method = ConvertUtils.toString(arguments.get("method"));
        String requestId = ConvertUtils.toString(arguments.get("requestId"));
        String contentType = ConvertUtils.toString(arguments.get("contentType"));
        String url = ConvertUtils.toString(arguments.get("url"));
        HashMap<String, Object> data = (HashMap<String, Object>) arguments.get("data");
        HashMap<String, String> headers = (HashMap<String, String>) arguments.get("headers");
        if (TextUtils.equals(method, "GET")) {
            request(requestId, url, contentType, RequestType.GET, data);
        } else if (TextUtils.equals(method, "PUT")) {
            request(requestId, url, contentType, RequestType.PUT, data);
        } else if (TextUtils.equals(method, "POST")) {
            request(requestId, url, contentType, RequestType.POST, data);
        } else if (TextUtils.equals(method, "DELETE")) {
            request(requestId, url, contentType, RequestType.DELETE, data);
        } else {
            responseHanndler("error", "");
        }
    }

    private void responseHanndler(String type, String data) {
        if (result == null) {
            return;
        }
        FlutterDataResponse response = new FlutterDataResponse();
        response.setType(type);
        response.setData(data);
        String content = JsonUtils.toJson(response);
        HandlerManager.getInstance().post(new RunnableParamsN<String>() {
            @Override
            public void run(String... params) {
                result.success(params[0]);
            }
        }, content);
    }

    private void request(String requestId, String url, String contentType, RequestType requestType, HashMap<String, String> headers, HashMap<String, Object> data) {
        TransParams transParams = new TransParams();
        transParams.setUrl(url);
        RetrofitParams retrofitParams = new RetrofitParams();
        if (TextUtils.equals(contentType, "form")) {
            retrofitParams.setRequestContentType(RequestContentType.Form);
        } else {
            retrofitParams.setRequestContentType(RequestContentType.Json);
        }
        retrofitParams.setRequestType(requestType);
        retrofitParams.setRequestUrl(url);
        retrofitParams.setResponseDataType(ResponseDataType.object);
        HashMap<String, String> headParams = retrofitParams.getHeadParams();
        headParams.putAll(headers);
        TreeMap<String, Object> params = retrofitParams.getParams();
        params.putAll(data);
        transParams.setRetrofitParams(retrofitParams);
        OkRxManager.getInstance().request(transParams, new Action1<SuccessResponse>() {
            @Override
            public void call(SuccessResponse response) {
                ResponseData responseData = response.getResponseData();
                responseHanndler("success", responseData.getResponse());
            }
        }, new CompleteAction(requestId));
    }

    private class CompleteAction implements Action1<CompleteResponse> {

        private String requestId;

        public CompleteAction(String requestId) {
            this.requestId = requestId;
        }

        @Override
        public void call(CompleteResponse response) {
            if (response.getRequestState() == RequestState.Error) {
                responseHanndler("error", "");
            } else if (response.getRequestState() == RequestState.Completed) {
                notifyComplete(requestId);
            }
        }
    }

    private void notifyComplete(String requestId) {
        HandlerManager.getInstance().post(new RunnableParamsN<String>() {
            @Override
            public void run(String... params) {
                ChannelPlugin.getInstance().invokeMethod(ChannelConstants.netRequestCompleteMethodName, params[0], null);
            }
        }, requestId);
    }
}
