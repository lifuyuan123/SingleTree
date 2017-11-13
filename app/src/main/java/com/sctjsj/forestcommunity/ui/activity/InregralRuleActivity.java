package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/InregralRule")
//积分规则
public class InregralRuleActivity extends BaseAppcompatActivity {

    @BindView(R.id.webview)
    WebView webview;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        initView();
    }

    private void initView() {
        webview.loadUrl(url);
        WebSettings webset = webview.getSettings();//获取设置相关
        webset.setUseWideViewPort(true);//设置此属性，可任意比例缩放 //将图片调整到适合webview的大小
        webset.setLoadWithOverviewMode(true);
        //使页面支持缩放
        webset.setJavaScriptEnabled(true);//支持js
        webset.setBuiltInZoomControls(true);
        webset.setSupportZoom(true);//支持缩放
        //如果webView中需要用户手动输入用户名、密码或其他，则webview必须设置支持获取手势焦点。
        webview.requestFocusFromTouch();
        //帮助WebView处理各种通知、请求事件的：//防止跳到其他页面，强制在webview中打开
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_inregral_rule;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }
}
