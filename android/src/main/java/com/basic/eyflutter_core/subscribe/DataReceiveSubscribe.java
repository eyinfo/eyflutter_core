package com.basic.eyflutter_core.subscribe;

import android.content.Context;

import com.basic.eyflutter_core.channel.OnDistributionSubscribe;
import com.basic.eyflutter_core.service.ReceiveDbService;
import com.cloud.eyutils.launchs.LauncherState;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

public class DataReceiveSubscribe extends OnDistributionSubscribe {

    @Override
    public void onSubscribe(MethodChannel.Result result, HashMap<String, Object> arguments) {
        ReceiveDbService dbService = new ReceiveDbService();
        Context applicationContext = LauncherState.getApplicationContext();
        dbService.receive(applicationContext, result, arguments);
    }
}
