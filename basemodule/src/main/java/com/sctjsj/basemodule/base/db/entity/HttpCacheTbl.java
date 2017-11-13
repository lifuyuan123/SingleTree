package com.sctjsj.basemodule.base.db.entity;

/**
 * Created by mayikang on 17/4/9.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Http请求后的数据缓存实体
 */
@Table(name = "sm_http_cache_tbl")
public class HttpCacheTbl {
    //id 主键 自增 从1开始
    @Column(name = "id",isId = true,property = "not null")
    private int id;

    //请求的连接 非空约束 唯一约束
    @Column(name = "url",property = "unique")
    private String url;

    //请求结果
    @Column(name = "content")
    private String content;

    //插入时间
    @Column(name = "insert_time")
    private long insertTime;

    //更新时间
    @Column(name = "update_time")
    private long updateTime;

    //最大有效期
    @Column(name = "max_age")
    private long maxAge;

    public HttpCacheTbl(){

    }

    public HttpCacheTbl(int id){
        this.id=id;
    }

    public HttpCacheTbl(String url, String content) {
        this.url = url;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "HttpCacheTbl{" +
                "content='" + content + '\'' +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", insertTime='" + insertTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", maxAge='" + maxAge + '\'' +
                '}';
    }
}
