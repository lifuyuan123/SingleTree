package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.ui.widget.rotate_pan.LuckPanLayout;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


@Route(path = "/main/act/Guide")
//过度页
public class GuideActivity extends BaseAppcompatActivity implements LuckPanLayout.AnimationEndListener {

    @BindView(R.id.go)
    ImageView go;
    @BindView(R.id.luckpan_layout)
    LuckPanLayout luckpanLayout;
    @BindView(R.id.tv_luck_times)
    TextView tvLuckTimes;
    @BindView(R.id.marqueeView)
    MarqueeView marqueeView;
    private String[] strs;
    private List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startMain();
        initData();
        initView();
    }


    private void initData() {
        strs = getResources().getStringArray(R.array.names);
        for (int i = 0; i < 10; i++) {
            stringList.add("老司机" + i + "抽中了" + i + 2 + "台" + "iphone" + i);
        }
    }

    private void initView() {
        luckpanLayout.setAnimationEndListener(this);
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

    private void startMain() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            ARouter.getInstance().build("/main/act/MainActivity").navigation();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_guide;
    }

    @Override
    public void reloadData() {

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

    @Override
    public void endAnimation(int position) {
        Toast.makeText(this, "Position = " + position + "," + strs[position], Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.go, R.id.tv_luck_rule,R.id.tv_come_in})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //抽奖
            case R.id.go:
                luckpanLayout.rotate(-1, 100);
                break;
            //规则
            case R.id.tv_luck_rule:
                ARouter.getInstance().build("/main/act/ActionRules").navigation();
                break;
            //跳转main
            case R.id.tv_come_in:
                ARouter.getInstance().build("/main/act/MainActivity").navigation();
                finish();
                break;
        }
    }

}
