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
import com.sctjsj.forestcommunity.util.Validator;
import com.squareup.picasso.MemoryPolicy;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/regist")
public class RegistActivity extends BaseAppcompatActivity {

    @BindView(R.id.edt_phone_number)
    EditText edtPhoneNumber;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.edt_password_agin)
    EditText edtPasswordAgin;
    @BindView(R.id.edt_phone_code)
    EditText edtPhoneCode;
    @BindView(R.id.iv_choose_clause)
    ImageView ivChooseClause;
    @BindView(R.id.bt_regist)
    Button btRegist;
    @BindView(R.id.Img_code_layout)
    LinearLayout Img_code_layout;
    @BindView(R.id.edt_img_code)
    EditText edt_img_code;
    @BindView(R.id.tv_get_phonecode)
    TextView tvGetPhonecode;

    private CountDownTimer timer;
    private long alltime = 61 * 1000L;
    private long intervaltime = 1000L;

    private boolean flag = false;
    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();

    }

    private void initView() {
        getIconCode();
        btRegist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btRegist.setBackgroundResource(R.drawable.longin_bt_down_background);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btRegist.setBackgroundResource(R.drawable.longin_bt_backgroungd);
                }
                return false;
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_regist;
    }

    @Override
    public void reloadData() {

    }

    private void getIconCode() {
        Img_code_layout.removeAllViews();
        ImageView view = new ImageView(this);
        PicassoUtil.getPicassoObject().load(MainUrl.getPicCode).memoryPolicy(MemoryPolicy.NO_CACHE).into(view);
        Img_code_layout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }


    @OnClick({R.id.linear_back, R.id.tv_get_phonecode, R.id.lin_agree_clause, R.id.bt_regist, R.id.tv_login, R.id.tv_clause})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.linear_back:
                finish();
                break;
            //获取短信验证码
            case R.id.tv_get_phonecode:
                getMessageCode();
                break;
            //同意条款
            case R.id.lin_agree_clause:
                if (flag) {
                    ivChooseClause.setVisibility(View.GONE);
                    flag = false;
                } else {
                    ivChooseClause.setVisibility(View.VISIBLE);
                    flag = true;
                }
                break;
            //注册
            case R.id.bt_regist:
                regist();
                break;
            //跳登陆
            case R.id.tv_login:
                ARouter.getInstance().build("/main/act/login").navigation();
                finish();
                break;
            //跳条款页
            case R.id.tv_clause:
                break;
            case R.id.Img_code_layout:
                //获取验证码
                getIconCode();
                break;
        }
    }

    //获取短信验证码
    private void getMessageCode() {
        if (StringUtil.isBlank(edtPhoneNumber.getText().toString().trim())) {
//            edtPhoneNumber.setError("号码格式不正确");
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!Validator.isMobile(edtPhoneNumber.getText().toString().trim())) {
                edtPhoneNumber.setError("请输入正确的电话号码");
                return;
            }
        }

        if (StringUtil.isBlank(edt_img_code.getText().toString().trim())) {
            Toast.makeText(this, "请输入图片验证码！", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> body = new HashMap<>();
        body.put("mobile", edtPhoneNumber.getText().toString().trim());
        body.put("code", edt_img_code.getText().toString().trim());
        service.doCommonPost(null, MainUrl.SendMessageUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getMessageCode", result.toString());
                if (!StringUtil.isBlank(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("resultMsg");
                        if (results) {
                            //倒计时
                            countDown();
                            Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getMessageJSONException", e.toString());
                    }

                }
            }

            @Override
            public void onError(Throwable ex) {
                Log.e("getMessageCodeonError", ex.toString());
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

    //注册
    private void regist() {
        //判断资料是否齐全
        if (StringUtil.isBlank(edtPhoneNumber.getText().toString().trim())) {
//            edtPhoneNumber.setError("请输入电话号码");
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!Validator.isMobile(edtPhoneNumber.getText().toString().trim())) {
                edtPhoneNumber.setError("请输入正确的电话号码");
                return;
            }
        }

        if (StringUtil.isBlank(edtEmail.getText().toString().trim())) {
//            edtEmail.setError("请输入e-mail");
            Toast.makeText(this, "请输入e-mail", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!Validator.isEmail(edtEmail.getText().toString().trim())) {
                edtEmail.setError("请输入正确的e-mail");
                return;
            }
        }

        if (StringUtil.isBlank(edtPassword.getText().toString().trim())) {
//            edtPassword.setError("请输入密码");
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(edtPassword.getText().toString().trim().length()<6){
                edtPassword.setError("密码长度至少6位");
                return;
            }
        }

        if (StringUtil.isBlank(edtPasswordAgin.getText().toString().trim())) {
//            edtPasswordAgin.setError("请输入确认密码");
            Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!edtPasswordAgin.getText().toString().trim().equals(edtPassword.getText().toString().trim())) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        //暂时还没做
        if (StringUtil.isBlank(edtPhoneCode.getText().toString().trim())) {
//            edtPhoneCode.setError("请输入验证码");
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!flag) {
            Toast.makeText(this, "您还未同意《独木成林管理条款》", Toast.LENGTH_SHORT).show();
            return;
        }

        final HashMap<String, String> map = new HashMap<>();
        map.put("username", edtPhoneNumber.getText().toString());
        map.put("password", edtPassword.getText().toString());
        map.put("phone", edtPhoneNumber.getText().toString());
        map.put("email", edtEmail.getText().toString());
        map.put("checkCode", edtPhoneCode.getText().toString());
        service.doCommonPost(null, MainUrl.Regist, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("registMap",map.toString());
                LogUtil.e("registonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("resultMsg");
                        if (results) {
                            Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
                            ARouter.getInstance().build("/main/act/login").navigation();
                            finish();
                        } else {
                            Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("registJSONException", e.toString());
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

    //倒计时
    private void countDown() {
        tvGetPhonecode.setText((int) alltime / 1000 - 1 + "S");
        tvGetPhonecode.setTextColor(getResources().getColor(R.color.color_primary_black));
        tvGetPhonecode.setEnabled(false);
        //参数alltime是总时间，intervaltime是间隔时间
        timer = new CountDownTimer(alltime + 100, intervaltime) {
            //每个间隔时间一到就会调用一次，需要自己实现。参数millisUntilFinished是指剩下的时间。
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("时间", millisUntilFinished + "");
                tvGetPhonecode.setText((int) millisUntilFinished / 1000 - 1 + "s后重试");
            }

            //倒计时完成的方法
            @Override
            public void onFinish() {
                tvGetPhonecode.setEnabled(true);
                tvGetPhonecode.setTextColor(getResources().getColor(R.color.color_code));
                tvGetPhonecode.setText("获取验证码");
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
}
