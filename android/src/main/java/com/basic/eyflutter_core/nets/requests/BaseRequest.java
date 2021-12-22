package com.basic.eyflutter_core.nets.requests;

import android.text.TextUtils;

import com.basic.eyflutter_core.beans.CacheDataItem;
import com.basic.eyflutter_core.nets.OkRx;
import com.basic.eyflutter_core.nets.OkRxKeys;
import com.basic.eyflutter_core.nets.beans.ResponseData;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.beans.SuccessResponse;
import com.basic.eyflutter_core.nets.enums.CallStatus;
import com.basic.eyflutter_core.nets.enums.DataType;
import com.basic.eyflutter_core.nets.enums.RequestContentType;
import com.basic.eyflutter_core.nets.enums.RequestType;
import com.basic.eyflutter_core.nets.enums.ResponseDataType;
import com.basic.eyflutter_core.nets.events.OnHeaderCookiesListener;
import com.basic.eyflutter_core.nets.properties.OkRxConfigParams;
import com.basic.eyflutter_core.utils.EffectiveMMkvUtils;
import com.cloud.eyutils.events.Action1;
import com.cloud.eyutils.logs.Logger;
import com.cloud.eyutils.utils.GlobalUtils;
import com.cloud.eyutils.utils.JsonUtils;
import com.cloud.eyutils.utils.ObjectJudge;
import com.cloud.eyutils.utils.ValidUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2017/11/15
 * Description:
 * Modifier:
 * ModifyContent:
 */
@SuppressWarnings("unchecked")
public class BaseRequest {

    //是否取消间隔缓存回调
    private boolean isCancelIntervalCacheCall = false;
    //内部处理后的参数
    private HashMap<String, Object> internalProcessParams = new HashMap<String, Object>();

    public boolean isCancelIntervalCacheCall() {
        return isCancelIntervalCacheCall;
    }

    public void setCancelIntervalCacheCall(boolean cancelIntervalCacheCall) {
        isCancelIntervalCacheCall = cancelIntervalCacheCall;
    }

    //解决地址中含有特殊字符的情况
    private URL toURLValid(String url) {
        try {
            if (url == null) {
                return null;
            }
            url = url.replaceAll("\"", "");
            OkRxConfigParams configParams = OkRx.getInstance().getOkRxConfigParams();
            if (configParams != null && !TextUtils.isEmpty(configParams.getUrlValidationRules())) {
                if (!ValidUtils.valid(configParams.getUrlValidationRules(), url)) {
                    return null;
                }
            }
            URL murl = new URL(url);
            return murl;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    protected Request.Builder getBuilder(String url,
                                         HashMap<String, String> headers,
                                         RetrofitParams retrofitParams) {
        Request.Builder builder = new Request.Builder();
        TreeMap<String, Object> params = retrofitParams.getParams();
        if (retrofitParams.getRequestType() == RequestType.GET) {
            url = addGetRequestParams(url, params);
        }
        URL murl = toURLValid(url);
        if (murl == null) {
            return null;
        }
        builder.url(murl);
        if (!ObjectJudge.isNullOrEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.removeHeader(entry.getKey());
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        if (retrofitParams.getRequestType() != RequestType.GET) {
            if (retrofitParams.getRequestType() == RequestType.HEAD) {
                builder.head();
            } else {
                addRequestParams(builder, params, retrofitParams.getFileSuffixParams(), retrofitParams.getRequestContentType(), retrofitParams.getRequestType());
            }
        }

        HashMap<String, String> map = new HashMap<>();
        //本次请求初始方法名
        String invokeMethodName = retrofitParams.getInvokeMethodName();
        if (!TextUtils.isEmpty(invokeMethodName)) {
            map.put("requestMethodName", invokeMethodName);
        }
        //请求类型
        map.put("requestType", retrofitParams.getRequestType().name());
        //请求url
        map.put("requestUrl", url);
        //请求头信息
        if (!ObjectJudge.isNullOrEmpty(headers)) {
            map.put("requestHeaders", JsonUtils.toJson(headers));
        }
        //请求参数
        if (!ObjectJudge.isNullOrEmpty(internalProcessParams)) {
            map.put("requestParams", JsonUtils.toJson(internalProcessParams));
        }
        builder.tag(HashMap.class, map);
        return builder;
    }

    private void submitMultipartParams(Request.Builder builder, MultipartBody requestBody, RequestType requestType) {
        if (requestType == RequestType.POST) {
            builder.post(requestBody);
        } else if (requestType == RequestType.PUT) {
            builder.put(requestBody);
        } else if (requestType == RequestType.DELETE) {
            builder.delete(requestBody);
        } else if (requestType == RequestType.PATCH) {
            builder.patch(requestBody);
        } else if (requestType == RequestType.OPTIONS) {
            builder.method("OPTIONS", requestBody);
        } else if (requestType == RequestType.TRACE) {
            builder.method("TRACE", requestBody);
        }
    }

    private void submitRequestParams(Request.Builder builder, RequestBody requestBody, RequestType requestType) {
        if (requestType == RequestType.POST) {
            builder.post(requestBody);
        } else if (requestType == RequestType.PUT) {
            builder.put(requestBody);
        } else if (requestType == RequestType.DELETE) {
            builder.delete(requestBody);
        } else if (requestType == RequestType.PATCH) {
            builder.patch(requestBody);
        } else if (requestType == RequestType.OPTIONS) {
            builder.method("OPTIONS", requestBody);
        } else if (requestType == RequestType.TRACE) {
            builder.method("TRACE", requestBody);
        }
    }

    private void addParamItem(String key, Object value, ValidResult validResult, MultipartBody.Builder requestBody, HashMap<String, String> suffixParams) {
        //文件后缀(若File类型默认取原后缀,若Byte默认以rxtiny为后缀)
        String suffix = "rxtiny";
        if (validResult.streamParamKeys.contains(key)) {
            //以字节流的形式上传文件
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, (byte[]) value);
            if (suffixParams != null && suffixParams.containsKey(key)) {
                String mfx = suffixParams.get(key);
                if (!TextUtils.isEmpty(mfx)) {
                    suffix = mfx;
                }
            }
            String filename = String.format("%s.%s", GlobalUtils.getGuidNoConnect(), suffix);
            //添加参数
            requestBody.addFormDataPart(key, filename, body);
            //内部使用
            internalProcessParams.put(key, "stream");
        } else if (validResult.fileParamKeys.containsValue(key)) {
            if (value instanceof File) {
                addSingleFile((File) value, suffixParams, suffix, key, "", requestBody);
            } else if (value instanceof Map) {
                Map<String, Object> fileMap = (Map<String, Object>) value;
                for (Map.Entry<String, Object> entry : fileMap.entrySet()) {
                    if (!(entry.getValue() instanceof File)) {
                        //同一集合非file排除
                        continue;
                    }
                    addSingleFile((File) entry.getValue(), suffixParams, suffix, key, entry.getKey(), requestBody);
                }
            }
            //内部使用
            internalProcessParams.put(key, "file");
        } else if ((value instanceof List) || (value instanceof Map)) {
            String json = JsonUtils.toJson(value);
            requestBody.addFormDataPart(key, json);
            //内部使用
            internalProcessParams.put(key, json);
        } else {
            requestBody.addFormDataPart(key, String.valueOf(value));
            //内部使用
            internalProcessParams.put(key, String.valueOf(value));
        }
    }

    private void addSingleFile(File file, HashMap<String, String> suffixParams, String suffix, String key, String filename, MultipartBody.Builder requestBody) {
        //以文件的形式上传文件
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody body = RequestBody.create(mediaType, file);
        if (TextUtils.isEmpty(filename)) {
            //后缀若参数中有指定则优先取参数后缀，反之默认从文件中取
            if (suffixParams != null && suffixParams.containsKey(key)) {
                String mfx = suffixParams.get(key);
                if (!TextUtils.isEmpty(mfx)) {
                    suffix = mfx;
                } else {
                    suffix = GlobalUtils.getSuffixName(file.getName());
                }
            } else {
                suffix = GlobalUtils.getSuffixName(file.getName());
            }
            filename = String.format("%s.%s", GlobalUtils.getGuidNoConnect(), suffix);
        }
        requestBody.addFormDataPart(key, filename, body);
    }

    //如果参数集合包含file或byte[]则无论requestContentType是否为json均以Form方式提交
    private void addRequestParams(Request.Builder builder, TreeMap<String, Object> params, HashMap<String, String> suffixParams, RequestContentType contentType, RequestType requestType) {
        ValidResult validResult = new ValidResult();
        validParams(params, validResult, "");
        if (!ObjectJudge.isNullOrEmpty(validResult.streamParamKeys) ||
                !ObjectJudge.isNullOrEmpty(validResult.fileParamKeys)) {
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                //如果key为空且value为map类型则取value转为map后再次循环(目前只支持2级)
                if (TextUtils.isEmpty(entry.getKey()) && entry.getValue() instanceof Map) {
                    Map<String, Object> map = (Map) entry.getValue();
                    for (Map.Entry<String, Object> childEntry : map.entrySet()) {
                        addParamItem(childEntry.getKey(), childEntry.getValue(), validResult, requestBody, suffixParams);
                    }
                } else {
                    addParamItem(entry.getKey(), entry.getValue(), validResult, requestBody, suffixParams);
                }
            }
            MultipartBody body = requestBody.build();
            submitMultipartParams(builder, body, requestType);
        } else {
            RequestBody requestBody = addJsonRequestParams(validResult.ignoreParamContainsKeys, params, contentType);
            submitRequestParams(builder, requestBody, requestType);
        }
    }

    private RequestBody addJsonRequestParams(List<String> ignoreParamKeys, TreeMap<String, Object> params, RequestContentType contentType) {
        if (contentType == RequestContentType.Form) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            if (!ObjectJudge.isNullOrEmpty(params)) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    if (entry.getValue() instanceof List) {
                        bodyBuilder.add(entry.getKey(), JsonUtils.toJson(entry.getValue()));
                    } else {
                        if (entry.getValue() instanceof Map) {
                            Map<String, Object> childMap = (Map<String, Object>) entry.getValue();
                            for (Map.Entry<String, Object> childEntry : childMap.entrySet()) {
                                bodyBuilder.add(childEntry.getKey(), String.valueOf(childEntry.getValue()));
                            }
                        } else {
                            bodyBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                    }
                }
            }
            RequestBody requestBody = bodyBuilder.build();
            return requestBody;
        } else {
            if (ObjectJudge.isNullOrEmpty(params)) {
                return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{}");
            }
            if (ObjectJudge.isNullOrEmpty(ignoreParamKeys)) {
                String body = JsonUtils.toJson(params);
                return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
            } else {
                //如果包含有忽略参数将忽略其它参数提交
                if (ignoreParamKeys.size() == 1) {
                    String key = ignoreParamKeys.get(0);
                    String value = String.valueOf(params.get(key));
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), value);
                    return requestBody;
                } else {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    for (String paramKey : ignoreParamKeys) {
                        map.put(paramKey, params.get(paramKey));
                    }
                    String value = JsonUtils.toJson(map);
                    return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), value);
                }
            }
        }
    }

    private class ValidResult {
        //被忽略参数key
        public List<String> ignoreParamContainsKeys = new ArrayList<String>();
        //流参数key
        public List<String> streamParamKeys = new ArrayList<String>();
        //文件名-参数key
        public Map<String, String> fileParamKeys = new HashMap<>();
    }

    private void validParams(Map<String, Object> params, ValidResult result, String levelParamsKey) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey().startsWith(OkRxKeys.ignoreParamContainsKey) && entry.getValue() instanceof String) {
                if (!result.ignoreParamContainsKeys.contains(entry.getKey())) {
                    result.ignoreParamContainsKeys.add(entry.getKey());
                }
            } else {
                Object value = entry.getValue();
                if (value instanceof Map) {
                    validParams((Map) value, result, TextUtils.isEmpty(levelParamsKey) ? entry.getKey() : levelParamsKey);
                } else {
                    if (value instanceof byte[] || value instanceof ByteArrayInputStream) {
                        if (!result.streamParamKeys.contains(entry.getKey())) {
                            result.streamParamKeys.add(entry.getKey());
                        }
                    } else if (value instanceof File) {
                        if (!result.fileParamKeys.containsKey(entry.getKey())) {
                            result.fileParamKeys.put(entry.getKey(), TextUtils.isEmpty(levelParamsKey) ? entry.getKey() : levelParamsKey);
                        }
                    }
                }
            }
        }
    }

    private String addGetRequestParams(String url, TreeMap<String, Object> params) {
        if (ObjectJudge.isNullOrEmpty(params)) {
            return url;
        }
        StringBuilder builder = new StringBuilder();
        int index = 0;
        int count = params.size();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> childMap = (Map) entry.getValue();
                count += childMap.size() - 1;
                for (Map.Entry<String, Object> childEntry : childMap.entrySet()) {
                    joinSingleParamForGet(builder, childEntry, index, count);
                    index++;
                }
            } else {
                joinSingleParamForGet(builder, entry, index, count);
                index++;
            }
        }
        //判断原url中是否包含?
        if (url.contains("?")) {
            return String.format("%s&%s&time=%s", url, builder.toString(), System.currentTimeMillis());
        } else {
            return String.format("%s?%s&time=%s", url, builder.toString(), System.currentTimeMillis());
        }
    }

    private void joinSingleParamForGet(StringBuilder builder, Map.Entry<String, Object> entry, int index, int count) {
        String value = "";
        try {
            value = URLEncoder.encode(String.valueOf(entry.getValue()).trim(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error(e);
        }
        builder.append(entry.getKey());
        builder.append("=");
        builder.append(value);
        builder.append((index + 1) < count ? "&" : "");
        //内部使用
        internalProcessParams.put(entry.getKey(), value);
    }

    protected String getAllParamsJoin(HashMap<String, String> headers, RetrofitParams retrofitParams) {
        StringBuilder builder = new StringBuilder();
        //拼接参数
        TreeMap<String, Object> params = retrofitParams.getParams();
        if (!ObjectJudge.isNullOrEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.append(String.format("_%s_%s", entry.getKey(), entry.getValue()));
            }
        }
        if (retrofitParams.isTokenValid()) {
            //只有需要用户登录的接口拼接身份数据
            //拼接headers
            if (!ObjectJudge.isNullOrEmpty(headers)) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    builder.append(String.format("_%s_%s", entry.getKey(), entry.getValue()));
                }
            }
            //拼接cookies参数
            OnHeaderCookiesListener cookiesListener = OkRx.getInstance().getOnHeaderCookiesListener();
            Map<String, String> map = cookiesListener.onCookiesCall();
            if (!ObjectJudge.isNullOrEmpty(map)) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    builder.append(String.format("_%s_%s", entry.getKey(), entry.getValue()));
                }
            }
        }
        return builder.toString();
    }

    protected void bindCookies(OkHttpClient client, HttpUrl url) {
        try {
            //获取cookies列表
            OnHeaderCookiesListener cookiesListener = OkRx.getInstance().getOnHeaderCookiesListener();
            Map<String, String> map = cookiesListener.onCookiesCall();
            if (ObjectJudge.isNullOrEmpty(map)) {
                return;
            }
            //获取已有cookie对象
            List<Cookie> cookies = client.cookieJar().loadForRequest(url);
            //移除包含中文cookies
            Set<String> keys = removeContainChineseRepeatCookies(cookies, map);
            //添加cookie
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Cookie.Builder builder = new Cookie.Builder();
                String key = getCookieCode(entry.getKey());
                String value = getCookieCode(entry.getValue());
                Cookie cookie = builder.name(key).value(value).domain(url.host()).build();
                if (keys.contains(key)) {
                    removeHasCookie(cookies, key);
                }
                cookies.add(cookie);
            }
            //重新设置到http
            client.cookieJar().saveFromResponse(url, cookies);
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private String getCookieCode(String value) {
        if (value == null) {
            return "";
        }
        String matche = ValidUtils.matche("[\\u4e00-\\u9fa5]", value);
        if (TextUtils.isEmpty(matche)) {
            return value;
        } else {
            try {
                return URLEncoder.encode(value, "utf-8");
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
    }

    private Set<String> removeContainChineseRepeatCookies(List<Cookie> cookies, Map<String, String> map) {
        Set<String> keys = new HashSet<String>();
        Iterator<Cookie> iterator = cookies.iterator();
        while (iterator.hasNext()) {
            Cookie next = iterator.next();
            String matche = ValidUtils.matche("[\\u4e00-\\u9fa5]", next.value());
            //如果有包含中文或在新增队列中移除
            if (!TextUtils.isEmpty(matche) || map.containsKey(next.name())) {
                //包含中文
                iterator.remove();
            } else {
                //如果有重复移除
                if (keys.contains(next.name())) {
                    iterator.remove();
                } else {
                    keys.add(next.name());
                }
            }
        }
        return keys;
    }

    private void removeHasCookie(List<Cookie> cookies, String key) {
        Iterator<Cookie> iterator = cookies.iterator();
        while (iterator.hasNext()) {
            Cookie next = iterator.next();
            String name = next.name();
            if (TextUtils.equals(String.valueOf(name).trim(), String.valueOf(key))) {
                iterator.remove();
                break;
            }
        }
    }

    //缓存处理返回true继续之后处理,false不作之后网络处理
    protected boolean cacheDealWith(CallStatus callStatus,
                                    Action1<SuccessResponse> successAction,
                                    String ckey,
                                    RetrofitParams retrofitParams) {
        //网络请求-在缓存未失效时网络数据与缓存只会返回其中一个,缓存失效后先请求网络->再缓存->最后返回;
        //即首次请求或缓存失效的情况会走网络,否则每次只取缓存数据;
        //OnlyCache,
        //
        //每次只作网络请求;
        //OnlyNet,
        //
        //网络请求-在缓存未失败时获取到网络数据和缓存数据均会回调,缓存失效后先请求网络->再缓存->最后返回(即此时只作网络数据的回调);
        //1.有缓存时先回调缓存数据再请求网络数据然后[缓存+回调];
        //2.无缓存时不作缓存回调直接请求网络数据后[缓存+回调];
        //WeakCacheAccept,
        //
        //1.有缓存时先回调缓存数据再请求网络数据然后[缓存]不作网络回调;
        //2.无缓存时不作缓存回调直接请求网络数据后[缓存]不作网络回调;
        //WeakCache
        if (callStatus != CallStatus.OnlyNet) {
            String cacheData = EffectiveMMkvUtils.getInstance().getString(String.valueOf(ckey.hashCode()));
            if (successAction != null && ObjectJudge.isEmptyString(cacheData)) {
                ResponseData responseData = new ResponseData();
                responseData.setResponseDataType(ResponseDataType.object);
                responseData.setResponse(cacheData);

                if (callStatus == CallStatus.TakNetwork) {
                    if (TextUtils.isEmpty(responseData.getResponse())) {
                        SuccessResponse successResponse = new SuccessResponse(responseData, DataType.EmptyForOnlyCache);
                        successResponse.setRetrofitParams(retrofitParams);
                        successResponse.setCode(200);
                        successAction.call(successResponse);
                    } else {
                        SuccessResponse successResponse = new SuccessResponse(responseData, DataType.CacheData);
                        successResponse.setRetrofitParams(retrofitParams);
                        successResponse.setCode(200);
                        successAction.call(successResponse);
                    }
                    //此状态下不作网络请求
                    return false;
                } else if (!TextUtils.isEmpty(cacheData)) {
                    SuccessResponse successResponse = new SuccessResponse(responseData, DataType.CacheData);
                    successResponse.setRetrofitParams(retrofitParams);
                    successResponse.setCode(200);
                    successAction.call(successResponse);
                    //1.有缓存时先回调缓存数据再请求网络数据然后[缓存+回调];
                    //2.无缓存时不作缓存回调直接请求网络数据后[缓存+回调];
                    //3.有缓存时先回调缓存数据再请求网络数据然后[缓存]不作网络回调;
                    //4.无缓存时不作缓存回调直接请求网络数据后[缓存]不作网络回调;
                    //首次请求时缓存失效的情况会走网络,否则每次只取缓存数据;
                    //具体类型参考{@link CallStatus}
                    if (callStatus == CallStatus.OnlyCache) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
