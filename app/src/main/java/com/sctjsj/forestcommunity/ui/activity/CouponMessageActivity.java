package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.DiscountsBean;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 优惠卷详情页面
 */
@Route(path = "/main/act/CouponMessageActivity")
public class CouponMessageActivity extends BaseAppcompatActivity {

    @BindView(R.id.coupon_date)
    TextView couponDate;
    @BindView(R.id.coupon_content)
    TextView couponContent;
    @BindView(R.id.coupon_condition)
    TextView couponCondition;
    @BindView(R.id.coupon_Price)
    TextView couponPrice;
    @BindView(R.id.coupon_buy_btn)
    Button couponBuyBtn;
    @BindView(R.id.activity_coupon_message)
    LinearLayout activityCouponMessage;

    @Autowired(name = "id")
    int id=0;
    private HttpServiceImpl service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        getCouponMessage();
    }



    @Override
    public int initLayout() {
        return R.layout.activity_coupon_message;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.coupon_back, R.id.coupon_buy_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.coupon_back:
                finish();
                break;
            case R.id.coupon_buy_btn:
                if(UserAuthUtil.isUserLogin()){
                    saveOrder();
                }else {
                    ARouter.getInstance().build("/main/act/login").navigation();
                }
                break;
        }
    }

    //获取卡卷详情
    private void getCouponMessage() {
        HashMap<String,String> body=new HashMap<>();
        body.put("ctype","active");
        body.put("id",id+"");
        service.doCommonPost(null, MainUrl.baseSingleQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getCouponMessage",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONObject disobj=object.getJSONObject("data");
                            DiscountsBean disbean=new DiscountsBean();
                            disbean.setBeginTime(disobj.getString("beginTime"));
                            disbean.setCondition(disobj.getString("condition"));
                            disbean.setContent(disobj.getString("content"));
                            disbean.setEndTime(disobj.getString("endTime"));
                            disbean.setId(disobj.getInt("id"));
                            disbean.setPrice(disobj.getDouble("price"));
                            disbean.setDisName(disobj.getString("name"));
                            initView(disbean);
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

    //保存订单
    private void saveOrder(){
        HashMap<String,String> body=new HashMap<>();
        body.put("goodId",id+"");
        service.doCommonPost(null, MainUrl.saveOrder, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            int orderId=object.getInt("orderId");
                            ARouter.getInstance().build("/main/act/ConfirmPayActivity").withInt("orderId",orderId).navigation();
                            finish();
                        }else {
                            Toast.makeText(CouponMessageActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
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


    //初始化布局
    private void initView( DiscountsBean disbean) {
        String[] splitStart = disbean.getBeginTime().split(" ");
        String start=splitStart[0];
        String[] splitEnd = disbean.getEndTime().split(" ");
        String end=splitEnd[0];
        couponDate.setText(start+"至"+end);
        couponContent.setText(disbean.getContent());
        couponCondition.setText(disbean.getCondition());
        couponPrice.setText(disbean.getPrice()+"");

    }
}
