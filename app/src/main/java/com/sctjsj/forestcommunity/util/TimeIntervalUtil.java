package com.sctjsj.forestcommunity.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by XiaoHaoWit on 2017/8/31.
 * 时间格式为 "2017-08-30 18:00:00"
 */

public class TimeIntervalUtil {

    public static  String getInterval(String time){
        try {

            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =simpleDateFormat .parse(time);

            //当前毫秒数
            long currMs=System.currentTimeMillis();
            //传过来的毫秒数
            long  timeStemp =  date.getTime();

            //当前时间大于
            long differMs=currMs-timeStemp;

            int day= 0;
            int hour=0;
            int minute=0;


            day=(int) (differMs/86400000);

            hour=(int) ((differMs-(day*86400000))/3600000);

            minute=(int) ((differMs-((day*86400000)+(hour*3600000)))/60000);


            if(day>30){
                return "一个月以前";
            }

            if(day<30&&day>0){
                return day+"天之前";
            }

            if(day==0&&hour>0){
                return hour+"小时之前";
            }

            if(minute>0&&day==0&&hour==0){
                return minute+"分钟之前";
            }

            if(day==0&&hour==0&&minute==0){
                return "刚刚";
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
