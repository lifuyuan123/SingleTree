package com.sctjsj.basemodule.base.util;


import android.util.Log;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mayikang on 17/4/8.
 */

public class TimeUtil {

    /**
     * 获取系统时间戳
     * @return
     */
    public static long getTimestamp(){

        long time= System.currentTimeMillis();

        return time;

    }

    /**
     * 判断time2是否比 time1晚 age
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isAvailable(long time1, long age, long time2){

        return (time1+age-time2)>0;
    }



}
