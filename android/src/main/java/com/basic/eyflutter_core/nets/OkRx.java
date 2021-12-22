package com.basic.eyflutter_core.nets;

import android.content.Context;
import android.text.TextUtils;

import com.basic.eyflutter_core.nets.cookie.CookieJarImpl;
import com.basic.eyflutter_core.nets.cookie.store.CookieStore;
import com.basic.eyflutter_core.nets.cookie.store.SPCookieStore;
import com.basic.eyflutter_core.nets.events.OnAuthListener;
import com.basic.eyflutter_core.nets.events.OnBeanParsingJsonListener;
import com.basic.eyflutter_core.nets.events.OnConfigParamsListener;
import com.basic.eyflutter_core.nets.events.OnGlobalRequestParamsListener;
import com.basic.eyflutter_core.nets.events.OnGlobalReuqestHeaderListener;
import com.basic.eyflutter_core.nets.events.OnHeaderCookiesListener;
import com.basic.eyflutter_core.nets.events.OnRequestAlarmApiListener;
import com.basic.eyflutter_core.nets.events.OnRequestErrorListener;
import com.basic.eyflutter_core.nets.properties.OkRxConfigParams;
import com.basic.eyflutter_core.utils.MmkvUtils;
import com.cloud.eyutils.CdLibConfig;
import com.cloud.eyutils.events.OnEntryCall;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.observable.BaseObservable;
import com.cloud.eyutils.observable.call.OnSubscribeConsumer;
import com.cloud.eyutils.storage.MemoryCache;
import com.cloud.eyutils.utils.JsonUtils;
import com.cloud.eyutils.utils.ObjectJudge;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * Author Gs
 * Email:gs_12@foxmail.com
 * CreateTime:2017/6/1
 * Description: OkGo基础
 * Modifier:
 * ModifyContent:
 */
@SuppressWarnings("unchecked")
public class OkRx extends BaseObservable {

    private static OkRx okRx = null;
    //是否要重新获取配置参数
    private boolean isUpdateConfig = false;
    private OkRxConfigParams okRxConfigParams = null;
    private OnConfigParamsListener onConfigParamsListener = null;
    //application context 在cookies持久化时使用(为空时cookies使用内存持久化)
    private Context applicationContext = null;
    //接口请求结果json解析处理监听
    private OnBeanParsingJsonListener parsingJsonListener = null;
    //请求头监听
    private OnGlobalReuqestHeaderListener globalReuqestHeaderListener = null;
    //全局请求参数
    private OnGlobalRequestParamsListener globalRequestParamsListener = null;
    //监听请求头参数
    private HashMap headers = null;
    //网络请求失败监听
    private OnRequestErrorListener onRequestErrorListener = null;
    //网络请求时用户授权相关回调监听
    private OnAuthListener onAuthListener = null;
    //用于http socket connect fail处理
    private Set<String> failDomainList = new HashSet<String>();
    //头部cookies监听
    private OnHeaderCookiesListener onHeaderCookiesListener = null;
    //跟踪日志是否带固件配置信息(默认false)
    private boolean isHasFirmwareConfigInformationForTraceLog = false;
    //ok http client
    private OkHttpClient httpClient;
    //请求缓存队列(class name<->requestKey-call)
    private HashMap<String, Map<String, Call>> requestQueue = new HashMap<>();
    //接口请求总时间超过警报最大时间(OkRxConfigParams->requestAlarmMaxTime)时回调
    private OnRequestAlarmApiListener onRequestAlarmApiListener = null;
    private String cacheGroup = "e984402acc49bcae";

    public static OkRx getInstance() {
        if (okRx == null) {
            okRx = new OkRx();
        }
        return okRx;
    }

    //在连接失败判断用,外面无须调用;
    public Set<String> getFailDomainList() {
        return failDomainList;
    }

    /**
     * 设置全局配置参数监听
     *
     * @param listener 全局配置参数监听
     */
    public OkRx setOnConfigParamsListener(OnConfigParamsListener listener) {
        this.onConfigParamsListener = listener;
        return this;
    }

    private class OkRxEntryCall implements OnEntryCall {

        private Object target;

        public OkRxEntryCall(Object target) {
            this.target = target;
        }

        @Override
        public Object onEntryResult() {
            return target;
        }
    }

    /**
     * 获取请求头回调监听
     *
     * @return OnGlobalReuqestHeaderListener
     */
    public OnGlobalReuqestHeaderListener getOnGlobalReuqestHeaderListener() {
        if (globalReuqestHeaderListener == null) {
            Object listener = CdLibConfig.getInstance().getConfigValue("OkRx_GlobalReuqestHeaderListener");
            if (listener instanceof OnGlobalReuqestHeaderListener) {
                this.globalReuqestHeaderListener = (OnGlobalReuqestHeaderListener) listener;
            }
        }
        return globalReuqestHeaderListener;
    }

    /**
     * 设置全局请求头回调监听
     *
     * @param listener OnGlobalReuqestHeaderListener
     * @return
     */
    public OkRx setOnGlobalReuqestHeaderListener(OnGlobalReuqestHeaderListener listener) {
        this.globalReuqestHeaderListener = listener;
        CdLibConfig.getInstance().addConfig("OkRx_GlobalReuqestHeaderListener", new OkRxEntryCall(listener));
        return this;
    }

    /**
     * 设置全局请求参数回调
     *
     * @param listener OnGlobalRequestParamsListener
     * @return OkRx
     */
    public OkRx setGlobalRequestParamsListener(OnGlobalRequestParamsListener listener) {
        this.globalRequestParamsListener = listener;
        CdLibConfig.getInstance().addConfig("OkRx_OnGlobalRequestParamsListener", new OkRxEntryCall(listener));
        return this;
    }

    /**
     * 获取全局请求参数回调
     *
     * @return OnGlobalRequestParamsListener
     */
    public OnGlobalRequestParamsListener getGlobalRequestParamsListener() {
        if (globalRequestParamsListener == null) {
            Object listener = CdLibConfig.getInstance().getConfigValue("OkRx_OnGlobalRequestParamsListener");
            if (listener instanceof OnGlobalRequestParamsListener) {
                this.globalRequestParamsListener = (OnGlobalRequestParamsListener) listener;
            }
        }
        return globalRequestParamsListener;
    }

    /**
     * okrx初始化
     * (一般在Application初始化时调用)
     *
     * @param context 上下文
     */
    public OkRx initialize(Context context) {
        this.applicationContext = context;
        this.isUpdateConfig = true;
        if (onConfigParamsListener != null) {
            okRxConfigParams = onConfigParamsListener.onConfigParamsCall(getDefaultConfigParams());
        }
        if (okRxConfigParams == null) {
            okRxConfigParams = new OkRxConfigParams();
        }
        return this;
    }

    //构建相关配置
    public void build() {
        if (onConfigParamsListener != null) {
            okRxConfigParams = onConfigParamsListener.onConfigParamsCall(getDefaultConfigParams());
        }
        if (okRxConfigParams == null) {
            okRxConfigParams = new OkRxConfigParams();
        }
        //缓存okRxConfigParams参数
        httpClient = newHttpClient(okRxConfigParams);
    }

    /**
     * 重新构建http client
     *
     * @param okRxConfigParams 全局配置参数
     * @return OkHttpClient
     */
    public OkHttpClient newHttpClient(OkRxConfigParams okRxConfigParams) {
        //创建http client对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //连接超时
        builder.connectTimeout(okRxConfigParams.getConnectTimeout(), TimeUnit.MILLISECONDS);
        //读取超时
        builder.readTimeout(okRxConfigParams.getReadTimeOut(), TimeUnit.MILLISECONDS);
        //写入超时
        builder.writeTimeout(okRxConfigParams.getWriteTimeOut(), TimeUnit.MILLISECONDS);
        //设置失败时重连次数,请求头信息
        builder.addInterceptor(new RequestRetryIntercepter(okRxConfigParams.getRetryCount(), okRxConfigParams.getHeaders()));
        //cookies持久化
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(applicationContext)));
        //添加证书信任
        SslSocketManager.SSLParams sslParams1 = SslSocketManager.getSslSocketFactory();
        if (sslParams1 != null) {
            builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        }
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
        //全局请求参数设置
        return builder.build();
    }

    /**
     * 获取http client对象
     *
     * @return OkHttpClient
     */
    public OkHttpClient getOkHttpClient() {
        if (httpClient == null) {
            synchronized (OkHttpClient.class) {
                if (httpClient == null) {
                    OkRxConfigParams configParams = getOkRxConfigParams();
                    httpClient = newHttpClient(configParams);
                }
            }
        }
        return httpClient;
    }

    /**
     * 获取okrx全局配置参数
     * (不要在application初始化时调用)
     *
     * @return
     */
    public OkRxConfigParams getOkRxConfigParams() {
        if (okRxConfigParams == null || isUpdateConfig) {
            if (onConfigParamsListener != null) {
                okRxConfigParams = onConfigParamsListener.onConfigParamsCall(getDefaultConfigParams());
            }
            isUpdateConfig = false;
        }
        //再次判断若全局参数为空则重新创建参数
        if (okRxConfigParams == null) {
            okRxConfigParams = new OkRxConfigParams();
        }
        return okRxConfigParams;
    }

    /**
     * 获取默认参数配置
     *
     * @return OkRxConfigParams
     */
    public OkRxConfigParams getDefaultConfigParams() {
        if (okRxConfigParams == null) {
            okRxConfigParams = new OkRxConfigParams();
        }
        return okRxConfigParams;
    }

    /**
     * 获取json解析监听对象
     *
     * @return json解析监听对象
     */
    public OnBeanParsingJsonListener getOnBeanParsingJsonListener() {
        if (parsingJsonListener == null) {
            Object listener = CdLibConfig.getInstance().getConfigValue("OkRx_BeanParsingJsonListener");
            if (listener instanceof OnBeanParsingJsonListener) {
                parsingJsonListener = (OnBeanParsingJsonListener) listener;
            }
        }
        return parsingJsonListener;
    }

    /**
     * 接口返回结果json解析需要自行处理的须实现此监听
     *
     * @param parsingJsonListener json解析监听对象
     * @return OkRx
     */
    public OkRx setOnBeanParsingJsonListener(OnBeanParsingJsonListener parsingJsonListener) {
        this.parsingJsonListener = parsingJsonListener;
        CdLibConfig.getInstance().addConfig("OkRx_BeanParsingJsonListener", new OkRxEntryCall(parsingJsonListener));
        return this;
    }

    /**
     * 设置http请求时header参数(全局)
     * (持久存储)
     * 根据业务场景也可以在setOnGlobalReuqestHeaderListener()设置的监听中回调
     *
     * @param headers header params
     * @return OkRx
     */
    public OkRx setHeaderParams(HashMap<String, String> headers) {
        this.headers = headers;
        String json = ObjectJudge.isNullOrEmpty(headers) ? "" : JsonUtils.toJson(headers);
        setCacheData("NetRequestHttpHeaderParams", json);
        return this;
    }

    /**
     * 获取请求头参数
     *
     * @return key-value header params
     */
    public HashMap<String, String> getHeaderParams() {
        if (ObjectJudge.isNullOrEmpty(headers)) {
            String params = getCacheData("NetRequestHttpHeaderParams");
            headers = JsonUtils.parseT(params, HashMap.class);
        }
        return headers;
    }

    /**
     * 获取http请求失败回调监听
     *
     * @return OnRequestErrorListener
     */
    public OnRequestErrorListener getOnRequestErrorListener() {
        if (onRequestErrorListener == null) {
            Object listener = CdLibConfig.getInstance().getConfigValue("OkRx_NetRequestErrorListener");
            if (listener instanceof OnRequestErrorListener) {
                onRequestErrorListener = (OnRequestErrorListener) listener;
            }
        }
        return onRequestErrorListener;
    }

    /**
     * 设置http请求失败回调监听
     *
     * @param listener http失败回调监听
     */
    public OkRx setOnRequestErrorListener(OnRequestErrorListener listener) {
        this.onRequestErrorListener = listener;
        CdLibConfig.getInstance().addConfig("OkRx_NetRequestErrorListener", new OkRxEntryCall(listener));
        return this;
    }

    /**
     * 获取授权相关监听
     *
     * @return OnAuthListener
     */
    public OnAuthListener getOnAuthListener() {
        if (onAuthListener == null) {
            Object listener = CdLibConfig.getInstance().getConfigValue("OkRx_NetAuthListener");
            if (listener instanceof OnAuthListener) {
                onAuthListener = (OnAuthListener) listener;
            }
        }
        return onAuthListener;
    }

    /**
     * 设置授权相关监听
     *
     * @param listener 授权相关监听
     */
    public OkRx setOnAuthListener(OnAuthListener listener) {
        this.onAuthListener = listener;
        CdLibConfig.getInstance().addConfig("OkRx_NetAuthListener", new OkRxEntryCall(listener));
        return this;
    }

    /**
     * 获取http cookies追加监听
     *
     * @return OnHeaderCookiesListener
     */
    public OnHeaderCookiesListener getOnHeaderCookiesListener() {
        if (onHeaderCookiesListener == null) {
            Object listener = CdLibConfig.getInstance().getConfigValue("OkRx_OnHeaderCookiesListener");
            if (listener instanceof OnHeaderCookiesListener) {
                onHeaderCookiesListener = (OnHeaderCookiesListener) listener;
            }
        }
        return onHeaderCookiesListener;
    }

    /**
     * 设置http cookies追加监听
     *
     * @param listener OnHeaderCookiesListener
     * @return OkRx
     */
    public OkRx setOnHeaderCookiesListener(OnHeaderCookiesListener listener) {
        this.onHeaderCookiesListener = listener;
        CdLibConfig.getInstance().addConfig("OkRx_OnHeaderCookiesListener", new OkRxEntryCall(listener));
        return this;
    }

    /**
     * 清除token信息
     * 在用户退出登录时调用
     */
    public void clearToken() {
        OkHttpClient client = getOkHttpClient();
        if (client == null) {
            return;
        }
        CookieJar cookieJar = client.cookieJar();
        if (!(cookieJar instanceof CookieJarImpl)) {
            return;
        }
        CookieJarImpl cookieImpl = (CookieJarImpl) cookieJar;
        CookieStore cookieStore = cookieImpl.getCookieStore();
        if (cookieStore == null) {
            return;
        }
        cookieStore.removeAllCookie();
    }

    /**
     * 清除接口请求的网络缓存
     *
     * @param cacheKey 请求接口时设置的缓存key
     */
    public void clearCache(String cacheKey) {
        removeByGroup(cacheKey);
    }

    /**
     * 设置跟踪日志是否带固件配置信息(默认false)
     *
     * @param isHasFirmwareConfigInformationForTraceLog true-对于请求失败跟踪日志带有设备相关配置信息;反之则不带;
     * @return OkRx
     */
    public OkRx setHasFirmwareConfigInformationForTraceLog(boolean isHasFirmwareConfigInformationForTraceLog) {
        this.isHasFirmwareConfigInformationForTraceLog = isHasFirmwareConfigInformationForTraceLog;
        MemoryCache.getInstance().setSoftCache("OkRx_HasFirmwareConfigInformationForTraceLog", isHasFirmwareConfigInformationForTraceLog);
        return this;
    }

    /**
     * 获取跟踪日志是否带固件配置信息(默认false)
     *
     * @return true-对于请求失败跟踪日志带有设备相关配置信息;反之则不带;
     */
    public boolean isHasFirmwareConfigInformationForTraceLog() {
        if (!this.isHasFirmwareConfigInformationForTraceLog) {
            Object o = MemoryCache.getInstance().getSoftCache("OkRx_HasFirmwareConfigInformationForTraceLog");
            this.isHasFirmwareConfigInformationForTraceLog = ObjectJudge.isTrue(o);
        }
        return this.isHasFirmwareConfigInformationForTraceLog;
    }

    /**
     * 取消所有请求
     */
    public void cancelAllRequest() {
        for (Map.Entry<String, Map<String, Call>> entry : requestQueue.entrySet()) {
            Map<String, Call> map = entry.getValue();
            if (map == null) {
                continue;
            }
            for (Map.Entry<String, Call> callEntry : map.entrySet()) {
                Call value = callEntry.getValue();
                if (value == null) {
                    continue;
                }
                value.cancel();
            }
        }
    }

    /**
     * 取消接口调用类对应的请求
     *
     * @param useClass 接口所调用的类
     */
    public void cancelRequest(Class useClass) {
        if (useClass == null) {
            return;
        }
        String className = useClass.getName();
        if (!requestQueue.containsKey(className)) {
            return;
        }
        Map<String, Call> callMap = requestQueue.get(className);
        if (callMap == null) {
            return;
        }
        for (Map.Entry<String, Call> entry : callMap.entrySet()) {
            Call value = entry.getValue();
            if (value == null) {
                continue;
            }
            value.cancel();
        }
    }

    /**
     * 从requestQueue中移除请求不作取消(相关请求已经结束)
     *
     * @param requestKey request key
     */
    public void removeRequest(String requestKey) {
        if (TextUtils.isEmpty(requestKey)) {
            return;
        }
        super.buildSubscribe(requestKey, new OnSubscribeConsumer<String, Object>() {
            @Override
            public void onSubscribe(String requestKey, Object o) throws Exception {
                synchronized (requestQueue) {
                    for (Map.Entry<String, Map<String, Call>> entry : requestQueue.entrySet()) {
                        Map<String, Call> value = entry.getValue();
                        if (value == null) {
                            continue;
                        }
                        if (!value.containsKey(requestKey)) {
                            return;
                        }
                        value.remove(requestKey);
                        if (value.size() == 0) {
                            requestQueue.remove(entry.getKey());
                        }
                    }
                }
            }
        }, null, null);
    }

    /**
     * 添加请求到队列
     *
     * @param useClassName 请求所使用的类
     * @param requestKey   url+[params]
     * @param call         请求对象
     */
    public void putRequest(String useClassName, String requestKey, Call call) {
        if (TextUtils.isEmpty(useClassName) || TextUtils.isEmpty(requestKey) || call == null) {
            return;
        }
        Map<String, Call> map = null;
        if (requestQueue.containsKey(useClassName)) {
            map = requestQueue.get(useClassName);
        }
        boolean inqueue = false;
        if (map == null) {
            inqueue = true;
            map = new HashMap<String, Call>();
        }
        if (!map.containsKey(requestKey)) {
            map.put(requestKey, call);
        }
        if (inqueue) {
            requestQueue.put(useClassName, map);
        }
    }

    /**
     * 接口请求总时间超过警报最大时间事件
     *
     * @return OnRequestAlarmApiListener
     */
    public OnRequestAlarmApiListener getOnRequestAlarmApiListener() {
        if (onRequestAlarmApiListener == null) {
            Object listener = CdLibConfig.getInstance().getConfigValue("OkRx_OnRequestAlarmApiListener");
            if (listener instanceof OnRequestAlarmApiListener) {
                onRequestAlarmApiListener = (OnRequestAlarmApiListener) listener;
            }
        }
        return onRequestAlarmApiListener;
    }

    /**
     * 设置接口请求总时间超过警报最大时间事件
     *
     * @param listener OnRequestAlarmApiListener
     * @return OkRx
     */
    public OkRx setOnRequestAlarmApiListener(OnRequestAlarmApiListener listener) {
        this.onRequestAlarmApiListener = listener;
        CdLibConfig.getInstance().addConfig("OkRx_OnRequestAlarmApiListener", new OkRxEntryCall(listener));
        return this;
    }

    public void setCacheData(String key, String data) {
        Context applicationContext = LauncherState.getApplicationContext();
        MmkvUtils.getInstance(applicationContext).putString(cacheGroup, key, "string", data);
    }

    public String getCacheData(String key) {
        Context applicationContext = LauncherState.getApplicationContext();
        return MmkvUtils.getInstance(applicationContext).getString(cacheGroup, key);
    }

    public void removeByGroup(String fuzzyKey) {
        Context applicationContext = LauncherState.getApplicationContext();
        MmkvUtils.getInstance(applicationContext).removeContains(cacheGroup, fuzzyKey);
    }
}