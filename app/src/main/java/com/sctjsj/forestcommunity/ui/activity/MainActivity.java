package com.sctjsj.forestcommunity.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.sctjsj.basemodule.base.constant.code;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.ui.widget.dialog.CommonDialog;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.permission.EasyPermissionsEx;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.event.ChooseCityEvent;
import com.sctjsj.forestcommunity.ui.fragment.FroumFragment;
import com.sctjsj.forestcommunity.ui.fragment.HomeFragment;
import com.sctjsj.forestcommunity.ui.fragment.MineFragment;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import org.greenrobot.eventbus.Subscribe;

import static com.sctjsj.forestcommunity.constant.requestcode.PermissionRequestCode.HOME_REQUEST_CAMERA_PERMISSION;
import static  com.sctjsj.forestcommunity.constant.requestcode.PermissionRequestCode.INDEX_REQUEST_LOCATION_PERMISSION;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/main/act/MainActivity")
public class MainActivity extends BaseAppcompatActivity implements EasyPermissionsEx.PermissionCallbacks {

    @BindView(R.id.main_Rb_home)
    RadioButton mainRbHome;
    @BindView(R.id.main_Rb_froum)
    RadioButton mainRbFroum;
    @BindView(R.id.main_Rb_mine)
    RadioButton mainRbMine;
    @BindView(R.id.main_Radiogroup)
    RadioGroup mainRadiogroup;

    private FragmentManager manager;
    private FroumFragment froum;
    private HomeFragment home;
    private MineFragment mine;



    /**退出相关**/
    private boolean isExited = false;//标志是否已经退出
    private Handler mHandler= new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isExited = false;
                    break;
            }
        }
    };



    /**
     * 定位相关
     **/
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;
    public AMapLocationClientOption mLocationClientOption = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager=getSupportFragmentManager();
        mainRbHome.setChecked(true);
        initView();
        initPermission();
        setOnclick();
    }
    //获取权限
    private void initPermission() {

        if(EasyPermissionsEx.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            initLocation();
        }else {
            EasyPermissionsEx.requestPermissions(this,"定位功能需要授予定位权限",INDEX_REQUEST_LOCATION_PERMISSION,Manifest.permission.ACCESS_FINE_LOCATION);

        }

        if(!EasyPermissionsEx.hasPermissions(this, Manifest.permission.CALL_PHONE)){
            EasyPermissionsEx.requestPermissions(this,"拨打电话需要授予拨号权限", code.REQUEST_CALL_PERMISSION,Manifest.permission.CALL_PHONE);
        }


    }






    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case INDEX_REQUEST_LOCATION_PERMISSION:
                initLocation();
                break;
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case INDEX_REQUEST_LOCATION_PERMISSION:
                Toast.makeText(this, "您已经禁止了定位权限,相关功能可能无法使用", Toast.LENGTH_SHORT).show();

                final CommonDialog dia1 = new CommonDialog(this);
                dia1.setTitle("系统提醒");
                dia1.setContent("你已经禁止了定位权限？立即开启");
                dia1.setCancelClickListener("暂不开启", new CommonDialog.CancelClickListener() {
                    @Override
                    public void clickCancel() {
                        dia1.dismiss();
                    }
                });
                dia1.setConfirmClickListener("确认", new CommonDialog.ConfirmClickListener() {
                    @Override
                    public void clickConfirm() {
                        dia1.dismiss();
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null)));

                    }
                });
                dia1.show();


                break;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        EasyPermissionsEx.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }





    /**
     * 高德定位
     */
    public void initLocation() {

        mLocationClient = new AMapLocationClient(this);
        //初始化定位监听器
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    //获取定位错误码
                    int errorCode = aMapLocation.getErrorCode();
                    Log.e("mainLocation",errorCode+"");
                    //定位成功
                    if (0 == errorCode) {

                        /**解析地址**/
                        String city = aMapLocation.getCity();
                        double longT=aMapLocation.getLongitude();
                        double lanT=aMapLocation.getLatitude();

                        /**保存位置信息到本地**/
                        SPFUtil.put(Tag.TAG_LONGITUDE,String.valueOf(longT));
                        SPFUtil.put(Tag.TAG_LATITUDE,String.valueOf(lanT));
                        SPFUtil.put(Tag.TAG_CITY,String.valueOf(city));

                    }
                }
            }
        };

        //定位客户端设置定位监听器
        mLocationClient.setLocationListener(mLocationListener);
        /**
         * 配置定位参数
         */

        mLocationClientOption = new AMapLocationClientOption();

        //设置定位模式：高精度定位模式
        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息
        mLocationClientOption.setNeedAddress(true);
        //设置是否指定位一次
        mLocationClientOption.setOnceLocation(false);
        //如果设置了只定位一次，将获取3s内经度最高的一次定位结果
        if (mLocationClientOption.isOnceLocation()) {
            mLocationClientOption.setOnceLocationLatest(true);
        }
        //设置是否强制刷新wifi
        mLocationClientOption.setWifiActiveScan(true);
        //设置是否允许模拟位置
        mLocationClientOption.setMockEnable(false);
        //设置定位时间间隔5min
        mLocationClientOption.setInterval(1000 * 60 * 5);
        mLocationClient.setLocationOption(mLocationClientOption);
        mLocationClient.startLocation();

    }

    //设置监听
    private void setOnclick() {
        mainRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.main_Rb_home:
                        //点击的home
                        hideAllFgIfNotNull();
                        if(null==home){
                            home=new HomeFragment();
                            manager.beginTransaction().add(R.id.main_replace_layout,home).show(home).commit();
                        }else {
                            manager.beginTransaction().show(home).commit();
                        }
                        Eyes.setStatusBarLightMode(MainActivity.this, getResources().getColor(R.color.color_toolbar));
                        break;
                    case R.id.main_Rb_froum:
                        //点击的论坛
                        hideAllFgIfNotNull();
                        if(null==froum){
                            froum=new FroumFragment();
                            manager.beginTransaction().add(R.id.main_replace_layout,froum).show(froum).commit();
                        }else {
                            manager.beginTransaction().show(froum).commit();
                        }
                        Eyes.setStatusBarLightMode(MainActivity.this, getResources().getColor(R.color.color_toolbar));
                        break;
                    case R.id.main_Rb_mine:
                        //点击的我的
                        hideAllFgIfNotNull();
                        if(null==mine){
                            mine=new MineFragment();
                            manager.beginTransaction().add(R.id.main_replace_layout,mine).show(mine).commit();
                        }else {
                            manager.beginTransaction().show(mine).commit();
                        }
                        Eyes.setStatusBarLightMode(MainActivity.this, getResources().getColor(R.color.home_RadioButton_check));
                        break;
                }
            }
        });

    }
    //初始化布局
    private void initView() {
        setDrawable(mainRbHome, R.drawable.home_radio_button_bg);
        setDrawable(mainRbFroum,R.drawable.forum_radio_button_bg);
        setDrawable(mainRbMine,R.drawable.mine_radio_button_bg);

        if(null==home){
            home=new HomeFragment();
            Eyes.setStatusBarLightMode(MainActivity.this, getResources().getColor(R.color.color_toolbar));
            manager.beginTransaction().add(R.id.main_replace_layout,home).show(home).commit();
        }
    }


    /**
     * 为 RadioButton 设置 selector
     *
     * @param mRadioButton
     * @param id
     */
    private void setDrawable(RadioButton mRadioButton, int id) {
        Drawable mDrawable = getResources().getDrawable(id);
        //设置图标大小
        mDrawable.setBounds(0, 0, 60, 60);
        mRadioButton.setCompoundDrawables(null, null, null, mDrawable);
    }

    //隐藏所有的
    private void hideAllFgIfNotNull(){
        FragmentTransaction transaction=manager.beginTransaction();
        if(null!=home){
            transaction.hide(home);
        }

        if(null!=froum){
            transaction.hide(froum);
        }

        if (null!=mine){
            transaction.hide(mine);
        }
        transaction.commitAllowingStateLoss();

    }


    @Override
    public int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void reloadData() {

    }


    /**
     * 重写返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //物理返回键值
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //连续点击两次返回键退出程序
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出程序
     */
    private void exit() {
        if (!isExited) {
            isExited = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            Message msg = new Message();
            msg.what = 0;
            mHandler.sendMessageDelayed(msg, 2000);
        } else {
            finish();

            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }



}
