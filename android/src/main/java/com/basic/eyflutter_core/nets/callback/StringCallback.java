package com.basic.eyflutter_core.nets.callback;

import android.text.TextUtils;

import com.basic.eyflutter_core.nets.OkRx;
import com.basic.eyflutter_core.nets.RequestCodeUtils;
import com.basic.eyflutter_core.nets.beans.CompleteResponse;
import com.basic.eyflutter_core.nets.beans.RequestAlarmInfo;
import com.basic.eyflutter_core.nets.beans.ResponseData;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.beans.SuccessResponse;
import com.basic.eyflutter_core.nets.enums.CallStatus;
import com.basic.eyflutter_core.nets.enums.DataType;
import com.basic.eyflutter_core.nets.enums.ErrorType;
import com.basic.eyflutter_core.nets.enums.RequestState;
import com.basic.eyflutter_core.nets.enums.ResponseDataType;
import com.basic.eyflutter_core.nets.events.OnRequestAlarmApiListener;
import com.basic.eyflutter_core.nets.requests.NetErrorWith;
import com.cloud.eyutils.beans.TaskEntry;
import com.cloud.eyutils.events.Action1;
import com.cloud.eyutils.logs.Logger;
import com.cloud.eyutils.tasks.TaskManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2018/9/30
 * Description:
 * Modifier:
 * ModifyContent:
 */
public abstract class StringCallback implements Callback {

    //处理成功回调
    private Action1<SuccessResponse> successAction = null;
    //请求完成时回调(成功或失败)
    private Action1<CompleteResponse> completeAction = null;
    //数据返回内容
    private ResponseData responseData = new ResponseData();
    //请求回调状态
    private CallStatus callStatus = CallStatus.OnlyNet;
    //是否取消间隔缓存回调
    private boolean isCancelIntervalCacheCall = false;
    //返回数据类型
    private Class dataClass = null;
    //headers
    private HashMap<String, String> headers = null;
    //响应数据类型
    private ResponseDataType responseDataType = null;
    //请求失败后是否重试
    private boolean isFailureRetry = false;
    //失败重试计数
    private int failureRetryCount;
    //request queue key
    private String requestKey;
    private RetrofitParams retrofitParams;
    //请求开始时间（毫秒）
    private long requestStartTime;

    public RetrofitParams getRetrofitParams() {
        if (retrofitParams == null) {
            retrofitParams = new RetrofitParams();
        }
        return retrofitParams;
    }

    public void setRetrofitParams(RetrofitParams retrofitParams) {
        this.retrofitParams = retrofitParams;
    }

    public boolean isCancelIntervalCacheCall() {
        return isCancelIntervalCacheCall;
    }

    public void setCancelIntervalCacheCall(boolean cancelIntervalCacheCall) {
        isCancelIntervalCacheCall = cancelIntervalCacheCall;
    }

    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public void setDataClass(Class dataClass) {
        this.dataClass = dataClass;
    }

    protected abstract void onSuccessCall(ResponseData responseData, RetrofitParams retrofitParams, HashMap<String, String> headers);

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void setResponseDataType(ResponseDataType responseDataType) {
        this.responseDataType = responseDataType;
    }

    public void setFailureRetry(boolean failureRetry) {
        isFailureRetry = failureRetry;
    }

    public void setFailureRetryCount(int failureRetryCount) {
        this.failureRetryCount = failureRetryCount;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public void setRequestStartTime(long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public StringCallback(Action1<SuccessResponse> successAction,
                          Action1<CompleteResponse> completeAction) {
        this.successAction = successAction;
        this.completeAction = completeAction;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        int code = RequestCodeUtils.getCodeByError(e.getMessage());
        if (call.isCanceled()) {
            if (completeAction != null) {
                //请求失败后是否重试
                if (isFailureRetry) {
                    if (failureAutoCall(call)) {
                        return;
                    }
                }
                //回调错误
                completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.netRequest, code));
                //结束回调
                completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, code));
                //从队列中移除请求对象
                OkRx.getInstance().removeRequest(requestKey);
            }
            return;
        }
        requestAlarmApiCallback(null, e);
        String message = e.getMessage() == null ? "" : e.getMessage();
        if (message.contains("Unable to resolve host") ||
                message.contains("Failed to connect")) {
            //这里做dns处理
            if (completeAction != null) {
                //错误回调
                completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.netRequest, code));
                //完成回调
                completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, code));
                //从队列中移除请求对象
                OkRx.getInstance().removeRequest(requestKey);
            }
            NetErrorWith netErrorWith = new NetErrorWith();
            netErrorWith.call(call, e);
            return;
        }
        if (!call.isExecuted()) {
            if (!failReConnect(call)) {
                //抛出失败回调到全局监听
                if (completeAction != null) {
                    //错误回调
                    completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.netRequest, code));
                    //完成回调
                    completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, code));
                    //从队列中移除请求对象
                    OkRx.getInstance().removeRequest(requestKey);
                }
                NetErrorWith netErrorWith = new NetErrorWith();
                netErrorWith.call(call, e);
            }
            return;
        }
        //抛出失败回调到全局监听
        if (completeAction != null) {
            //请求失败后是否重试
            if (isFailureRetry) {
                if (failureAutoCall(call)) {
                    return;
                }
            }
            //错误回调
            completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.netRequest, 0));
            //完成回调
            completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, 0));
            //从队列中移除请求对象
            OkRx.getInstance().removeRequest(requestKey);
        }
        NetErrorWith netErrorWith = new NetErrorWith();
        netErrorWith.call(call, e);
    }

    private boolean failReConnect(Call call) {
        Request request = call.request();
        HttpUrl url = request.url();
        String host = url.host();
        Set<String> domainList = OkRx.getInstance().getFailDomainList();
        if (domainList.contains(host)) {
            //如果域名已在失败列表在新创建连接并重新请求仍失败,服务器地址有问题或当前网络异常;
            //此时直接返回即可
            return false;
        }
        domainList.add(host);
        //如果连接已经被取消时则重新建立
        OkHttpClient client = OkRx.getInstance().getOkHttpClient();
        //创建新请求
        Call clone = call.clone();
        client.newCall(clone.request()).enqueue(this);
        return true;
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
            //请求成功后将连接从缓存列表移除
            Request request = call.request();
            HttpUrl url = request.url();
            String host = url.host();
            Set<String> domainList = OkRx.getInstance().getFailDomainList();
            if (domainList.contains(host)) {
                domainList.remove(host);
            }
            if (!response.isSuccessful()) {
                if (completeAction != null) {
                    //请求失败后是否重试
                    if (isFailureRetry) {
                        if (failureAutoCall(call)) {
                            requestAlarmApiCallback(response, null);
                            return;
                        }
                    }
                    int code = response.code();
                    completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.businessProcess, code));
                }
            } else {
                ResponseBody body = response.body();
                if (body == null) {
                    if (completeAction != null) {
                        //请求失败后是否重试
                        if (isFailureRetry) {
                            if (failureAutoCall(call)) {
                                requestAlarmApiCallback(response, null);
                                return;
                            }
                        }
                        int code = response.code();
                        completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.businessProcess, code));
                    }
                } else {
                    int code = response.code();
                    bindResponseData(call, body, code);
                }
                requestAlarmApiCallback(response, null);
            }
        } catch (Exception e) {
            Logger.error(e);
            int code = response.code();
            completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.businessProcess, code));
            requestAlarmApiCallback(null, e);
        } finally {
            if (completeAction != null) {
                int code = response.code();
                completeAction.call(new CompleteResponse(RequestState.Completed, ErrorType.none, code));
            }
            //从队列中移除请求对象
            OkRx.getInstance().removeRequest(requestKey);
        }
    }

    private void bindResponseData(Call call, ResponseBody body, int code) throws IOException {
        responseData.setResponseDataType(responseDataType);
        if (responseDataType == ResponseDataType.object) {
            //object\int\double\float\long\string
            responseData.setResponse(body.string());
            body.close();
            if (successAction == null) {
                return;
            }
            //如果不是json且请求的数据类型不是基础数据类型则回调error
            if (dataClass == String.class ||
                    dataClass == Integer.class ||
                    dataClass == Double.class ||
                    dataClass == Float.class ||
                    dataClass == Long.class ||
                    !TextUtils.isEmpty(responseData.getResponse())) {
                if (callStatus != CallStatus.WeakCache && !isCancelIntervalCacheCall()) {
                    //此状态下不做网络回调但做缓存
                    SuccessResponse successResponse = new SuccessResponse(responseData, DataType.NetData);
                    successResponse.setRetrofitParams(retrofitParams);
                    successResponse.setCode(code);
                    successAction.call(successResponse);
                }
                onSuccessCall(responseData, retrofitParams, headers);
            } else {
                if (completeAction != null) {
                    //请求失败后是否重试
                    if (isFailureRetry) {
                        if (failureAutoCall(call)) {
                            return;
                        }
                    }
                    completeAction.call(new CompleteResponse(RequestState.Error, ErrorType.businessProcess, code));
                }
            }
        } else if (responseDataType == ResponseDataType.byteData) {
            responseData.setBytes(body.bytes());
            if (successAction == null) {
                return;
            }
            SuccessResponse successResponse = new SuccessResponse(responseData, DataType.NetData);
            successResponse.setRetrofitParams(retrofitParams);
            successResponse.setCode(code);
            successAction.call(successResponse);
        } else if (responseDataType == ResponseDataType.stream) {
            responseData.setStream(body.byteStream());
            if (successAction == null) {
                return;
            }
            SuccessResponse successResponse = new SuccessResponse(responseData, DataType.NetData);
            successResponse.setRetrofitParams(retrofitParams);
            successResponse.setCode(code);
            successAction.call(successResponse);
        }
    }

    //失败自动回调
    private boolean failureAutoCall(Call call) {
        //获取请求对象
        Request request = call.request();
        //获取请求url
        HttpUrl httpUrl = request.url();
        String url = httpUrl.toString();

        TaskManager taskManager = TaskManager.getInstance();
        TaskEntry<? extends Runnable> taskEntry = taskManager.getTask(url);
        if (taskEntry == null) {
            taskManager.addPerformTask(url, new TaskRunable(url, call.clone(), this, taskManager), failureRetryCount, 5000);
            requestStartTime = System.currentTimeMillis() + 5000;
        } else {
            if (taskEntry.getCount() > taskEntry.getPerformCounts()) {
                taskManager.removeTask(url);
                return false;
            } else {
                taskEntry.setCount(taskEntry.getCount() + 1);
                taskEntry.setDelayTime(taskEntry.getDelayTime() + 5000);
                taskManager.execute(taskEntry);
                requestStartTime = System.currentTimeMillis() + taskEntry.getDelayTime() + 5000;
            }
        }
        return true;
    }

    private class TaskRunable implements Runnable {

        private String key;
        private Call call;
        private StringCallback callback;
        private TaskManager taskManager;

        public TaskRunable(String key, Call call, StringCallback callback, TaskManager taskManager) {
            this.key = key;
            this.call = call;
            this.callback = callback;
            this.taskManager = taskManager;
        }

        @Override
        public void run() {
            if (call == null || callback == null) {
                if (taskManager != null) {
                    taskManager.removeTask(key);
                }
                return;
            }
            OkHttpClient client = OkRx.getInstance().getOkHttpClient();
            //创建新请求
            client.newCall(call.request()).enqueue(callback);
        }
    }

    private void requestAlarmApiCallback(Response response, Exception e) {
        OnRequestAlarmApiListener requestAlarmApiListener = OkRx.getInstance().getOnRequestAlarmApiListener();
        if (null == requestAlarmApiListener) {
            return;
        }
        if (requestStartTime <= 0) {
            return;
        }

        if (System.currentTimeMillis() - requestStartTime < 500) {
            //请求响应时间小于500ms则不上报
            return;
        }

        RequestAlarmInfo requestAlarmInfo = new RequestAlarmInfo();
        requestAlarmInfo.setUrl(retrofitParams.getRequestUrl());
        requestAlarmInfo.setHeaders(retrofitParams.getHeadParams());
        requestAlarmInfo.setParams(retrofitParams.getParams());
        requestAlarmInfo.setRequestType(retrofitParams.getRequestType());
        requestAlarmInfo.setRequestStartTime(requestStartTime);
        requestAlarmInfo.setRequestEndTime(System.currentTimeMillis());
        if (null != e) {
            requestAlarmInfo.setMessage(e.getMessage());
        }
        if (null != response) {
            requestAlarmInfo.setCode(response.code());
        }
        requestAlarmApiListener.onRequestAlarmApi(requestAlarmInfo);
    }
}
