package com.basic.eyflutter_core.greens;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;

import com.basic.eyflutter_core.ChannelConstants;
import com.basic.eyflutter_core.annotations.ProjectDBPath;
import com.cloud.eyutils.CdLibConfig;
import com.cloud.eyutils.beans.BasicConfigItem;
import com.cloud.eyutils.events.Action0;
import com.cloud.eyutils.events.Func2;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.observable.ObservableComponent;
import com.cloud.eyutils.storage.files.StorageUtils;
import com.cloud.eyutils.utils.ConvertUtils;
import com.cloud.eyutils.utils.GlobalUtils;
import com.cloud.eyutils.utils.ObjectJudge;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2017/11/14
 * Description:数据库管理类
 * Modifier:
 * ModifyContent:
 */
public class DBManager {

    private static DBManager dbManager = null;
    private HashMap<String, String> dbpathmap = new HashMap<>();
    private HashMap<String, DBOpenHelper> helperMap = new HashMap<>();
    private long helperCreateTime;

    private DBManager() {
        //init
    }

    public static DBManager getInstance() {
        if (dbManager == null) {
            synchronized (DBManager.class) {
                if (dbManager == null) {
                    dbManager = new DBManager();
                }
            }
        }
        return dbManager;
    }

    private Object getDatabasePathsModel() {
        Object model = CdLibConfig.getInstance().getConfigValue(ChannelConstants.databasePathModelKey);
        return model;
    }

    /**
     * 1.数据库创建;
     * 2.数据表模型创建及优化;
     * 3.可以在启动页面处理，其它流程在回调函数之后处理;
     *
     * @param applicationContext application context
     * @param complete           完成回调
     */
    public void build(Context applicationContext, Action0 complete) {
        Object pathsModel = getDatabasePathsModel();
        if (pathsModel == null) {
            if (complete != null) {
                complete.call();
            }
            return;
        }
        ObservableComponent<Void, Object> component = new ObservableComponent<Void, Object>() {
            @Override
            protected Void subscribeWith(Object... params) throws Exception {
                if (params[0] == null) {
                    return null;
                }
                //根据注解过滤有效路径
                Object model = params[0];
                getDbPath(model.getClass(), model);
                for (Map.Entry<String, String> entry : dbpathmap.entrySet()) {
                    DBOpenHelper helper = buildHelper((Context) params[2], entry.getValue());
                    helper.setTagKey(entry.getKey());
                    helperMap.put(entry.getKey(), helper);
                }
                return super.subscribeWith(params);
            }

            @Override
            protected void completeWith(boolean isError, Throwable throwable, Object... params) {
                if (params[1] instanceof Action0) {
                    Action0 complete = (Action0) params[1];
                    complete.call();
                }
            }
        };
        component.build(pathsModel, complete, applicationContext);
    }

    /**
     * 1.数据库创建;
     * 2.数据表模型创建及优化;
     * 3.可以在启动页面处理，其它流程在回调函数之后处理;
     *
     * @param complete 完成回调
     */
    public void build(Action0 complete) {
        build(null, complete);
    }

    private boolean isDestoryHelper(DBOpenHelper helper) {
        long time = System.currentTimeMillis();
        if (helperCreateTime == 0) {
            helperCreateTime = time;
            helper.setSqLiteDatabase(null);
            return false;
        }
        long diff = time - helperCreateTime;
        helperCreateTime = time;
        if ((diff / 1000) >= 10) {
            return true;
        }
        helper.setSqLiteDatabase(null);
        return false;
    }

    /**
     * 关闭数据库(在读取或写入完成后调用)
     *
     * @param helper helper
     */
    public void close(DBOpenHelper helper) {
        try {
            if (helper == null) {
                return;
            }
            String tagKey = helper.getTagKey();
            if (isDestoryHelper(helper)) {
                helper.close();
                if (helperMap.containsKey(tagKey)) {
                    helperMap.remove(tagKey);
                }
                helper = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sqlite对象
     *
     * @param tagKey {@link AndroidDaoModel}字段名
     * @return RxSqliteOpenHelper
     */
    public synchronized DBOpenHelper getHelper(Context applicationContext, String tagKey) {
        Func2<DBOpenHelper, String, Context> helperFun = (tagKey1, context) -> {
            String path = getDatabasePath(tagKey1);
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            DBOpenHelper helper = buildHelper(context, path);
            helper.setTagKey(tagKey1);
            helper.setSqLiteDatabase(helper.getWritableDatabase());
            return helper;
        };
        DBOpenHelper openHelper = helperMap.get(tagKey);
        if (openHelper == null || isDestoryHelper(openHelper)) {
            if (openHelper != null) {
                openHelper.close();
            }
            DBOpenHelper helper = helperFun.call(tagKey, applicationContext);
            if (helper == null) {
                return null;
            }
            helperMap.put(tagKey, helper);
            return helper;
        }
        SQLiteDatabase sqLiteDatabase = openHelper.getSqLiteDatabase();
        if (sqLiteDatabase != null && sqLiteDatabase.isReadOnly()) {
            //使用时创建
            openHelper.setSqLiteDatabase(null);
        }
        return openHelper;
    }

    public synchronized DBOpenHelper getHelper(String tagKey) {
        return getHelper(LauncherState.getApplicationContext(), tagKey);
    }

    private synchronized DBOpenHelper buildHelper(Context applicationContext, String dbpath) {
        if (applicationContext == null) {
            applicationContext = LauncherState.getApplicationContext();
        }
        File file = StorageUtils.getFile(dbpath, false);
        String fileName = file.getName();
        BasicConfigItem basicConfigItem = CdLibConfig.getInstance().getBasicConfigItem();
        String packgeName = basicConfigItem.getProjectPackgeName();
        Object schemaVersion = GlobalUtils.getBuildConfigValue(String.format("%s.BuildConfig", packgeName), "schemaVersion");
        int version = ConvertUtils.toInt(schemaVersion);
        DBOpenHelper helper = new DBOpenHelper(applicationContext, file, fileName, version > 0 ? version : 1);
        return helper;
    }

    /**
     * 获取数据路径
     *
     * @param tag 标识路径,AndroidDaoModel模型中的字段名
     * @return database path
     */
    public String getDatabasePath(String tag) {
        if (ObjectJudge.isNullOrEmpty(dbpathmap)) {
            Object model = getDatabasePathsModel();
            if (model == null) {
                return "";
            }
            getDbPath(model.getClass(), model);
        }
        String path = dbpathmap.get(tag);
        return path == null ? "" : path;
    }

    //key:field name
    //value:DbPathInfo
    private void getDbPath(Class configModelClass, Object model) {
        Field[] fields = configModelClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(ProjectDBPath.class)) {
                continue;
            }
            String path = getCompletePath(model, field);
            if (TextUtils.isEmpty(path)) {
                continue;
            }
            dbpathmap.put(field.getName(), path);
        }
        String simpleName = configModelClass.getSimpleName();
        if (!TextUtils.equals(simpleName, "AndroidDaoModel") && !TextUtils.equals(simpleName, "Object")) {
            Class<?> modelClass = configModelClass.getSuperclass();
            getDbPath(modelClass, model);
        }
    }

    private ProjectDBPath getDBPathAnno(Field field) {
        if (Build.VERSION.SDK_INT >= 24) {
            return field.getDeclaredAnnotation(ProjectDBPath.class);
        } else {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ProjectDBPath) {
                    return (ProjectDBPath) annotation;
                }
            }
            return null;
        }
    }

    private String getCompletePath(Object configModel, Field field) {
        ProjectDBPath annotation = getDBPathAnno(field);
        if (annotation != null) {
            String fieldName = field.getName();
            Object value = GlobalUtils.getPropertiesValue(configModel, fieldName);
            if (value == null) {
                return "";
            }
            String path = String.valueOf(value);
            //判断路径是否有效
            File file = StorageUtils.getFile(path, false);
            if (!file.exists()) {
                try {
                    String parent = file.getParent();
                    File dir = new File(parent);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    return "";
                }
            }
            return path;
        }
        return "";
    }
}