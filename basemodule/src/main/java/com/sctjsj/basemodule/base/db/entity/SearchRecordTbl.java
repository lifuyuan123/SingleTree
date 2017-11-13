package com.sctjsj.basemodule.base.db.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by mayikang on 17/5/10.
 */

/**
 * 搜索记录表
 */

@Table(name = "sm_search_record_tbl")
public class SearchRecordTbl {

    //id 主键 自增 从1开始
    @Column(name = "id",isId = true,property = "not null")
    private int id;

    //搜索的内容 非空约束 唯一约束
    @Column(name = "content",property = "unique")
    private String content;

    //插入时间
    @Column(name = "insert_time")
    private long insertTime;

    public SearchRecordTbl(){

    }

    public SearchRecordTbl(int id) {
        this.id = id;
    }

    public SearchRecordTbl(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public SearchRecordTbl(String content, long insertTime) {
        this.content = content;
        this.insertTime = insertTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "SearchRecordTbl{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", insertTime=" + insertTime +
                '}';
    }
}
