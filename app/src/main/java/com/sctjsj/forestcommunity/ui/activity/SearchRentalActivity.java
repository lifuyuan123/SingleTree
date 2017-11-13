package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import com.sctjsj.forestcommunity.adapter.RentalCenterAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.RentalCenter;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

@Route(path = "/main/act/SearchRental")
//租售中心搜索
public class SearchRentalActivity extends BaseAppcompatActivity {

    @BindView(R.id.edt_search_rental)
    EditText edtSearchRental;
    @BindView(R.id.rv_search_rental)
    RecyclerView rvSearchRental;
    @BindView(R.id.ql_search_rental)
    QRefreshLayout qlSearchRental;
    boolean isflag = false;
    @BindView(R.id.tv_ok_cancel)
    TextView tvOkCancel;
    @BindView(R.id.lin_rental_rental_nodata)
    LinearLayout linRentalRentalNodata;

    private HttpServiceImpl service;
    private int pageIndex = 1;
    private RentalCenterAdapter adapter;
    private LinearLayoutManager manager;
    private List<RentalCenter> rentalCenters = new ArrayList<>();
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();

    }

    private void initView() {
        edtSearchRental.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0 || s.toString().equals("")) {
                    tvOkCancel.setText("取消");
                    isflag = false;
                } else {
                    tvOkCancel.setText("确定");
                    isflag = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        qlSearchRental.setLoadMoreEnable(true);
        qlSearchRental.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                initSearchData(address);
                qlSearchRental.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                initSearchDataMore(address);
                qlSearchRental.LoadMoreComplete();
            }
        });

        manager = new LinearLayoutManager(this);
        adapter = new RentalCenterAdapter(rentalCenters, this);
        rvSearchRental.setLayoutManager(manager);
        rvSearchRental.setAdapter(adapter);

    }

    @Override
    public int initLayout() {
        return R.layout.activity_search_rental;
    }

    @Override
    public void reloadData() {

    }

    //取消和确定按钮
    @OnClick(R.id.lin_search_rental_cancel)
    public void onViewClicked() {
        if (isflag) {
            //搜索数据
            address = edtSearchRental.getText().toString();
            initSearchData(address);
        } else {
            if (edtSearchRental.getText().toString().equals("")
                    || StringUtil.isBlank(edtSearchRental.getText().toString())) {
                finish();
            } else {
                edtSearchRental.setText("");
                tvOkCancel.setText("取消");
            }
        }
    }

    //获取搜索数据
    private void initSearchData(final String address) {
        rentalCenters.clear();
        pageIndex = 1;
        final HashMap<String, String> map = new HashMap<>();
        map.put("pageSize", "8");
        map.put("pageIndex", pageIndex + "");
        map.put("jf", "thumbnail");
        if (!StringUtil.isBlank(address) && !address.equals("")) {
            map.put("address", address);//按照小区名称模糊查询
        }
        service.doCommonPost(null, MainUrl.RentalCenter, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("Rentalonmap", map.toString());
                LogUtil.e("RentalonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    pageIndex++;
                    try {
                        JSONObject object = new JSONObject(result);

                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");

                        if(results){
                            JSONArray array = object.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                RentalCenter bean = new RentalCenter();

                                String title = object1.getString("title");
                                String address = object1.getString("area");
                                String monthly = object1.getString("price");
                                String houseType = object1.getString("houseType");
                                String acreage = object1.getString("acreage");
                                String orientation = object1.getString("orientation");
                                if (object1.getJSONObject("thumbnail").has("url")) {
                                    String url = object1.getJSONObject("thumbnail").getString("url");
                                    bean.setUrl(url);
                                }
                                int id = object1.getInt("id");
                                int issell = object1.getInt("issell");

                                bean.setTitle(title);
                                bean.setAdress(address);
                                bean.setMonthly(monthly);
                                bean.setHouseType(houseType);
                                bean.setAcreage(acreage);
                                bean.setOrientation(orientation);
                                bean.setId(id);
                                bean.setIssell(issell);
                                rentalCenters.add(bean);

                            }
                        }else {
                            Toast.makeText(SearchRentalActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("RentalJSONException", e.toString());
                    }finally {
                        if(rentalCenters.size()>0){
                            qlSearchRental.setVisibility(View.VISIBLE);
                            linRentalRentalNodata.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }else {
                            qlSearchRental.setVisibility(View.GONE);
                            linRentalRentalNodata.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("RentalonError", ex.toString());
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

    private void initSearchDataMore(String address) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("pageSize", "8");
        map.put("pageIndex", pageIndex + "");
        map.put("jf", "thumbnail");
        if (!StringUtil.isBlank(address) && !address.equals("")) {
            map.put("address", address);//按照小区名称模糊查询
        }
        service.doCommonPost(null, MainUrl.RentalCenter, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("Rentalonmap", map.toString());
                LogUtil.e("RentalonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    pageIndex++;
                    try {
                        JSONObject object = new JSONObject(result);

                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");

                        if(results){
                            JSONArray array = object.getJSONArray("resultList");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                RentalCenter bean = new RentalCenter();

                                String title = object1.getString("title");
                                String address = object1.getString("area");
                                String monthly = object1.getString("price");
                                String houseType = object1.getString("houseType");
                                String acreage = object1.getString("acreage");
                                String orientation = object1.getString("orientation");
                                if (object1.getJSONObject("thumbnail").has("url")) {
                                    String url = object1.getJSONObject("thumbnail").getString("url");
                                    bean.setUrl(url);
                                }
                                int id = object1.getInt("id");
                                int issell = object1.getInt("issell");

                                bean.setTitle(title);
                                bean.setAdress(address);
                                bean.setMonthly(monthly);
                                bean.setHouseType(houseType);
                                bean.setAcreage(acreage);
                                bean.setOrientation(orientation);
                                bean.setId(id);
                                bean.setIssell(issell);
                                rentalCenters.add(bean);

                            }
                        }else {
                            Toast.makeText(SearchRentalActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("RentalJSONException", e.toString());
                    }finally {
                        if(rentalCenters.size()>0){
                            qlSearchRental.setVisibility(View.VISIBLE);
                            linRentalRentalNodata.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }else {
                            qlSearchRental.setVisibility(View.GONE);
                            linRentalRentalNodata.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("RentalonError", ex.toString());
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
}
