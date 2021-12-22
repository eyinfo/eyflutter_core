package com.basic.eyflutter_core.dao;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.basic.eyflutter_core.events.OnDataChainRunnable;
import com.basic.eyflutter_core.greens.DBManager;
import com.basic.eyflutter_core.greens.DBOpenHelper;
import com.cloud.eyutils.events.OnChainRunnable;
import com.cloud.eyutils.storage.files.StorageUtils;
import com.cloud.eyutils.tasks.SyncChainTasks;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.AbstractDaoSession;

import java.io.File;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-07-08
 * Description:基础抽象方法
 * Modifier:
 * ModifyContent:
 */
public abstract class CacheAbstractMethods {

    protected abstract AbstractDaoMaster getDaoMaster(SQLiteDatabase database);

    private int retryCount = 0;

    private SyncChainTasks chainTasks = SyncChainTasks.getInstance();

    private AbstractDaoSession getDaoSession(DBOpenHelper helper) {
        if (helper == null) {
            return null;
        }
        AbstractDaoSession daoSession = null;
        try {
            SQLiteDatabase database = helper.getSqLiteDatabase();
            if (database == null || database.isReadOnly()) {
                database = helper.getWritableDatabase();
                helper.setSqLiteDatabase(database);
            }
            AbstractDaoMaster daoMaster = getDaoMaster(database);
            daoSession = daoMaster.newSession();
            if (daoSession == null) {
                DBManager.getInstance().close(helper);
            }
        } catch (Exception e) {
            //配置的schemaVersion小于历史版本时会报错
            DBManager.getInstance().close(helper);
        }
        return daoSession;
    }

    private <CR extends OnChainRunnable, T> void perform(CR runnable, T extras, DBOpenHelper helper) {
        AbstractDaoSession daoSession = getDaoSession(helper);
        if (daoSession == null) {
            return;
        }
        chainTasks.addChain(runnable);
        chainTasks.build(daoSession, extras);
    }

    private <T> void addDataChain(final DBOpenHelper helper, final OnDataChainRunnable<Object, AbstractDaoSession, Object> runnable, T extras) {
        perform(new OnChainRunnable<Void, AbstractDaoSession>() {
            @Override
            public Void run(AbstractDaoSession daoSession, Object extras) {
                Object result = runnable.run(daoSession);
                runnable.complete(result, extras);
                return null;
            }

            @Override
            public void finish() {
                if (helper != null) {
                    DBManager.getInstance().close(helper);
                }
            }
        }, extras, helper);
    }

    @SuppressWarnings("unchecked")
    protected <T> void addDataChain(final OnDataChainRunnable runnable, String tagKey, String dynamicTagKey, T extras) {
        try {
            String dbkey = TextUtils.isEmpty(dynamicTagKey) ? tagKey : dynamicTagKey;
            DBOpenHelper helper = DBManager.getInstance().getHelper(dbkey);
            if (helper == null) {
                return;
            }
            addDataChain(helper, runnable, extras);
        } catch (Exception e) {
            if (retryCount <= 2) {
                //删除-journal文件
                String databasePath = DBManager.getInstance().getDatabasePath(tagKey);
                if (TextUtils.isEmpty(databasePath)) {
                    return;
                }
                databasePath = String.format("%s-journal", databasePath);
                File file = new File(databasePath);
                if (file.exists()) {
                    StorageUtils.forceDelete(file);
                }
                addDataChain(runnable, tagKey, dynamicTagKey, extras);
                retryCount++;
            }
            e.printStackTrace();
        }
    }
}
