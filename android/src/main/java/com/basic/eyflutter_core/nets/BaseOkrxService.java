package com.basic.eyflutter_core.nets;

import android.text.TextUtils;

import com.basic.eyflutter_core.nets.annotations.APIUrlInterfaceClass;
import com.basic.eyflutter_core.nets.beans.RetrofitParams;
import com.basic.eyflutter_core.nets.events.OnRequestApiUrl;
import com.basic.eyflutter_core.nets.filters.ReturnCodeCheck;
import com.basic.eyflutter_core.nets.properties.OkRxConfigParams;
import com.basic.eyflutter_core.nets.properties.OkRxValidParam;
import com.cloud.eyutils.beans.MapEntry;
import com.cloud.eyutils.events.Func2;
import com.cloud.eyutils.observable.ObservableComponent;
import com.cloud.eyutils.utils.JsonUtils;
import com.cloud.eyutils.utils.ObjectJudge;

import java.lang.annotation.Annotation;
import java.util.HashMap;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2018/6/25
 * @Description:接口请求服务基类
 * @Modifier:
 * @ModifyContent:
 */
@SuppressWarnings("unchecked")
public class BaseOkrxService extends BaseService {

    private HashMap<Integer, String> keyUrls = new HashMap<Integer, String>();

    private <S extends BaseService> String getBaseUrls(S server, Integer apiUrlTypeName) {
        if (keyUrls.containsKey(apiUrlTypeName)) {
            String url = keyUrls.get(apiUrlTypeName);
            if (TextUtils.isEmpty(url)) {
                return getAnnonBaseUrl(server, apiUrlTypeName);
            } else {
                return url;
            }
        } else {
            return getAnnonBaseUrl(server, apiUrlTypeName);
        }
    }

    private void getServerAPIUrlAnnon(Class<?> cls, APIUrlInterfaceClass[] apiUrlInterfaceClasss) {
        Annotation[] annotations = cls.getDeclaredAnnotations();
        if (ObjectJudge.isNullOrEmpty(annotations)) {
            Class<?> superclass = cls.getSuperclass();
            if (superclass != null) {
                getServerAPIUrlAnnon(superclass, apiUrlInterfaceClasss);
            }
        } else {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == APIUrlInterfaceClass.class) {
                    apiUrlInterfaceClasss[0] = (APIUrlInterfaceClass) annotation;
                    break;
                }
            }
            if (apiUrlInterfaceClasss[0] == null) {
                Class<?> superclass = cls.getSuperclass();
                if (superclass != null) {
                    getServerAPIUrlAnnon(superclass, apiUrlInterfaceClasss);
                }
            }
        }
    }

    private <S extends BaseService> String getAnnonBaseUrl(S server, Integer apiUrlTypeName) {
        Class<? extends BaseService> cls = server.getClass();
        APIUrlInterfaceClass[] apiUrlInterfaceClasss = new APIUrlInterfaceClass[1];
        getServerAPIUrlAnnon(cls, apiUrlInterfaceClasss);
        if (apiUrlInterfaceClasss[0] == null) {
            return "";
        }
        Object apiObj = JsonUtils.newNull(apiUrlInterfaceClasss[0].value());
        if (apiObj == null || !(apiObj instanceof OnRequestApiUrl)) {
            return "";
        }
        OnRequestApiUrl requestApiUrl = (OnRequestApiUrl) apiObj;
        String url = requestApiUrl.onBaseUrl(apiUrlTypeName);
        keyUrls.put(apiUrlTypeName, url);
        return url;
    }

    /**
     * 网络请求配置
     *
     * @param apiClass       接口定义类Class
     * @param server         请求接口服务类，一般传this
     * @param baseSubscriber 数据返回订阅器
     * @param decApiAction   定义接口回调方法，用来调用apiClass中的接口
     * @param <I>            接口泛型类
     * @param <S>            接口服务泛型类
     */
    public <I, S extends BaseService> void requestObject(Class<I> apiClass,
                                                         S server,
                                                         BaseSubscriber baseSubscriber,
                                                         Func2<RetrofitParams, I, HashMap<String, Object>> decApiAction) {
        requestObject(apiClass, server, baseSubscriber, decApiAction, null);
    }

    /**
     * 网络请求配置
     *
     * @param apiClass       接口定义类Class
     * @param server         请求接口服务类，一般传this
     * @param baseSubscriber 数据返回订阅器
     * @param decApiAction   定义接口回调方法，用来调用apiClass中的接口
     * @param params         扩展参数
     * @param <I>            接口泛型类
     * @param <S>            接口服务泛型类
     */
    public <I, S extends BaseService> void requestObject(Class<I> apiClass,
                                                         S server,
                                                         BaseSubscriber baseSubscriber,
                                                         Func2<RetrofitParams, I, HashMap<String, Object>> decApiAction,
                                                         HashMap<String, Object> params) {
        //此方法越早调用性能会比较好(不能放在线程中执行)
        MapEntry<String, String> useTarget = OkrxRequestValid.getInvokingUseTarget();
        //开始验证并请求
        ObservableComponent<OkRxValidParam, Object> component = requestTask();
        component.build(apiClass, server, baseSubscriber, decApiAction, params, useTarget);
    }

    private <I, S extends BaseService> ObservableComponent<OkRxValidParam, Object> requestTask() {
        ObservableComponent<OkRxValidParam, Object> component = new ObservableComponent<OkRxValidParam, Object>() {
            @Override
            protected OkRxValidParam subscribeWith(final Object... params) {
                //网络请求前检查，网络、token以及是否需要缓存
                OkrxRequestValid requestValid = new OkrxRequestValid();
                MapEntry<String, String> useTarget = (MapEntry<String, String>) params[5];
                //第二个参数为service
                OkRxValidParam validParam = requestValid.check((S) params[1], useTarget.getKey());
                //若状态码拦截被禁用后，则对应实现也无须处理
                OkRxConfigParams okRxConfigParams = OkRx.getInstance().getOkRxConfigParams();
                if (okRxConfigParams.isNetStatusCodeIntercept()) {
                    ReturnCodeCheck codeCheck = new ReturnCodeCheck();
                    validParam.setReturnCodeFilter(codeCheck.getCodeFilter((S) params[1]));
                }
                apiRequest(validParam,
                        (Class<I>) params[0],
                        (S) params[1],
                        (BaseSubscriber) params[2],
                        (Func2<RetrofitParams, I, HashMap<String, Object>>) params[3],
                        (HashMap<String, Object>) params[4],
                        useTarget.getValue());
                return validParam;
            }
        };
        return component;
    }

    private <I, S extends BaseService> void apiRequest(OkRxValidParam validParam,
                                                       Class<I> apiClass,
                                                       S server,
                                                       BaseSubscriber baseSubscriber,
                                                       Func2<RetrofitParams, I, HashMap<String, Object>> decApiAction,
                                                       HashMap<String, Object> params,
                                                       String useClass) {
        requestObject(apiClass, server, baseSubscriber, validParam, new Func2<String, S, Integer>() {
                    @Override
                    public String call(S server, Integer apiUrlTypeName) {
                        return getBaseUrls(server, apiUrlTypeName);
                    }
                },
                decApiAction,
                params,
                useClass);
    }
}