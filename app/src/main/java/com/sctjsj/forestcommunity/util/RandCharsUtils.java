package com.sctjsj.forestcommunity.util;

/**
 * Created by mayikang on 17/1/16.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * nonce_strÀÊº¥◊÷∑˚¥Æ
 * @author iYjrg_xiebin
 * @date 2015ƒÍ11‘¬25»’œ¬ŒÁ5:10:32  ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz
 */
public class RandCharsUtils {
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String getRandomString(int length) { //length±Ì æ…˙≥…◊÷∑˚¥Æµƒ≥§∂»
        String base = "81979a741d667c30ec56ba9d60c005c9";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        int number = 0;
        for (int i = 0; i < length; i++) {
            number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /*
     * ∂©µ•ø™ ºΩª“◊µƒ ±º‰
     */
    public static String timeStart(){
        return df.format(new Date());
    }

    /*
     * ∂©µ•ø™ ºΩª“◊µƒ ±º‰
     */
    public static String timeExpire(){
        Calendar now=Calendar.getInstance();
        now.add(Calendar.MINUTE,35);
        return df.format(now.getTimeInMillis());
    }




}
