package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;
import com.sctjsj.forestcommunity.util.Validator;
import com.squareup.picasso.MemoryPolicy;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/ChangePhone")
//修改手机号码
public class ChangePhoneActivity extends BaseAppcompatActivity {

    @BindView(R.id.tv_nowPhone)
    TextView tvNowPhone;
    @BindView(R.id.edt_newPhone)
    EditText edtNewPhone;
    @BindView(R.id.edt_inputCode)
    EditText edtInputCode;
    @BindView(R.id.bt_confirm)
    Button btConfirm;
    @Autowired(name = "phone")
    String phone;
    @BindView(R.id.tv_getPhoneCode)
    TextView tvGetPhoneCode;
    @BindView(R.id.edt_input_imgCode)
    EditText edtInputImgCode;
    @BindView(R.id.lin_change_imgcode)
    LinearLayout linChangeImgcode;

    private CountDownTimer timer;
    private long alltime = 61 * 1000L;
    private long intervaltime = 1000L;
    private HttpServiceImpl server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        server= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();

    }

    private void initView() {
        //获取图片验证码
        getIconCode();

        btConfirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btConfirm.setBackgroundResource(R.drawable.longin_bt_down_background);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btConfirm.setBackgroundResource(R.drawable.longin_bt_backgroungd);
                }
                return false;
            }
        });
        StringBuilder builder = new StringBuilder();
        builder.append(phone.substring(0, 3) + "-").append(phone.substring(3, 7) + "-")
                .append(phone.substring(7, 11));
        tvNowPhone.setText(builder);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_change_phone;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.tv_getPhoneCode, R.id.bt_confirm,R.id.lin_change_imgcode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_back:
                finish();
                break;
            case R.id.tv_getPhoneCode:
                //获取短信验证码
                getcode();
                break;
            //提交
            case R.id.bt_confirm:
                saveNewPhone();
                break;
            //获取图片验证码
            case R.id.lin_change_imgcode:
                getIconCode();
                break;
        }
    }

    //保存新手机
    private void saveNewPhone() {

        if(StringUtil.isBlank(edtNewPhone.getText().toString().trim())){
            Toast.makeText(this, "请输入新手机号码", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(!Validator.isMobile(edtNewPhone.getText().toString().trim())){
                Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(StringUtil.isBlank(edtInputImgCode.getText().toString().trim())){
            Toast.makeText(this, "请输入图片验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(StringUtil.isBlank(edtInputCode.getText().toString().trim())){
            Toast.makeText(this, "请输入短信验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", UserAuthUtil.getUserId()+"");
        map.put("phone",edtNewPhone.getText().toString());
        map.put("code",edtInputCode.getText().toString());
        server.doCommonPost(null, MainUrl.saveNewPhone, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("saveNewPhoneonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject o=new JSONObject(result);
                        boolean results=o.getBoolean("result");
                        String msg=o.getString("msg");
                        if(results){
                            Toast.makeText(ChangePhoneActivity.this, msg, Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ChangePhoneActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("saveNewPhoneJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("saveNewPhoneonError",ex.toString());
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

    //获取短信验证码
    public void getcode() {
        if(StringUtil.isBlank(edtNewPhone.getText().toString().trim())){
            Toast.makeText(this, "请输入新手机号码", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(!Validator.isMobile(edtNewPhone.getText().toString().trim())){
                edtNewPhone.setError("请输入正确的手机号码");
                return;
            }
        }

        if(StringUtil.isBlank(edtInputImgCode.getText().toString().trim())){
            Toast.makeText(this, "请输入图片验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String,String> map=new HashMap<>();
        map.put("mobile",edtNewPhone.getText().toString().trim());
        map.put("code",edtInputImgCode.getText().toString().trim());
        server.doCommonPost(null, MainUrl.getPhoenCode, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("getCodeonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("resultMsg");
                        if(results){
                            //倒计时
                            countDown();
                            Toast.makeText(ChangePhoneActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ChangePhoneActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("getCodeJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("getCodeonError",ex.toString());
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

    //倒计时
    private void countDown() {
        tvGetPhoneCode.setText((int) alltime / 1000 - 1 + "S");
        tvGetPhoneCode.setTextColor(getResources().getColor(R.color.color_primary_black));
        tvGetPhoneCode.setEnabled(false);
        //参数alltime是总时间，intervaltime是间隔时间
        timer = new CountDownTimer(alltime + 100, intervaltime) {
            //每个间隔时间一到就会调用一次，需要自己实现。参数millisUntilFinished是指剩下的时间。
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("时间", millisUntilFinished + "");
                tvGetPhoneCode.setText((int) millisUntilFinished / 1000 - 1 + "s后重试");
            }

            //倒计时完成的方法
            @Override
            public void onFinish() {
                tvGetPhoneCode.setEnabled(true);
                tvGetPhoneCode.setTextColor(getResources().getColor(R.color.color_code));
                tvGetPhoneCode.setText("获取验证码");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //获取图片验证码
    private void getIconCode() {
        linChangeImgcode.removeAllViews();
        ImageView view = new ImageView(this);
        PicassoUtil.getPicassoObject().load(MainUrl.getPicCode).memoryPolicy(MemoryPolicy.NO_CACHE).into(view);
        linChangeImgcode.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }



}
