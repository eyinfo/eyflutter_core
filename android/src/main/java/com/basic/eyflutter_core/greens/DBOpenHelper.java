package com.basic.eyflutter_core.greens;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.DatabaseOpenHelper;

import java.io.File;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-10
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class DBOpenHelper extends DatabaseOpenHelper {

    private String tagKey;
    private SQLiteDatabase sqLiteDatabase;

    public DBOpenHelper(Context context, File file, String name, int version) {
        super(new DatabaseContext(context, file), name, version);
    }

    public String getTagKey() {
        return tagKey == null ? "" : tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    public void setSqLiteDatabase(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }
}
