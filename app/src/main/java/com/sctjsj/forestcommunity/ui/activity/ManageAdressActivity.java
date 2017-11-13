package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.MainLooper;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.AdressAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.AdressBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

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

@Route(path = "/main/act/ManageAdress")
//管理收货地址
public class ManageAdressActivity extends BaseAppcompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refresh)
    QRefreshLayout refresh;
    @BindView(R.id.lin_NoData)
    LinearLayout linNoData;
    @BindView(R.id.lin_recycler_data)
    LinearLayout linRecyclerData;
    @BindView(R.id.bt_confirm)
    Button btConfirm;
    @BindView(R.id.bt_add_newAdress)
    Button btAddNewAdress;
    private AdressAdapter adapter;
    private LinearLayoutManager manager;
    private List<AdressBean> list = new ArrayList<>();
    private HttpServiceImpl service;
    private int pageIndex=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        adapter = new AdressAdapter(list, this);
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        refresh.setLoadMoreEnable(true);
        refresh.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                initData();
                refresh.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                initDataMore();
                refresh.LoadMoreComplete();
            }
        });

        btConfirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    btConfirm.setBackgroundResource(R.drawable.longin_bt_down_background);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    btConfirm.setBackgroundResource(R.drawable.longin_bt_backgroungd);
                }
                return false;
            }
        });
        btAddNewAdress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    btAddNewAdress.setBackgroundResource(R.color.color_bt_up);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    btAddNewAdress.setBackgroundResource(R.color.color_bt_down);
                }
                return false;
            }
        });

        //删除地址
        adapter.setCallBack(new AdressAdapter.CallBack() {
            @Override
            public void deleteItem(int position) {
                //删除地址
                deleteAddress(position);
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_manage_adress;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.bt_confirm, R.id.bt_add_newAdress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.linear_back:
                finish();
                break;
            // 添加地址
            case R.id.bt_confirm:
                ARouter.getInstance().build("/main/act/AddAdress").withInt("key", 1).navigation();
                break;
            //新增地址
            case R.id.bt_add_newAdress:
                ARouter.getInstance().build("/main/act/AddAdress").withInt("key", 1).navigation();
                break;
        }
    }


    private void initData() {
        list.clear();
        pageIndex=1;
        final HashMap<String,String> map=new HashMap<>();
        map.put("ctype","address");
        map.put("cond","{suser:{id:"+ UserAuthUtil.getUserId()+"},state:1}");
        map.put("pageIndex",pageIndex+"");
        map.put("size","8");
        map.put("orderby","updateTime");
        map.put("jf","sarea|parent");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("adressMap",map.toString());
                LogUtil.e("adressonSuccess",result.toString());
            if(!StringUtil.isBlank(result)&&result!=null){
                try {
                    JSONObject object=new JSONObject(result);
                    boolean results=object.getBoolean("result");
                    String msg=object.getString("msg");
                    if(results){
                        pageIndex++;
                        JSONArray array=object.getJSONArray("resultList");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object1=array.getJSONObject(i);
                            AdressBean bean=new AdressBean();
                            String areaInfo=object1.getString("areaInfo");
                            int id=object1.getInt("id");
                            String mobile=object1.getString("mobile");
                            String recName=object1.getString("recName");


                            String areaName=object1.getJSONObject("sarea").getString("areaName");
                            String parent1=object1.getJSONObject("sarea").getJSONObject("parent").getString("areaName");
                            String parent2=object1.getJSONObject("sarea").getJSONObject("parent").getJSONObject("parent").getString("areaName");
                            int areaId =object1.getJSONObject("sarea").getInt("id");
                            String sarea=parent2+parent1+areaName;
                            if(i==array.length()-1){
                                bean.setDefault(true);
                            }

                            bean.setAreaId(areaId);
                            bean.setProvince(parent2);
                            bean.setCity(parent1);
                            bean.setArea(areaName);
                            bean.setAdressRegion(sarea);
                            bean.setName(recName);
                            bean.setAdress(areaInfo);
                            bean.setId(id);
                            bean.setPhone(mobile);
                            list.add(bean);
                        }
                    }else {
                        Toast.makeText(ManageAdressActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e("adressJSONException",e.toString());
                }finally {
                    if(list.size()>0){
                        linNoData.setVisibility(View.GONE);
                        linRecyclerData.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }else {
                        linNoData.setVisibility(View.VISIBLE);
                        linRecyclerData.setVisibility(View.GONE);
                    }
                }
            }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("adressonError",ex.toString());
            }
            @Override
            public void onCancelled(Callback.CancelledException cex) {}
            @Override
            public void onFinished() {
                dismissLoading();
            }
            @Override
            public void onWaiting() {}
            @Override
            public void onStarted() {
                showLoading(false,"加载中...");
            }
            @Override
            public void onLoading(long total, long current) {}
        });
    }

    private void initDataMore() {
        final HashMap<String,String> map=new HashMap<>();
        map.put("ctype","address");
        map.put("cond","{suser:{id:"+ UserAuthUtil.getUserId()+"},state:1}");
        map.put("pageIndex",pageIndex+"");
        map.put("size","8");
        map.put("orderby","updateTime");
        map.put("jf","sarea|parent");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("adressMap",map.toString());
                LogUtil.e("adressonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");
                        if(results){
                            pageIndex++;
                            JSONArray array=object.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1=array.getJSONObject(i);
                                AdressBean bean=new AdressBean();
                                String areaInfo=object1.getString("areaInfo");
                                int id=object1.getInt("id");
                                String mobile=object1.getString("mobile");
                                String recName=object1.getString("recName");
                                if(i==array.length()-1){
                                    bean.setDefault(true);
                                }
                                String areaName=object1.getJSONObject("sarea").getString("areaName");
                                String parent1=object1.getJSONObject("sarea").getJSONObject("parent").getString("areaName");
                                String parent2=object1.getJSONObject("sarea").getJSONObject("parent").getJSONObject("parent").getString("areaName");
                                int areaId =object1.getJSONObject("sarea").getInt("id");
                                String sarea=parent2+parent1+areaName;

                                bean.setAreaId(areaId);
                                bean.setProvince(parent2);
                                bean.setCity(parent1);
                                bean.setArea(areaName);
                                bean.setAdressRegion(sarea);
                                bean.setName(recName);
                                bean.setAdress(areaInfo);
                                bean.setId(id);
                                bean.setPhone(mobile);
                                list.add(bean);
                            }
                        }else {
                            Toast.makeText(ManageAdressActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("adressJSONException",e.toString());
                    }finally {
                        if(list.size()>0){
                            linNoData.setVisibility(View.GONE);
                            linRecyclerData.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }else {
                            linNoData.setVisibility(View.VISIBLE);
                            linRecyclerData.setVisibility(View.GONE);
                        }
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("adressonError",ex.toString());
            }
            @Override
            public void onCancelled(Callback.CancelledException cex) {}
            @Override
            public void onFinished() {
                dismissLoading();
            }
            @Override
            public void onWaiting() {}
            @Override
            public void onStarted() {
                showLoading(false,"加载中...");
            }
            @Override
            public void onLoading(long total, long current) {}
        });
    }


    //删除地址
    private void deleteAddress(final int position) {
        HashMap<String,String> map=new HashMap<>();
        map.put("ctype","address");
        map.put("data","{id:"+list.get(position).getId()+",state:2}");
        service.doCommonPost(null, MainUrl.baseModifyUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("deleteonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");
                        if(results){
                            list.remove(position);
                            if(list.size()>0){
                                adapter.notifyDataSetChanged();
                            }else {
                                linRecyclerData.setVisibility(View.GONE);
                                linNoData.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(ManageAdressActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ManageAdressActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("deleteJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("deleteonError",ex.toString());
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
