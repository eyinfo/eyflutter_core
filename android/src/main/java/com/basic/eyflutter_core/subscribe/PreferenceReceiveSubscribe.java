package com.basic.eyflutter_core.subscribe;

import android.content.Context;

import com.basic.eyflutter_core.channel.OnDistributionSubscribe;
import com.basic.eyflutter_core.service.ReceiveSharedPreferencesService;
import com.cloud.eyutils.launchs.LauncherState;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

public class PreferenceReceiveSubscribe extends OnDistributionSubscribe {

    @Override
    public void onSubscribe(MethodChannel.Result result, HashMap<String, Object> arguments) {
        ReceiveSharedPreferencesService preferencesService = new ReceiveSharedPreferencesService();
        Context applicationContext = LauncherState.getApplicationContext();
        preferencesService.receive(applicationContext, result, arguments);
    }
}
