package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
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
import com.sctjsj.forestcommunity.adapter.BrandAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.BrandBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/user/OwnerIntegral")
//业主积分
public class OwnerIntegralActivity extends BaseAppcompatActivity {

    @BindView(R.id.owner_rv)
    RecyclerView ownerRv;
    @BindView(R.id.tv_omner_integral)
    TextView tvOmnerIntegral;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;

    private BrandAdapter adapter;
    private GridLayoutManager manager;
    private List<BrandBean> list = new ArrayList<>();
    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initData();
        initView();
    }

    private void initView() {
        adapter = new BrandAdapter(list, this);
        manager = new GridLayoutManager(this, 4);
        ownerRv.setLayoutManager(manager);
        ownerRv.setAdapter(adapter);
    }

    private void initData() {
//        for (int i = 0; i < 8; i++) {
//            BrandBean brandBean = new BrandBean();
//            list.add(brandBean);
//        }
        //获取可使用店铺
        getAvailableShop();
        //获取积分
        getIntegral();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_owner_integral;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.lin_rule, R.id.lin_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.linear_back:
                finish();
                break;
            //积分规则
            case R.id.lin_rule:
                ARouter.getInstance().build("/main/act/InregralRule").navigation();
                break;
            //积分记录
            case R.id.lin_record:
                ARouter.getInstance().build("/main/act/user/IntegralRecord").navigation();
                break;
        }
    }

    //获取可使用店铺
    private void getAvailableShop() {
        HashMap<String, String> map = new HashMap<>();
        map.put("jf", "store");
        service.doCommonPost(null, MainUrl.Availableshop, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("AvailableShoponSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("msg");
                        if (results) {
                            JSONArray array=object.getJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1=array.getJSONObject(i);
                                BrandBean bean=new BrandBean();
                                int id=object1.getInt("id");
                                String url=object1.getString("url");

                                bean.setId(id);
                                bean.setIconUrl(url);
                                list.add(bean);
                            }
                        } else {
                            Toast.makeText(OwnerIntegralActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("AvailableShopJSONException", e.toString());
                    } finally {
                        if (list.size() > 0) {
                            ownerRv.setVisibility(View.VISIBLE);
                            tvNoData.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                            ownerRv.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("AvailableShoponError", ex.toString());
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
                showLoading(false, "加载中...");
            }

            @Override
            public void onLoading(long total, long current) {
            }
        });
    }

    //获取积分
    private void getIntegral() {
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
                            tvOmnerIntegral.setText(integral);
                        } else {
                            Toast.makeText(OwnerIntegralActivity.this, msg, Toast.LENGTH_SHORT).show();
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
