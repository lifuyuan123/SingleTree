package com.sctjsj.forestcommunity.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bigkoo.pickerview.OptionsPickerView;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.AdressBean;
import com.sctjsj.forestcommunity.javabean.JsonBean;
import com.sctjsj.forestcommunity.javabean.ProvincialCityBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;
import com.suke.widget.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/AddAdress")
//新增收获地址
public class AddAdressActivity extends BaseAppcompatActivity {

    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.tv_region)
    TextView tvRegion;
    @BindView(R.id.edt_adress)
    EditText edtAdress;
    @BindView(R.id.lin_isDefault)
    LinearLayout linIsDefault;
    @BindView(R.id.bt_switch)
    SwitchButton btSwitch;
    @Autowired(name = "key")
    int title;
    @Autowired(name = "data")
    AdressBean bean;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.bt_confirm)
    Button btConfirm;
    @BindView(R.id.lin_region)
    LinearLayout linRegion;

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    private ArrayList<ProvincialCityBean> options11 = new ArrayList<>();
    private ArrayList<ArrayList<String>> options22 = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options33 = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<ProvincialCityBean.CityBean.AreaBean>>> options3id = new ArrayList<>();
    private int areaId = -1;

    private boolean isLoaded = false;
    private boolean isdefault = false;
    private HttpServiceImpl service;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 4:
                    isLoaded=true;
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initData();
        initView();
    }

    //获取数据
    private void initData() {
        //获取省下列表
        if ( SPFUtil.get("city", "none").equals("none")) {
            getProvinceList();
        } else {
            String result = (String) SPFUtil.get("city", "none");
            getCity(result);
        }
    }

    private void initView() {
        if (title == 1) {
            tvTitle.setText("新增收货地址");
        } else if (title == 2) {
            tvTitle.setText("编辑收货地址");
            if (bean != null) {
                edtName.setText(bean.getName());
                edtPhone.setText(bean.getPhone());
                tvRegion.setText(bean.getProvince() + "-" + bean.getCity() + "-" + bean.getArea());
                edtAdress.setText(bean.getAdress());
                btSwitch.setChecked(bean.isDefault());
                areaId = bean.getAreaId();
            }
        }
        //没有动画
        btSwitch.setEnableEffect(false);
        btSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                isdefault = isChecked;
            }
        });
        btConfirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btConfirm.setBackgroundResource(R.drawable.longin_bt_down_background);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btConfirm.setBackgroundResource(R.drawable.longin_bt_backgroungd);
                }
                return false;
            }
        });

    }

    @Override
    public int initLayout() {
        return R.layout.activity_add_adress;
    }

    @Override
    public void reloadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.linear_back, R.id.lin_region, R.id.bt_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //返回
            case R.id.linear_back:
                finish();
                break;
            //选择地区
            case R.id.lin_region:
                if (isLoaded) {
                    //关闭输入法
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(AddAdressActivity.this.getCurrentFocus().getWindowToken()
                            , InputMethodManager.HIDE_NOT_ALWAYS);
                    //城市选择器
                    ShowPickerView();
                }
                break;
            //确认
            case R.id.bt_confirm:
                //保存
                //新增
                if (title == 1) {
                    addAdress();
                    //编辑
                } else if (title == 2) {
                    saveData();
                }
                break;
        }
    }


    private void ShowPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                tvRegion.setText(options11.get(options1).getPickerViewText() + "-"
                        + options22.get(options1).get(options2) + "-"
                        + options33.get(options1).get(options2).get(options3));
                areaId = options3id.get(options1).get(options2).get(options3).getId();
            }
        })
                .setTitleText("选择地址")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        pvOptions.setPicker(options11, options22, options33);//三级选择器
        pvOptions.show();
    }


//    private void initJsonData() {//解析数据
//        /**
//         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
//         * 关键逻辑在于循环体
//         *
//         * */
//        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据
//        LogUtil.e("city", JsonData.toString());
//        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
//
//        /**
//         * 添加省份数据
//         *
//         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
//         * PickerView会通过getPickerViewText方法获取字符串显示出来。
//         */
//        options1Items = jsonBean;
//
//        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
//            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
//            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
//
//            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
//                String CityName = jsonBean.get(i).getCityList().get(c).getName();
//                CityList.add(CityName);//添加城市
//
//                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
//
//                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
//                if (jsonBean.get(i).getCityList().get(c).getArea() == null
//                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
//                    City_AreaList.add("");
//                } else {
//
//                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
//                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);
//
//                        City_AreaList.add(AreaName);//添加该城市所有地区数据
//                    }
//                }
//                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
//            }
//
//            /**
//             * 添加城市数据
//             */
//            options2Items.add(CityList);
//
//            /**
//             * 添加地区数据
//             */
//            options3Items.add(Province_AreaList);
//        }
//        LogUtil.e("city", options1Items.toString() + options2Items.toString() + options3Items.toString());
//        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
//    }


//    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
//        ArrayList<JsonBean> detail = new ArrayList<>();
//        try {
//            JSONArray data = new JSONArray(result);
//            Gson gson = new Gson();
//            for (int i = 0; i < data.length(); i++) {
//                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
//                detail.add(entity);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
//        }
//        return detail;
//    }

    //新增地址
    private void addAdress() {
        if (StringUtil.isBlank(edtName.getText().toString().trim())) {
            Toast.makeText(this, "请填写收货人姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isBlank(edtPhone.getText().toString().trim())) {
            Toast.makeText(this, "请填写收货人电话", Toast.LENGTH_SHORT).show();
            return;
        }
        if (areaId == -1) {
            Toast.makeText(this, "请选择收货区域", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isBlank(edtAdress.getText().toString().trim())) {
            Toast.makeText(this, "请填写收货地址", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("ctype", "address");
        Date date = new Date();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        map.put("data", "{sarea:{id:" + areaId + "},suser:{id:" + UserAuthUtil.getUserId() + "}," +
                "recName:\"" + edtName.getText().toString() + "\",updateTime:\"" + time + "\",areaInfo:\"" +
                edtAdress.getText().toString() + "\",mobile:\"" + edtPhone.getText().toString() + "\",state:1}");
        LogUtil.e("addadressmap", map.toString());
        service.doCommonPost(null, MainUrl.addAddress, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("addadressSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("msg");
                        if (results) {
                            finish();
                            Toast.makeText(AddAdressActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddAdressActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("addadressJSONException", e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("addadressonError", ex.toString());
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

    //保存编辑修改
    private void saveData() {
        if (areaId == -1) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        Date date = new Date();
        String time = "";
        map.put("ctype", "address");
        if (isdefault) {
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            map.put("data", "{id:" + bean.getId() + ",sarea:{id:" + areaId + "},suser:{id:" + UserAuthUtil.getUserId() + "}," +
                    "recName:\"" + edtName.getText().toString() + "\",updateTime:\"" + time + "\",areaInfo:\"" +
                    edtAdress.getText().toString() + "\",mobile:\"" + edtPhone.getText().toString() + "\",state:1}");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -1);//当前时间前去一个月，即一个月前的时间
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
            map.put("data", "{id:" + bean.getId() + ",sarea:{id:" + areaId + "},suser:{id:" + UserAuthUtil.getUserId() + "}," +
                    "recName:\"" + edtName.getText().toString() + "\",updateTime:\"" + time + "\",areaInfo:\"" + edtAdress.getText().toString() + "\"," +
                    "mobile:\"" + edtPhone.getText().toString() + "\",state:1}");
        }
        LogUtil.e("savemap", map.toString());
        service.doCommonPost(null, MainUrl.baseModifyUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("savaonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        boolean results = object.getBoolean("result");
                        String msg = object.getString("msg");
                        if (results) {
                            finish();
                            Toast.makeText(AddAdressActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddAdressActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("savaJSONException", e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("savaonError", ex.toString());
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


    //获取省下列表
    private void getProvinceList() {
        HashMap<String, String> map = new HashMap<>();
        service.doCommonPost(null, MainUrl.getProvincialCityList, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("ProvinceonSuccess", result.toString());
                if (!StringUtil.isBlank(result) && result != null) {
                    SPFUtil.put("city", result);
                    //解析地址json
                    getCity(result);
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("ProvinceonError", ex.toString());
            }
            @Override
            public void onCancelled(Callback.CancelledException cex) {}
            @Override
            public void onFinished() {
                dismissLoading();
            }
            @Override
            public void onWaiting() {}
            @Override
            public void onStarted() {
                showLoading(false,"加载中...");}
            @Override
            public void onLoading(long total, long current) {}
        });
    }

    //解析地址json
    private void getCity(String result) {
        try {
            JSONObject object = new JSONObject(result);
            boolean results = object.getBoolean("result");
            if (results) {
                JSONArray array = object.getJSONArray("resultData");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.getJSONObject(i);
                    ProvincialCityBean bean = new ProvincialCityBean();
                    List<ProvincialCityBean.CityBean> cityBeanList = new ArrayList<ProvincialCityBean.CityBean>();
                    String name = object1.getString("areaName");
                    int id = object1.getInt("id");
                    JSONArray array1 = object1.getJSONArray("children");
                    for (int j = 0; j < array1.length(); j++) {
                        JSONObject object2 = array1.getJSONObject(j);
                        ProvincialCityBean.CityBean bean1 = new ProvincialCityBean.CityBean();
                        List<ProvincialCityBean.CityBean.AreaBean> areaBeanList = new ArrayList<ProvincialCityBean.CityBean.AreaBean>();
                        String name1 = object2.getString("areaName");
                        int id1 = object2.getInt("id");
                        JSONArray array2 = object2.getJSONArray("children");
                        for (int k = 0; k < array2.length(); k++) {
                            JSONObject object3 = array2.getJSONObject(k);
                            ProvincialCityBean.CityBean.AreaBean areaBean = new ProvincialCityBean.CityBean.AreaBean();
                            String name2 = object3.getString("areaName");
                            int id2 = object3.getInt("id");
                            areaBean.setName(name2);
                            areaBean.setId(id2);
                            areaBeanList.add(areaBean);
                        }
                        bean1.setName(name1);
                        bean1.setId(id1);
                        bean1.setAreaBeanList(areaBeanList);
                        cityBeanList.add(bean1);
                    }
                    bean.setName(name);
                    bean.setId(id);
                    bean.setCityBeanList(cityBeanList);
                    options11.add(bean);
                    Message message=Message.obtain();
                    message.what=4;
                    mHandler.sendMessage(message);
                }
                analysisi(options11);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e("ProvinceJSONException", e.toString());
        }
    }

    private void analysisi(ArrayList<ProvincialCityBean> options11) {
        for (int i = 0; i < options11.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            ArrayList<ArrayList<ProvincialCityBean.CityBean.AreaBean>> Province_AreaList_id = new ArrayList<>();//该省的所有地区列表（第三极）
            for (int c = 0; c < options11.get(i).getCityBeanList().size(); c++) {//遍历该省份的所有城市
                String CityName = options11.get(i).getCityBeanList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                ArrayList<ProvincialCityBean.CityBean.AreaBean> City_AreaList_id = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (options11.get(i).getCityBeanList().get(c).getAreaBeanList() == null
                        || options11.get(i).getCityBeanList().get(c).getAreaBeanList().size() == 0) {
                    ProvincialCityBean.CityBean.AreaBean bean = new ProvincialCityBean.CityBean.AreaBean();
                    bean.setName("");
                    bean.setId(-1);
                    City_AreaList.add("");
                    City_AreaList_id.add(bean);
                } else {

                    for (int d = 0; d < options11.get(i).getCityBeanList().get(c).getAreaBeanList().size(); d++) {//该城市对应地区所有数据
                        String AreaName = options11.get(i).getCityBeanList().get(c).getAreaBeanList().get(d).getName();
                        int id = options11.get(i).getCityBeanList().get(c).getAreaBeanList().get(d).getId();
                        ProvincialCityBean.CityBean.AreaBean bean = new ProvincialCityBean.CityBean.AreaBean();
                        bean.setName(AreaName);
                        bean.setId(id);
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                        City_AreaList_id.add(bean);
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据

                Province_AreaList_id.add(City_AreaList_id);
            }

            /**
             * 添加城市数据
             */
            options22.add(CityList);

            /**
             * 添加地区数据
             */
            options33.add(Province_AreaList);
            options3id.add(Province_AreaList_id);
        }
    }
}
