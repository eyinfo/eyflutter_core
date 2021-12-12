package com.basic.eyflutter_core.subscribe;

import com.basic.eyflutter_core.channel.OnDistributionSubscribe;
import com.cloud.eyutils.utils.NetworkUtils;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

public class NetConnectSubscribe extends OnDistributionSubscribe {
    @Override
    public void onSubscribe(MethodChannel.Result result, HashMap<String, Object> hashMap) {
        result.success(NetworkUtils.isConnected());
    }
}
