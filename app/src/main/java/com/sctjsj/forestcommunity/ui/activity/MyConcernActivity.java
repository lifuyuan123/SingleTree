package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.ConcernAdapter;
import com.sctjsj.forestcommunity.javabean.ConcernBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

@Route(path = "/main/act/user/MyConcern")
//我的关注
public class MyConcernActivity extends BaseAppcompatActivity {

    @BindView(R.id.rv_concern)
    RecyclerView rvConcern;
    @BindView(R.id.ql_concern)
    QRefreshLayout qlConcern;

    private ConcernAdapter adapter;
    private LinearLayoutManager manager;
    private List<ConcernBean> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        initData();
        initView();
    }

    private void initData() {

        for (int i = 0; i < 3; i++) {
            ConcernBean bean=new ConcernBean();
            bean.setContent("我是描述我是描述我是描述"+i);
            bean.setName("我是姓名"+i);
            bean.setIconUrl("ddd");
            list.add(bean);
        }

    }

    private void initView() {
        adapter=new ConcernAdapter(this,list);
        manager=new LinearLayoutManager(this);
        rvConcern.setLayoutManager(manager);
        rvConcern.setAdapter(adapter);
        qlConcern.setLoadMoreEnable(true);
        qlConcern.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                qlConcern.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                 qlConcern.LoadMoreComplete();
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_my_concern;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }
}
