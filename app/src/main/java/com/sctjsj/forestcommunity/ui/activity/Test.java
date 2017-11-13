package com.sctjsj.forestcommunity.ui.activity;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.VerticalUltraPagerAdapter;
import com.tmall.ultraviewpager.UltraViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;



public class Test extends BaseAppcompatActivity {

    @BindView(R.id.tv)TextView tv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
    }

    @Override
    public int initLayout() {
        return R.layout.activity_test;
    }

    @Override
    public void reloadData() {

    }
}
