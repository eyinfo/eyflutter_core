package com.basic.eyflutter_core.greens;

import android.content.Context;

import com.cloud.eyutils.events.OnEntryCall;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.storage.files.StorageUtils;

import java.io.File;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-11
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class DBPathsModelConfig implements OnEntryCall {

    private Context applicationContext;

    public DBPathsModelConfig(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    class DBPathsModel extends AndroidDaoModel {

        @Override
        public String getInternal() {
            if (applicationContext == null) {
                applicationContext = LauncherState.getApplicationContext();
            }
            File databaseFile = applicationContext.getDatabasePath("1dd341e4450f44c19ddfa469aee6f931");
            return databaseFile.getAbsolutePath();
        }

        @Override
        public String getPrivacy() {
            File rootDir = StorageUtils.getRootDir();
            File file = StorageUtils.getFile(rootDir, "temporary_cache", false);
            return file.getAbsolutePath();
        }
    }

    @Override
    public Object onEntryResult() {
        return new DBPathsModel();
    }
}
