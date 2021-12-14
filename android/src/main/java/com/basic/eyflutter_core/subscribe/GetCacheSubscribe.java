package com.basic.eyflutter_core.subscribe;

import android.content.Context;

import com.basic.eyflutter_core.channel.OnDistributionSubscribe;
import com.basic.eyflutter_core.enums.DirectoryNames;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.storage.files.DirectoryUtils;
import com.cloud.eyutils.storage.files.StorageUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

public class GetCacheSubscribe extends OnDistributionSubscribe {

    @Override
    public void onSubscribe(MethodChannel.Result result, HashMap<String, Object> arguments) {
        try {
            File imagesDir = DirectoryUtils.getInstance().getDirectory(DirectoryNames.images.name());
            long imagesSize = StorageUtils.getFileOrDirSize(imagesDir);
            File cacheDir = DirectoryUtils.getInstance().getDirectory(DirectoryNames.cacheDir.name());
            long cacheSize = StorageUtils.getFileOrDirSize(cacheDir);
            File temporaryDir = DirectoryUtils.getInstance().getDirectory(DirectoryNames.temporary.name());
            long temporarySize = StorageUtils.getFileOrDirSize(temporaryDir);
            File videosDir = DirectoryUtils.getInstance().getDirectory(DirectoryNames.videos.name());
            long videosSize = StorageUtils.getFileOrDirSize(videosDir);
            Context applicationContext = LauncherState.getApplicationContext();
            File internalCacheDir = applicationContext.getCacheDir();
            long internalCacheSize = StorageUtils.getFileOrDirSize(internalCacheDir);
            long total = imagesSize + cacheSize + temporarySize + videosSize + internalCacheSize;
            result.success(getFormatSize(total));
        } catch (Exception e) {
            result.success("0MB");
        }
    }

    private String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0KB";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}
