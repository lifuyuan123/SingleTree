package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/NewsDetails")
//新闻详情页
public class NewsDetailsActivity extends BaseAppcompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.webview)
    WebView webview;
    String url;
    @Autowired(name = "id")
    int id;
    @Autowired(name = "title")
    String title;

    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();
        upData();//上传足迹
    }

    private void initView() {
        tvTitle.setText(title);
        url= MainUrl.InformationWeb+"?id="+id+"&url=news_detail";
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
        return R.layout.activity_news_details;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }


    //上传足迹
    private void upData() {
        HashMap<String,String> map=new HashMap<>();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        map.put("data","{tuser:{id:"+ UserAuthUtil.getUserId()+"},type:2,tid:"+id+",skimTime:\""+time+"\"}");
        map.put("type","2");
        map.put("tid",id+"");
        map.put("title",title);
        service.doCommonPost(null, MainUrl.saveFootMark, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("savafootonSuccess",result.toString());
                try {
                    JSONObject object=new JSONObject(result);
                    boolean results=object.getBoolean("result");
                    String msg=object.getString("msg");
                    if(results){
                        LogUtil.e("savafoot",msg);
                    }else {
                        LogUtil.e("savafoot",msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e("savafootJSONException",e.toString());
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("savafootonError",ex.toString());
            }
            @Override
            public void onCancelled(Callback.CancelledException cex) {}
            @Override
            public void onFinished() {}
            @Override
            public void onWaiting() {}
            @Override
            public void onStarted() {}
            @Override
            public void onLoading(long total, long current) {}
        });
    }
}
