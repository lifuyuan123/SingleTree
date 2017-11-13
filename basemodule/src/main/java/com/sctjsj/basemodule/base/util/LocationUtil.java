package com.sctjsj.basemodule.base.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuha on 2017/4/14.
 */

public class LocationUtil {
    //不能被实例化
    private static  final int LocationUtilsCode=5;
    private static  Map<String,String> locaInfo=new HashMap<>();
    private static  LocationListener mLocationListener;
    private LocationUtil()
    {
        throw new UnsupportedOperationException("LocationUtils   无法实例化");
    }
    //6.0检查权限是否开启
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkLocationOk(Context mContext){
        Activity mActivity=null;

        if(mContext instanceof Activity){
            mActivity= (Activity) mContext;
        }

        if(mActivity==null){
            try {
                throw new Exception("mContext cannot cast to Activity");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        if(mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                &&mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true;
        }else if(mActivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    ||mActivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
            //检测到权限已经禁止提示了到设置中开启
            Intent mIntent=new Intent(Settings.ACTION_SETTINGS);
            mContext.startActivity(mIntent);
        }
        return false;
    }
    //检查网络和GPS是否打开
    private static  boolean NetOrGPSisOk(Context mContext){
        LocationManager mLocationManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if(!(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                &&mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))){
              Toast.makeText(mContext,"请打开网络和GPS",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //初始化定位（获取权限以及打开网络和GPS）
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void initLocation(Context mContext){
       if(!checkLocationOk(mContext)){//检查权限
           Activity mActivity= (Activity) mContext;
           mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LocationUtilsCode);
       }else if(!NetOrGPSisOk(mContext)){
           //跳转到设置界面
           Intent mIntent=new Intent(Settings.ACTION_SETTINGS);
           mContext.startActivity(mIntent);
       }
    }

    /***
     *   获取地理位置
     * @param mContext
     * @return  一个Map集合 里面包含（经纬度和确切的地址）
     *            键： Location 地理位置
     *                  Provice  省份
     *                  City        城市
     *                  Street      街道
     *                  CountryName 国家名称
     *                  Latitude   纬度
     *                  Longitude  经度
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Map<String,String> getLocation(Context mContext){
        //初始化工作完成
        LocationManager mLocationManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location mLocation=null;
        List<Address> fromLocation=null;

        if(NetOrGPSisOk(mContext)&&checkLocationOk(mContext)){
            try {
                mLocation=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(mLocation==null){
                    mLocation= mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                //设置位置监听
                mLocationListener=new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.e("-----","onLocationChanged");
                        if(null!=location){
                            Log.e("------",location.getLongitude()+"");
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.e("-----","onStatusChanged");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.e("-----","onProviderEnabled");
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.e("-----","onProviderDisabled");
                    }
                };
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10,mLocationListener);
                if(mLocation!=null){
                    Geocoder mGeocoder=new Geocoder(mContext);
                    fromLocation = mGeocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    Log.e("位置信息",fromLocation.toString());
                    if (fromLocation!=null&&fromLocation.size()>0) {
                        locaInfo.clear();
                        Address address = fromLocation.get(0);
                        locaInfo.put("Location", address.getAddressLine(0));
                        locaInfo.put("Provice", address.getAdminArea());
                        locaInfo.put("City", address.getLocality());
                        locaInfo.put("Street", address.getThoroughfare());
                        locaInfo.put("CountryName", address.getCountryName());
                        locaInfo.put("Latitude", address.getLatitude() + "");
                        locaInfo.put("Longitude", address.getLongitude() + "");
                        //获取位置成功，关闭位置监听
                        mLocationManager.removeUpdates(mLocationListener);
                        return locaInfo;

                    }

                }else {
                    Toast.makeText(mContext,"定位失败,请稍候再试！",Toast.LENGTH_SHORT).show();
                    return locaInfo;
                }

            }catch (SecurityException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e("locationIOException",e.toString());
            }
        }else {
            Toast.makeText(mContext,"定位初始化失败！",Toast.LENGTH_SHORT).show();
        }
        return locaInfo;
    }


    public static void closeLocation(){


    }


}
