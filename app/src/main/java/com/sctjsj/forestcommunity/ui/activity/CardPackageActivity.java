package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.CardAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.CardBean;
import com.sctjsj.forestcommunity.javabean.DiscountsBean;
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

@Route(path = "/main/act/user/CardPackage")
//卡包
public class CardPackageActivity extends BaseAppcompatActivity {

    @BindView(R.id.rv_card)
    RecyclerView rvCard;
    @BindView(R.id.ql_card)
    QRefreshLayout qlCard;
    @BindView(R.id.lin_NoData)
    LinearLayout linNoData;

    private List<DiscountsBean> list = new ArrayList<>();
    private LinearLayoutManager manager;
    private CardAdapter adapter;
    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initData();
        initView();
    }

    private void initData() {
        getMineCard();
    }

    private void initView() {
        manager = new LinearLayoutManager(this);
        adapter = new CardAdapter(list, this);
        rvCard.setLayoutManager(manager);
        rvCard.setAdapter(adapter);
        setFootView(rvCard);
        qlCard.setLoadMoreEnable(true);
        qlCard.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                qlCard.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                qlCard.LoadMoreComplete();
            }
        });
    }

    //
    private void setFootView(RecyclerView view) {
        View foot= LayoutInflater.from(this).inflate(R.layout.card_footview,view,false);
        adapter.setFootView(foot);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_card_package;
    }

    @Override
    public void reloadData() {

    }


    @OnClick({R.id.linear_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_back:
                finish();
                break;
        }
    }

    //获取卡券
    private void  getMineCard(){
        HashMap<String,String> body=new HashMap<>();
        body.put("isoverdue","1");
        service.doCommonPost(null, MainUrl.overdueCard, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getMineCard",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONArray arr=object.getJSONArray("list");
                            if(null!=arr&&arr.length()>0){
                                for (int i = 0; i <arr.length() ; i++) {
                                    JSONObject disObj=arr.getJSONObject(i);
                                    DiscountsBean disBean=new DiscountsBean();
                                    disBean.setShowPrice(disObj.getDouble("show_price"));
                                    disBean.setCondition(disObj.getString("condition"));
                                    disBean.setContent(disObj.getString("content"));
                                    disBean.setDisName(disObj.getString("name"));
                                    disBean.setBeginTime(disObj.getString("begin_time"));
                                    disBean.setEndTime(disObj.getString("end_time"));
                                    disBean.setType(1);
                                    list.add(disBean);
                                }
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getMineCard",e.toString());
                    }finally {
                       /* if(list.size()>0){
                            adapter.notifyDataSetChanged();
                            qlCard.setVisibility(View.VISIBLE);
                            linNoData.setVisibility(View.GONE);
                        }else {
                            qlCard.setVisibility(View.GONE);
                            linNoData.setVisibility(View.VISIBLE);
                        }*/
                        adapter.notifyDataSetChanged();
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
