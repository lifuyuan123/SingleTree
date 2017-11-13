package com.sctjsj.forestcommunity.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.sctjsj.basemodule.base.ui.widget.dialog.CommonDialog;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.base.util.setup.SetupUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.HomeMultiAdapter;
import com.sctjsj.forestcommunity.event.ChooseCityEvent;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.HotInformationBean;
import com.sctjsj.forestcommunity.ui.activity.ConfirmPayActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/**
 * Created by haohaoliu on 2017/8/9.
 * explain:首页的Fragment
 */

public class HomeFragment extends Fragment {
    View rootView = null;
    @BindView(R.id.refresh)
    QRefreshLayout refresh;
    private Unbinder unbinder;
    //子适配器
    private HomeMultiAdapter bannerAdapter, navAdapter, actionAdapter, messageTitleAdapter, messageAdapter;
    //代理适配器
    private DelegateAdapter adapter;

    /**
     * 对应的子适配器列表
     **/
    private List<DelegateAdapter.Adapter> adapterList = new LinkedList<>();

    //    item数据 hashmap
    private ArrayList<HashMap<String, Object>> messageData = new ArrayList<>();

    private HashMap<String, Object> bannerMap = new HashMap<>();
    private ArrayList<HashMap<String, Object>> hotMap = new ArrayList<>();
    private HashMap<String, Object> RecommendToda = new HashMap<>();

    @BindView(R.id.home_rv)
    RecyclerView mRV;
    private HttpServiceImpl service;
    private int pageIndex=1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_home, null);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       EventBus.getDefault().register(this);
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
//        初始化布局
        initRVLayout();
        refresh.setLoadMoreEnable(true);
        refresh.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                initData();
                refresh.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                initHotInformationMore();
                refresh.LoadMoreComplete();
            }
        });

        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        //initData();//放在这里会报错Attempt to read from field 'java.lang.Object android.util.Pair.second' on a null object reference


    }

    //获取数据
    private void initData() {
        //获取首页baner图
        initBanerData();
        //获取热点资讯
        initHotInformation();
        //获取今日推荐标题
        initRecommendToda();

    }



    @Subscribe
    public void onEventMainThread(ChooseCityEvent event) {
        if(event!=null){
            Log.e("onEventMainThread","选择了城市");
            bannerMap.put("city", SPFUtil.get(Tag.TAG_CITY,"成都"));
            bannerAdapter.notifyDataSetChanged();

        }
    }


    //获取今日推荐标题
    private void initRecommendToda() {
        RecommendToda.clear();
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", "1");
        map.put("pageSize", "8");
        map.put("keyword", "JIN_RI_TUI_JIAN");
        map.put("jf", "pic|thumbnail");
        service.doCommonPost(null, MainUrl.HotInformation, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("HotInformationonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("msg");
                        if (results) {
                            List<String> tiltes = new ArrayList<String>();
                            JSONArray array = object.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                String title = object1.getString("title");
                                tiltes.add(title);
                            }
                            RecommendToda.put("tiltes", tiltes);
                            actionAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("HotInformatiJSONException", e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("HotInformationonError", ex.toString());
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

    //获取热点资讯
    private void initHotInformation() {
        hotMap.clear();
        pageIndex=1;
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex+"");
        map.put("pageSize", "8");
        map.put("keyword", "RE_DIAN_ZI_XUN");
        map.put("jf", "pic|thumbnail");
        service.doCommonPost(null, MainUrl.HotInformation, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("HotInformationonSuccess", result.toString());
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
                                int count = object1.getInt("viewTimes");
                                int id = object1.getInt("id");
                                List<String> list1 = new ArrayList<String>();
                                JSONArray array1 = object1.getJSONArray("pic");
                                int type = 0;
                                if (array1.length() == 1) {
                                    type = 1;
                                } else if (array1.length() == 3) {
                                    type = 2;
                                }
                                for (int j = 0; j < array1.length(); j++) {
                                    JSONObject object2 = array1.getJSONObject(j);
                                    String url2 = object2.getString("url");
                                    list1.add(url2);
                                }

                                bean.setCount(count);
                                bean.setId(id);
                                bean.setTitle(title);
                                bean.setType(type);
                                bean.setIconUrlList(list1);
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("bean", bean);
                                hotMap.add(map);
                                messageAdapter.notifyItemInserted(hotMap == null ? 0 : hotMap.size());
                            }
                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("HotInformatiJSONException", e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("HotInformationonError", ex.toString());
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

    //获取热点资讯
    private void initHotInformationMore() {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex+"");
        map.put("pageSize", "8");
        map.put("keyword", "RE_DIAN_ZI_XUN");
        map.put("jf", "pic|thumbnail");
        service.doCommonPost(null, MainUrl.HotInformation, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("HotInformationonSuccess", result.toString());
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
                                int count = object1.getInt("viewTimes");
                                int id = object1.getInt("id");
                                List<String> list1 = new ArrayList<String>();
                                JSONArray array1 = object1.getJSONArray("pic");
                                int type = 0;
                                if (array1.length() == 1) {
                                    type = 1;
                                } else if (array1.length() == 3) {
                                    type = 2;
                                }
                                for (int j = 0; j < array1.length(); j++) {
                                    JSONObject object2 = array1.getJSONObject(j);
                                    String url2 = object2.getString("url");
                                    list1.add(url2);
                                }

                                bean.setCount(count);
                                bean.setId(id);
                                bean.setTitle(title);
                                bean.setType(type);
                                bean.setIconUrlList(list1);
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("bean", bean);
                                hotMap.add(map);
                                messageAdapter.notifyItemInserted(hotMap == null ? 0 : hotMap.size());
                                ;
                            }
                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("HotInformatiJSONException", e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("HotInformationonError", ex.toString());
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

    //获取首页baner图
    private void initBanerData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("jf", "accessory");
        service.doCommonPost(null, MainUrl.HomeBaner, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("baneronSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("msg");
                        if (results) {
                            JSONArray array = object.getJSONArray("resultList");
                            List<String> list = new ArrayList<String>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                JSONObject accessory = object1.getJSONObject("accessory");
                                if (accessory.has("url")) {
                                    String url = accessory.getString("url");
                                    list.add(url);
                                }
                            }
                            bannerMap.put("banner", list);
                            bannerAdapter.notifyItemChanged(0);
                        } else {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("banerJSONException", e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("baneronError", ex.toString());
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


    private void initRVLayout() {
        //虚拟适配器
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(getActivity());
        mRV.setLayoutManager(layoutManager);
//      banner
        initBannerHelper();
//      功能导航
        initNavHelper();
//      活动专区
        initActionHelper();
//      资讯专区 title
        initMessageTitleHelper();
//      资讯列表
        initMessageHelper();
        //设置代理适配器
        adapter = new DelegateAdapter(layoutManager, false);
        adapter.setAdapters(adapterList);
        mRV.setAdapter(adapter);
    }

    private void initBannerHelper() {
        SingleLayoutHelper singleLayoutHelper_banner = new SingleLayoutHelper();
        //设置宽高比
        //只能有一个 item 项目
        singleLayoutHelper_banner.setItemCount(1);

        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        bannerMap = new HashMap<>();
        //一个 data 的内容 就是一个 item
        data.add(bannerMap);

        bannerAdapter = new HomeMultiAdapter(getActivity(), singleLayoutHelper_banner, 1, data, 1);
        adapterList.add(bannerAdapter);
    }

    private void initNavHelper() {
        SingleLayoutHelper singleLayoutHelper_column = new SingleLayoutHelper();
        //只能有一个 item 项目
        singleLayoutHelper_column.setItemCount(1);
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();

        data.add(map);

        navAdapter = new HomeMultiAdapter(getActivity(), singleLayoutHelper_column, 1, data, 2);
        adapterList.add(navAdapter);
        navAdapter.setAdapterListener(new HomeMultiAdapter.HomeAdapterCallBack() {
            @Override
            public void PayLifeClick() {
                if(SetupUtil.getInstance(getActivity()).isXSetuped("com.eg.android.AlipayGphone")){

                    final CommonDialog dialog=new CommonDialog(getActivity());
                    dialog.setTitle("温馨提醒");
                    dialog.setContent("即将进入支付生活缴费");
                    dialog.setCancelClickListener("确认", new CommonDialog.CancelClickListener() {
                        @Override
                        public void clickCancel() {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse("alipays://platformapi/startapp?appId=20000193");
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    });

                    dialog.setConfirmClickListener("取消", new CommonDialog.ConfirmClickListener() {
                        @Override
                        public void clickConfirm() {
                            dialog.dismiss();
                        }
                    });


                    dialog.show();

                }else {
                    Snackbar.make(rootView, "您未安装支付宝！", Snackbar.LENGTH_LONG).setAction("去下载", new View.OnClickListener() {
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
        });
    }

    private void initActionHelper() {
        SingleLayoutHelper singleLayoutHelper_column = new SingleLayoutHelper();
        //只能有一个 item 项目
        singleLayoutHelper_column.setItemCount(1);
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();

        data.add(RecommendToda);

        actionAdapter = new HomeMultiAdapter(getActivity(), singleLayoutHelper_column, 1, data, 3);
        adapterList.add(actionAdapter);
    }

    private void initMessageTitleHelper() {
        SingleLayoutHelper singleLayoutHelper_column = new SingleLayoutHelper();
        //只能有一个 item 项目
        singleLayoutHelper_column.setItemCount(1);
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();

        data.add(map);

        messageTitleAdapter = new HomeMultiAdapter(getActivity(), singleLayoutHelper_column, 1, data, 4);
        adapterList.add(messageTitleAdapter);
    }

    //热点资讯
    private void initMessageHelper() {
        LinearLayoutHelper linearLayoutHelper_store = new LinearLayoutHelper();
        //只显示两条数据
        linearLayoutHelper_store.setItemCount(2);
        messageAdapter = new HomeMultiAdapter(getActivity(), linearLayoutHelper_store, 2, hotMap, 5);
        adapterList.add(messageAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}
