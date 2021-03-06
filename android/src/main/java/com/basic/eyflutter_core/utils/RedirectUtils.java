package com.basic.eyflutter_core.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

/**
 * @Author lijinghuan
 * @Email: ljh0576123@163.com
 * @CreateTime:2016/4/1 18:54
 * @Description:对象跳转工具类
 * @Modifier:
 * @ModifyContent:
 */
public class RedirectUtils extends BaseRedirectUtils {

    /**
     * 打开App设置页面
     *
     * @param context 上下文
     */
    public static void startAppSettings(Context applicationContext) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", applicationContext.getPackageName(), null);
        intent.setData(uri);
        applicationContext.startActivity(intent);
    }

    /**
     * 启动通话界面
     *
     * @param activity    上下文
     * @param phonenumber 电话号码
     */
    public static void startTel(FragmentActivity activity, final String phonenumber) {
        Intent intent = null;
        if (TextUtils.isEmpty(phonenumber)) {
            return;
        }
        if (phonenumber.contains("tel")) {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse(phonenumber));
        } else {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format(
                    "tel:%s", phonenumber)));
        }
        activity.startActivity(intent);
    }

    /**
     * 启动发送短信界面
     *
     * @param activity    上下文
     * @param phonenumber 电话号码
     */
    public static void startSms(FragmentActivity activity, final String phonenumber) {
        Intent intent = null;
        if (TextUtils.isEmpty(phonenumber)) {
            return;
        }
        if (phonenumber.contains("sms") || phonenumber.contains("smsto")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(phonenumber));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(
                    "smsto:%s", phonenumber)));
        }
        activity.startActivity(intent);
    }

    /**
     * 启动桌面
     *
     * @param context 上下文
     */
    public static void startHome(Context context) {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(home);
    }

    /**
     * 判断某个Activity是否存在
     *
     * @param context     上下文
     * @param packageName 包名
     * @param className   要判断activity的类名(全路径)
     * @return true:存在;false:不存在;
     */
    public static boolean isActivityExist(Context context,
                                          String packageName,
                                          String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        if (context.getPackageManager().resolveActivity(intent, 0) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 安装apk
     *
     * @param context context
     * @param apkFile apk文件
     */
    public static void installApk(Context context, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            // android4.0以前可以出现安装成功界面,
            // 但在4.0或以后版本不加FLAG_ACTIVITY_NEW_TASK则不会出现安装完成界面;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用通知界面
     *
     * @param context context
     */
    public static void startAppNotication(Context context) {
        if (context == null) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        String packageName = applicationContext.getPackageName();
        Intent notificationIntent = new Intent();
        //android 8.0引导
        if (Build.VERSION.SDK_INT >= 26) {
            notificationIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            notificationIntent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
        }
        //android 5.0-7.0
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 26) {
            ApplicationInfo applicationInfo = applicationContext.getApplicationInfo();
            notificationIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            notificationIntent.putExtra("app_package", packageName);
            notificationIntent.putExtra("app_uid", applicationInfo.uid);
        }
        //其他
        if (Build.VERSION.SDK_INT < 21) {
            notificationIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            notificationIntent.setData(Uri.fromParts("package", packageName, null));
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(notificationIntent);
    }
}
