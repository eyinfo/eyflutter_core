package com.basic.eyflutter_core;

import com.basic.eyflutter_core.channel.ChannelPlugin;
import com.basic.eyflutter_core.enums.ChannelMode;
import com.basic.eyflutter_core.subscribe.NetConnectSubscribe;
import com.cloud.eyutils.launchs.LauncherState;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

public class EyflutterCorePlugin implements FlutterPlugin {

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        if (LauncherState.getApplicationContext() == null) {
            LauncherState.setApplicationContext(binding.getApplicationContext());
        }
        ChannelPlugin.getInstance().register(binding.getBinaryMessenger(), ChannelMode.method);
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.networkConnectChannelName, new NetConnectSubscribe());
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.networkConnectChannelName);
        ChannelPlugin.getInstance().destroy();
    }
}
