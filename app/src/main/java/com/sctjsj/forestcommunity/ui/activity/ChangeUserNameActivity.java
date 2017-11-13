package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/ChangeUserName")
//修改昵称
public class ChangeUserNameActivity extends BaseAppcompatActivity {

    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.bt_confirm)
    Button btConfirm;
    private HttpServiceImpl service;
    @Autowired(name = "name")
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();

    }

    private void initView() {
        edtName.setText(name);
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtil.isBlank(edtName.getText().toString())) {
                    ivDelete.setVisibility(View.VISIBLE);
                } else {
                    ivDelete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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
        return R.layout.activity_change_user_name;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.bt_confirm, R.id.lin_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_back:
                finish();
                break;
            //清除
            case R.id.lin_delete:
                edtName.setText("");
                break;
            //提交
            case R.id.bt_confirm:
                changeName();
                break;
        }
    }

    //修改昵称
    private void changeName() {
        if (StringUtil.isBlank(edtName.getText().toString().trim())){
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String,String> map=new HashMap<>();
        map.put("data","{id:"+ UserAuthUtil.getUserId()+",realName:\""+edtName.getText().toString()+"\"}");
        map.put("ctype","user");
        service.doCommonPost(null, MainUrl.baseModifyUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("changeNameonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");
                        if(results){
                            Toast.makeText(ChangeUserNameActivity.this, msg, Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ChangeUserNameActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("changeNameJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("changeNameonError",ex.toString());
            }
            @Override
            public void onCancelled(Callback.CancelledException cex) {}
            @Override
            public void onFinished() {
            }
            @Override
            public void onWaiting() {}
            @Override
            public void onStarted() {}
            @Override
            public void onLoading(long total, long current) {}
        });
    }

}
