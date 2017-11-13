package com.sctjsj.forestcommunity.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.base.util.setup.SetupUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.DecorationAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.DecorationBean;
import com.sctjsj.forestcommunity.javabean.PerShopBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

@Route(path = "/main/act/Decoration")
//装修建材
public class DecorationActivity extends BaseAppcompatActivity {
    @BindView(R.id.rv_decoration)
    RecyclerView rvDecoration;
    @BindView(R.id.ql_decoration)
    QRefreshLayout qlDecoration;
    @BindView(R.id.decor_layout)
    LinearLayout decorLayout;
    @BindView(R.id.dec_lay_nodata)
    LinearLayout nodata;
    private DecorationAdapter adapter;
    private List<DecorationBean> list = new ArrayList<>();
    private GridLayoutManager manager;
    private HttpServiceImpl service;
    private int pageIndex = 1;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        getDecorationData();
        initView();
    }


    private void initView() {
        manager = new GridLayoutManager(this, 3);
        adapter = new DecorationAdapter(list, this);
        rvDecoration.setLayoutManager(manager);
        rvDecoration.setAdapter(adapter);
        qlDecoration.setLoadMoreEnable(true);
        qlDecoration.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                flag = false;
                getDecorationData();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                getDecorationMoreData();

            }
        });

        adapter.setDecorCallBack(new DecorationAdapter.DecorationCallBack() {
            @Override
            public void dialogGoToCallBack(DecorationBean bean) {
                //dialog里面的去商家导航
                callMap(bean);
            }
        });
    }


    @Override
    public int initLayout() {
        return R.layout.activity_decoration;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }

    //获取装修建材列表
    private void getDecorationData() {
        list.clear();
        pageIndex = 1;
        HashMap<String, String> body = new HashMap<>();
        body.put("ctype", "store");
        body.put("cond", "{'type':2}");
        body.put("pageIndex", pageIndex + "");
        body.put("size", "12");
        body.put("jf", "storeLogo");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if (!StringUtil.isBlank(result)) {
                    Log.e("getDecorationData", result.toString());
                    try {
                        pageIndex++;
                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            JSONArray arr = object.getJSONArray("resultList");
                            if (null != arr && arr.length() > 0) {
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject decObj = arr.getJSONObject(i);
                                    DecorationBean bean = new DecorationBean();
                                    bean.setIntroduce(decObj.getString("describe"));
                                    bean.setId(decObj.getInt("id"));
                                    bean.setLatitude(decObj.getDouble("latitude"));
                                    bean.setLongitude(decObj.getDouble("longitude"));
                                    bean.setName(decObj.getString("storeName"));
                                    bean.setPhoneNumber(decObj.getString("telephone"));
                                    bean.setIconUrl(decObj.getJSONObject("storeLogo").getString("url"));
                                    list.add(bean);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if (list.size() > 0) {
                            adapter.notifyDataSetChanged();
                            qlDecoration.setVisibility(View.VISIBLE);
                            nodata.setVisibility(View.GONE);
                        } else {
                            //没有数据显示默认布局
                            qlDecoration.setVisibility(View.GONE);
                            nodata.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (flag) {
                    dismissLoading();
                }
                qlDecoration.LoadMoreComplete();
                qlDecoration.refreshComplete();

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                if (flag) {
                    showLoading(true, "加载中...");
                }
            }

            @Override
            public void onLoading(long total, long current) {

            }
        });


    }

    //获取装修建材列表
    private void getDecorationMoreData() {
        HashMap<String, String> body = new HashMap<>();
        body.put("ctype", "store");
        body.put("cond", "{'type':2}");
        body.put("pageIndex", pageIndex + "");
        body.put("size", "12");
        body.put("jf", "storeLogo");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if (!StringUtil.isBlank(result)) {
                    try {
                        pageIndex++;
                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            JSONArray arr = object.getJSONArray("resultList");
                            if (null != arr && arr.length() > 0) {
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject decObj = arr.getJSONObject(i);
                                    DecorationBean bean = new DecorationBean();
                                    bean.setIntroduce(decObj.getString("describe"));
                                    bean.setId(decObj.getInt("id"));
                                    bean.setLatitude(decObj.getDouble("latitude"));
                                    bean.setLongitude(decObj.getDouble("longitude"));
                                    bean.setName(decObj.getString("storeName"));
                                    bean.setPhoneNumber(decObj.getString("telephone"));
                                    bean.setIconUrl(decObj.getJSONObject("storeLogo").getString("url"));
                                    list.add(bean);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if (list.size() > 0) {
                            adapter.notifyDataSetChanged();
                            qlDecoration.setVisibility(View.VISIBLE);
                            nodata.setVisibility(View.GONE);
                        } else {
                            //没有数据显示默认布局
                            qlDecoration.setVisibility(View.GONE);
                            nodata.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                qlDecoration.LoadMoreComplete();
                qlDecoration.refreshComplete();

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });
    }

    //调用地图进行导航去商家
    private void callMap(DecorationBean bean) {
        String GAODE_HEAD = "amapuri://route/plan/?";
        // dev 起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
        // t = 1(公交) =2（驾车） =4(步行)
        //String GAODE_MODE ="&dev=0&t=4";
        //高德地图包名
        String GAODE_PKG = "com.autonavi.minimap";

        //检测安装和唤起
        if (SetupUtil.getInstance(DecorationActivity.this).isXSetuped(GAODE_PKG)) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setPackage("com.autonavi.minimap");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(GAODE_HEAD + "sourceApplication=forestCommunity&" + "dlat=" + bean.getLatitude() +
                    "&dlat=" + bean.getLongitude() + "&dev=0" + "&t=0"));
            startActivity(intent);
        } else {
            Snackbar.make(decorLayout, "您未安装高德地图", Snackbar.LENGTH_LONG).setAction("去下载", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://shouji.baidu.com/");
                    intent.setData(content_url);
                    startActivity(intent);
                }
            }).show();
        }
    }
}
