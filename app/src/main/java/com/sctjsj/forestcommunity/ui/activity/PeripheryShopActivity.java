package com.sctjsj.forestcommunity.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.CallUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.base.util.setup.SetupUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.PeripheryShopAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.PerShopBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.ui.widget.dialog.ShopPhoneDialog;

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

/*
 *周边商家页面
 */
@Route(path = "/main/act/PeripheryShop")
public class PeripheryShopActivity extends BaseAppcompatActivity {

    @BindView(R.id.periphery_shop_back)
    RelativeLayout peripheryShopBack;
    @BindView(R.id.periphery_shop_rv)
    RecyclerView peripheryShopRv;
    @BindView(R.id.activity_periphery_shop)
    LinearLayout activityPeripheryShop;
    @BindView(R.id.periphery_shop_qr)
    QRefreshLayout peripheryShopQr;
    @BindView(R.id.per_layout)
    LinearLayout perLayout;
    @BindView(R.id.per_lay_nodata)
    LinearLayout perLayNodata;


    private ShopPhoneDialog dialog;

    private PeripheryShopAdapter adapter;

    private HttpServiceImpl service;

    private int pageIndex = 1;

    private List<PerShopBean> data = new ArrayList<>();

    private boolean flag = true;

    private String lon = "";
    private String lan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        initView();
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getShopList();
    }


    //初始化View
    private void initView() {
        adapter = new PeripheryShopAdapter(data, this);
        peripheryShopRv.setLayoutManager(new LinearLayoutManager(this));
        peripheryShopRv.setAdapter(adapter);
        dialog = new ShopPhoneDialog(this, R.style.All_dialog);
        setListener();
    }

    //设置其他的监听
    private void setListener() {
        adapter.setAdapterCallBack(new PeripheryShopAdapter.PerShoCallBack() {
            @Override
            public void callPhoneClick(final String phone) {//点击查看商家电话
                dialog.setPhone(phone);
                dialog.setCallBack(new ShopPhoneDialog.CallBack() {
                    @Override
                    public void click() {
                        CallUtil.makeCall(PeripheryShopActivity.this,phone.replaceAll("-",""));
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

            @Override
            public void gotoStoreClick(PerShopBean bean) {//点击使用地图导航
                if (null != bean) {
                    callMap(bean);
                }

            }
        });

        peripheryShopQr.setLoadMoreEnable(true);
        peripheryShopQr.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                //下拉刷新
                flag = false;
                getData();

            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                //上拉加载
                getMoerData();

            }
        });

    }


    //调用地图进行导航去商家
    private void callMap(PerShopBean bean) {
        String GAODE_HEAD = "amapuri://route/plan/?";
        // dev 起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
        // t = 1(公交) =2（驾车） =4(步行)
        //String GAODE_MODE ="&dev=0&t=4";
        //高德地图包名
        String GAODE_PKG = "com.autonavi.minimap";

        //检测安装和唤起
        if (SetupUtil.getInstance(PeripheryShopActivity.this).isXSetuped(GAODE_PKG)) {
            goToNaviActivity(this,"ForestCommunity",null,bean.getLatitude()+"", bean.getLongitude()+"","1","2");
        } else {
            Snackbar.make(activityPeripheryShop, "您未安装高德地图", Snackbar.LENGTH_LONG).setAction("去下载", new View.OnClickListener() {
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

    @Override
    public int initLayout() {
        return R.layout.activity_periphery_shop;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.periphery_shop_back)
    public void onViewClicked() {
        finish();
    }

    //获取周边商家列表
    private void getShopList() {
        lon = (String) SPFUtil.get(Tag.TAG_LONGITUDE, "null");
        lan = (String) SPFUtil.get(Tag.TAG_LATITUDE, "null");
        if (!lon.equals("null") && !lan.equals("null")) {
            getData();
        } else {
            //定位失败
            perLayout.setVisibility(View.GONE);
            perLayNodata.setVisibility(View.VISIBLE);
        }
    }


    //获取数据
    private void getData() {
        data.clear();
        pageIndex = 1;
        HashMap<String, String> body = new HashMap<>();
        //获取经纬度成功
        body.put("lng", lon);
        body.put("lat", lan);
        body.put("jf", "storeLogo");
        body.put("pageIndex", pageIndex + "");
        service.doCommonPost(null, MainUrl.PeripheryShopUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if (!StringUtil.isBlank(result)) {
                    Log.e("perData",result.toString());
                    try {
                        pageIndex++;
                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            //获取数据成功
                            JSONArray list = object.getJSONArray("resultList");
                            if (null != list && list.length() > 0) {
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject store = list.getJSONObject(i);
                                    PerShopBean bean = new PerShopBean();
                                    bean.setDescribe(store.getString("describe"));
                                    bean.setId(store.getInt("id"));
                                    bean.setIsRecommend(store.getInt("isRecommend"));
                                    bean.setLatitude(store.getDouble("latitude"));
                                    bean.setLongitude(store.getDouble("longitude"));
                                    bean.setStoreAddress(store.getString("storeAddress"));
                                    bean.setStoreName(store.getString("storeName"));
                                    bean.setTelephone(store.getString("telephone"));
                                    bean.setStoreLogo(store.getJSONObject("storeLogo").getString("url"));
                                    data.add(bean);
                                }
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("perData",e.toString());

                    } finally {
                        if (data.size() > 0) {
                            adapter.notifyDataSetChanged();
                            perLayout.setVisibility(View.VISIBLE);
                            perLayNodata.setVisibility(View.GONE);
                        } else {
                            //没有数据的默认布局
                            perLayout.setVisibility(View.GONE);
                            perLayNodata.setVisibility(View.VISIBLE);

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
                peripheryShopQr.refreshComplete();
                peripheryShopQr.LoadMoreComplete();

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


    //获取数据
    private void getMoerData() {
        HashMap<String, String> body = new HashMap<>();
        //获取经纬度成功
        body.put("lng", lon);
        body.put("lat", lan);
        body.put("jf", "storeLogo");
        body.put("pageIndex", pageIndex + "");
        service.doCommonPost(null, MainUrl.PeripheryShopUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if (!StringUtil.isBlank(result)) {
                    try {
                        pageIndex++;
                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            //获取数据成功
                            JSONArray list = object.getJSONArray("resultList");
                            if (null != list && list.length() > 0) {
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject store = list.getJSONObject(i);
                                    PerShopBean bean = new PerShopBean();
                                    bean.setDescribe(store.getString("describe"));
                                    bean.setId(store.getInt("id"));
                                    bean.setIsRecommend(store.getInt("isRecommend"));
                                    bean.setLatitude(store.getDouble("latitude"));
                                    bean.setLongitude(store.getDouble("longitude"));
                                    bean.setStoreAddress(store.getString("storeAddress"));
                                    bean.setStoreName(store.getString("storeName"));
                                    bean.setTelephone(store.getString("telephone"));
                                    bean.setStoreLogo(store.getJSONObject("storeLogo").getString("url"));
                                    data.add(bean);
                                }
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if (data.size() > 0) {
                            adapter.notifyDataSetChanged();
                            perLayout.setVisibility(View.VISIBLE);
                            perLayNodata.setVisibility(View.GONE);
                        } else {
                            //没有数据的默认布局
                            perLayout.setVisibility(View.GONE);
                            perLayNodata.setVisibility(View.VISIBLE);

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
                peripheryShopQr.refreshComplete();
                peripheryShopQr.LoadMoreComplete();
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

    public void goToNaviActivity(Context context, String sourceApplication, String poiname, String lat, String lon, String dev, String style) {
        StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
                .append(sourceApplication);
        if (!TextUtils.isEmpty(poiname)) {
            stringBuffer.append("&poiname=").append(poiname);
        }
        stringBuffer.append("&lat=").append(lat)
                .append("&lon=").append(lon)
                .append("&dev=").append(dev)
                .append("&style=").append(style)
                .append("&t=2");
        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);
    }


}
