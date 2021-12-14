package com.basic.eyflutter_core.greens;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.cloud.eyutils.utils.ObjectJudge;

import java.io.File;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2018/8/7
 * @Description:数据上下文,在初始化时传入
 * @Modifier:
 * @ModifyContent:
 */
public class DatabaseContext extends ContextWrapper {

    private File dbFile;

    public DatabaseContext(Context base, File dbFile) {
        super(base);
        //数据库文件
        this.dbFile = dbFile;
    }

    @Override
    public File getDatabasePath(String name) {
        return dbFile;
    }

    private SQLiteDatabase getDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        String[] databaseList = this.databaseList();
        File databasePath = getDatabasePath(name);
        String path = databasePath.getPath();
        if (ObjectJudge.isContains(name, databaseList)) {
            return SQLiteDatabase.openDatabase(path, factory, mode);
        }
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return getDatabase(name, mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return getDatabase(name, mode, factory);
    }
}
