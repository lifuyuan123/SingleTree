package com.sctjsj.basemodule.core.router_service.impl;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.SingletonManager;
import com.sctjsj.basemodule.base.db.entity.HttpCacheTbl;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.TimeUtil;
import com.sctjsj.basemodule.base.util.setup.ApplicationUtil;
import com.sctjsj.basemodule.core.config.SystemConfig;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.ICacheService;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by mayikang on 17/4/9.
 */

/**
 * 基于 XUtils3 DB 模块封装的Http 请求缓存service
 */
@Route(path = "/basemodule/service/cache")
public class CacheServiceImpl implements ICacheService {

    private final String TAG=getClass().getSimpleName().toString();

    private SystemConfig config;

    private DbManager manager;


    @Override
    public HttpCacheTbl getCacheContentByUrl(String url) {
        if(!manager.getDatabase().isOpen()){
            manager=x.getDb(manager.getDaoConfig());
        }
        try {
            List<HttpCacheTbl> caches=manager.selector(HttpCacheTbl.class)
                    .findAll();

            if(caches!=null && caches.size()>0){
                return caches.get(0);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
            finally {
                try {
                    //关闭数据库
                    manager.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        /**
         * 准备接收空指针
         */
        return null;
    }

    /**
     * 保存或者跟新缓存，根据 id 匹配
     * @param cache
     */
    @Override
    public void saveCache(HttpCacheTbl cache) {

        if(cache==null ||cache.getUrl()==null || cache.getUrl().isEmpty() ||cache.getContent()==null){
            return;
        }

        try {
            if(!manager.getDatabase().isOpen()){
                manager=x.getDb(manager.getDaoConfig());
            }
            //如果数据库中尚未存在该 url 的缓存记录，则直接插入该缓存
            if(manager.selector(HttpCacheTbl.class).where("url","=",cache.getUrl()).count()<=0){
                manager.save(cache);
            } else {
                //已有数据，直接更新 content 和 update_time
                HttpCacheTbl tempCache = manager.selector(HttpCacheTbl.class).findFirst();
                tempCache.setContent(cache.getContent());
                tempCache.setUpdateTime(TimeUtil.getTimestamp());
                //更新内容
                manager.update(tempCache,"content");
                //更新修改时间
                manager.update(tempCache,"update_time");
            }

        } catch (DbException e) {
            e.printStackTrace();
            try {
                Toast.makeText(config.getContext(), "保存或更新缓存失败", Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        finally {
            try {
                //关闭数据库
                manager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void init(Context context) {
        //获取到系统配置
        Object obj= SingletonManager.INSTANCE.getObj(Tag.TAG_SINGLETON_SYSTEM_CONFIG_HOLDER);
        if(obj instanceof SystemConfig){
            config=(SystemConfig) obj;
        }

        try {
            DbManager.DaoConfig daoConfig=new DbManager.DaoConfig()
                    //设置数据库名
                    .setDbName(config.getDB_NAME())
                    //设置数据库路径
                    .setDbDir(new File(config.getDB_DIR()))
                    //设置数据库版本
                    .setDbVersion(ApplicationUtil.getLocalVersion(config.getContext()))
                    //设置开启事务
                    .setAllowTransaction(true)
                    //监听数据库打开
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            LogUtil.e(TAG,"数据库开启");
                        }
                    })
                    //监听数据库版本升级
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            LogUtil.e(TAG,"数据库版本升级："+oldVersion+"-->"+newVersion);
                            db.getDaoConfig().setDbVersion(newVersion);
                        }
                    })
                    .setTableCreateListener(new DbManager.TableCreateListener() {
                        @Override
                        public void onTableCreated(DbManager db, TableEntity<?> table) {
                            LogUtil.e(TAG,"数据表创建："+table.getName());
                        }
                    });

            manager= x.getDb(daoConfig);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG,"DBManager 创建失败");
        }

    }
}
