package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.HotInformationAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.HotInformationBean;
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

@Route(path = "/main/act/HotInformation")
//热点资讯
public class HotInformationActivity extends BaseAppcompatActivity {

    @BindView(R.id.rv_hot)
    RecyclerView rvHot;
    @BindView(R.id.ql_hot)
    QRefreshLayout qlHot;

    @Autowired(name = "key")
    int key;
    @BindView(R.id.iv_top_title)
    ImageView ivTopTitle;
    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.lin_hot_nodata)
    LinearLayout linHotNodata;
    private List<HotInformationBean> lists = new ArrayList<>();
    private HotInformationAdapter adapter;
    private LinearLayoutManager manager;
    private HttpServiceImpl service;
    private String keyword;
    private int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        if (key == 6) {
            ivTopTitle.setVisibility(View.GONE);
            tvTopTitle.setText("今日推荐");
            keyword = "JIN_RI_TUI_JIAN";
        } else {
            keyword = "RE_DIAN_ZI_XUN";
        }
        initData();
        initView();
    }

    private void initData() {
        initHotInformation();
    }

    private void initView() {
        manager = new LinearLayoutManager(this);
        adapter = new HotInformationAdapter(this, lists);
        rvHot.setLayoutManager(manager);
        rvHot.setAdapter(adapter);
        qlHot.setLoadMoreEnable(true);
        qlHot.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                initHotInformation();
                qlHot.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                initHotInformationMore();
                qlHot.LoadMoreComplete();
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_hot_information;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }


    //获取热点资讯
    private void initHotInformation() {
        lists.clear();
        pageIndex = 1;
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "8");
        map.put("keyword", keyword);
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
                            List<HotInformationBean> list = new ArrayList<HotInformationBean>();
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
                                lists.add(bean);
                            }

                        }else {
                            Toast.makeText(HotInformationActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("HotInformatiJSONException", e.toString());
                    }finally {
                        if(lists.size()>0){
                            qlHot.setVisibility(View.VISIBLE);
                            linHotNodata.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }else {
                            qlHot.setVisibility(View.GONE);
                            linHotNodata.setVisibility(View.VISIBLE);
                        }
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
                dismissLoading();
            }

            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
                showLoading(false, "加载中...");
            }

            @Override
            public void onLoading(long total, long current) {
            }
        });
    }


    //获取热点资讯
    private void initHotInformationMore() {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "8");
        map.put("keyword", keyword);
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
                            List<HotInformationBean> list = new ArrayList<HotInformationBean>();
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
                                lists.add(bean);
                            }
                        }else {
                            Toast.makeText(HotInformationActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("HotInformatiJSONException", e.toString());
                    }finally {
                        if(lists.size()>0){
                            qlHot.setVisibility(View.VISIBLE);
                            linHotNodata.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }else {
                            qlHot.setVisibility(View.GONE);
                            linHotNodata.setVisibility(View.VISIBLE);
                        }
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
                dismissLoading();
            }

            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
                showLoading(false, "加载中...");
            }

            @Override
            public void onLoading(long total, long current) {
            }
        });
    }
}
