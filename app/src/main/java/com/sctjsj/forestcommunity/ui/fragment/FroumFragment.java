package com.sctjsj.forestcommunity.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.FroumLayAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.FroumPostBean;
import com.sctjsj.forestcommunity.javabean.HotInformationBean;
import com.sctjsj.forestcommunity.javabean.UserBean;
import com.sctjsj.forestcommunity.ui.activity.CommunityNewsListActivity;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

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

import static android.R.attr.data;

/**
 * Created by haohaoliu on 2017/8/9.
 * explain:论坛的fragment
 */

public class FroumFragment extends Fragment {
    View rootView = null;
    @BindView(R.id.froum_rv)
    RecyclerView froumRv;
    @BindView(R.id.froum_qrl)
    QRefreshLayout froumQrl;
    private Unbinder unbinder;
    private FroumLayAdapter Type_a,Type_b,Type_c,Type_d,Type_e,Type_f,Type_g;
    private DelegateAdapter adapter;
    //对应的字适配器
    private ArrayList<DelegateAdapter.Adapter> adapterList=new ArrayList<>();
    //论坛类别的数据
    private ArrayList<HashMap<String,Object>> froumSort=new ArrayList<>();
    //论坛内容的数据
    private  ArrayList<HashMap<String,Object>> froumdList=new ArrayList<>();
    //社区新闻的数据
    private ArrayList<HashMap<String,Object>> newsList=new ArrayList<>();
    private int pageIndex=1;
    private HttpServiceImpl service;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_froum, null);
        unbinder = ButterKnife.bind(this, rootView);
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initData();
        getData();
        initView();
        return rootView;
    }

    private void getData() {
        //获取社区新闻
        getNews();

        //获取社区帖子
        getFroumPost();
    }

    //初始化数据
    private void initData() {
        //虚拟适配器
        VirtualLayoutManager layoutManager=new VirtualLayoutManager(getActivity());
        froumRv.setLayoutManager(layoutManager);

        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        froumRv.setRecycledViewPool(viewPool);

        viewPool.setMaxRecycledViews(6,10);

        initTypeAHelper();
        initTypeBHelper();
        initTypeCHelper();
        initTypeDHelper();
        initTypeEHelper();
        initTypeFHelper();
        initTypeGHelper();
        adapter=new DelegateAdapter(layoutManager,false);
        adapter.setAdapters(adapterList);
        froumRv.setAdapter(adapter);

    }

    //初始化社区新闻的列表
    private void initTypeGHelper() {
        LinearLayoutHelper lineraG=new LinearLayoutHelper();
        lineraG.setItemCount(3);
        Type_g=new FroumLayAdapter(newsList,getActivity(),lineraG,7);
        adapterList.add(Type_g);
    }

    //初始化社区新闻的title
    private void initTypeFHelper() {
        SingleLayoutHelper singleF=new SingleLayoutHelper();
        singleF.setItemCount(1);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap<String,Object> typeFmap=new HashMap<>();
        data.add(typeFmap);
        Type_f=new FroumLayAdapter(data,getActivity(),singleF,6);
        adapterList.add(Type_f);

    }

    //初始化我要发帖模块
    private void initTypeEHelper() {
        SingleLayoutHelper singleE=new SingleLayoutHelper();
        singleE.setItemCount(1);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap<String,Object> typeEmap=new HashMap<>();
        data.add(typeEmap);
        Type_e=new FroumLayAdapter(data,getActivity(),singleE,5);
        adapterList.add(Type_e);

    }

    //初始化论坛发帖的列表
    private void initTypeDHelper() {
        LinearLayoutHelper lineraD=new LinearLayoutHelper();
        Type_d=new FroumLayAdapter(froumdList,getActivity(),lineraD,4);
        adapterList.add(Type_d);
    }

    //初始化社区论坛的分类
    private void initTypeCHelper() {
        SingleLayoutHelper singleB=new SingleLayoutHelper();
        initSortImg();
        Type_c=new FroumLayAdapter(froumSort,getActivity(),singleB,3);
        adapterList.add(Type_c);

    }



    //初始化社区论坛的title
    private void initTypeBHelper() {
        SingleLayoutHelper singleB=new SingleLayoutHelper();
        singleB.setItemCount(1);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap<String,Object> typeBmap=new HashMap<>();
        data.add(typeBmap);
        Type_b=new FroumLayAdapter(data,getActivity(),singleB,2);
        adapterList.add(Type_b);

    }

    //初始化论坛页面title
    private void initTypeAHelper() {
        SingleLayoutHelper singleA=new SingleLayoutHelper();
        singleA.setItemCount(1);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap<String,Object> typeAmap=new HashMap<>();
        data.add(typeAmap);
        Type_a=new FroumLayAdapter(data,getActivity(),singleA,1);
        adapterList.add(Type_a);
    }

    //初始化布局
    private void initView() {
        setListener();

    }
    //设置监听
    private void setListener() {
        froumQrl.setLoadMoreEnable(true);
        froumQrl.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                getNews();
               froumQrl.refreshComplete();
            }
            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                getNewsMore();
                froumQrl.LoadMoreComplete();
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //初始化论坛模块的tag
    private void initSortImg() {
        HashMap<String,Object> shzt=new HashMap<>();
        shzt.put("tag","生活杂谈");
        shzt.put("img",R.mipmap.ic_defult_sltp);
        froumSort.add(shzt);

        HashMap<String,Object> sqbl=new HashMap<>();
        sqbl.put("tag","社区爆料");
        sqbl.put("img",R.drawable.ic_sqbl_img);
        froumSort.add(sqbl);

        HashMap<String,Object> qz=new HashMap<>();
        qz.put("tag","求助");
        qz.put("img",R.drawable.ic_qiuzhu_img);
        froumSort.add(qz);

        HashMap<String,Object> esxz=new HashMap<>();
        esxz.put("tag","二手闲置");
        esxz.put("img",R.drawable.ic_esxz_img);
        froumSort.add(esxz);


        HashMap<String,Object> hrhs=new HashMap<>();
        hrhs.put("tag","好人好事");
        hrhs.put("img",R.drawable.ic_hrhs_img);
        froumSort.add(hrhs);

        HashMap<String,Object> hr=new HashMap<>();
        hr.put("tag","好人");
        hr.put("img",R.drawable.ic_hr_img);
        froumSort.add(hr);

    }



    //获取社区新闻
    private void getNews() {
        newsList.clear();
        pageIndex = 1;
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "8");
        map.put("keyword", "SHE_QU_XIN_WEN");
        map.put("jf", "pic|thumbnail");
        service.doCommonPost(null, MainUrl.CommunityNew, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("communityonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    pageIndex++;
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("msg");
                        if (results) {
                            JSONArray array = object.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                HotInformationBean bean = new HotInformationBean();
                                String title = object1.getString("title");
                                int id = object1.getInt("id");
                                String keyword=object1.getString("keyword");
                                String time=object1.getString("issueTime");
                                String url=object1.getJSONObject("thumbnail").getString("url");

                                bean.setId(id);
                                bean.setTitle(title);
                                bean.setUrl(url);
                                bean.setTime(time);
                                bean.setKeyWord(keyword);
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("bean", bean);
                                newsList.add(map);
                            }
                            Type_g.notifyItemInserted(newsList == null ? 0 : newsList.size());
                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("communityJSONException", e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("communityonError", ex.toString());
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
    //获取社区新闻
    private void getNewsMore() {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "8");
        map.put("keyword", "SHE_QU_XIN_WEN");
        map.put("jf", "pic|thumbnail");
        service.doCommonPost(null, MainUrl.CommunityNew, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("communityonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    pageIndex++;
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("msg");
                        if (results) {
                            JSONArray array = object.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                HotInformationBean bean = new HotInformationBean();
                                String title = object1.getString("title");
                                int id = object1.getInt("id");
                                String keyword=object1.getString("keyword");
                                String time=object1.getString("issueTime");
                                String url=object1.getJSONObject("thumbnail").getString("url");

                                bean.setId(id);
                                bean.setTitle(title);
                                bean.setUrl(url);
                                bean.setTime(time);
                                bean.setKeyWord(keyword);
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("bean", bean);
                                newsList.add(map);
                            }
                            Type_g.notifyItemInserted(newsList == null ? 0 : newsList.size());
                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("communityJSONException", e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("communityonError", ex.toString());
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


    //获取社区帖子列表
    private void  getFroumPost(){
        HashMap<String,String> body=new HashMap<>();
        body.put("ctype","tribune");
        body.put("cond","{tribune:1}");
        body.put("orderby","insertTime desc");
        body.put("jf","tuser|thumbnail");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getFroumPost",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONArray array=object.getJSONArray("resultList");
                            if(null!=array&&array.length()>0){
                                for (int i = 0; i <3 ; i++) {
                                    JSONObject arrObj=array.getJSONObject(i);
                                    FroumPostBean bean=new FroumPostBean();
                                    UserBean user=new UserBean();
                                    JSONObject userJson=arrObj.getJSONObject("tuser");
                                    user.setUserIcon(userJson.getJSONObject("thumbnail").getString("url"));
                                    user.setId(userJson.getInt("id"));
                                    user.setUserName(userJson.getString("username"));
                                    bean.setBean(user);
                                    bean.setInsertTime(arrObj.getString("insertTime"));
                                    bean.setContent(arrObj.getString("content"));
                                    bean.setId(arrObj.getInt("id"));
                                    bean.setTags(arrObj.getString("tags"));
                                    HashMap<String,Object> map=new HashMap<String, Object>();
                                    map.put("data",bean);
                                    froumdList.add(map);
                                }
                                //通知适配器
                                Type_d.notifyItemChanged(froumdList.size());
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
