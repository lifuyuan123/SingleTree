package com.sctjsj.basemodule.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mayikang on 17/5/16.
 */

public class StringUtil {

    /**
     * 判断 str 是否为空
     * @param str
     * @return
     */
    public static boolean isBlank(String str){

        if(str==null){
           return true;
        }

        if("null".equals(str)){
            return true;
        }


        if(str.isEmpty()){
            return true;
        }

        return false;
    }

    /**
     * 判断是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        if(!StringUtil.isBlank(str)){
            Pattern pattern = Pattern.compile("^[0-9]+(.[0-9])?$");
            Matcher isNum = pattern.matcher(str);
            if( !isNum.matches() ){
                return false;
            }
            return true;
        }
        return false;
    }

}
