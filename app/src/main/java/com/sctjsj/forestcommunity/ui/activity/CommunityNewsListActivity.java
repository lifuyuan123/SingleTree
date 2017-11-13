package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.CommunityNewListAdapter;
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

@Route(path = "/main/act/CommunityNewsList")
public class CommunityNewsListActivity extends BaseAppcompatActivity {

    @BindView(R.id.comm_new_list_back)
    RelativeLayout commNewListBack;
    @BindView(R.id.comm_new_list_rv)
    RecyclerView commNewListRv;
    @BindView(R.id.comm_new_list_qr)
    QRefreshLayout commNewListQr;
    @BindView(R.id.activity_community_news_list)
    LinearLayout activityCommunityNewsList;
    @BindView(R.id.lin_communuty_news_nodata)
    LinearLayout linCommunutyNewsNodata;
    private int pageIndex = 1;
    private HttpServiceImpl service;


    private CommunityNewListAdapter adapter;
    private List<HotInformationBean> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initData();
        initView();
        adapter = new CommunityNewListAdapter(data, this);
        commNewListRv.setLayoutManager(new LinearLayoutManager(this));
        commNewListRv.setAdapter(adapter);

    }

    //获取数据
    private void initData() {
        data.clear();
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
                                data.add(bean);
                            }
                        } else {
                            Toast.makeText(CommunityNewsListActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("communityJSONException", e.toString());
                    } finally {
                        if (data.size() > 0) {
                            commNewListQr.setVisibility(View.VISIBLE);
                            linCommunutyNewsNodata.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                            commNewListQr.setVisibility(View.GONE);
                            linCommunutyNewsNodata.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("communityonError", ex.toString());
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


    //获取数据
    private void initDataMore() {
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

                                bean.setUrl(url);
                                bean.setId(id);
                                bean.setTitle(title);
                                bean.setTime(time);
                                bean.setKeyWord(keyword);
                                data.add(bean);
                            }
                        } else {
                            Toast.makeText(CommunityNewsListActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("communityJSONException", e.toString());
                    } finally {
                        if (data.size() > 0) {
                            commNewListQr.setVisibility(View.VISIBLE);
                            linCommunutyNewsNodata.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                            commNewListQr.setVisibility(View.GONE);
                            linCommunutyNewsNodata.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("communityonError", ex.toString());
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

    private void initView() {
        setListener();

    }

    //设置其他的监听
    private void setListener() {
        commNewListQr.setLoadMoreEnable(true);
        commNewListQr.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                initData();
                commNewListQr.refreshComplete();

            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                initDataMore();
                commNewListQr.LoadMoreComplete();

            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_community_news_list;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.comm_new_list_back)
    public void onViewClicked() {
        finish();
    }
}
