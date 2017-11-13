package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import butterknife.OnClick;

@Route(path = "/main/act/MoreContent")
//更多内容
public class MoreContentActivity extends BaseAppcompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
    }

    @Override
    public int initLayout() {
        return R.layout.activity_more_content;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.rel_finance, R.id.rel_study, R.id.rel_market, R.id.rel_tourism, R.id.rel_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.linear_back:
                finish();
                break;
            //金融咨询
            case R.id.rel_finance:
                Toast.makeText(this, "\"金融资讯\"正在完善中...", Toast.LENGTH_SHORT).show();
                break;
            //共享学习
            case R.id.rel_study:
                Toast.makeText(this, "\"共享学习\"正在完善中...", Toast.LENGTH_SHORT).show();
                break;
            //置换市场
            case R.id.rel_market:
                Toast.makeText(this, "\"金融资讯\"正在完善中...", Toast.LENGTH_SHORT).show();
                break;
            //携程旅游
            case R.id.rel_tourism:
                Toast.makeText(this, "\"携程旅游\"正在完善中...", Toast.LENGTH_SHORT).show();
                break;
            //e首付
            case R.id.rel_pay:
                Toast.makeText(this, "\"e首付\"正在完善中...", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
