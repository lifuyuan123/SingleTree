package com.sctjsj.forestcommunity.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.base.util.setup.SetupUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.ShopDetailMultiAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.DiscountsBean;
import com.sctjsj.forestcommunity.javabean.PerShopBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/***
 * 商家详情页面
 */
@Route(path = "/main/act/ShopDetail")
public class ShopDetailActivity extends BaseAppcompatActivity {

    @BindView(R.id.shop_detali_back)
    RelativeLayout shopDetaliBack;
    @BindView(R.id.shop_detali_rv)
    RecyclerView shopDetaliRv;
    @BindView(R.id.shop_detali_qrf)
    QRefreshLayout shopDetaliQrf;
    @BindView(R.id.activity_shop_detail)
    LinearLayout activityShopDetail;
    private boolean flag=true;

    @Autowired(name = "storeId")
    int id=0;

    private HttpServiceImpl service;

    private ShopDetailMultiAdapter Type_a, Type_b, Type_c, Type_d, Type_e, Type_f;//子适配器
    private ArrayList<DelegateAdapter.Adapter> adapterList = new ArrayList<>();
    private DelegateAdapter adapter;//代理适配器

    private ArrayList<HashMap<String, Object>> shopTypeAData = new ArrayList<>();//商家信息数据
    private ArrayList<HashMap<String, Object>> shopTypeCData = new ArrayList<>();//商家优惠信息
    private ArrayList<HashMap<String, Object>> shopTypeEData = new ArrayList<>();//商家介绍数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();

    }

    //初始化控件
    private void initView() {
        setListener();
        initData();
        getShopMessage();
    }

    //初始化数据
    private void initData() {
        //虚拟适配器
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
        shopDetaliRv.setLayoutManager(layoutManager);

        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        shopDetaliRv.setRecycledViewPool(viewPool);

        viewPool.setMaxRecycledViews(6, 10);

        initTypeAHelper();
        initTypeBHelper();
        initTypeCHelper();
        initTypeDHelper();
        initTypeEHelper();


        adapter = new DelegateAdapter(layoutManager, false);
        adapter.setAdapters(adapterList);
        shopDetaliRv.setAdapter(adapter);

        Type_a.setCallBack(new ShopDetailMultiAdapter.ShopDetailCallBack() {

            @Override
            public void gotoLayoutClick(PerShopBean bean) {
                //点击导航去店家
                callMap(bean);
            }
        });


    }


    //初始化向商家付款helper
    private void initTypeFHelper() {
        SingleLayoutHelper helperF = new SingleLayoutHelper();
        HashMap<String, Object> data = new HashMap<>();
        ArrayList<HashMap<String, Object>> Fdata = new ArrayList<>();
        Fdata.add(data);
        Type_f = new ShopDetailMultiAdapter(Fdata, this, 6, helperF);
        adapterList.add(Type_f);

    }

    //初始化商家简介内容helper
    private void initTypeEHelper() {
        SingleLayoutHelper helperE = new SingleLayoutHelper();
        Type_e = new ShopDetailMultiAdapter(shopTypeEData, this, 5, helperE);
        adapterList.add(Type_e);
    }

    //初始化商家简介标题helper
    private void initTypeDHelper() {
        SingleLayoutHelper helperD = new SingleLayoutHelper();
        HashMap<String, Object> data = new HashMap<>();
        ArrayList<HashMap<String, Object>> Ddata = new ArrayList<>();
        Ddata.add(data);
        Type_d = new ShopDetailMultiAdapter(Ddata, this, 4, helperD);
        adapterList.add(Type_d);

    }

    //初始化优惠信息helper
    private void initTypeCHelper() {
        LinearLayoutHelper linearC = new LinearLayoutHelper();
        Type_c = new ShopDetailMultiAdapter(shopTypeCData, this, 3, linearC);
        adapterList.add(Type_c);
    }

    //初始化优惠信息标题helper
    private void initTypeBHelper() {
        SingleLayoutHelper helperB = new SingleLayoutHelper();
        HashMap<String, Object> data = new HashMap<>();
        ArrayList<HashMap<String, Object>> Bdata = new ArrayList<>();
        Bdata.add(data);
        Type_b = new ShopDetailMultiAdapter(Bdata, this, 2, helperB);
        adapterList.add(Type_b);
    }

    //初始化商家信息的helper
    private void initTypeAHelper() {
        SingleLayoutHelper helperA = new SingleLayoutHelper();
        Type_a = new ShopDetailMultiAdapter(shopTypeAData, this, 1, helperA);
        adapterList.add(Type_a);
    }

    //绑定其他监听
    private void setListener() {
        shopDetaliQrf.setLoadMoreEnable(false);
        shopDetaliQrf.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                //下拉刷新
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shopDetaliQrf.refreshComplete();

                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {

            }
        });



    }
    //获取商家详情数据
    private void getShopMessage() {

        shopTypeAData.clear();
        shopTypeCData.clear();
        shopTypeEData.clear();

        HashMap<String,String> body=new HashMap<>();
        body.put("ctype","store");
        body.put("id",id+"");
        body.put("jf","storeLogo|storeactives|area");
        service.doCommonPost(null, MainUrl.baseSingleQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getShopMessage",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONObject data=object.getJSONObject("data");
                            PerShopBean bean=new PerShopBean();
                            bean.setDescribe(data.getString("describe"));
                            bean.setStoreName(data.getString("storeName"));
                            bean.setStoreLogo(data.getJSONObject("storeLogo").getString("url"));
                            bean.setLatitude(data.getDouble("latitude"));
                            bean.setLongitude(data.getDouble("longitude"));
                            bean.setStoreAddress(data.getString("storeAddress"));
                            bean.setTelephone(data.getString("telephone"));
                            bean.setLable(data.getString("label"));
                            bean.setAreaName(data.getJSONObject("area").getString("areaName"));
                            ArrayList<HashMap<String,Object>> dis=new ArrayList<>();
                            JSONArray arr=data.getJSONArray("storeactives");

                            if(null!=arr&&arr.length()>0){
                                for (int i = 0; i <arr.length() ; i++) {
                                    JSONObject disobj=arr.getJSONObject(i);
                                    DiscountsBean disbean=new DiscountsBean();
                                    disbean.setStoreImg(data.getJSONObject("storeLogo").getString("url"));
                                    disbean.setBeginTime(disobj.getString("beginTime"));
                                    disbean.setCondition(disobj.getString("condition"));
                                    disbean.setContent(disobj.getString("content"));
                                    disbean.setEndTime(disobj.getString("endTime"));
                                    disbean.setPrice(disobj.getDouble("price"));
                                    disbean.setId(disobj.getInt("id"));
                                    disbean.setDisName(disobj.getString("name"));
                                    HashMap<String,Object> disMap =new HashMap<>();
                                    disMap.put("dis",disbean);
                                    shopTypeCData.add(disMap);
                                    dis.add(disMap);
                                }
                            }else {
                                HashMap<String,Object> disMap =new HashMap<>();
                                disMap.put("noData","null");
                                shopTypeCData.add(disMap);
                            }
                            bean.setDisList(dis);
                            HashMap<String,Object> shopdata=new HashMap<>();
                            shopdata.put("data",bean);
                            shopTypeAData.add(shopdata);
                            shopTypeEData.add(shopdata);

                           //上传足迹
                            upData(bean.getStoreName());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getShopMessage",e.toString());
                    }finally {
                        Type_a.notifyItemChanged(shopTypeAData.size());
                        Type_c.notifyItemChanged(shopTypeCData.size());
                        Type_e.notifyItemChanged(shopTypeEData.size());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                Log.e("getShopMessage",ex.toString());
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if(flag){
                    dismissLoading();
                }
                flag=false;
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                if(flag){
                    showLoading(true,"加载中...");
                }
            }

            @Override
            public void onLoading(long total, long current) {

            }
        });



    }

    @Override
    public int initLayout() {
        return R.layout.activity_shop_detail;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.shop_detali_back)
    public void onViewClicked() {
        finish();
    }


    //调用地图进行导航去商家
    private void callMap(PerShopBean bean) {
        String GAODE_HEAD="amapuri://route/plan/?";
        // dev 起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
        // t = 1(公交) =2（驾车） =4(步行)
        //String GAODE_MODE ="&dev=0&t=4";
        //高德地图包名
        String GAODE_PKG = "com.autonavi.minimap";

        //检测安装和唤起
        if (SetupUtil.getInstance(ShopDetailActivity.this).isXSetuped(GAODE_PKG)){
          /*  Intent intent=new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setPackage("com.autonavi.minimap");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(GAODE_HEAD+"sourceApplication=forestCommunity&"+"dlat="+bean.getLatitude()+
                    "&dlat="+bean.getLongitude()+"&dev=0"+"&t=0"));
            startActivity(intent);*/
            goToNaviActivity(this,"ForestCommunity",null,bean.getLatitude()+"", bean.getLongitude()+"","1","2");
        }else {
            Snackbar.make(activityShopDetail,"您未安装高德地图",Snackbar.LENGTH_LONG).setAction("去下载", new View.OnClickListener() {
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


    //上传足迹
    private void upData(String name) {
        HashMap<String,String> map=new HashMap<>();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        map.put("data","{tuser:{id:"+ UserAuthUtil.getUserId()+"},type:1,tid:"+id+",skimTime:\""+time+"\"}");
//        map.put("ctype","track");
        map.put("type","1");
        map.put("tid",id+"");
        map.put("title",name);
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
