package com.sctjsj.basemodule.base.util.setup;

import android.content.Context;
import android.provider.Settings;

import com.sctjsj.basemodule.base.util.LogUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Created by mayikang on 17/4/11.
 */

public class DeviceUtil {
    private  static String TAG="DeviceUtil";

    //获取DeviceId用Android_ID
    public static String getDeviceId(Context mContext){
        String DeviceId = Settings.System.getString(mContext.getContentResolver(), Settings.System.ANDROID_ID);
        if(DeviceId!=null && !DeviceId.isEmpty()){

            return DeviceId;
        }else {
            return "none-device-id";
        }

    }

    /**
     * 获取手机的 cpu 核心数量
     * @return
     */
    public static int  getCpuCoreNum() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            LogUtil.e(TAG,"cpu 核心数为"+files.length);
            return files.length;
        } catch(Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG,"cpu 核心数获取失败，默认至少为1");
            return 1;
        }
    }

    /**
     * 获取 android 设备型号
     * @return
     */
    public static String getPhoneModel(){
        String model= android.os.Build.MODEL;
        return model;
    }

    /**
     * 获取手机操作系统型号
     * @return
     */
    public static String getPhoneOSVersion(){
        String OSVersion="android-os-"+android.os.Build.VERSION.RELEASE;
        return OSVersion;
    }


}
