package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/ChangePsw")
//修改密码
public class ChangePswActivity extends BaseAppcompatActivity {

    @BindView(R.id.edt_oldPsw)
    EditText edtOldPsw;
    @BindView(R.id.edt_newPsw)
    EditText edtNewPsw;
    @BindView(R.id.bt_confirm)
    Button btConfirm;
    private boolean flag = false;
    private boolean ishide = true;
    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();

    }

    private void initView() {
        edtNewPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btConfirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    btConfirm.setBackgroundResource(R.drawable.longin_bt_down_background);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    btConfirm.setBackgroundResource(R.drawable.longin_bt_backgroungd);
                }
                return false;
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_change_psw;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.bt_confirm, R.id.lin_hide})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.linear_back:
                finish();
                break;
            //确认
            case R.id.bt_confirm:
                changePSW();
                break;
            case R.id.lin_hide:
                if (ishide) {
                    edtNewPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ishide = false;
                } else {
                    edtNewPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ishide = true;
                }
                break;
        }
    }

    //修改密码
    private void changePSW() {
        if(StringUtil.isBlank(edtOldPsw.getText().toString().trim())){
            Toast.makeText(this, "请输入原密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(StringUtil.isBlank(edtNewPsw.getText().toString().trim())){
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(edtNewPsw.getText().toString().trim().length()<6){
                edtNewPsw.setError("密码长度至少6位");
                return;
            }
        }

        HashMap<String,String> map=new HashMap<>();
        map.put("userId", UserAuthUtil.getUserId()+"");
        map.put("olderPasswoed",edtOldPsw.getText().toString().trim());
        map.put("newPassword",edtNewPsw.getText().toString().trim());
        service.doCommonPost(null, MainUrl.ChangePSW, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("changePSWonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");
                        if(results){
                            Toast.makeText(ChangePswActivity.this, msg+"请重新登录", Toast.LENGTH_SHORT).show();
                            ARouter.getInstance().build("/main/act/login").navigation();
                            finish();
                        }else {
                            Toast.makeText(ChangePswActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("changePSWJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("changePSWonError",ex.toString());
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
