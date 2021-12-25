package com.basic.eyflutter_core;

import android.content.Context;

import com.basic.eyflutter_core.channel.ChannelPlugin;
import com.basic.eyflutter_core.enums.ChannelMode;
import com.basic.eyflutter_core.greens.BasicConfig;
import com.basic.eyflutter_core.greens.BuildDirectoryConfig;
import com.basic.eyflutter_core.greens.DBManager;
import com.basic.eyflutter_core.greens.DBPathsModelConfig;
import com.basic.eyflutter_core.subscribe.CleanCacheSubscribe;
import com.basic.eyflutter_core.subscribe.DataReceiveSubscribe;
import com.basic.eyflutter_core.subscribe.GetCacheSubscribe;
import com.basic.eyflutter_core.subscribe.MMKVDeleteSubscribe;
import com.basic.eyflutter_core.subscribe.MMKVReceiveSubscribe;
import com.basic.eyflutter_core.subscribe.MMKVTakeSubscribe;
import com.basic.eyflutter_core.subscribe.NetConnectSubscribe;
import com.basic.eyflutter_core.subscribe.NetRequestSubscribe;
import com.basic.eyflutter_core.subscribe.PreferenceReceiveSubscribe;
import com.basic.eyflutter_core.utils.MmkvUtils;
import com.cloud.eyutils.CdLibConfig;
import com.cloud.eyutils.constants.LibConfigKeys;
import com.cloud.eyutils.launchs.LauncherState;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

public class EyflutterCorePlugin implements FlutterPlugin {

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        Context applicationContext = binding.getApplicationContext();
        if (LauncherState.getApplicationContext() == null) {
            LauncherState.setApplicationContext(applicationContext);
        }
        MmkvUtils.getInstance(applicationContext);
        initDb(applicationContext);
        ChannelPlugin.getInstance().register(binding.getBinaryMessenger(), ChannelMode.method);
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.networkConnectChannelName, new NetConnectSubscribe());
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.storageMethodName, new DataReceiveSubscribe());
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.sharedPreferencesMethodName, new PreferenceReceiveSubscribe());
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.mmkvMethodName, new MMKVReceiveSubscribe());
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.mmkvTakeMethodName, new MMKVTakeSubscribe());
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.mmkvDeleteMethodName, new MMKVDeleteSubscribe());
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.cacheSizeMethodName, new GetCacheSubscribe());
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.cleanCacheMethodName, new CleanCacheSubscribe());
        ChannelPlugin.getInstance().putSubscribe(ChannelConstants.netRequestMethodName, new NetRequestSubscribe());
    }

    private void initDb(Context applicationContext) {
        //添加配置
        CdLibConfig.getInstance()
                .addConfig(LibConfigKeys.basicConfigKey, new BasicConfig(applicationContext.getPackageName()))
                .addConfig(LibConfigKeys.dirBuildConfigKey, new BuildDirectoryConfig())
                .addConfig(ChannelConstants.databasePathModelKey, new DBPathsModelConfig(applicationContext));
        //初始化数据库
        DBManager.getInstance().build(applicationContext, null);
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.networkConnectChannelName);
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.storageMethodName);
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.sharedPreferencesMethodName);
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.mmkvMethodName);
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.mmkvTakeMethodName);
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.mmkvDeleteMethodName);
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.cacheSizeMethodName);
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.cleanCacheMethodName);
        ChannelPlugin.getInstance().removeSubScribe(ChannelConstants.netRequestMethodName);
        ChannelPlugin.getInstance().destroy();
    }
}
