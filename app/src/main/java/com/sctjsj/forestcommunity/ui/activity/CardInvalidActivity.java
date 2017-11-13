package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.CardAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.CardBean;
import com.sctjsj.forestcommunity.javabean.DiscountsBean;
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

@Route(path = "/main/act/user/CardInvalid")
//已失效的券
public class CardInvalidActivity extends BaseAppcompatActivity {
    @BindView(R.id.rv_card_invalid)
    RecyclerView rvCardInvalid;
    @BindView(R.id.ql_card_invalid)
    QRefreshLayout qlCardInvalid;
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



    private void initView() {
        manager = new LinearLayoutManager(this);
         adapter = new CardAdapter(list, this);
        rvCardInvalid.setLayoutManager(manager);
        rvCardInvalid.setAdapter(adapter);
        qlCardInvalid.setLoadMoreEnable(true);
        qlCardInvalid.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                qlCardInvalid.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                qlCardInvalid.LoadMoreComplete();
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_card_invalid;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }

    //获取已失效的卡券
    private void initData() {
        //获取卡券
        getMineCard();
    }


    private void  getMineCard(){
        HashMap<String,String> body=new HashMap<>();
        body.put("isoverdue","2");
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
                                    disBean.setType(2);
                                    list.add(disBean);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getMineCard",e.toString());
                    }finally {
                        if(list.size()>0){
                            adapter.notifyDataSetChanged();
                            qlCardInvalid.setVisibility(View.VISIBLE);
                            linNoData.setVisibility(View.GONE);
                        }else {
                            qlCardInvalid.setVisibility(View.GONE);
                            linNoData.setVisibility(View.VISIBLE);
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
