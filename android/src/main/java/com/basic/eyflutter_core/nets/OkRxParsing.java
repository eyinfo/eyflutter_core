package com.basic.eyflutter_core.nets;

import android.text.TextUtils;

import com.basic.eyflutter_core.nets.annotations.ApiHeadersCall;
import com.basic.eyflutter_core.nets.annotations.BYTES;
import com.basic.eyflutter_core.nets.annotations.BaseUrlTypeName;
import com.basic.eyflutter_core.nets.annotations.DELETE;
import com.basic.eyflutter_core.nets.annotations.DataCallStatus;
import com.basic.eyflutter_core.nets.annotations.DataParam;
import com.basic.eyflutter_core.nets.annotations.DelQuery;
import com.basic.eyflutter_core.nets.annotations.GET;
import com.basic.eyflutter_core.nets.annotations.Header;
import com.basic.eyflutter_core.nets.annotations.HeaderPart;
import com.basic.eyflutter_core.nets.annotations.Headers;
import com.basic.eyflutter_core.nets.annotations.PATCH;
import com.basic.eyflutter_core.nets.annotations.POST;
import com.basic.eyflutter_core.nets.annotations.PUT;
import com.basic.eyflutter_core.nets.annotations.Param;
import com.basic.eyflutter_core.nets.annotations.ParamList;
import com.basic.eyflutter_core.nets.annotations.Path;
import com.basic.eyflutter_core.nets.annotations.RequestTimeLimit;
import com.basic.eyflutter_core.nets.annotations.RequestTimePart;
import com.basic.eyflutter_core.nets.annotations.RetCodes;
import com.basic.eyflutter_core.nets.annotations.UrlItem;
import com.basic.eyflutter_core.nets.annotations.UrlItemKey;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.enums.CallStatus;
import com.basic.eyflutter_core.nets.enums.RequestContentType;
import com.basic.eyflutter_core.nets.enums.RequestType;
import com.cloud.eyutils.enums.RuleParams;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.JsonUtils;
import com.cloud.eyutils.utils.ObjectJudge;
import com.cloud.eyutils.utils.ValidUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2018/10/8
 * Description:okrx??????
 * Modifier:
 * ModifyContent:
 */
@SuppressWarnings("unchecked")
public class OkRxParsing {
    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????;
    public <T> T createAPI(Class<T> service, CallStatus callStatus) {
        if (!ValidUtils.validateServiceInterface(service)) {
            return null;
        }
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new ApiInvocationHandler(service, callStatus));
    }

    private class ApiInvocationHandler<T> implements InvocationHandler {

        private Class<T> apiClass = null;
        private CallStatus callStatus = CallStatus.OnlyNet;

        public ApiInvocationHandler(Class<T> apiClass, CallStatus callStatus) {
            this.apiClass = apiClass;
            this.callStatus = callStatus;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            try {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                } else if (method.getReturnType() == RetrofitParams.class) {
                    RetrofitParams retrofitParams = new RetrofitParams();
                    //???????????????????????????
                    retrofitParams.setCallStatus(callStatus);
                    //?????????????????????
                    Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
                    if (ObjectJudge.isNullOrEmpty(declaredAnnotations)) {
                        retrofitParams.setFlag(false);
                        return retrofitParams;
                    }
                    Annotation requestAnnotation = getRequestAnnotation(declaredAnnotations);
                    if (requestAnnotation == null) {
                        //???????????????????????????????????????
                        retrofitParams.setFlag(false);
                        return retrofitParams;
                    }
                    //???isRemoveEmptyValueField???????????????????????????,?????????false
                    boolean isRemoveEmptyValueField = bindRequestTypes(declaredAnnotations, method, apiClass, retrofitParams, args);
                    //??????????????????
                    bindParamAnnontation(method, retrofitParams, args, isRemoveEmptyValueField, requestAnnotation.annotationType());
                    return retrofitParams;
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    //??????????????????
    private <T> boolean bindRequestTypes(Annotation[] declaredAnnotations, Method method, Class<T> apiClass, RetrofitParams retrofitParams, Object[] args) {
        boolean isRemoveEmptyValueField = false;
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation.annotationType() == POST.class) {
                POST annotation = method.getAnnotation(POST.class);
                isRemoveEmptyValueField = annotation.isRemoveEmptyValueField();
                retrofitParams.setRequestType(RequestType.POST);
                retrofitParams.setFailureRetry(annotation.isFailureRetry());
                retrofitParams.setFailureRetryCount(annotation.failureRetryCount());
                retrofitParams.setTimeoutMillis(annotation.timeoutMillis());
                bindRequestAnnontation(apiClass, method, retrofitParams, args, annotation.value(), annotation.isFullUrl(), annotation.values(), annotation.contentType());
            } else if (declaredAnnotation.annotationType() == BYTES.class) {
                BYTES annotation = method.getAnnotation(BYTES.class);
                isRemoveEmptyValueField = annotation.isRemoveEmptyValueField();
                retrofitParams.setRequestType(RequestType.BYTES);
                retrofitParams.setFailureRetry(annotation.isFailureRetry());
                retrofitParams.setFailureRetryCount(annotation.failureRetryCount());
                retrofitParams.setTimeoutMillis(annotation.timeoutMillis());
                bindRequestAnnontation(apiClass, method, retrofitParams, args, annotation.value(), annotation.isFullUrl(), annotation.values(), annotation.contentType());
            } else if (declaredAnnotation.annotationType() == GET.class) {
                GET annotation = method.getAnnotation(GET.class);
                isRemoveEmptyValueField = annotation.isRemoveEmptyValueField();
                retrofitParams.setRequestType(RequestType.GET);
                retrofitParams.setFailureRetry(annotation.isFailureRetry());
                retrofitParams.setFailureRetryCount(annotation.failureRetryCount());
                retrofitParams.setTimeoutMillis(annotation.timeoutMillis());
                bindRequestAnnontation(apiClass, method, retrofitParams, args, annotation.value(), annotation.isFullUrl(), annotation.values(), RequestContentType.None);
            } else if (declaredAnnotation.annotationType() == DELETE.class) {
                DELETE annotation = method.getAnnotation(DELETE.class);
                isRemoveEmptyValueField = annotation.isRemoveEmptyValueField();
                retrofitParams.setRequestType(RequestType.DELETE);
                retrofitParams.setFailureRetry(annotation.isFailureRetry());
                retrofitParams.setFailureRetryCount(annotation.failureRetryCount());
                retrofitParams.setTimeoutMillis(annotation.timeoutMillis());
                bindRequestAnnontation(apiClass, method, retrofitParams, args, annotation.value(), annotation.isFullUrl(), annotation.values(), annotation.contentType());
            } else if (declaredAnnotation.annotationType() == PUT.class) {
                PUT annotation = method.getAnnotation(PUT.class);
                isRemoveEmptyValueField = annotation.isRemoveEmptyValueField();
                retrofitParams.setRequestType(RequestType.PUT);
                retrofitParams.setFailureRetry(annotation.isFailureRetry());
                retrofitParams.setFailureRetryCount(annotation.failureRetryCount());
                retrofitParams.setTimeoutMillis(annotation.timeoutMillis());
                bindRequestAnnontation(apiClass, method, retrofitParams, args, annotation.value(), annotation.isFullUrl(), annotation.values(), annotation.contentType());
            } else if (declaredAnnotation.annotationType() == PATCH.class) {
                PATCH annotation = method.getAnnotation(PATCH.class);
                isRemoveEmptyValueField = annotation.isRemoveEmptyValueField();
                retrofitParams.setRequestType(RequestType.PATCH);
                retrofitParams.setFailureRetry(annotation.isFailureRetry());
                retrofitParams.setFailureRetryCount(annotation.failureRetryCount());
                retrofitParams.setTimeoutMillis(annotation.timeoutMillis());
                bindRequestAnnontation(apiClass, method, retrofitParams, args, annotation.value(), annotation.isFullUrl(), annotation.values(), annotation.contentType());
            } else if (declaredAnnotation.annotationType() == Header.class) {
                bindHeaderAnnontation(method, retrofitParams, args, isRemoveEmptyValueField);
            } else if (declaredAnnotation.annotationType() == Headers.class) {
                bindHeadersAnnontation(method, retrofitParams, args, isRemoveEmptyValueField);
            } else if (declaredAnnotation.annotationType() == DataParam.class) {
                DataParam annotation = method.getAnnotation(DataParam.class);
                retrofitParams.setDataClass(annotation.value());
                retrofitParams.setCollectionDataType(annotation.isCollection());
                retrofitParams.setResponseDataType(annotation.responseDataType());
                retrofitParams.setParsingFieldMapping(annotation.parsingFieldMapping());
            } else if (declaredAnnotation.annotationType() == RetCodes.class) {
                RetCodes annotation = method.getAnnotation(RetCodes.class);
                if (!ObjectJudge.isNullOrEmpty(annotation.value())) {
                    retrofitParams.setAllowRetCodes(Arrays.asList(annotation.value()));
                }
            } else if (declaredAnnotation.annotationType() == ApiHeadersCall.class) {
                ApiHeadersCall annotation = method.getAnnotation(ApiHeadersCall.class);
                retrofitParams.setApiHeadersCall(annotation);
            } else if (declaredAnnotation.annotationType() == RequestTimeLimit.class) {
                bindRequestTimeAnnontation(method, retrofitParams, args);
            }
        }
        return isRemoveEmptyValueField;
    }

    private void bindRequestTime(String totalTime, TimeUnit unit, RetrofitParams retrofitParams) {
        if (!TextUtils.isDigitsOnly(totalTime)) {
            return;
        }
        long time = Long.parseLong(totalTime);
        long milliseconds = ConvertUtils.toMilliseconds(time, unit);
        retrofitParams.setRequestTotalTime(milliseconds);
    }

    private void bindRequestTimeAnnontation(Method method, RetrofitParams retrofitParams, Object[] args) {
        RequestTimeLimit annotation = method.getAnnotation(RequestTimeLimit.class);
        if (annotation == null) {
            return;
        }
        String pattent = String.format(RuleParams.MatchTagBetweenContent.getValue(), "\\{", "\\}");
        String matche = ValidUtils.matche(pattent, annotation.totalTime());
        if (TextUtils.isEmpty(matche)) {
            //???????????????????????????
            bindRequestTime(annotation.totalTime(), annotation.unit(), retrofitParams);
        } else {
            TreeMap<Integer, RequestTimePart> paramAnnotationObject = getParamAnnotationObject(method, RequestTimePart.class);
            if (ObjectJudge.isNullOrEmpty(paramAnnotationObject)) {
                return;
            }
            for (Map.Entry<Integer, RequestTimePart> entry : paramAnnotationObject.entrySet()) {
                RequestTimePart part = entry.getValue();
                if (!TextUtils.equals(part.value(), matche)) {
                    continue;
                }
                String dataValue = String.valueOf(args[entry.getKey()]);
                bindRequestTime(dataValue, annotation.unit(), retrofitParams);
                break;
            }
        }
    }

    private void addJsonParams(String paramKey, Param key, Object arg, TreeMap<String, Object> params, boolean isRemoveEmptyValueField) {
        //????????????null?????????
        if (key.isRemoveEmptyValueField() || isRemoveEmptyValueField) {
            if (arg == null) {
                return;
            }
        }
        if (arg instanceof String) {
            //string??????
            if (key.isRemoveEmptyValueField() || isRemoveEmptyValueField) {
                //??????""??????
                if (!TextUtils.isEmpty(String.valueOf(arg))) {
                    params.put(paramKey, arg);
                }
            } else {
                params.put(paramKey, arg);
            }
        } else {
            //?????????????????????,????????????????????????null??????????????????????????????;
            String json = JsonUtils.toJson(arg);
            params.put(paramKey, json);
        }
    }

    private void bindJsonParams(int position, Param key, int paramPosition, TreeMap<String, Object> params, Object[] args, boolean isRemoveEmptyValueField) {
        if (TextUtils.isEmpty(key.value())) {
            //?????????key?????????value?????????int double float long file byte[] Byte[]????????????????????????????????????
            Object arg = args[paramPosition];
            if (arg != null &&
                    !(arg instanceof Integer) &&
                    !(arg instanceof Double) &&
                    !(arg instanceof Float) &&
                    !(arg instanceof Long) &&
                    !(arg instanceof File) &&
                    !(arg instanceof byte[]) &&
                    !(arg instanceof Byte[])) {
                //????????????key,??????????????????????????????????????????;
                String paramKey = OkRxKeys.ignoreParamContainsKey + position;
                //??????json??????
                addJsonParams(paramKey, key, arg, params, isRemoveEmptyValueField);
            }
        } else {
            //???????????????????????????key
            if (!params.containsKey(key.value())) {
                Object arg = args[paramPosition];
                if (arg != null &&
                        !(arg instanceof Integer) &&
                        !(arg instanceof Double) &&
                        !(arg instanceof Float) &&
                        !(arg instanceof Long) &&
                        !(arg instanceof File) &&
                        !(arg instanceof byte[]) &&
                        !(arg instanceof Byte[])) {
                    //??????json??????
                    addJsonParams(key.value(), key, arg, params, isRemoveEmptyValueField);
                } else {
                    //????????????????????????
                    putParamValue(key, arg, params, null, isRemoveEmptyValueField);
                }
            }
        }
    }

    private void bindSingleParam(Param key, int position, int paramPosition, TreeMap<String, Object> params, HashMap<String, String> suffixParams, Object[] args, boolean isRemoveEmptyValueField) {
        if (key.isJson()) {
            bindJsonParams(position, key, paramPosition, params, args, isRemoveEmptyValueField);
        } else {
            if (!params.containsKey(key.value())) {
                Object arg = args[paramPosition];
                putParamValue(key, arg, params, suffixParams, isRemoveEmptyValueField);
            }
        }
    }

    private void bindParams(TreeMap<Integer, Param> paramAnnotationObject, RetrofitParams retrofitParams, Object[] args, boolean isRemoveEmptyValueField) {
        if (ObjectJudge.isNullOrEmpty(paramAnnotationObject)) {
            return;
        }
        int position = 0;
        //????????????
        TreeMap<String, Object> params = retrofitParams.getParams();
        //???????????????????????????
        HashMap<String, String> suffixParams = retrofitParams.getFileSuffixParams();
        for (Map.Entry<Integer, Param> paramIntegerEntry : paramAnnotationObject.entrySet()) {
            Param key = paramIntegerEntry.getValue();
            if (key.isTargetFile()) {
                Object arg = args[paramIntegerEntry.getKey()];
                if (arg instanceof File) {
                    File file = (File) arg;
                    retrofitParams.setTargetFilePath(file.getAbsolutePath());
                } else if (arg instanceof String) {
                    retrofitParams.setTargetFilePath((String) arg);
                }
                continue;
            }
            //????????????
            bindSingleParam(key, position, paramIntegerEntry.getKey(), params, suffixParams, args, isRemoveEmptyValueField);
            position++;
        }
        //????????????
        checkParams(params);
    }

    //????????????
    private void checkParams(TreeMap<String, Object> params) {
        if (ObjectJudge.isNullOrEmpty(params) || params.size() == 1) {
            //????????????????????????????????????1,?????????????????????
            /**
             * params.size() == 1??????????????????:
             * 1.??????key-value??????????????????;
             * 2.json??????????????????key,???key-value??????????????????;
             * 3.json??????????????????key,???????????????????????????????????????;
             */
            return;
        }
//        int notSpecifiedParamKeyCount = 0;//?????????????????????key?????????????????????
        Set<String> notSpecifiedParamKeys = new HashSet<String>();//?????????????????????key?????????params??????key??????
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey().contains(OkRxKeys.ignoreParamContainsKey)) {
                notSpecifiedParamKeys.add(entry.getKey());
            }
        }
        if (ObjectJudge.isNullOrEmpty(notSpecifiedParamKeys)) {
            //??????????????????????????????????????????
            return;
        }
        //?????????????????????1???params.size()>1,?????????????????????????????????
        //??????params.size()?????????1,???????????????????????????;
        if (notSpecifiedParamKeys.size() == 1) {
            Iterator<String> iterator = notSpecifiedParamKeys.iterator();
            String next = iterator.next();
            params.remove(next);
            return;
        }
        //????????????????????????>1????????????params.size(),??????????????????????????????;
        if (notSpecifiedParamKeys.size() > 1 && notSpecifiedParamKeys.size() != params.size()) {
            for (String next : notSpecifiedParamKeys) {
                params.remove(next);
            }
            return;
        }
        //???????????????????????????params??????,?????????????????????[{}|[],{}]???json?????????????????????????????????
        if (notSpecifiedParamKeys.size() == params.size()) {
            String json = JsonUtils.toJson(params.values());
            params.clear();
            params.put(OkRxKeys.ignoreParamContainsKey + 1, json);
        }
    }

    private void bindDeletes(Method method, RetrofitParams retrofitParams, Object[] args, boolean isRemoveEmptyValueField, Class<? extends Annotation> annotationType) {
        if (annotationType == DELETE.class) {
            //delete ????????????query???????????????????????????url??????
            TreeMap<Integer, DelQuery> delQueryIntegerHashMap = getParamAnnotationObject(method, DelQuery.class);
            if (!ObjectJudge.isNullOrEmpty(delQueryIntegerHashMap)) {
                HashMap<String, String> params = retrofitParams.getDelQueryParams();
                for (Map.Entry<Integer, DelQuery> delQueryIntegerEntry : delQueryIntegerHashMap.entrySet()) {
                    DelQuery key = delQueryIntegerEntry.getValue();
                    if (!params.containsKey(key.value())) {
                        Object arg = args[delQueryIntegerEntry.getKey()];
                        if (key.isRemoveEmptyValueField()) {
                            if (arg != null && !TextUtils.isEmpty(String.valueOf(arg))) {
                                params.put(key.value(), String.valueOf(arg));
                            }
                        } else {
                            if (isRemoveEmptyValueField) {
                                if (arg != null && !TextUtils.isEmpty(String.valueOf(arg))) {
                                    params.put(key.value(), String.valueOf(arg));
                                }
                            } else {
                                params.put(key.value(), String.valueOf(arg));
                            }
                        }
                    }
                }
            }
        }
    }

    private void bindSingleParamList(ParamList key, HashMap<String, Object> argmap, RetrofitParams retrofitParams, boolean isRemoveEmptyValueField) {
        isRemoveEmptyValueField = key.isRemoveEmptyValueField() ? key.isRemoveEmptyValueField() : isRemoveEmptyValueField;
        TreeMap<String, Object> params = retrofitParams.getParams();
        for (Map.Entry<String, Object> entry : argmap.entrySet()) {
            if (isRemoveEmptyValueField && (entry.getValue() == null || TextUtils.isEmpty(String.valueOf(entry.getValue())))) {
                //??????isRemoveEmptyValueField==true???????????????????????????
                continue;
            }
            if (params.containsKey(entry.getKey())) {
                //????????????key???????????????????????????????????????
                continue;
            }
            params.put(entry.getKey(), entry.getValue());
        }
    }

    private void bindParamList(TreeMap<Integer, ParamList> paramAnnotations, RetrofitParams retrofitParams, Object[] args, boolean isRemoveEmptyValueField) {
        if (ObjectJudge.isNullOrEmpty(paramAnnotations)) {
            return;
        }
        for (Map.Entry<Integer, ParamList> entry : paramAnnotations.entrySet()) {
            Object arg = args[entry.getKey()];
            if (!(arg instanceof HashMap)) {
                //???hash map???????????????
                continue;
            }
            HashMap<String, Object> argmap = (HashMap<String, Object>) arg;
            if (ObjectJudge.isNullOrEmpty(argmap)) {
                //????????????????????????
                continue;
            }
            ParamList key = entry.getValue();
            bindSingleParamList(key, argmap, retrofitParams, isRemoveEmptyValueField);
        }
    }

    private void bindParamAnnontation(Method method,
                                      RetrofitParams retrofitParams,
                                      Object[] args,
                                      boolean isRemoveEmptyValueField,
                                      Class<? extends Annotation> annotationType) {
        //????????????????????????????????????Param?????????????????????
        TreeMap<Integer, Param> paramAnnotationObject = getParamAnnotationObject(method, Param.class);
        //??????Param??????
        bindParams(paramAnnotationObject, retrofitParams, args, isRemoveEmptyValueField);
        //??????ParamList??????
        TreeMap<Integer, ParamList> paramAnnotations = getParamAnnotationObject(method, ParamList.class);
        bindParamList(paramAnnotations, retrofitParams, args, isRemoveEmptyValueField);
        //??????DelQuery??????
        bindDeletes(method, retrofitParams, args, isRemoveEmptyValueField, annotationType);
        //??????DataCallStatus??????
        bindDataCallStatus(method, retrofitParams, args);
    }

    private void bindDataCallStatus(Method method, RetrofitParams retrofitParams, Object[] args) {
        TreeMap<Integer, DataCallStatus> callStatusTreeMap = getParamAnnotationObject(method, DataCallStatus.class);
        if (ObjectJudge.isNullOrEmpty(callStatusTreeMap)) {
            //?????????????????????
            return;
        }
        //????????????????????????????????????????????????????????????????????????
        Map.Entry<Integer, DataCallStatus> entry = callStatusTreeMap.firstEntry();
        Object arg = args[entry.getKey()];
        //????????????????????????CallStatus??????????????????
        if (!(arg instanceof CallStatus)) {
            return;
        }
        CallStatus callStatus = (CallStatus) arg;
        retrofitParams.setCallStatus(callStatus);
    }

    private void putValueByIsRemoveEmpty(Param key, Object arg, TreeMap<String, Object> params) {
        if (arg == null) {
            //???????????????????????????
            //Integer\Double\Long\Float\String\File\Byte\Byte[]\byte[]\???????????????\Map\List\Set
            return;
        }
        //??????
        if (arg instanceof File) {
            File file = (File) arg;
            if (!file.exists()) {
                //???????????????
                return;
            }
            params.put(key.value(), arg);
            return;
        }
        //?????????
        if ((arg instanceof byte[]) || (arg instanceof Byte[])) {
            params.put(key.value(), arg);
            return;
        }
        if (arg instanceof String) {
            params.put(key.value(), arg);
            return;
        }
        params.put(key.value(), arg);
    }

    private void putParamValue(Param key, Object arg, TreeMap<String, Object> params, HashMap<String, String> suffixParams, boolean isRemoveEmptyValueField) {
        if (suffixParams != null && !TextUtils.isEmpty(key.fileSuffixAfterUpload())) {
            //????????????file?????????????????????????????????????????????????????????????????????????????????????????????
            suffixParams.put(key.value(), key.fileSuffixAfterUpload());
        }
        if (key.isRemoveEmptyValueField() || isRemoveEmptyValueField) {
            //?????????????????????key-value???????????????????????????????????????
            putValueByIsRemoveEmpty(key, arg, params);
        } else {
            params.put(key.value(), arg);
        }
    }

    private UrlItem getMatchUrlItem(UrlItem[] urlItems, String matchKey) {
        UrlItem urlItem = null;
        if (!ObjectJudge.isNullOrEmpty(urlItems)) {
            for (UrlItem item : urlItems) {
                if (!TextUtils.isEmpty(matchKey) && TextUtils.equals(item.key(), matchKey)) {
                    urlItem = item;
                    break;
                }
            }
        }
        return urlItem;
    }

    private <T> void bindRequestAnnontation(Class<T> apiClass,
                                            Method method,
                                            RetrofitParams retrofitParams,
                                            Object[] args,
                                            String formatUrl,
                                            boolean isFullUrl,
                                            UrlItem[] urlItems,
                                            RequestContentType methodRequestContentType) {
        //???formatUrl????????????urlItems??????????????????????????????
        if (TextUtils.isEmpty(formatUrl)) {
            if (ObjectJudge.isNullOrEmpty(urlItems)) {
                retrofitParams.setFlag(false);
                return;
            }
            TreeMap<Integer, UrlItemKey> urlItemKeys = getParamAnnotationObject(method, UrlItemKey.class);
            if (ObjectJudge.isNullOrEmpty(urlItemKeys)) {
                UrlItem urlItem = urlItems[0];
                if (TextUtils.isEmpty(urlItem.value())) {
                    retrofitParams.setFlag(false);
                    return;
                }
                formatUrl = urlItem.value().trim();
                if (formatUrl.endsWith("/")) {
                    retrofitParams.setLastContainsPath(true);
                }
            } else {
                for (Map.Entry<Integer, UrlItemKey> entry : urlItemKeys.entrySet()) {
                    UrlItem urlItem = getMatchUrlItem(urlItems, String.valueOf(args[entry.getKey()]));
                    if (urlItem == null) {
                        retrofitParams.setFlag(false);
                        return;
                    }
                    formatUrl = urlItem.value().trim();
                    if (formatUrl.endsWith("/")) {
                        retrofitParams.setLastContainsPath(true);
                    }
                    break;
                }
            }
        } else {
            if (formatUrl.trim().endsWith("/")) {
                retrofitParams.setLastContainsPath(true);
            }
        }
        if (TextUtils.isEmpty(formatUrl)) {
            retrofitParams.setFlag(false);
            return;
        }
        //url????????????
        String[] furlsplit = formatUrl.split("/|\\?|=");
        if (ObjectJudge.isNullOrEmpty(furlsplit)) {
            retrofitParams.setFlag(false);
            return;
        }
        List<String> matches = new ArrayList<String>();
        String pattent = String.format(RuleParams.MatchTagBetweenContent.getValue(), "\\{", "\\}");
        for (String fsitem : furlsplit) {
            if (TextUtils.isEmpty(fsitem)) {
                continue;
            }
            //??????????????????????????????{}
            if (ObjectJudge.isNullOrEmpty(ValidUtils.matches("[\\{\\}]{1,2}", fsitem))) {
                continue;
            }
            String matche = ValidUtils.matche(pattent, fsitem);
            if (!TextUtils.isEmpty(matche) && !matches.contains(matche)) {
                matches.add(matche);
            }
        }
        if (ObjectJudge.isNullOrEmpty(matches)) {
            matchRequestUrl(apiClass,
                    method,
                    retrofitParams,
                    formatUrl,
                    isFullUrl,
                    methodRequestContentType);
        } else {
            String rativeUrl = formatUrl;
            TreeMap<Integer, Path> paramAnnotationObject = getParamAnnotationObject(method, Path.class);
            if (!ObjectJudge.isNullOrEmpty(paramAnnotationObject)) {
                //???????????????????????????????????????
                for (Map.Entry<Integer, Path> pathIntegerEntry : paramAnnotationObject.entrySet()) {
                    Path path = pathIntegerEntry.getValue();
                    if (matches.contains(path.value())) {
                        rativeUrl = rativeUrl.replace(String.format("{%s}", path.value()), "%s");
                        rativeUrl = String.format(rativeUrl, String.valueOf(args[pathIntegerEntry.getKey()]));
                    }
                }

                matchRequestUrl(apiClass,
                        method,
                        retrofitParams,
                        rativeUrl,
                        isFullUrl,
                        methodRequestContentType);
            } else {
                retrofitParams.setFlag(false);
            }
        }
    }

    private <T> void matchRequestUrl(Class<T> apiClass,
                                     Method method,
                                     RetrofitParams retrofitParams,
                                     String url,
                                     boolean isFullUrl,
                                     RequestContentType methodRequestContentType) {
        retrofitParams.setApiName(url);
        RequestContentType contentType = RequestContentType.None;
        BaseUrlTypeName urlTypeName = method.getAnnotation(BaseUrlTypeName.class);
        if (urlTypeName == null) {
            //????????????????????????
            urlTypeName = apiClass.getAnnotation(BaseUrlTypeName.class);
            if (urlTypeName != null) {
                contentType = urlTypeName.contentType();
            }
        } else {
            //??????content-type
            BaseUrlTypeName annotation = apiClass.getAnnotation(BaseUrlTypeName.class);
            if (urlTypeName.contentType() == RequestContentType.None) {
                if (annotation != null) {
                    contentType = annotation.contentType();
                }
            } else {
                contentType = urlTypeName.contentType();
            }
        }
        retrofitParams.setUrlTypeName(urlTypeName);
        if (methodRequestContentType != null && methodRequestContentType != RequestContentType.None) {
            retrofitParams.setRequestContentType(contentType == methodRequestContentType ? contentType : methodRequestContentType);
        } else {
            retrofitParams.setRequestContentType(contentType);
        }
        if (urlTypeName == null) {
            //????????????????????????????????????
            retrofitParams.setFlag(false);
        } else {
            if (isFullUrl) {
                retrofitParams.setRequestUrl(url);
                retrofitParams.setIsJoinUrl(false);
            } else {
                retrofitParams.setRequestUrl(url);
                if (ValidUtils.valid(RuleParams.Url.getValue(), url)) {
                    retrofitParams.setIsJoinUrl(false);
                } else {
                    retrofitParams.setIsJoinUrl(true);
                }
            }
        }
    }

    private void bindHeaderAnnontation(Method method, RetrofitParams retrofitParams, Object[] args, boolean isRemoveEmptyValueField) {
        Header annotation = method.getAnnotation(Header.class);
        HashMap<String, String> headParams = retrofitParams.getHeadParams();
        if (!headParams.containsKey(annotation.name()) &&
                !TextUtils.isEmpty(annotation.value())) {
            String pattent = String.format(RuleParams.MatchTagBetweenContent.getValue(), "\\{", "\\}");
            String matche = ValidUtils.matche(pattent, annotation.value());
            if (TextUtils.isEmpty(matche)) {
                if (annotation.isRemoveEmptyValueField()) {
                    if (!TextUtils.isEmpty(annotation.value())) {
                        headParams.put(annotation.name(), annotation.value());
                    }
                } else {
                    if (isRemoveEmptyValueField) {
                        if (!TextUtils.isEmpty(annotation.value())) {
                            headParams.put(annotation.name(), annotation.value());
                        }
                    } else {
                        headParams.put(annotation.name(), annotation.value());
                    }
                }
            } else {
                TreeMap<Integer, HeaderPart> paramAnnotationObject = getParamAnnotationObject(method, HeaderPart.class);
                if (!ObjectJudge.isNullOrEmpty(paramAnnotationObject)) {
                    for (Map.Entry<Integer, HeaderPart> headerPartIntegerEntry : paramAnnotationObject.entrySet()) {
                        HeaderPart headerPart = headerPartIntegerEntry.getValue();
                        if (TextUtils.equals(headerPart.value(), matche)) {
                            String dataValue = String.valueOf(args[headerPartIntegerEntry.getKey()]);
                            if (headerPart.isRemoveEmptyValueField()) {
                                if (!TextUtils.isEmpty(dataValue)) {
                                    headParams.put(annotation.name(), dataValue);
                                }
                            } else {
                                if (isRemoveEmptyValueField) {
                                    if (!TextUtils.isEmpty(dataValue)) {
                                        headParams.put(annotation.name(), dataValue);
                                    }
                                } else {
                                    headParams.put(annotation.name(), dataValue);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void bindHeadersAnnontation(Method method, RetrofitParams retrofitParams, Object[] args, boolean isRemoveEmptyValueField) {
        Headers annotation = method.getAnnotation(Headers.class);
        if (annotation != null) {
            String[] values = annotation.value();
            if (!ObjectJudge.isNullOrEmpty(values)) {
                HashMap<String, String> headParams = retrofitParams.getHeadParams();
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    String[] lst = value.split(":");
                    if (lst.length == 2 && !TextUtils.isEmpty(lst[0])
                            && !TextUtils.isEmpty(lst[1]) && !headParams.containsKey(lst[0])) {
                        String pattent = String.format(RuleParams.MatchTagBetweenContent.getValue(), "\\{", "\\}");
                        String matche = ValidUtils.matche(pattent, lst[1]);
                        if (TextUtils.isEmpty(matche)) {
                            if (annotation.isRemoveEmptyValueField()) {
                                if (!TextUtils.isEmpty(lst[1])) {
                                    headParams.put(lst[0], lst[1]);
                                }
                            } else {
                                if (isRemoveEmptyValueField) {
                                    if (!TextUtils.isEmpty(lst[1])) {
                                        headParams.put(lst[0], lst[1]);
                                    }
                                } else {
                                    headParams.put(lst[0], lst[1]);
                                }
                            }
                        } else {
                            //???HeaderPart????????????
                            TreeMap<Integer, HeaderPart> paramAnnotationObject = getParamAnnotationObject(method, HeaderPart.class);
                            if (!ObjectJudge.isNullOrEmpty(paramAnnotationObject)) {
                                for (Map.Entry<Integer, HeaderPart> headerPartIntegerEntry : paramAnnotationObject.entrySet()) {
                                    HeaderPart headerPart = headerPartIntegerEntry.getValue();
                                    if (TextUtils.equals(headerPart.value(), matche)) {
                                        String dataValue = String.valueOf(args[headerPartIntegerEntry.getKey()]);
                                        if (annotation.isRemoveEmptyValueField()) {
                                            if (!TextUtils.isEmpty(dataValue)) {
                                                headParams.put(lst[0], dataValue);
                                            }
                                        } else {
                                            if (isRemoveEmptyValueField) {
                                                if (!TextUtils.isEmpty(dataValue)) {
                                                    headParams.put(lst[0], dataValue);
                                                }
                                            } else {
                                                headParams.put(lst[0], dataValue);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Annotation getRequestAnnotation(Annotation[] declaredAnnotations) {
        Annotation annotation = null;
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation.annotationType() == POST.class ||
                    declaredAnnotation.annotationType() == GET.class ||
                    declaredAnnotation.annotationType() == DELETE.class ||
                    declaredAnnotation.annotationType() == PUT.class ||
                    declaredAnnotation.annotationType() == PATCH.class ||
                    declaredAnnotation.annotationType() == BYTES.class) {
                annotation = declaredAnnotation;
                break;
            }
        }
        return annotation;
    }

    //?????????????????????????????????
    private <T> TreeMap<Integer, T> getParamAnnotationObject(Method method, Class<T> annotationClass) {
        TreeMap<Integer, T> lst = new TreeMap<Integer, T>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (!ObjectJudge.isNullOrEmpty(parameterAnnotations)) {
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] parameterAnnotation = parameterAnnotations[i];
                if (ObjectJudge.isNullOrEmpty(parameterAnnotation)) {
                    continue;
                }
                if (parameterAnnotation[0].annotationType() == annotationClass) {
                    lst.put(i, (T) parameterAnnotation[0]);
                }
            }
        }
        return lst;
    }
}
