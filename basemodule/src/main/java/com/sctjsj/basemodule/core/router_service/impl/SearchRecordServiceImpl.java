package com.sctjsj.basemodule.core.router_service.impl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.SingletonManager;
import com.sctjsj.basemodule.base.db.entity.SearchRecordTbl;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.TimeUtil;
import com.sctjsj.basemodule.base.util.setup.ApplicationUtil;
import com.sctjsj.basemodule.core.config.SystemConfig;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.ISearchRecordService;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by mayikang on 17/5/10.
 */

//操作搜索记录

    @Route(path = "/basemodule/service/search_record")
public class SearchRecordServiceImpl implements ISearchRecordService{
    private final String TAG=getClass().getSimpleName().toString();

    private SystemConfig config;

    private  DbManager manager;

    @Override
    public List<SearchRecordTbl> findAllRecords() {

           if(!manager.getDatabase().isOpen()){
               manager=x.getDb(manager.getDaoConfig());
           }

        List<SearchRecordTbl> recordList=null;
        try {
            recordList=manager.selector(SearchRecordTbl.class).findAll();

        } catch (DbException e) {
            e.printStackTrace();
        }finally {
            try {
                manager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return recordList;
    }

    @Override
    public SearchRecordTbl findRecordById(int id) {
        if(!manager.getDatabase().isOpen()){
            manager=x.getDb(manager.getDaoConfig());
        }
        SearchRecordTbl record=null;
        try {
            record=manager.findById(SearchRecordTbl.class,id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        finally {
            try {
                manager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return record;
    }

    @Override
    public void clearAllRecord() {
        if(!manager.getDatabase().isOpen()){
            manager=x.getDb(manager.getDaoConfig());
        }
        try {
            manager.delete(SearchRecordTbl.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        finally {
            try {
                manager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insertRecord(SearchRecordTbl searchRecordTbl) {

        if(searchRecordTbl==null || searchRecordTbl.getContent()==null || searchRecordTbl.getContent().isEmpty()){
            return;
        }

        if(!manager.getDatabase().isOpen()){
            manager=x.getDb(manager.getDaoConfig());
        }

        try {
            if(manager.selector(SearchRecordTbl.class).where("content","=",searchRecordTbl.getContent()).count()<=0){
                manager.save(searchRecordTbl);
            }else {
                SearchRecordTbl recordTbl=manager.selector(SearchRecordTbl.class).
                        where("content","=",searchRecordTbl.getContent()).findFirst();

                recordTbl.setContent(searchRecordTbl.getContent());
                recordTbl.setInsertTime(TimeUtil.getTimestamp());
                manager.update(recordTbl,"content");
                manager.update(recordTbl,"insert_time");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        finally {
            try {
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
