package com.sctjsj.forestcommunity.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alipay.sdk.app.PayTask;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.base.util.setup.SetupUtil;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.OrderBean;
import com.sctjsj.forestcommunity.javabean.PerShopBean;
import com.sctjsj.forestcommunity.pay.alipay.PayResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 确认支付的界面
 */

@Route(path = "/main/act/ConfirmPayActivity")
public class ConfirmPayActivity extends BaseAppcompatActivity {

    @BindView(R.id.pay_order_back)
    RelativeLayout payOrderBack;
    @BindView(R.id.confirm_Store_Img)
    ImageView confirmStoreImg;
    @BindView(R.id.confirm_remark)
    TextView confirmRemark;
    @BindView(R.id.pay_order_alipayImg)
    ImageView payOrderAlipayImg;
    @BindView(R.id.ll_pay_order_alipayImg)
    LinearLayout llPayOrderAlipayImg;
    @BindView(R.id.pay_order_weixinImg)
    ImageView payOrderWeixinImg;
    @BindView(R.id.pay_order_weixin)
    LinearLayout payOrderWeixin;
    @BindView(R.id.pay_order_Btnsure)
    Button payOrderBtnsure;
    @BindView(R.id.activity_confirm_pay)
    LinearLayout activityConfirmPay;
    @BindView(R.id.confirm_pay_StoreName)
    TextView confirmPayStoreName;
    @BindView(R.id.confirm_pay_price)
    TextView confirmPayPrice;

    @Autowired(name = "orderId")
    int orderId = 0;


    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    private int flag = 1; //1支付宝  2微信
    private boolean state = false;//状态  false未支付 true 支付
    private HttpServiceImpl service;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Log.e("支付成功", "--------");


                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        checkAlipay();
        getOrderMessage();

    }

    //获取订单信息
    private void getOrderMessage() {
        HashMap<String, String> body = new HashMap<>();
        body.put("ctype", "order");
        body.put("id", orderId + "");
        body.put("jf", "active|store|storeLogo");
        service.doCommonPost(null, MainUrl.baseSingleQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getOrderMessage", result.toString());
                if (!StringUtil.isBlank(result)) {
                    try {

                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            JSONObject data = object.getJSONObject("data");
                            OrderBean order = new OrderBean();
                            PerShopBean shop = new PerShopBean();
                            double payValue = data.getDouble("payValue");
                            JSONObject store = data.getJSONObject("active").getJSONObject("store");
                            String shopName = store.getString("storeName");
                            int storeId = store.getInt("id");
                            String storeLogo = store.getJSONObject("storeLogo").getString("url");
                            JSONObject active=data.getJSONObject("active");
                            shop.setStoreName(shopName);
                            shop.setStoreLogo(storeLogo);
                            shop.setId(storeId);
                            shop.setStoreAddress(active.getString("content"));
                            order.setId(data.getInt("id"));
                            order.setShop(shop);
                            order.setState(data.getInt("state"));
                            order.setPayValue(payValue);
                            initView(order);
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


    //获取订单数据 初始化数据
    private void initView(OrderBean order) {
        if (null != order) {
            PerShopBean bean = order.getShop();
            PicassoUtil.getPicassoObject().load(bean.getStoreLogo())
                    .resize(DpUtils.dpToPx(this, 80), DpUtils.dpToPx(this, 80))
                    .into(confirmStoreImg);
            confirmPayPrice.setText("¥"+order.getPayValue() + "");
            confirmRemark.setText(bean.getStoreAddress());
            confirmPayStoreName.setText(bean.getStoreName());
        }
    }

    @Override
    public int initLayout() {
        return R.layout.activity_confirm_pay;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.pay_order_back, R.id.ll_pay_order_alipayImg, R.id.pay_order_weixin, R.id.pay_order_Btnsure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pay_order_back:
                finish();
                break;
            case R.id.ll_pay_order_alipayImg:
                checkAlipay();
                break;
            case R.id.pay_order_weixin:
                checkWeiXin();
                break;
            case R.id.pay_order_Btnsure:
                //确认支付
                if (getPayWayIsOrNot()) {
                    switch (flag) {

                        case 1:
                            //支付宝
                            getAlipaySign();
                            break;
                        case 2:
                            //微信

                            break;
                    }
                }
                break;
        }
    }


    //判断支付是否可用
    private boolean getPayWayIsOrNot() {
        boolean type = false;
        switch (flag) {
            case 0:
                Toast.makeText(this, "请选择支付方式！", Toast.LENGTH_SHORT).show();
                type = false;
                break;
            case 1:
                //判断手机是否安装支付宝
                if (SetupUtil.getInstance(ConfirmPayActivity.this).isXSetuped("com.eg.android.AlipayGphone")) {
                    type = true;
                } else {
                    //未安装支付宝
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmPayActivity.this);
                    builder.setTitle("异常提示");
                    builder.setMessage("系统检测您的手机暂未安装支付宝，无法使用支付宝支付结账");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                    type = false;
                }

                break;
            case 2:
                if (SetupUtil.getInstance(ConfirmPayActivity.this).isXSetuped("com.tencent.mm")) {
                    type = true;
                } else {
                    //未安装微信
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmPayActivity.this);
                    builder.setTitle("异常提示");
                    builder.setMessage("系统检测您的手机暂未安装微信，无法使用微信支付结账");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                    type = false;
                }
                break;


        }
        return type;
    }


    //选择支付宝
    private void checkAlipay() {
        payOrderWeixinImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_no));
        payOrderAlipayImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_yes));
        flag = 1;
    }


    //选择微信付款
    private void checkWeiXin() {
        payOrderWeixinImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_yes));
        payOrderAlipayImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_no));
        flag = 2;
    }


    /**
     * 调用支付宝支付
     */
    private void alipay(final String sign) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(ConfirmPayActivity.this);
                Map<String, String> result = alipay.payV2(sign, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //获取支付宝签名
    private void getAlipaySign() {
        HashMap<String, String> body = new HashMap<>();
        body.put("ofId", orderId + "");
        service.doCommonPost(null, MainUrl.getAlipaySignUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getAlipaySign", result.toString());
                if (!StringUtil.isBlank(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        String sign = object.getString("sign");
                        if (!StringUtil.isBlank(sign)) {
                            alipay(sign);
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
