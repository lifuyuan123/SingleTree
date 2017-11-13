package com.sctjsj.forestcommunity.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.IHttpService;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.event.UserLoginEvent;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.UserBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sctjsj.basemodule.core.config.AsyncNotifyCode.CODE_LOGIN_FAILED;
import static com.sctjsj.basemodule.core.config.AsyncNotifyCode.CODE_LOGIN_SUCCESS;
import static com.sctjsj.basemodule.core.config.Tag.TAG_LOGIN_RESULT;

@Route(path = "/main/act/login")
public class LoginActivity extends BaseAppcompatActivity {

    @BindView(R.id.edt_login_phone)
    EditText edtLoginPhone;
    @BindView(R.id.edt_login_password)
    EditText edtLoginPassword;
    @BindView(R.id.lin_regist)
    LinearLayout linRegist;
    @BindView(R.id.bt_login)
    Button btLogin;

    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        btLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    btLogin.setBackgroundResource(R.drawable.longin_bt_down_background);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    btLogin.setBackgroundResource(R.drawable.longin_bt_backgroungd);
                }
                return false;
            }
        });
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();


    }

    @Override
    public int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.bt_login, R.id.tv_forget_password, R.id.tv_regist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.linear_back:
                finish();
                break;
            //登陆
            case R.id.bt_login:
                login();
                break;
            //忘记密码
            case R.id.tv_forget_password:
                ARouter.getInstance().build("/main/act/ForgetPSW").navigation();
                finish();
                break;
            //跳注册
            case R.id.tv_regist:
                ARouter.getInstance().build("/main/act/regist").navigation();
                finish();
                break;
        }
    }

    //登录的方法
    private void login() {
        //判断输入的用户名和密码
        if(StringUtil.isBlank(edtLoginPhone.getText().toString().trim())){
            edtLoginPhone.setError("用户名格式错误");
            Toast.makeText(this, "请输入正确的用户名！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(StringUtil.isBlank(edtLoginPassword.getText().toString().trim())){
            edtLoginPassword.setError("密码格式错误");
            Toast.makeText(this, "请输入正确的用户名！", Toast.LENGTH_SHORT).show();
            return;
        }


        //用户名和账号ok
        HashMap<String,String> body=new HashMap<>();
        body.put("userName",edtLoginPhone.getText().toString().trim());
        body.put("password",edtLoginPassword.getText().toString().trim());

        service.doCommonPost(null, MainUrl.LoginUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("login",result);
                if(!StringUtil.isBlank(result)){

                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            //登录成功
                            String info=object.getString("info");
                            if(!StringUtil.isBlank(info)){
                                //获取到用户信息
                                JSONObject userObj=new JSONObject(info);
                                UserBean bean=new UserBean();
                                String email=userObj.getString("email");
                                int id=userObj.getInt("id");
                                String phone=userObj.getString("phone");
                                String realName=userObj.getString("realName");
                                String remark="";
                                if (userObj.has("remark")){
                                    remark=userObj.getString("remark");
                                }
                                String username=userObj.getString("username");
                                bean.setEmail(email);
                                bean.setId(id);
                                bean.setPhone(phone);
                                bean.setRealName(realName);
                                bean.setRemark(remark);
                                bean.setUserName(username);
                                SPFUtil.put(Tag.TAG_USER,new Gson().toJson(bean));
                                //发送用户登录成功
                                EventBus.getDefault().post(new UserLoginEvent(1));

                                //发送本地广播通知拦截器
                                Intent intent=new Intent(Tag.TAG_LOGIN_FILTER);
                                intent.putExtra(TAG_LOGIN_RESULT,CODE_LOGIN_SUCCESS);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                finish();
                            }


                        }else {
                            //登录失败
                            if (SPFUtil.contains(Tag.TAG_TOKEN)) {
                                SPFUtil.removeOne(Tag.TAG_TOKEN);
                            }
                            //清除本地用户信息
                            if (SPFUtil.contains(Tag.TAG_USER)) {
                                SPFUtil.removeOne(Tag.TAG_USER);
                            }
                            Intent intent=new Intent(Tag.TAG_LOGIN_FILTER);
                            intent.putExtra(TAG_LOGIN_RESULT,CODE_LOGIN_FAILED);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            Toast.makeText(LoginActivity.this,object.getString("resultMsg") , Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("loginJSONException",e.toString());
                        if (SPFUtil.contains(Tag.TAG_TOKEN)) {
                            SPFUtil.removeOne(Tag.TAG_TOKEN);
                        }
                        //清除本地用户信息
                        if (SPFUtil.contains(Tag.TAG_USER)) {
                            SPFUtil.removeOne(Tag.TAG_USER);
                        }
                    }

                }
            }

            @Override
            public void onError(Throwable ex) {
                Log.e("login",ex.toString());
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
                showLoading(false,"登录中....");

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });
    }
}
