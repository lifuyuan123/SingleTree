package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.FragmentAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.ui.fragment.IntegralFragment;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


@Route(path = "/main/act/user/IntegralRecord")
//积分记录
public class IntegralRecordActivity extends BaseAppcompatActivity {

    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.tv_record_integral)
    TextView tvRecordIntegral;
    private List<String> titles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentAdapter adapter;
    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        getData();
        initView();
    }

    private void initView() {
        adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        vp.setAdapter(adapter);
        tab.setupWithViewPager(vp);
    }

    private void getData() {
        titles.add("全部");
        titles.add("收入");
        titles.add("支出");
        for (int i = 0; i < titles.size(); i++) {
            IntegralFragment Integral = new IntegralFragment();
            fragments.add(Integral);
            Bundle bundle = new Bundle();
            bundle.putString("key", titles.get(i));
            Integral.setArguments(bundle);
        }
        //获取积分
        getIntegral();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_integral_record;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }


    //获取积分
    public void getIntegral() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", UserAuthUtil.getUserId() + "");
        map.put("ctype", "user");
        service.doCommonPost(null, MainUrl.baseSingleQueryUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("integralonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        boolean results = obj.getBoolean("result");
                        String msg = obj.getString("msg");
                        if (results) {
                            String integral = obj.getJSONObject("data").getString("userExp");
                            tvRecordIntegral.setText(integral);
                        } else {
                            Toast.makeText(IntegralRecordActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("integralJSONException", e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("integralonError", ex.toString());
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
