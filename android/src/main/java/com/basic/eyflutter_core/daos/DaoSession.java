package com.basic.eyflutter_core.daos;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.basic.eyflutter_core.beans.CacheDataItem;

import com.basic.eyflutter_core.daos.CacheDataItemDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig cacheDataItemDaoConfig;

    private final CacheDataItemDao cacheDataItemDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        cacheDataItemDaoConfig = daoConfigMap.get(CacheDataItemDao.class).clone();
        cacheDataItemDaoConfig.initIdentityScope(type);

        cacheDataItemDao = new CacheDataItemDao(cacheDataItemDaoConfig, this);

        registerDao(CacheDataItem.class, cacheDataItemDao);
    }
    
    public void clear() {
        cacheDataItemDaoConfig.clearIdentityScope();
    }

    public CacheDataItemDao getCacheDataItemDao() {
        return cacheDataItemDao;
    }

}
