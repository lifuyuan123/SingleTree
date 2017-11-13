package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.LuckAdapter;
import com.sctjsj.forestcommunity.javabean.LuckBean;
import com.sctjsj.forestcommunity.ui.widget.rotate_pan.LuckPanLayout;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/user/LuckDraw")
//抽奖
public class LuckDrawActivity extends BaseAppcompatActivity implements LuckPanLayout.AnimationEndListener {

    @BindView(R.id.tv_luck_times)
    TextView tvLuckTimes;

    @BindView(R.id.luckpan_layout)
    LuckPanLayout luckPanLayout;
    @BindView(R.id.marqueeView)
    MarqueeView marqueeView;

    private String[] strs;
    private List<String> stringList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strs = getResources().getStringArray(R.array.names);
        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            stringList.add("老司机" + i+"抽中了"+i + 2+"台"+"iphone" + i);
        }
    }

    public void rotation(View view) {
        luckPanLayout.rotate(-1, 100);
    }

    private void initView() {
        luckPanLayout.setAnimationEndListener(this);
        marqueeView.startWithList(stringList);
        // 在代码里设置自己的动画
        marqueeView.startWithList(stringList, R.anim.anim_bottom_in, R.anim.anim_top_out);
        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                Toast.makeText(getApplicationContext(), String.valueOf(marqueeView.getPosition()) + ". " + textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_luck_draw;
    }

    @Override
    public void reloadData() {

    }

    //规则
    @OnClick(R.id.tv_luck_rule)
    public void onViewClicked() {
        ARouter.getInstance().build("/main/act/ActionRules").navigation();
    }

    @Override
    public void endAnimation(int position) {
        Toast.makeText(this, "Position = " + position + "," + strs[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        marqueeView.startFlipping();
    }

    @Override
    public void onStop() {
        super.onStop();
        marqueeView.stopFlipping();
    }
}
