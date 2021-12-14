package com.basic.eyflutter_core.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.basic.eyflutter_core.beans.ColumnProperty;
import com.basic.eyflutter_core.daos.DaoMaster;
import com.basic.eyflutter_core.greens.DBManager;
import com.basic.eyflutter_core.greens.DBOpenHelper;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.utils.ObjectJudge;

import org.greenrobot.greendao.AbstractDaoMaster;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/4/18
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class BaseCacheDao extends CacheAbstractMethods {

    @Override
    protected AbstractDaoMaster getDaoMaster(SQLiteDatabase database) {
        return new DaoMaster(database);
    }

    public void execSql(Context applicationContext, String tagKey, String tableName, String createTableSql, ColumnProperty[] columns, String sql) {
        if (TextUtils.isEmpty(tagKey) ||
                TextUtils.isEmpty(tableName) ||
                TextUtils.isEmpty(createTableSql) ||
                ObjectJudge.isNullOrEmpty(columns) ||
                TextUtils.isEmpty(sql)) {
            return;
        }
        try {
            DBOpenHelper helper = DBManager.getInstance().getHelper(applicationContext, tagKey);
            if (helper != null) {
                SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
                sqLiteDatabase.beginTransaction();

                sqLiteDatabase.execSQL(createTableSql);

                ColumnsCheck columnsCheck = new ColumnsCheck();
                columnsCheck.addColumnIfNotExistNonTransaction(sqLiteDatabase, tagKey, tableName, columns);

                sqLiteDatabase.execSQL(sql);

                sqLiteDatabase.setTransactionSuccessful();
                sqLiteDatabase.endTransaction();
                DBManager.getInstance().close(helper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execSql(String tagKey, String tableName, String createTableSql, ColumnProperty[] columns, String sql) {
        execSql(LauncherState.getApplicationContext(), tagKey, tableName, createTableSql, columns, sql);
    }

    public Cursor query(DBOpenHelper helper, String tableName, String createTableSql, ColumnProperty[] columns, String sql) {
        if (helper == null ||
                TextUtils.isEmpty(tableName) ||
                TextUtils.isEmpty(createTableSql) ||
                ObjectJudge.isNullOrEmpty(columns) ||
                TextUtils.isEmpty(sql)) {
            return null;
        }
        try {
            SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
            sqLiteDatabase.beginTransaction();

            sqLiteDatabase.execSQL(createTableSql);

            ColumnsCheck columnsCheck = new ColumnsCheck();
            columnsCheck.addColumnIfNotExistNonTransaction(sqLiteDatabase, helper.getTagKey(), tableName, columns);

            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
            //exec sql
            Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
