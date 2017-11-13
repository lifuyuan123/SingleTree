package com.sctjsj.forestcommunity.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.widget.LoadingDialog;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.InregralAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.IntegralBean;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/**
 * Created by lifuy on 2017/8/14.
 */

public class IntegralFragment extends Fragment {

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.qr)
    QRefreshLayout qr;
    @BindView(R.id.lin_integral_nodata)
    LinearLayout linIntegralNodata;
    private Unbinder unbinder;
    private String Arguments;
    private InregralAdapter adapter;
    private List<IntegralBean> list = new ArrayList<>();
    private LinearLayoutManager manager;
    private HttpServiceImpl service;
    private int pageIndex = 1;
    /**加载进度对话框**/
    private LoadingDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_integral, container, false);
        unbinder = ButterKnife.bind(this, view);
        Arguments = (String) getArguments().getSerializable("key");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        getData();
        initView();
    }

    private void initView() {
        manager = new LinearLayoutManager(getActivity());
        adapter = new InregralAdapter(getActivity(), list);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        qr.setLoadMoreEnable(true);
        qr.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                getData();
                qr.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                getDataMore();
                qr.LoadMoreComplete();
            }
        });

        //删除监听
        adapter.setCallBack(new InregralAdapter.CallBack() {
            @Override
            public void onClick(int position) {
//                deleteItem(position);
            }
        });
    }

    //删除
//    private void deleteItem(int position) {
//        HashMap<String ,String> map=new HashMap<>();
//        map.put("data","{id:"+UserAuthUtil.getUserId()+",thumbnail:{id:1420},realName:'昵称',birthday:1503116055000,strArea:'我的房屋'}");
//        map.put("ctype","integralRecord");
//        service.doCommonPost(null, MainUrl.baseModifyUrl, map, new XProgressCallback() {
//            @Override
//            public void onSuccess(String result) {
//                LogUtil.e("integradelonSuccess",result.toString());
//                if(!StringUtil.isBlank(result)&&result!=null){
//                    try {
//                        JSONObject object=new JSONObject(result);
//
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        LogUtil.e("integradelJSONException",e.toString());
//                    }
//                }
//            }
//            @Override
//            public void onError(Throwable ex) {
//                LogUtil.e("integradelonError",ex.toString());
//            }
//            @Override
//            public void onCancelled(Callback.CancelledException cex) {}
//            @Override
//            public void onFinished() {}
//            @Override
//            public void onWaiting() {}
//            @Override
//            public void onStarted() {}
//            @Override
//            public void onLoading(long total, long current) {}
//        });
//    }

    private void getData() {
        list.clear();
        pageIndex = 1;
        HashMap<String, String> map = new HashMap<>();
        map.put("ctype", "integralRecord");
        map.put("cond", "{user:{id:"+ UserAuthUtil.getUserId()+"}}");
        map.put("pageIndex", pageIndex + "");
        map.put("size", "8");
        map.put("jf", "user|thumbnail");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("integralonSuccess", result.toString());
                try {
                    JSONObject o = new JSONObject(result);
                    if (!StringUtil.isBlank(result) && result != null) {
                        boolean results = o.getBoolean("result");
                        String msg = o.getString("msg");
                        if (results) {
                            pageIndex++;
                            JSONArray array = o.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                IntegralBean bean = new IntegralBean();
                                int id = object.getInt("id");
                                String name = object.getString("explain");
                                String insertTime = object.getString("insertTime");
                                String remark = object.getString("remark");
                                int tradeType=object.getInt("tradeType");
                                int tranAmount = object.getInt("tranAmount");
                                String url = object.getJSONObject("user").getJSONObject("thumbnail").getString("url");
                                bean.setId(id);
                                bean.setName(name);
                                bean.setIntegral(tranAmount);
                                bean.setTime(insertTime);
                                bean.setRemark(remark);
                                bean.setIconUrl(url);
                                bean.setType(tradeType);

                                switch (Arguments){
                                    case "全部":
                                        list.add(bean);
                                        break;
                                    case "收入":
                                        if(bean.getType()==1){
                                            list.add(bean);
                                        }
                                        break;
                                    case "支出":
                                        if(bean.getType()==2){
                                            list.add(bean);
                                        }
                                        break;
                                }

                            }

                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e("integralJSONException", e.toString());
                } finally {
                    if(list.size()>0){
                        qr.setVisibility(View.VISIBLE);
                        linIntegralNodata.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }else {
                        qr.setVisibility(View.GONE);
                        linIntegralNodata.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("integralonError", ex.toString());
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
            public void onLoading(long total, long current) {

            }
        });
    }

    public void getDataMore() {
        HashMap<String, String> map = new HashMap<>();
        map.put("ctype", "integralRecord");
        map.put("cond", "{user:{id:"+ UserAuthUtil.getUserId()+"}}");
        map.put("pageIndex", pageIndex + "");
        map.put("size", "8");
        map.put("jf", "user|thumbnail");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("integralonSuccess", result.toString());
                try {
                    JSONObject o = new JSONObject(result);
                    if (!StringUtil.isBlank(result) && result != null) {
                        boolean results = o.getBoolean("result");
                        String msg = o.getString("msg");
                        if (results) {
                            pageIndex++;
                            JSONArray array = o.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                IntegralBean bean = new IntegralBean();
                                int id = object.getInt("id");
                                String name = object.getString("explain");
                                String insertTime = object.getString("insertTime");
                                String remark = object.getString("remark");
                                int tradeType=object.getInt("tradeType");
                                int tranAmount = object.getInt("tranAmount");
                                String url = object.getJSONObject("user").getJSONObject("thumbnail").getString("url");
                                bean.setId(id);
                                bean.setName(name);
                                bean.setIntegral(tranAmount);
                                bean.setTime(insertTime);
                                bean.setRemark(remark);
                                bean.setIconUrl(url);
                                bean.setType(tradeType);
                                switch (Arguments){
                                    case "全部":
                                        list.add(bean);
                                        break;
                                    case "收入":
                                        if(bean.getType()==1){
                                            list.add(bean);
                                        }
                                        break;
                                    case "支出":
                                        if(bean.getType()==2){
                                            list.add(bean);
                                        }
                                        break;
                                }
                            }

                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e("integralJSONException", e.toString());
                } finally {
                    if(list.size()>0){
                        qr.setVisibility(View.VISIBLE);
                        linIntegralNodata.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }else {
                        qr.setVisibility(View.GONE);
                        linIntegralNodata.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("integralonError", ex.toString());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * 显示加载dialog
     * @param str
     */
    public void showLoading(boolean b,String str){
        if(dialog==null){
            dialog=new LoadingDialog(this);
        }
        dialog.setCancelable(b);
        dialog.setLoadingStr(str);
        if(!dialog.isShowing() ){
            dialog.show();
        }

    }

    /**
     * 关闭加载 dialog
     */
    public void dismissLoading(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

}
