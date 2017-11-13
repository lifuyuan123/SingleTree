package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.FootMarkAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.FootMarkBean;
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

@Route(path = "/main/act/user/Footmark")
//我的足迹
public class FootmarkActivity extends BaseAppcompatActivity {
    @BindView(R.id.rv_footmark)
    RecyclerView rvFootmark;
    @BindView(R.id.ql_footmarl)
    QRefreshLayout qlFootmarl;
    @BindView(R.id.tv_footmark_edit)
    TextView tvFootmarkEdit;
    @BindView(R.id.lin_foormark_edit)
    LinearLayout linFoormarkEdit;
    @BindView(R.id.lin_delete)
    LinearLayout linDelete;
    @BindView(R.id.lin_footmark_nodata)
    LinearLayout linFootmarkNodata;
    @BindView(R.id.checkbox_all)
    CheckBox checkboxAll;
    private FootMarkAdapter adapter;
    private LinearLayoutManager manager;
    private List<FootMarkBean> list = new ArrayList<>();
    private boolean isShow = true;
    private HttpServiceImpl service;
    private int pageIndex = 1;
    private boolean isAll=false;//是否全选

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        getFootMark();
//        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < 15; i++) {
            FootMarkBean bean = new FootMarkBean();
            bean.setCheck(false);
            bean.setName("足迹足迹足迹足迹足迹足迹足迹足迹足迹足迹");
            bean.setIconUrl("adsf");
            bean.setTime("2017-8-" + i);
            list.add(bean);

        }

        if (list.size() > 0) {
            linFoormarkEdit.setVisibility(View.VISIBLE);
            qlFootmarl.setVisibility(View.VISIBLE);
            linFootmarkNodata.setVisibility(View.GONE);

        } else {
            linFoormarkEdit.setVisibility(View.GONE);

            qlFootmarl.setVisibility(View.GONE);
            linFootmarkNodata.setVisibility(View.VISIBLE);
        }

    }

    private void initView() {
        adapter = new FootMarkAdapter(this, list);
        manager = new LinearLayoutManager(this);
        rvFootmark.setLayoutManager(manager);
        rvFootmark.setAdapter(adapter);
        qlFootmarl.setLoadMoreEnable(true);
        qlFootmarl.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                getFootMark();
                qlFootmarl.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                getFootMarkMore();
                qlFootmarl.LoadMoreComplete();
            }
        });

        checkboxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    adapter.setAllMark();
                    isAll=true;
                }else {
                    if(isAll){
                        adapter.clearMark();
                        isAll=false;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setCallBack(new FootMarkAdapter.CallBack() {
            @Override
            public void onItemClick(int position) {
                FootMarkBean bean=list.get(position);
                if(isShow){
                    switch (bean.getType()){
                        //周边商家
                        case 1:
                            ARouter.getInstance().build("/main/act/ShopDetail").withInt("storeId",bean.getTid()).navigation();
                            break;
                        //新闻
                        case 2:
                            ARouter.getInstance().build("/main/act/NewsDetails").withInt("id",bean.getTid()).withString("title",bean.getName()).navigation();
                            break;
                        //帖子
                        case 3:
                            ARouter.getInstance().build("/main/act/CommunityForumActivity").withInt("froumId",bean.getTid()).navigation();
                            break;
                    }
                }else {
                    if(bean.isCheck()){
                        if(isAll){
                            isAll=false;
                            checkboxAll.setChecked(false);
                        }
                        bean.setCheck(false);
                    }else {
                        bean.setCheck(true);
                    }
                    adapter.notifyItemChanged(position);
                }

            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_footmark;
    }

    @Override
    public void reloadData() {

    }


    @OnClick({R.id.linear_back, R.id.lin_foormark_edit, R.id.tv_cancel, R.id.tv_foot_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.linear_back:
                finish();
                break;
            //编辑
            case R.id.lin_foormark_edit:
                if (isShow) {
                    adapter.setShow(isShow);
                    adapter.clearMark();
                    tvFootmarkEdit.setText("完成");
                    linDelete.setVisibility(View.VISIBLE);
                    isShow = false;
                } else {
                    adapter.setShow(isShow);
                    tvFootmarkEdit.setText("编辑");
                    linDelete.setVisibility(View.GONE);
                    isShow = true;
                }
                break;
            //取消
            case R.id.tv_cancel:
                adapter.setShow(isShow);
                tvFootmarkEdit.setText("编辑");
                linDelete.setVisibility(View.GONE);
                isShow = true;
                break;
            //删除
            case R.id.tv_foot_delete:
                if (adapter.delete()==null) {
                    Toast.makeText(this, "您还没有选中足迹", Toast.LENGTH_SHORT).show();
                } else {
                    deleteFootMark();
                };
                break;
        }
    }

    public void getFootMark() {
        list.clear();
        pageIndex = 1;
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("ctype", "track");
        map.put("cond", "{tuser:{id:" + UserAuthUtil.getUserId() + "}}");
        map.put("orderby", "id desc");
        service.doCommonPost(null, MainUrl.footMark, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("footmarkonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    pageIndex++;
                    try {
                        JSONObject o = new JSONObject(result);
                        boolean results = o.getBoolean("result");
                        String msg = o.getString("msg");
                        if (results) {
                            JSONArray array = o.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                FootMarkBean bean = new FootMarkBean();
                                JSONObject object = array.getJSONObject(i);
                                int id = object.getInt("id");
                                int tid = object.getInt("tid");
                                int type = object.getInt("type");
                                String time = object.getString("skimTime");
                                String title=object.getString("title");

                                bean.setId(id);
                                bean.setType(type);
                                bean.setTid(tid);
                                bean.setTime(time);
                                bean.setName(title);
                                bean.setCheck(false);
                                list.add(bean);
                            }
                        } else {
                            Toast.makeText(FootmarkActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("footmarkJSONException", e.toString());
                    } finally {
                        if (list.size() > 0) {
                            linFoormarkEdit.setVisibility(View.VISIBLE);
                            qlFootmarl.setVisibility(View.VISIBLE);
                            linFootmarkNodata.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                            linFoormarkEdit.setVisibility(View.GONE);
                            qlFootmarl.setVisibility(View.GONE);
                            linFootmarkNodata.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("footmarkonError", ex.toString());
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

    public void getFootMarkMore() {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("ctype", "track");
        map.put("cond", "{tuser:{id:" + UserAuthUtil.getUserId() + "}}");
        map.put("orderby", "id desc");
        service.doCommonPost(null, MainUrl.footMark, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("footmarkonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    pageIndex++;
                    try {
                        JSONObject o = new JSONObject(result);
                        boolean results = o.getBoolean("result");
                        String msg = o.getString("msg");
                        if (results) {
                            JSONArray array = o.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                FootMarkBean bean = new FootMarkBean();
                                JSONObject object = array.getJSONObject(i);
                                int id = object.getInt("id");
                                int tid = object.getInt("tid");
                                int type = object.getInt("type");
                                String time = object.getString("skimTime");
                                String title=object.getString("title");


                                bean.setId(id);
                                bean.setType(type);
                                bean.setTid(tid);
                                bean.setTime(time);
                                bean.setName(title);
                                bean.setCheck(false);
                                list.add(bean);
                            }
                        } else {
                            Toast.makeText(FootmarkActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("footmarkJSONException", e.toString());
                    } finally {
                        if (list.size() > 0) {
                            linFoormarkEdit.setVisibility(View.VISIBLE);
                            qlFootmarl.setVisibility(View.VISIBLE);
                            linFootmarkNodata.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                            linFoormarkEdit.setVisibility(View.GONE);
                            qlFootmarl.setVisibility(View.GONE);
                            linFootmarkNodata.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("footmarkonError", ex.toString());
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

    //批量删除足迹
    public void deleteFootMark(){

        StringBuffer stringlist=new StringBuffer();
        final List<FootMarkBean> movelist=adapter.delete();

        for (int i = 0; i <movelist.size() ; i++) {
            if(movelist.size()-1==i||movelist.size()==1){
                stringlist.append(movelist.get(i).getId());
            }else {
                stringlist.append(movelist.get(i).getId()+",");
            }
        }
        HashMap<String,String> map=new HashMap<>();
        map.put("footlist",stringlist.toString());
        LogUtil.e("deleteMap",map.toString());
        service.doCommonPost(null, MainUrl.deleteFootMark, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("deleteFootonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");
                        if(results){
                            list.removeAll(movelist);
                            if (adapter.getList().size() <= 0) {
                                //删除后没有数据隐藏“编辑/完成”
                                linFoormarkEdit.setVisibility(View.GONE);
                                //显示没有数据
                                qlFootmarl.setVisibility(View.GONE);
                                linFootmarkNodata.setVisibility(View.VISIBLE);
                                //隐藏删除布局
                                linDelete.setVisibility(View.GONE);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                            Toast.makeText(FootmarkActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(FootmarkActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("deleteFootJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("deleteFootonError",ex.toString());
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
