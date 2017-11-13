package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/ModifyPSW")
//修改密码
public class ModifyPSWActivity extends BaseAppcompatActivity {
    @BindView(R.id.edt_modify_psw)
    EditText edtModifyPsw;
    @BindView(R.id.edt_modify_pswAgin)
    EditText edtModifyPswAgin;
    @BindView(R.id.edt_modify_code)
    EditText edtModifyCode;
    private HttpServiceImpl service;
    @Autowired(name = "phone")
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_modify_psw;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.bt_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_back:
                finish();
                break;
            case R.id.bt_confirm:
                //保存密码
                savePSW();
                break;
        }
    }

    //保存密码
    private void savePSW() {
        if(StringUtil.isBlank(edtModifyPsw.getText().toString().trim())){
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(edtModifyPsw.getText().toString().trim().length()<6){
                edtModifyPsw.setError("密码长度至少6位");
                return;
            }
        }
        if(StringUtil.isBlank(edtModifyPswAgin.getText().toString().trim())){
            Toast.makeText(this, "请确认新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isBlank(edtModifyCode.getText().toString().trim())){
            Toast.makeText(this, "请输入短信验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!edtModifyPsw.getText().toString().trim().equals(edtModifyPswAgin.getText().toString().trim())){
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String,String> map=new HashMap<>();
        map.put("phone",phone);
        map.put("code",edtModifyCode.getText().toString());
        map.put("password",edtModifyPsw.getText().toString());
        service.doCommonPost(null, MainUrl.modifyPSW, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("savePWSonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                       boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");
                        if(results){
                            Toast.makeText(ModifyPSWActivity.this, msg, Toast.LENGTH_SHORT).show();
                            ARouter.getInstance().build("/main/act/login").navigation();
                            finish();
                        }else {
                            Toast.makeText(ModifyPSWActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("savePWSJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("savePWSonError",ex.toString());
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
