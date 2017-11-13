package com.sctjsj.forestcommunity.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/AboutUs")
//关于我们
public class AboutUsActivity extends BaseAppcompatActivity {


    @BindView(R.id.about_us_web)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        initData();
    }

    //初始化数据
    private void initData() {
        webView.loadUrl(MainUrl.AboutUs);
        WebSettings settings=webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

    }


    @Override
    public int initLayout() {
        return R.layout.activity_about_us;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }

    @OnClick({R.id.linear_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_back:
                finish();
                break;

        }
    }
}
