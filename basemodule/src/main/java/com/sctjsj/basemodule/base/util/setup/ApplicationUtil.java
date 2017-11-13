package com.sctjsj.basemodule.base.util.setup;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


import com.sctjsj.basemodule.base.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayikang on 17/3/16.
 */

public class ApplicationUtil {
    private ApplicationUtil(){

    }

    private final static String TAG="ApplicationUtil";
    /**
     * 判断某一个 Service 是否正在运行
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if(!(serviceList.size() > 0)) {
            return false;
        }
        for(int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if(serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取当前安装的版本号
     * @return
     */
    public static int getLocalVersion(Context context){
        int version=-1;
        try {
            version= context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取当前程序的包名
     * @param context
     * @return
     */
    public static String getPackageName(Context context){
        String pkgName="pkg";
        try{
            pkgName=context.getPackageName();
        }catch (Exception ex){
            LogUtil.e(TAG,ex.toString());
        }

        return pkgName;
    }

}
