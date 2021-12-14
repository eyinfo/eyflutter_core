package com.basic.eyflutter_core.subscribe;

import android.content.Context;

import com.basic.eyflutter_core.channel.OnDistributionSubscribe;
import com.basic.eyflutter_core.enums.DirectoryNames;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.storage.files.DirectoryUtils;
import com.cloud.eyutils.storage.files.StorageUtils;

import java.io.File;
import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

public class CleanCacheSubscribe extends OnDistributionSubscribe {
    @Override
    public void onSubscribe(MethodChannel.Result result, HashMap<String, Object> arguments) {
        try {
            File imagesDir = DirectoryUtils.getInstance().getDirectory(DirectoryNames.images.name());
            StorageUtils.cleanDirectory(imagesDir);
            File cacheDir = DirectoryUtils.getInstance().getDirectory(DirectoryNames.cacheDir.name());
            StorageUtils.cleanDirectory(cacheDir);
            File temporaryDir = DirectoryUtils.getInstance().getDirectory(DirectoryNames.temporary.name());
            StorageUtils.cleanDirectory(temporaryDir);
            File videosDir = DirectoryUtils.getInstance().getDirectory(DirectoryNames.videos.name());
            StorageUtils.cleanDirectory(videosDir);
            Context applicationContext = LauncherState.getApplicationContext();
            File internalCacheDir = applicationContext.getCacheDir();
            StorageUtils.cleanDirectory(internalCacheDir);
            result.success(true);
        } catch (Exception e) {
            result.success(false);
        }
    }
}
