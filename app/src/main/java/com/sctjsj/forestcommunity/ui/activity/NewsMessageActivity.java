package com.sctjsj.forestcommunity.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

/***
 * 资讯详情
 */
@Route(path = "/main/act/NewsMessage")
public class NewsMessageActivity extends BaseAppcompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));

    }

    @Override
    public int initLayout() {
        return R.layout.activity_news_message;
    }

    @Override
    public void reloadData() {

    }
}
