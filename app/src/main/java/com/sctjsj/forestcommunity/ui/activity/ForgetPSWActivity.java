package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.DpUtils;
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

@Route(path = "/main/act/ForgetPSW")
//忘记密码
public class ForgetPSWActivity extends BaseAppcompatActivity {
    @BindView(R.id.edt_forget_phone)
    EditText edtForgetPhone;
    @BindView(R.id.edt_forget_code)
    EditText edtForgetCode;
    @BindView(R.id.lin_icon_code)
    LinearLayout linIconCode;
    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        getIconCode();
    }

    private void getIconCode() {
        linIconCode.removeAllViews();
        ImageView view = new ImageView(this);
        PicassoUtil.getPicassoObject().load(MainUrl.getPicCode).memoryPolicy(MemoryPolicy.NO_CACHE).into(view);
        linIconCode.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_forget_psw;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.bt_confirm,R.id.lin_icon_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_back:
                finish();
                break;
            //获取短信验证码
            case R.id.bt_confirm:
                //获取短信验证码
                getMessageCode();
                break;
            //换一张图片
            case R.id.lin_icon_code:
                getIconCode();
                break;
        }
    }
    //获取短信验证码
    private void getMessageCode() {
        if(StringUtil.isBlank(edtForgetPhone.getText().toString().trim())){
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(Validator.isMobile(edtForgetPhone.getText().toString().trim())){
                edtForgetPhone.setError("请输入正确的手机号码");
                return;
            }
        }
        if(StringUtil.isBlank(edtForgetCode.getText().toString().trim())){
            Toast.makeText(this, "请输图片验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String,String> map=new HashMap<>();
        map.put("mobile",edtForgetPhone.getText().toString());
        map.put("code",edtForgetCode.getText().toString());
        service.doCommonPost(null, MainUrl.getPhoenCode, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("codeonSuccess",result.toString());
               if(!StringUtil.isBlank(result)&&result!=null){
                   try {
                       JSONObject object=new JSONObject(result);
                       boolean results=object.getBoolean("result");
                       String msg=object.getString("resultMsg");
                       if(results){
                           ARouter.getInstance().build("/main/act/ModifyPSW").withString("phone",edtForgetPhone.getText().toString()).navigation();
                           Toast.makeText(ForgetPSWActivity.this, msg, Toast.LENGTH_SHORT).show();
                           finish();
                       }else {
                           Toast.makeText(ForgetPSWActivity.this, msg, Toast.LENGTH_SHORT).show();
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                       LogUtil.e("codeJSONException",e.toString());
                   }
               }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("codeonError",ex.toString());
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
