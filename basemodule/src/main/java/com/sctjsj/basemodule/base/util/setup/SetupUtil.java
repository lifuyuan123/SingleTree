package com.sctjsj.basemodule.base.util.setup;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by mayikang on 17/2/6.
 * 安装的 app 检测工具
 */

public class SetupUtil {
    /**
     * 支付宝：com.eg.android.AlipayGphone
     * 微信：com.tencent.mm
     */
    private String TAG = "SetupUtil";
    private static Context context;
    private static SetupUtil instance;

    public static SetupUtil getInstance(Context cont) {
        if (instance == null) {
            instance = new SetupUtil();
            context = cont;
        }
        return instance;
    }

    /**
     * 传入相应的包名，判断是否有安装
     *
     * @param name
     * @return
     */
    public static boolean isXSetuped(String name) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(name)) {
                    return true;
                }
            }
        }
        return false;

    }

    public void getAllPackage() {
        ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            //非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appList.add(tmpInfo);//如果非系统应用，则添加至appList
                Log.e(TAG, "appName:" + tmpInfo.appName + "packageName:" + tmpInfo.packageName);
            }

        }


    }

    /**
     * 判断某个 activity 是否存在于任务栈中
     * @return
     */
    public static boolean isXInTaskStack(Context context,Class<?> cls){

            Intent intent = new Intent(context, cls);
            ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
            if (cmpName != null) { // 说明系统中存在这个activity
                ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
                for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                    if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                        return true;
                    }
                }
            }

        return false;
    }
}