package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.CityListAdapter;
import com.sctjsj.forestcommunity.adapter.RentalCenterAdapter;
import com.sctjsj.forestcommunity.adapter.TermListAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.MenuCityBean;
import com.sctjsj.forestcommunity.javabean.RentalCenter;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.yyydjk.library.DropDownMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/**
 * 租售中心
 */

@Route(path = "/main/act/RentalCenter")
public class RentalCenterActivity extends BaseAppcompatActivity {

    @BindView(R.id.rental_center_back)
    RelativeLayout rentalCenterBack;
    @BindView(R.id.rental_center_search)
    LinearLayout rentalCenterSearch;
    DropDownMenu rentalCenterDropMenu;
    @BindView(R.id.activity_rental_center)
    LinearLayout activityRentalCenter;
    private View contentView;
    QRefreshLayout rental_center_qrf;
    RecyclerView rental_center_rv;
    private LinearLayout noData;
    private String city="";

    private String headers[] = {"区域", "装修类型", "价格排序"};
   // private String citys[] = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private List<MenuCityBean> cityData = new ArrayList<>();
    private List<MenuCityBean> termData = new ArrayList<>();
    private List<MenuCityBean> moreData = new ArrayList<>();
    private List<RentalCenter> rentalCenters = new ArrayList<>();
    private List<View> popupViews = new ArrayList<>();

    private CityListAdapter cityAdapter;
    private TermListAdapter terAdapter;
    private TermListAdapter otherTer;
    private HttpServiceImpl service;
    private View mView;
    private TextView rental_center_currCity;
    private TextView rental_center_qh;
    private ListView rental_center_list;
    private int pageIndex = 1;


    private RentalCenterAdapter renAdapter;
    private String Area = "", Issell = "", Sort = "", Address = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        contentView = getLayoutInflater().inflate(R.layout.rental_content, null);
        rentalCenterDropMenu = (DropDownMenu) findViewById(R.id.dropDownMenu);
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();
        initData();
        getCurrCity();
    }



    //初始化控件
    private void initView() {
        rental_center_qrf = (QRefreshLayout) contentView.findViewById(R.id.rental_center_qrf);
        rental_center_rv = (RecyclerView) contentView.findViewById(R.id.rental_center_rv);
        noData= (LinearLayout) contentView.findViewById(R.id.lin_rental_center_nodata);
        rental_center_qrf.setLoadMoreEnable(true);
        rental_center_qrf.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                initRentalCenterData();
                rental_center_qrf.refreshComplete();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                initRentalCenterDataMore();
                rental_center_qrf.LoadMoreComplete();
            }
        });

        mView= LayoutInflater.from(this).inflate(R.layout.rental_center_choosecity,null);
        rental_center_currCity= (TextView) mView.findViewById(R.id.rental_center_currCity);
        rental_center_list= (ListView) mView.findViewById(R.id.rental_center_list);
        rental_center_qh= (TextView) mView.findViewById(R.id.rental_center_qh);
        rental_center_qh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/main/act/ChoiceCity").withInt("type",2).navigation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        city= (String) SPFUtil.get(Tag.TAG_CITY,"成都");
        rental_center_currCity.setText("当前城市："+city);
        getCurrCity();
    }

    //初始化数据
    private void initData() {
        //获取当前定位城市
        city= (String) SPFUtil.get(Tag.TAG_CITY,"成都");
        rental_center_currCity.setText("当前城市："+city);
        //初始化租售条件的数据
        termData.add(new MenuCityBean("二手房", false));
        termData.add(new MenuCityBean("芬兰新楼盘", false));
        termData.add(new MenuCityBean("其他", false));

        //初始化更过的选项
        moreData.add(new MenuCityBean("升序", false));
        moreData.add(new MenuCityBean("降序", false));
        //获取租房信息
        initRentalCenterData();
        setAdapter();


    }

    //获取租房信息
    private void initRentalCenterData() {
        rentalCenters.clear();
        pageIndex = 1;
        final HashMap<String, String> map = new HashMap<>();
        map.put("pageSize", "8");
        map.put("pageIndex", pageIndex + "");
        map.put("jf", "thumbnail");
        if (!StringUtil.isBlank(Area)) {
            map.put("area", Area);//地区
        }
        if (!StringUtil.isBlank(Issell)) {
            map.put("issell", Issell);//1在售 2在租 3 其他
        }
        if (!StringUtil.isBlank(Sort)) {
            map.put("sort", Sort);//按照价格排序
        }
        if (!StringUtil.isBlank(Address)) {
            map.put("address", Address);//按照小区名称模糊查询
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
                                String address = object1.getString("address");
                                String area=object1.getString("area");
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
                                bean.setArea(area);
                                bean.setId(id);
                                bean.setIssell(issell);
                                rentalCenters.add(bean);
                            }
                        }else {
                            Toast.makeText(RentalCenterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("RentalJSONException", e.toString());
                    }finally {
                      if(rentalCenters.size()>0){
                           rental_center_qrf.setVisibility(View.VISIBLE);
                           noData.setVisibility(View.GONE);
                           renAdapter.notifyDataSetChanged();
                      }else {
                          rental_center_qrf.setVisibility(View.GONE);
                          noData.setVisibility(View.VISIBLE);
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

    //获取租房信息
    private void initRentalCenterDataMore() {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageSize", "8");
        map.put("pageIndex", pageIndex + "");
        map.put("jf", "thumbnail");
        if (!StringUtil.isBlank(Area)) {
            map.put("area", Area);//地区
        }
        if (!StringUtil.isBlank(Issell)) {
            map.put("issell", Issell);//1在售 2在租 3 其他
        }
        if (!StringUtil.isBlank(Sort)) {
            map.put("sort", Sort);//按照价格排序
        }
        if (!StringUtil.isBlank(Address)) {
            map.put("address", Address);//按照小区名称模糊查询
        }
        service.doCommonPost(null, MainUrl.RentalCenter, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
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

                                bean.setTitle(title);
                                bean.setAdress(address);
                                bean.setMonthly(monthly);
                                bean.setHouseType(houseType);
                                bean.setAcreage(acreage);
                                bean.setOrientation(orientation);
                                bean.setId(id);
                                rentalCenters.add(bean);

                            }
                        }else {
                            Toast.makeText(RentalCenterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("RentalJSONException", e.toString());
                    }finally {
                        if(rentalCenters.size()>0){
                            rental_center_qrf.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                            renAdapter.notifyDataSetChanged();
                        }else {
                            rental_center_qrf.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
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


    //获取当前选择的城市
    private void getCurrCity(){
        cityData.clear();
        HashMap<String,String> body=new HashMap<>();
        body.put("cityName",city);
        body.put("level","2");
        body.put("pageSize","999");
        service.doCommonPost(null, MainUrl.getCity, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getCurrCity",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        JSONArray arry=object.getJSONArray("resultList");
                        if(null!=arry&&arry.length()>0){
                            for (int i = 0; i <arry.length() ; i++) {
                                JSONObject jsonObject = arry.getJSONObject(i);
                                MenuCityBean bean=new MenuCityBean();
                                bean.setCyitName(jsonObject.getString("areaName"));
                                cityData.add(bean);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        //通知适配器
                        cityAdapter.notifyDataSetChanged();
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


    //设置适配器
    private void setAdapter() {
        //设置城市的
        cityAdapter = new CityListAdapter(cityData, this);
        rental_center_list.setDividerHeight(0);
        rental_center_list.setAdapter(cityAdapter);
        rental_center_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearCheck(cityData);
                cityData.get(position).setCheck(true);
                cityAdapter.notifyDataSetChanged();
                rentalCenterDropMenu.setTabText(position == 0 ? headers[0] : cityData.get(position).getCyitName());
                rentalCenterDropMenu.closeMenu();
                String name=cityData.get(position).getCyitName();
                if(!name.equals("不限")){
                    Area = name;
                }else {
                    Area="";
                }
                initRentalCenterData();
            }
        });


        //设置赛选条件
        terAdapter = new TermListAdapter(termData, this);
        ListView terListView = new ListView(this);
        terListView.setDividerHeight(0);
        terListView.setAdapter(terAdapter);
        terListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearCheck(termData);
                termData.get(position).setCheck(true);
                terAdapter.notifyDataSetChanged();
                rentalCenterDropMenu.setTabText(position == 0 ? headers[1] : termData.get(position).getCyitName());
                rentalCenterDropMenu.closeMenu();
                String name = termData.get(position).getCyitName();
                switch (name) {
                    case "在售":
                        Issell = "1";
                        break;
                    case "在租":
                        Issell = "2";
                        break;
                    case "其他":
                        Issell = "3";
                        break;
                }
                initRentalCenterData();
            }
        });

        //设置其他的赛选条件
        otherTer = new TermListAdapter(moreData, this);
        ListView oterListView = new ListView(this);
        oterListView.setDividerHeight(0);
        oterListView.setAdapter(otherTer);
        oterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearCheck(moreData);
                moreData.get(position).setCheck(true);
                otherTer.notifyDataSetChanged();
                rentalCenterDropMenu.setTabText(position == 0 ? headers[2] : moreData.get(position).getCyitName());
                rentalCenterDropMenu.closeMenu();
                String name = moreData.get(position).getCyitName();
                if (name.equals("升序")) {
                    Sort = "price desc";
                } else {
                    Sort = "";
                }
                initRentalCenterData();
            }
        });


        popupViews.add(mView);
        popupViews.add(terListView);
        popupViews.add(oterListView);


        //设置recycleView的数据
        renAdapter = new RentalCenterAdapter(rentalCenters, this);
        rental_center_rv.setLayoutManager(new LinearLayoutManager(this));
        rental_center_rv.setAdapter(renAdapter);


        rentalCenterDropMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);


    }

    //清除选的选项
    public void clearCheck(List<MenuCityBean> data) {
        for (int i = 0; i < data.size(); i++) {
            MenuCityBean bean = data.get(i);
            bean.setCheck(false);
        }
    }

    @Override
    public int initLayout() {
        return R.layout.activity_rental_center;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.rental_center_back, R.id.rental_center_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rental_center_back:
                //退出activity前关闭菜单
                if (rentalCenterDropMenu.isShowing()) {
                    rentalCenterDropMenu.closeMenu();
                } else {
                    super.onBackPressed();
                }
                finish();
                break;
            //跳转搜索页面
            case R.id.rental_center_search:
                ARouter.getInstance().build("/main/act/SearchRental").navigation();
                break;
        }
    }



}
