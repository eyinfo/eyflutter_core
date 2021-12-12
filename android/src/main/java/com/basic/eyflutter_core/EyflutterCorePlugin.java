package com.basic.eyflutter_core;

import com.basic.eyflutter_core.channel.ChannelPlugin;
import com.basic.eyflutter_core.enums.ChannelMode;
import com.cloud.eyutils.launchs.LauncherState;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

public class EyflutterCorePlugin implements FlutterPlugin {

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        if (LauncherState.getApplicationContext() == null) {
            LauncherState.setApplicationContext(binding.getApplicationContext());
        }
        ChannelPlugin.getInstance().register(binding.getBinaryMessenger(), ChannelMode.method);
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        ChannelPlugin.getInstance().destroy();
    }
}
