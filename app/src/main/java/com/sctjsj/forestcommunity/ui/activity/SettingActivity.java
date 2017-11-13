package com.sctjsj.forestcommunity.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.ui.widget.dialog.CommonDialog;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.tencent.bugly.beta.Beta;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/user/Setting")
//设置
public class SettingActivity extends BaseAppcompatActivity {

    @BindView(R.id.bt_loginOut)
    Button btLoginOut;
    @BindView(R.id.tv_verson)
    TextView tvVerson;
    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();

    }

    private void initView() {
        initVersion();
        btLoginOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btLoginOut.setBackgroundResource(R.drawable.longin_bt_down_background);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btLoginOut.setBackgroundResource(R.drawable.longin_bt_backgroungd);
                }
                return false;
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back,R.id.lin_setting_verson, R.id.lin_setting_aboutOwn, R.id.bt_loginOut})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_back:
                finish();
                break;
            //版本
            case R.id.lin_setting_verson:
                Beta.checkUpgrade();
                break;
            //关于我们
            case R.id.lin_setting_aboutOwn:
                ARouter.getInstance().build("/main/act/AboutUs").navigation();
                break;
            //退出登录
            case R.id.bt_loginOut:
                logoutDialog();
                break;
        }
    }

    //退出的dialog
    private void logoutDialog() {
        final CommonDialog dia = new CommonDialog(this);
        dia.setTitle("确认退出");
        dia.setContent("确认退出登录");
        dia.setCancelClickListener("取消", new CommonDialog.CancelClickListener() {
            @Override
            public void clickCancel() {
                dia.dismiss();
            }
        });
        dia.setConfirmClickListener("退出", new CommonDialog.ConfirmClickListener() {
            @Override
            public void clickConfirm() {
                dia.dismiss();
                logout();
            }
        });
        dia.show();
    }

    //获取版本号
    private void initVersion() {
        try {
            String version = getPackageManager().
                    getPackageInfo(getPackageName(), 0).versionName;
            if (version != null) {
                tvVerson.setText("当前版本 " + version);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //退出登录
    private void logout() {
        service.doCommonPost(null, MainUrl.logOut, null, new XProgressCallback() {
            @Override
            public void onSuccess(String resultStr) {

                LogUtil.e("logout", resultStr.toString());
                if (!StringUtil.isBlank(resultStr)) {
                    try {
                        JSONObject obj = new JSONObject(resultStr);
                        boolean result = obj.getBoolean("result");
                        String msg = obj.getString("msg");

                        //退出成功
                        if (result) {
                            Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show();
//                            SPFUtil.clearAll();清除全部会将获取的收货地址给清掉，这里只清理用户相关
                            if (SPFUtil.contains(Tag.TAG_TOKEN)) {
                                SPFUtil.removeOne(Tag.TAG_TOKEN);
                            }
                            //清除本地用户信息
                            if (SPFUtil.contains(Tag.TAG_USER)) {
                                SPFUtil.removeOne(Tag.TAG_USER);
                            }

                            Log.e("SPFUtil", SPFUtil.getAll().size() + "----------");
                            finish();
//                            EventBus.getDefault().post("out");
//                            android.os.Process.killProcess(android.os.Process.myPid());
                            ARouter.getInstance().build("/main/act/login").navigation();
                        } else {
                            Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e(ex.toString());
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
