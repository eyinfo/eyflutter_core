package com.basic.eyflutter_core.channel;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

//事件分发订阅
public abstract class OnDistributionSubscribe {

    public void onInit() {
        //init
    }

    public abstract void onSubscribe(MethodChannel.Result result, HashMap<String, Object> arguments);
}
