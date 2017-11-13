package com.sctjsj.basemodule.core.router_service;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.sctjsj.basemodule.base.db.entity.HttpCacheTbl;

import org.xutils.DbManager;

/**
 * Created by mayikang on 17/4/9.
 */

public interface ICacheService extends IProvider{


    /**
     * 通过请求的连接获取缓存内容
     */
    public HttpCacheTbl getCacheContentByUrl(String url);


    /**
     * 保存 cache
     * @param cache
     */
    public void saveCache(HttpCacheTbl cache);



}
