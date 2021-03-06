package com.basic.eyflutter_core.nets;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.basic.eyflutter_core.nets.annotations.ApiCheckAnnotation;
import com.basic.eyflutter_core.nets.annotations.DataKeyField;
import com.basic.eyflutter_core.nets.annotations.DetailCacheParsingField;
import com.basic.eyflutter_core.nets.annotations.ReturnCodeFilter;
import com.basic.eyflutter_core.nets.beans.CompleteResponse;
import com.basic.eyflutter_core.nets.beans.ResponseData;
import com.basic.eyflutter_core.nets.beans.ResponseParsing;
import com.basic.eyflutter_core.nets.beans.ResultParams;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.beans.SuccessResponse;
import com.basic.eyflutter_core.nets.beans.TransParams;
import com.basic.eyflutter_core.nets.enums.CallStatus;
import com.basic.eyflutter_core.nets.enums.DataType;
import com.basic.eyflutter_core.nets.enums.ErrorType;
import com.basic.eyflutter_core.nets.enums.RequestState;
import com.basic.eyflutter_core.nets.enums.RequestType;
import com.basic.eyflutter_core.nets.enums.ResponseDataType;
import com.basic.eyflutter_core.nets.events.OnApiRetCodesFilterListener;
import com.basic.eyflutter_core.nets.events.OnAuthListener;
import com.basic.eyflutter_core.nets.events.OnBeanParsingJsonListener;
import com.basic.eyflutter_core.nets.events.OnGlobalRequestParamsListener;
import com.basic.eyflutter_core.nets.events.OnGlobalReuqestHeaderListener;
import com.basic.eyflutter_core.nets.events.OnSuccessfulListener;
import com.basic.eyflutter_core.nets.properties.ByteRequestItem;
import com.basic.eyflutter_core.nets.properties.OkRxConfigParams;
import com.basic.eyflutter_core.nets.properties.OkRxValidParam;
import com.basic.eyflutter_core.utils.EffectiveMMkvUtils;
import com.cloud.eyutils.events.Action1;
import com.cloud.eyutils.events.Action2;
import com.cloud.eyutils.events.Func2;
import com.cloud.eyutils.logs.Logger;
import com.cloud.eyutils.observable.ObservableComponent;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.GlobalUtils;
import com.cloud.eyutils.utils.JsonUtils;
import com.cloud.eyutils.utils.ObjectJudge;
import com.cloud.eyutils.utils.PathsUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2016/6/14
 * Description:????????????????????????(????????????)
 */
public class BaseService {

    private Handler mhandler = new Handler(Looper.getMainLooper());
    private ReturnCodeFilter returnCodeFilter = null;

    protected void onRequestCompleted() {
        //????????????(??????)
    }

    protected void onRequestError() {
        //????????????
    }

    protected <S extends BaseService> void baseConfig(BaseService baseService, BaseSubscriber<Object, S> baseSubscriber, RetrofitParams retrofitParams, OkRxValidParam validParam, String useClass) {
        try {
            if (!TextUtils.isEmpty(retrofitParams.getRequestUrl())) {
                String requestUrl = retrofitParams.getRequestUrl();
                //????????????????????????
                HashMap<String, String> mHeaders = bindGlobalHeaders();
                //???????????????
                mHeaders.putAll(retrofitParams.getHeadParams());
                //?????????????????????
                if (returnCodeFilter == null) {
                    returnCodeFilter = validParam.getReturnCodeFilter();
                }
                retrofitParams.setInvokeMethodName(validParam.getInvokeMethodName());
                if (retrofitParams.getRequestType() == RequestType.BYTES) {
                    HashMap<String, Object> updateByteParams = getUploadByteParams(retrofitParams);
                    List<ByteRequestItem> uploadByteItems = getUploadByteItems(retrofitParams);
                    subBytes(requestUrl, mHeaders, updateByteParams, retrofitParams, uploadByteItems, baseService, baseSubscriber);
                } else {
                    //????????????????????????(delQuery???params?????????????????????)
                    OnGlobalRequestParamsListener globalRequestParamsListener = OkRx.getInstance().getGlobalRequestParamsListener();
                    if (globalRequestParamsListener != null) {
                        HashMap<String, Object> globalParams = globalRequestParamsListener.onGlobalParams();
                        if (!ObjectJudge.isNullOrEmpty(globalParams)) {
                            TreeMap<String, Object> params = retrofitParams.getParams();
                            for (Map.Entry<String, Object> entry : globalParams.entrySet()) {
                                if (!params.containsKey(entry.getKey())) {
                                    params.put(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }
                    request(requestUrl, mHeaders, retrofitParams, useClass, baseService, baseSubscriber);
                }
            } else {
                finishedRequest(baseService);
            }
        } catch (Exception e) {
            finishedRequest(baseService);
            Logger.error(e);
        }
    }

    //?????????????????????
    private HashMap<String, String> bindGlobalHeaders() {
        HashMap<String, String> headParams = new HashMap<String, String>();
        HashMap<String, String> defaultHeaderParams = OkRx.getInstance().getHeaderParams();
        if (!ObjectJudge.isNullOrEmpty(defaultHeaderParams)) {
            for (Map.Entry<String, String> entry : defaultHeaderParams.entrySet()) {
                headParams.put(entry.getKey(), entry.getValue());
            }
        }
        //????????????????????????
        OnGlobalReuqestHeaderListener headerListener = OkRx.getInstance().getOnGlobalReuqestHeaderListener();
        if (headerListener == null) {
            return headParams;
        }
        HashMap<String, String> globalHeaderParams = headerListener.onHeaderParams();
        if (ObjectJudge.isNullOrEmpty(globalHeaderParams)) {
            return headParams;
        }
        for (Map.Entry<String, String> entry : globalHeaderParams.entrySet()) {
            headParams.put(entry.getKey(), entry.getValue());
        }
        return headParams;
    }

    private void finishedRequest(final BaseService baseService) {
        if (ObjectJudge.isMainThread()) {
            baseService.onRequestCompleted();
        } else {
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    baseService.onRequestCompleted();
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private <S extends BaseService> void successDealWith(SuccessResponse successResponse,
                                                         BaseService baseService,
                                                         BaseSubscriber<Object, S> baseSubscriber) {
        boolean isBasicData = false;
        RetrofitParams retrofitParams = successResponse.getRetrofitParams();
        Class dataClass = retrofitParams.getDataClass();
        //?????????????????????
        ResponseParsing responseParsing = new ResponseParsing();
        responseParsing.setDataClass(dataClass);
        //????????????object\byte\stream
        ResponseData responseData = successResponse.getResponseData();
        ResponseDataType responseDataType = responseData.getResponseDataType();
        responseParsing.setResponseDataType(responseDataType);
        if (responseDataType == ResponseDataType.object) {
            if (dataClass == String.class ||
                    dataClass == Integer.class ||
                    dataClass == Double.class ||
                    dataClass == Float.class ||
                    dataClass == Long.class) {
                //??????dataClass???????????????????????????????????????
                isBasicData = true;
                responseParsing.setData(responseData.getResponse());
            } else {
                OnBeanParsingJsonListener jsonListener = OkRx.getInstance().getOnBeanParsingJsonListener();
                if (jsonListener == null) {
                    if (retrofitParams.isCollectionDataType()) {
                        responseParsing.setData(JsonUtils.parseArray(responseData.getResponse(), dataClass));
                    } else {
                        responseParsing.setData(JsonUtils.parseT(responseData.getResponse(), dataClass));
                    }
                } else {
                    responseParsing.setData(jsonListener.onBeanParsingJson(responseData.getResponse(), dataClass, retrofitParams.isCollectionDataType()));

                    if (responseParsing.getData() != null && retrofitParams.getCacheTime() != null && !TextUtils.isEmpty(retrofitParams.getCacheKey())) {
                        DetailCacheParam detailCacheParam = new DetailCacheParam();
                        detailCacheParam.cacheKey = retrofitParams.getCacheKey();
                        detailCacheParam.data = responseParsing.getData();
                        detailCacheParam.isCollectionDataType = retrofitParams.isCollectionDataType();
                        detailCacheParam.duration = retrofitParams.getCacheTime();
                        detailCacheParam.parsingFieldMapping = retrofitParams.getParsingFieldMapping();
                        detailCacheComponent.build(detailCacheParam);
                    }
                }
            }
            //????????????????????????
            //???????????????????????????????????????????????????
            if (responseParsing.getData() == null) {
                if (successResponse.getDataType() == DataType.CacheData) {
                    return;
                }
                //????????????EmptyForOnlyCache(????????????????????????????????????)
                if (successResponse.getDataType() == DataType.EmptyForOnlyCache) {
                    successCall(baseService, baseSubscriber, responseParsing, retrofitParams, successResponse.getDataType(), successResponse.getCode());
                } else {
                    sendErrorAction(baseService, baseSubscriber, ErrorType.businessProcess, successResponse.getCode());
                    finishedRequest(baseService);
                }
                return;
            }
        } else if (responseDataType == ResponseDataType.byteData) {
            //????????????
            responseParsing.setBytes(responseData.getBytes());
            //????????????????????????
            if (responseParsing.getBytes() == null) {
                sendErrorAction(baseService, baseSubscriber, ErrorType.businessProcess, successResponse.getCode());
                finishedRequest(baseService);
                return;
            }
        } else if (responseDataType == ResponseDataType.stream) {
            //?????????
            responseParsing.setStream(responseData.getStream());
            //????????????????????????
            if (responseParsing.getStream() == null) {
                sendErrorAction(baseService, baseSubscriber, ErrorType.businessProcess, successResponse.getCode());
                finishedRequest(baseService);
                return;
            }
        }
        //???????????????\byte\stream???????????????
        if (retrofitParams.isCollectionDataType() || isBasicData || responseDataType != ResponseDataType.object) {
            //????????????
            if (responseDataType == ResponseDataType.byteData && dataClass != Class.class) {
                if (dataClass == Bitmap.class) {
                    responseParsing.setData(ConvertUtils.toBitmap(responseParsing.getBytes()));
                    responseParsing.setBytes(null);
                } else if (dataClass == String.class) {
                    responseParsing.setData(new String(responseParsing.getBytes()));
                    responseParsing.setBytes(null);
                }
            }
            successCall(baseService, baseSubscriber, responseParsing, retrofitParams, successResponse.getDataType(), successResponse.getCode());
        } else {
            //???????????????????????????????????????
            OkRxConfigParams okRxConfigParams = OkRx.getInstance().getOkRxConfigParams();
            if (okRxConfigParams.isNetStatusCodeIntercept()) {
                if (!filterMatchRetCodes(responseParsing.getData())) {
                    successCall(baseService, baseSubscriber, responseParsing, retrofitParams, successResponse.getDataType(), successResponse.getCode());
                }
            } else {
                successCall(baseService, baseSubscriber, responseParsing, retrofitParams, successResponse.getDataType(), successResponse.getCode());
            }
        }
    }

    private class DetailCacheParam {
        String cacheKey;
        boolean isCollectionDataType;
        Object data;
        Duration duration;
        String parsingFieldMapping;
    }

    private ObservableComponent<Object, DetailCacheParam> detailCacheComponent = new ObservableComponent<Object, DetailCacheParam>() {

        private String getDataId(Object object) {
            if (object == null) {
                return "";
            }
            String dataId = "";
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(DataKeyField.class)) {
                    Object value = GlobalUtils.getPropertiesValue(object, field.getName());
                    dataId = (value == null ? "" : String.valueOf(value));
                    break;
                }
            }
            return dataId;
        }

        private Object getParsingObject(Object object) {
            if (object == null || object instanceof List) {
                return object;
            }
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(DetailCacheParsingField.class)) {
                    return GlobalUtils.getPropertiesValue(object, field.getName());
                }
            }
            return object;
        }

        private void saveDetals(Object object, String cacheKey, Duration duration) {
            List list = (List) object;
            HashMap<String, String> dataMap = new HashMap<>();
            for (Object item : list) {
                String dataId = getDataId(item);
                if (TextUtils.isEmpty(dataId)) {
                    continue;
                }
                String value = JsonUtils.toJson(item);
                dataMap.put(dataId, value);
            }
            String json = JsonUtils.toJson(dataMap);
            EffectiveMMkvUtils.getInstance().putString(cacheKey, json, duration);
        }

        private Object getParsingObject(Object data, String[] mappingFields) {
            if (ObjectJudge.isNullOrEmpty(mappingFields) || data instanceof List) {
                return data;
            }
            for (String field : mappingFields) {
                Object value = GlobalUtils.getPropertiesValue(data, field);
                if (value instanceof List) {
                    return value;
                } else {
                    data = value;
                }
            }
            return data;
        }

        @Override
        protected Object subscribeWith(DetailCacheParam... detailCacheParams) throws Exception {
            if (!ObjectJudge.isNullOrEmpty(detailCacheParams)) {
                DetailCacheParam param = detailCacheParams[0];
                //????????????????????????????????????
                if (!TextUtils.isEmpty(param.parsingFieldMapping)) {
                    String[] mfields = param.parsingFieldMapping.split("->");
                    param.data = getParsingObject(param.data, mfields);
                }
                //??????????????????
                if (!param.isCollectionDataType) {
                    Object parsingObject = getParsingObject(param.data);
                    if (parsingObject instanceof List) {
                        saveDetals(parsingObject, param.cacheKey, param.duration);
                    } else {
                        String dataId = getDataId(parsingObject);
                        if (TextUtils.isEmpty(dataId)) {
                            return null;
                        }
                        String value = JsonUtils.toJson(parsingObject);
                        String key = String.format("%s_%s", dataId, param.cacheKey);
                        EffectiveMMkvUtils.getInstance().putString(key, value, param.duration);
                    }
                    return null;
                }
                if (!(param.data instanceof List)) {
                    return null;
                }
                saveDetals(param.data, param.cacheKey, param.duration);
            }
            return super.subscribeWith(detailCacheParams);
        }
    };

    @SuppressWarnings("unchecked")
    private <S extends BaseService> void successCall(BaseService baseService,
                                                     BaseSubscriber<Object, S> baseSubscriber,
                                                     ResponseParsing responseParsing,
                                                     RetrofitParams retrofitParams,
                                                     DataType dataType,
                                                     int code) {
        if (baseSubscriber == null) {
            return;
        }
        //????????????
        ResponseDataType responseDataType = responseParsing.getResponseDataType();
        if (responseDataType == ResponseDataType.object) {
            ResultParams resultParams = new ResultParams();
            resultParams.setData(responseParsing.getData());
            resultParams.setDataType(dataType);
            resultParams.setRequestStartTime(retrofitParams.getCurrentRequestTime());
            resultParams.setRequestTotalTime(retrofitParams.getRequestTotalTime());
            resultParams.setCode(code);
            baseSubscriber.onNext(resultParams);
        } else if (responseDataType == ResponseDataType.byteData) {
            //????????????
            bindBytes(baseSubscriber, responseParsing, retrofitParams, dataType, retrofitParams.getCurrentRequestTime(), retrofitParams.getRequestTotalTime(), code);
        } else if (responseDataType == ResponseDataType.stream) {
            //?????????
            bindStream(baseSubscriber, responseParsing, retrofitParams, dataType, retrofitParams.getCurrentRequestTime(), retrofitParams.getRequestTotalTime(), code);
        }
    }

    private <T> boolean filterMatchRetCodes(T data) {
        Class<?> codesListeningClass = returnCodeFilter.retCodesListeningClass();
        if (returnCodeFilter == null || ObjectJudge.isNullOrEmpty(returnCodeFilter.retCodes()) || codesListeningClass == null) {
            return false;
        }
        List<String> codes = Arrays.asList(returnCodeFilter.retCodes());
        String code = String.valueOf(GlobalUtils.getPropertiesValue(data, "code"));
        if (!codes.contains(code)) {
            return false;
        }
        Object obj = JsonUtils.newNull(codesListeningClass);
        if (!(obj instanceof OnApiRetCodesFilterListener)) {
            //????????????????????????????????????????????????????????????????????????;
            return true;
        }
        OnApiRetCodesFilterListener filterListener = (OnApiRetCodesFilterListener) obj;
        filterListener.onApiRetCodesFilter(code, data);
        return true;
    }

    private <S extends BaseService> void subBytes(String requestUrl,
                                                  HashMap<String, String> httpHeaders,
                                                  HashMap<String, Object> httpParams,
                                                  final RetrofitParams retrofitParams,
                                                  List<ByteRequestItem> byteRequestItems,
                                                  final BaseService baseService,
                                                  final BaseSubscriber<Object, S> baseSubscriber) {
        OkRxManager.getInstance().uploadBytes(
                requestUrl,
                httpHeaders,
                httpParams,
                byteRequestItems,
                retrofitParams,
                new Action1<SuccessResponse>() {
                    @Override
                    public void call(SuccessResponse response) {
                        successDealWith(response, baseService, baseSubscriber);
                    }
                },
                new Action1<CompleteResponse>() {
                    @Override
                    public void call(CompleteResponse response) {
                        if (response.getRequestState() == RequestState.Error) {
                            sendErrorAction(baseService, baseSubscriber, response.getErrorType(), response.getCode());
                        } else if (response.getRequestState() == RequestState.Completed) {
                            finishedRequest(baseService);
                        }
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private <S extends BaseService> void sendErrorAction(final BaseService baseService,
                                                         final BaseSubscriber<Object, S> baseSubscriber,
                                                         final ErrorType errorType,
                                                         final int code) {
        final Action2<ErrorType, Integer> errorAction = new Action2<ErrorType, Integer>() {
            @Override
            public void call(ErrorType errorType, Integer code) {
                if (baseSubscriber == null) {
                    return;
                }
                OnSuccessfulListener successfulListener = baseSubscriber.getOnSuccessfulListener();
                if (successfulListener == null) {
                    return;
                }
                successfulListener.setCode(code == null ? 0 : code);
                successfulListener.onError(null, errorType, baseSubscriber.getExtra());
                successfulListener.onError(errorType, baseSubscriber.getExtra());
                successfulListener.onCompleted(baseSubscriber.getExtra());
            }
        };

        if (ObjectJudge.isMainThread()) {
            errorAction.call(errorType, code);
            baseService.onRequestError();
        } else {
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    errorAction.call(errorType, code);
                    baseService.onRequestError();
                }
            });
        }
    }

    private <S extends BaseService> void request(String reqreuestUrl,
                                                 HashMap<String, String> headers,
                                                 final RetrofitParams retrofitParams,
                                                 String useClass,
                                                 final BaseService baseService,
                                                 final BaseSubscriber<Object, S> baseSubscriber) {
        TransParams transParams = new TransParams();
        transParams.setUrl(reqreuestUrl);
        transParams.setHeaders(headers);
        transParams.setRetrofitParams(retrofitParams);
        transParams.setUseClass(useClass);
        OkRxManager.getInstance().request(transParams, new Action1<SuccessResponse>() {
                    @Override
                    public void call(SuccessResponse response) {
                        successDealWith(response, baseService, baseSubscriber);
                    }
                },
                new Action1<CompleteResponse>() {
                    @Override
                    public void call(CompleteResponse response) {
                        if (response.getRequestState() == RequestState.Error) {
                            sendErrorAction(baseService, baseSubscriber, response.getErrorType(), response.getCode());
                        } else if (response.getRequestState() == RequestState.Completed) {
                            finishedRequest(baseService);
                        }
                    }
                });
    }

    private List<ByteRequestItem> getUploadByteItems(RetrofitParams retrofitParams) {
        List<ByteRequestItem> lst = new ArrayList<ByteRequestItem>();
        TreeMap<String, Object> params = retrofitParams.getParams();
        if (ObjectJudge.isNullOrEmpty(params)) {
            return lst;
        }
        for (HashMap.Entry<String, Object> entry : params.entrySet()) {
            //?????????
            String key = entry.getKey();
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            //?????????
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if ((value instanceof byte[]) || (value instanceof Byte[])) {
                ByteRequestItem requestItem = new ByteRequestItem();
                requestItem.setFieldName(key);
                requestItem.setBs((byte[]) value);
                lst.add(requestItem);
            }
        }
        return lst;
    }

    private HashMap<String, Object> getUploadByteParams(RetrofitParams retrofitParams) {
        HashMap<String, Object> params2 = new HashMap<String, Object>();
        if (ObjectJudge.isNullOrEmpty(retrofitParams.getParams())) {
            return params2;
        }
        TreeMap<String, Object> params = retrofitParams.getParams();
        for (HashMap.Entry<String, Object> entry : params.entrySet()) {
            //?????????
            String key = entry.getKey();
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            //?????????
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof Integer) {
                params2.put(key, value);
            } else if (value instanceof Long) {
                params2.put(key, value);
            } else if (value instanceof String) {
                params2.put(key, value);
            } else if (value instanceof Double) {
                params2.put(key, value);
            } else if (value instanceof Float) {
                params2.put(key, value);
            } else if (value instanceof Boolean) {
                params2.put(key, value);
            } else if (value instanceof List) {
                params2.put(key, JsonUtils.toJson(value));
            }
        }
        return params2;
    }

    @SuppressWarnings("unchecked")
    private <T, S extends BaseService> void finishedRequest(final ErrorType errorType, final BaseSubscriber<T, S> baseSubscriber) {
        if (ObjectJudge.isMainThread()) {
            OnSuccessfulListener successfulListener = baseSubscriber.getOnSuccessfulListener();
            if (successfulListener != null) {
                successfulListener.onError(null, errorType, baseSubscriber.getExtra());
                successfulListener.onError(errorType, baseSubscriber.getExtra());
                successfulListener.onCompleted(baseSubscriber.getExtra());
            }
        } else {
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    OnSuccessfulListener successfulListener = baseSubscriber.getOnSuccessfulListener();
                    if (successfulListener != null) {
                        successfulListener.onError(null, errorType, baseSubscriber.getExtra());
                        successfulListener.onError(errorType, baseSubscriber.getExtra());
                        successfulListener.onCompleted(baseSubscriber.getExtra());
                    }
                }
            });
        }
    }

    protected <I, S extends BaseService> void requestObject(Class<I> apiClass,
                                                            S server,
                                                            final BaseSubscriber<Object, S> baseSubscriber,
                                                            OkRxValidParam validParam,
                                                            Func2<String, S, Integer> urlAction,
                                                            Func2<RetrofitParams, I, HashMap<String, Object>> decApiAction,
                                                            HashMap<String, Object> params,
                                                            String useClass) {
        try {
            //??????????????????????????????????????????
            if (validParam.isNeedLogin()) {
                OnAuthListener authListener = OkRx.getInstance().getOnAuthListener();
                if (authListener != null) {
                    authListener.onLoginCall(validParam.getInvokeMethodName());
                }
                finishedRequest(ErrorType.businessProcess, baseSubscriber);
                return;
            }
            //????????????????????????(?????????????????????????????????????????????????????????)
            if (!validParam.isFlag()) {
                finishedRequest(ErrorType.businessProcess, baseSubscriber);
                return;
            }
            if (urlAction == null || server == null) {
                finishedRequest(ErrorType.businessProcess, baseSubscriber);
                return;
            }
            OkRxParsing parsing = new OkRxParsing();
            ApiCheckAnnotation apiCheckAnnotation = validParam.getApiCheckAnnotation();
            I decApi = parsing.createAPI(apiClass, apiCheckAnnotation.callStatus());
            if (decApiAction == null || decApi == null || validParam.getApiCheckAnnotation() == null) {
                finishedRequest(ErrorType.businessProcess, baseSubscriber);
                return;
            }
            ApiRequestRunnable<I, S> runnable = new ApiRequestRunnable<>(server, baseSubscriber, validParam, urlAction, decApi, params, decApiAction, useClass, apiCheckAnnotation.detailCacheKey());
            runnable.run();
        } catch (Exception e) {
            finishedRequest(ErrorType.businessProcess, baseSubscriber);
        }
    }

    private class ApiRequestRunnable<I, S extends BaseService> implements Runnable {

        private S server;
        private BaseSubscriber<Object, S> baseSubscriber;
        private OkRxValidParam validParam;
        private Func2<String, S, Integer> urlAction;
        private I decApi;
        private HashMap<String, Object> params;
        private Func2<RetrofitParams, I, HashMap<String, Object>> decApiAction;
        private String useClass;
        private String detailCacheKey;

        public ApiRequestRunnable(S server,
                                  final BaseSubscriber<Object, S> baseSubscriber,
                                  OkRxValidParam validParam,
                                  Func2<String, S, Integer> urlAction,
                                  I decApi,
                                  HashMap<String, Object> params,
                                  Func2<RetrofitParams, I, HashMap<String, Object>> decApiAction,
                                  String useClass,
                                  String detailCacheKey) {
            this.server = server;
            this.baseSubscriber = baseSubscriber;
            this.validParam = validParam;
            this.urlAction = urlAction;
            this.decApi = decApi;
            this.params = params;
            this.decApiAction = decApiAction;
            this.useClass = useClass;
            this.detailCacheKey = detailCacheKey;
        }

        @Override
        public void run() {
            RetrofitParams retrofitParams = decApiAction.call(decApi, params);
            retrofitParams.setCurrentRequestTime(validParam.getCurrentRequestTime());
            if (!retrofitParams.getFlag()) {
                finishedRequest(ErrorType.businessProcess, baseSubscriber);
                return;
            }
            //???api????????????base url?????????????????????????????????
            if (retrofitParams.getIsJoinUrl() && retrofitParams.getUrlTypeName() == null) {
                finishedRequest(ErrorType.businessProcess, baseSubscriber);
                return;
            }
            if (retrofitParams.getIsJoinUrl() && retrofitParams.getUrlTypeName().value() == 0) {
                finishedRequest(ErrorType.businessProcess, baseSubscriber);
                return;
            }
            ApiCheckAnnotation apiCheckAnnotation = validParam.getApiCheckAnnotation();
            retrofitParams.setTokenValid(apiCheckAnnotation.isTokenValid());
            retrofitParams.setInvokeMethodName(validParam.getInvokeMethodName());
            apiRequest(server, baseSubscriber, validParam, retrofitParams, urlAction, useClass);
        }
    }

    private <I, S extends BaseService> void apiRequest(S server,
                                                       final BaseSubscriber<Object, S> baseSubscriber,
                                                       OkRxValidParam validParam,
                                                       final RetrofitParams retrofitParams,
                                                       Func2<String, S, Integer> urlAction,
                                                       String useClass) {
        //??????????????????????????????
        if (!ObjectJudge.isNullOrEmpty(retrofitParams.getAllowRetCodes())) {
            List<String> allowRetCodes = baseSubscriber.getAllowRetCodes();
            allowRetCodes.addAll(retrofitParams.getAllowRetCodes());
        }
        //??????????????????
        if (retrofitParams.getUrlTypeName() != null) {
            if (retrofitParams.getIsJoinUrl()) {
                String baseUrl = urlAction.call(server, retrofitParams.getUrlTypeName().value());
                retrofitParams.setRequestUrl(PathsUtils.combine(baseUrl, retrofitParams.getRequestUrl()));
                if (retrofitParams.isLastContainsPath() && !retrofitParams.getRequestUrl().endsWith("/")) {
                    retrofitParams.setRequestUrl(retrofitParams.getRequestUrl() + "/");
                }
            }
        }
        //NO_CACHE: ???????????????,????????????,cacheKey,cacheTime ???????????????
        //DEFAULT: ??????HTTP???????????????????????????????????????304?????????????????????
        //REQUEST_FAILED_READ_CACHE??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //IF_NONE_CACHE_REQUEST???????????????????????????????????????????????????????????????
        //FIRST_CACHE_THEN_REQUEST???????????????????????????????????????????????????????????????
        //?????????????????????,????????????
        //????????????????????????????????????????????????????????????????????????????????????5???
        ApiCheckAnnotation apiCheckAnnotation = validParam.getApiCheckAnnotation();
        retrofitParams.setCacheKey(apiCheckAnnotation.cacheKey());
        //??????????????????
        CallStatus status = retrofitParams.getCallStatus();
        if (status != CallStatus.OnlyNet) {
            long milliseconds = ConvertUtils.toMilliseconds(apiCheckAnnotation.cacheTime(), apiCheckAnnotation.cacheTimeUnit());
            retrofitParams.setCacheTime(Duration.ofMillis(milliseconds));
        }
        //???????????????url
        //del?????????delQuery?????????????????????
        if (!ObjectJudge.isNullOrEmpty(retrofitParams.getDelQueryParams())) {
            StringBuffer querysb = new StringBuffer();
            for (Map.Entry<String, String> entry : retrofitParams.getDelQueryParams().entrySet()) {
                querysb.append(MessageFormat.format("{0}={1},", entry.getKey(), entry.getValue()));
            }
            if (querysb.length() > 0) {
                if (!retrofitParams.getRequestUrl().contains("?")) {
                    retrofitParams.setRequestUrl(String.format("%s?%s",
                            retrofitParams.getRequestUrl(),
                            querysb.substring(0, querysb.length() - 1)));
                } else {
                    retrofitParams.setRequestUrl(String.format("%s&%s",
                            retrofitParams.getRequestUrl(),
                            querysb.substring(0, querysb.length() - 1)));
                }
            }
        }
        server.baseConfig(server, baseSubscriber, retrofitParams, validParam, useClass);
    }

    private <S extends BaseService> void bindStream(BaseSubscriber<Object, S> baseSubscriber, ResponseParsing responseParsing, RetrofitParams retrofitParams, DataType dataType, Long requestStartTime, Long requestTotalTime, int code) {
        ResultParams<Object> resultParams = new ResultParams<>();
        resultParams.setDataType(dataType);
        resultParams.setRequestStartTime(requestStartTime);
        resultParams.setRequestTotalTime(requestTotalTime);
        resultParams.setCode(code);
        if (responseParsing.getDataClass() == File.class && !TextUtils.isEmpty(retrofitParams.getTargetFilePath())) {
            File file = new File(retrofitParams.getTargetFilePath());
            if (file.exists()) {
                ConvertUtils.toFile(file, responseParsing.getStream());
                resultParams.setData(file);
                baseSubscriber.onNext(resultParams);
            } else {
                resultParams.setData(responseParsing.getStream());
                baseSubscriber.onNext(resultParams);
            }
        } else {
            resultParams.setData(responseParsing.getStream());
            baseSubscriber.onNext(resultParams);
        }
    }

    private <S extends BaseService> void bindBytes(BaseSubscriber<Object, S> baseSubscriber, ResponseParsing responseParsing, RetrofitParams retrofitParams, DataType dataType, Long requestStartTime, Long requestTotalTime, int code) {
        ResultParams<Object> resultParams = new ResultParams<>();
        resultParams.setDataType(dataType);
        resultParams.setRequestStartTime(requestStartTime);
        resultParams.setRequestTotalTime(requestTotalTime);
        resultParams.setCode(code);
        if (responseParsing.getBytes() == null) {
            resultParams.setData(responseParsing.getData());
            baseSubscriber.onNext(resultParams);
        } else {
            if (responseParsing.getDataClass() == File.class && !TextUtils.isEmpty(retrofitParams.getTargetFilePath())) {
                File file = new File(retrofitParams.getTargetFilePath());
                if (file.exists()) {
                    ConvertUtils.toFile(file, responseParsing.getBytes());
                    resultParams.setData(file);
                    baseSubscriber.onNext(resultParams);
                } else {
                    resultParams.setData(responseParsing.getBytes());
                    baseSubscriber.onNext(resultParams);
                }
            } else {
                resultParams.setData(responseParsing.getBytes());
                baseSubscriber.onNext(resultParams);
            }
        }
    }
}
