package com.sctjsj.basemodule.core.router_service;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.sctjsj.basemodule.base.db.entity.SearchRecordTbl;

import java.util.List;

/**
 * Created by mayikang on 17/5/10.
 */

public interface ISearchRecordService extends IProvider{
    //查询所有搜索记录
     List<SearchRecordTbl> findAllRecords();

    //根据 id 查询搜索记录
    SearchRecordTbl findRecordById(int id);

    //清除所有搜索记录
    void clearAllRecord();

    //新增搜索记录
    void insertRecord(SearchRecordTbl searchRecordTbl);

}
