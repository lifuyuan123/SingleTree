package com.sctjsj.forestcommunity.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * Created by XiaoHaoWit on 2017/9/6.
 */

public class TextColorUtil {


    /**
     *  对指定字符设置颜色
     * @param str
     *                         字符串
     * @param str
     *                         要变色的文字
     * @param str2
     *                         不用变色的文字
     * @param color
     *                         设置的颜色
     */
    public static SpannableStringBuilder setTVColor(String str, String str2, int space, int color ){
        StringBuffer buffer=new StringBuffer();
        buffer.append(str);
        for (int i = 0; i <space ; i++) {
            buffer.append(" ");
        }
        buffer.append(str2);

        String curr=buffer.toString();
        //变色从str的0到str的长度的位置
        int a = 0;
        int b = str.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(curr);
        builder.setSpan(new ForegroundColorSpan(color), a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }
}
