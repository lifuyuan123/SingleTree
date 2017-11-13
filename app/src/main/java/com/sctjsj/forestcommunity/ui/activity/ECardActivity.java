package com.sctjsj.forestcommunity.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.ECardMultiAdapter;
import com.sctjsj.forestcommunity.adapter.HomeMultiAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.DiscountsBean;
import com.sctjsj.forestcommunity.javabean.PerShopBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/**
 * 一卡通
 */
@Route(path = "/main/act/user/e_card")
public class ECardActivity extends BaseAppcompatActivity {

    @BindView(R.id.rv)RecyclerView mRV;
    private DelegateAdapter adapter;
    @BindView(R.id.ecard_qr_rf)
    QRefreshLayout qr;
    @BindView(R.id.ecard_lay_nodata)
    LinearLayout nodata;

    private ECardMultiAdapter titleAdapter,listAdapter;
    private HttpServiceImpl service;
    private int pageIndex=1;
    HashMap<String, Object> Titlemap = new HashMap<>();
    ArrayList<HashMap<String, Object>> data = new ArrayList<>();
    private  boolean flag=true;

    /**
     * 对应的子适配器列表
     **/
    private List<DelegateAdapter.Adapter> adapterList = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        initRVLayout();
        getEcardData();
        setListener();

    }


    //设置监听
    private void setListener() {
        qr.setLoadMoreEnable(true);
        qr.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                flag=false;
                getEcardData();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                getEcardMoreData();
            }
        });

    }

    @Override
    public int initLayout() {
        return R.layout.activity_ecard;
    }

    @Override
    public void reloadData() {

    }

    private void initRVLayout() {
        //虚拟适配器
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
        mRV.setLayoutManager(layoutManager);

        initTitleHelper();

        initListHelper();

        //设置代理适配器
        adapter = new DelegateAdapter(layoutManager, false);
        adapter.setAdapters(adapterList);
        mRV.setAdapter(adapter);
    }

    private void initTitleHelper(){
        SingleLayoutHelper singleLayoutHelper_title = new SingleLayoutHelper();
        //设置宽高比
        //只能有一个 item 项目
        singleLayoutHelper_title.setItemCount(1);

        ArrayList<HashMap<String, Object>> data = new ArrayList<>();

        data.add(Titlemap);

        titleAdapter = new ECardMultiAdapter(this, singleLayoutHelper_title, 1, data, 1);
        adapterList.add(titleAdapter);
    }

    private void initListHelper(){
        LinearLayoutHelper linearLayoutHelper_list = new LinearLayoutHelper();
        //设置宽高比
        //只能有一个 item 项目
        linearLayoutHelper_list.setItemCount(1);
        listAdapter = new ECardMultiAdapter(this, linearLayoutHelper_list, 1, data, 2);
        adapterList.add(listAdapter);
    }

    @OnClick({R.id.back})
    public void eCardClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    //获取周边商家的一卡通
    private void getEcardData(){
        pageIndex=1;
        data.clear();
        HashMap<String,String> body=new HashMap<>();
        body.put("ctype","active");
        body.put("cond","{isDelete:1}");
        body.put("orderby","id");
        body.put("jf","store");
        body.put("pageIndex",pageIndex+"");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                pageIndex++;
                Log.e("getEcardData",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONArray array=object.getJSONArray("resultList");
                            if(null!=array&&array.length()>0){
                                Titlemap.put("data",array.length());
                                for (int i = 0; i <array.length() ; i++) {
                                    JSONObject active=array.getJSONObject(i);
                                    DiscountsBean disBean=new DiscountsBean();
                                    disBean.setShowPrice(active.getDouble("showPrice"));
                                    disBean.setCondition(active.getString("condition"));
                                    PerShopBean shop=new PerShopBean();
                                    shop.setId(active.getJSONObject("store").getInt("id"));
                                    disBean.setShop(shop);
                                    HashMap<String,Object> dataMap=new HashMap<>();
                                    dataMap.put("data",disBean);
                                    data.add(dataMap);
                                }
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        if(data.size()>0){
                            nodata.setVisibility(View.GONE);
                            qr.setVisibility(View.VISIBLE);
                            titleAdapter.notifyDataSetChanged();
                            listAdapter.notifyDataSetChanged();
                        }else {
                            nodata.setVisibility(View.VISIBLE);
                            qr.setVisibility(View.GONE);
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
                if(flag){
                   dismissLoading();
                }
                qr.LoadMoreComplete();
                qr.refreshComplete();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
               if(flag){
                   showLoading(true,"加载中....");
               }

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }


    //获取更多的数据
    private void getEcardMoreData(){
        HashMap<String,String> body=new HashMap<>();
        body.put("ctype","active");
        body.put("cond","{isDelete:1}");
        body.put("orderby","id");
        body.put("jf","store");
        body.put("pageIndex",pageIndex+"");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                pageIndex++;
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONArray array=object.getJSONArray("resultList");
                            if(null!=array&&array.length()>0){
                                int number= (int) Titlemap.get("data");
                                Titlemap.put("data",number+array.length());
                                for (int i = 0; i <array.length() ; i++) {
                                    JSONObject active=array.getJSONObject(i);
                                    DiscountsBean disBean=new DiscountsBean();
                                    disBean.setShowPrice(active.getDouble("showPrice"));
                                    disBean.setCondition(active.getString("condition"));
                                    PerShopBean shop=new PerShopBean();
                                    shop.setId(active.getJSONObject("store").getInt("id"));
                                    disBean.setShop(shop);
                                    HashMap<String,Object> dataMap=new HashMap<>();
                                    dataMap.put("data",disBean);
                                    data.add(dataMap);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        if(data.size()>0){
                            nodata.setVisibility(View.GONE);
                            qr.setVisibility(View.VISIBLE);
                            titleAdapter.notifyDataSetChanged();
                            listAdapter.notifyDataSetChanged();
                        }else {
                            nodata.setVisibility(View.VISIBLE);
                            qr.setVisibility(View.GONE);
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
                qr.LoadMoreComplete();
                qr.refreshComplete();
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

}
