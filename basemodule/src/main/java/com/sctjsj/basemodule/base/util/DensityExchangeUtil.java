package com.sctjsj.basemodule.base.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by mayikang on 17/2/6.
 * 常见的密度单位之间相互转换工具
 */

public class DensityExchangeUtil
{
    private DensityExchangeUtil()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("DensityExchangeUtil 无法实例化");
    }

    /**
     * dp转px
     * @param context
     * @return
     */
    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     * @param context
     * @return
     */
    public static int sp2px(Context context, float spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal)
    {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

}