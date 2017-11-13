package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.FroumSortAdapter;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/***
 * 选择论坛的分类
 */
@Route(path = "/main/act/FroumSort")
public class FroumSortActivity extends BaseAppcompatActivity {

    @BindView(R.id.froum_sort_back)
    RelativeLayout froumSortBack;
    @BindView(R.id.froum_sort_rv)
    RecyclerView froumSortRv;
    @BindView(R.id.activity_froum_sort)
    LinearLayout activityFroumSort;
    private FroumSortAdapter adapter;
    private ArrayList<HashMap<String,Object>> data=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        initView();

    }

    //初始化View
    private void initView() {
        HashMap<String,Object> s=new HashMap<>();
        data.add(s);
        data.add(s);
        data.add(s);
        data.add(s);
        data.add(s);
        adapter=new FroumSortAdapter(data,this);
        froumSortRv.setLayoutManager(new GridLayoutManager(this,2));
        froumSortRv.setAdapter(adapter);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_froum_sort;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.froum_sort_back)
    public void onViewClicked() {
        finish();
    }
}
