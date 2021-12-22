package com.basic.eyflutter_core.nets;

import android.annotation.SuppressLint;

import com.basic.eyflutter_core.nets.beans.ResultParams;
import com.basic.eyflutter_core.nets.enums.DataType;
import com.basic.eyflutter_core.nets.events.OnSuccessfulListener;
import com.cloud.eyutils.HandlerManager;
import com.cloud.eyutils.events.RunnableParamsN;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2016/6/14
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class BaseSubscriber<T, BaseT extends BaseService> {

    private Object[] extra = null;
    private List<String> allowRetCodes = new ArrayList<String>();
    //接口成功回调监听
    private OnSuccessfulListener onSuccessfulListener = null;

    /**
     * 设置接口成功回调监听
     *
     * @param onSuccessfulListener 监听对象
     */
    public void setOnSuccessfulListener(OnSuccessfulListener onSuccessfulListener) {
        this.onSuccessfulListener = onSuccessfulListener;
    }

    public OnSuccessfulListener getOnSuccessfulListener() {
        return onSuccessfulListener;
    }

    /**
     * 设置扩展数据
     *
     * @param extra 扩展数据
     */
    public void setExtra(Object... extra) {
        this.extra = extra;
    }

    /**
     * 获取扩展数据
     *
     * @return 扩展数据
     */
    public Object[] getExtra() {
        return extra;
    }

    /**
     * 获取接口定义中的请允许验证通过的返回码
     *
     * @return 返回码集合
     */
    public List<String> getAllowRetCodes() {
        return this.allowRetCodes;
    }

    public <ExtraT> BaseSubscriber(BaseT cls) {

    }

    @SuppressWarnings("unchecked")
    public void onNext(ResultParams<T> resultParams) {
        if (resultParams.getDataType() == DataType.NetData) {
            long cdiff = System.currentTimeMillis() - resultParams.getRequestStartTime();
            if (cdiff >= resultParams.getRequestTotalTime()) {
                //将数据发送到main线程
                HandlerManager.getInstance().post(new ApiHandlerRun(resultParams));
            } else {
                //倒计时时间差(以毫秒为单位)
                long mdiff = resultParams.getRequestTotalTime() - cdiff;
                delayCall(mdiff, new ApiHandlerRun(resultParams));
            }
        } else {
            //将数据发送到main线程
            HandlerManager.getInstance().post(new ApiHandlerRun(resultParams));
        }
    }

    @SuppressLint("CheckResult")
    private void delayCall(long milliseconds, ApiHandlerRun run) {
        Observable.just(run)
                .delay(milliseconds, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApiHandlerRun>() {
                    @Override
                    public void accept(ApiHandlerRun run) {
                        if (run == null) {
                            return;
                        }
                        run.run();
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private class ApiHandlerRun extends RunnableParamsN {

        private ResultParams params = null;

        public ApiHandlerRun(ResultParams params) {
            this.params = params;
        }

        @Override
        public void run(Object... objects) {
            if (params == null) {
                return;
            }
            if (onSuccessfulListener != null) {
                onSuccessfulListener.setCode(params.getCode());
                onSuccessfulListener.onSuccessful(params.getData(), params.getDataType(), extra);
                onSuccessfulListener.onCompleted(extra);
            }
        }
    }
}
