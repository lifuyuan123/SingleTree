package com.sctjsj.basemodule.core.exception;

/**
 * Created by mayikang on 17/5/4.
 */

import android.content.Context;

/**
 * 类重复初始化异常
 */
public class DuplicateInitException extends RuntimeException {

    private Context context;
    public DuplicateInitException(Context context,Class<?> c){
        super("类重复初始化异常-Context：@"+context.toString(),new Throwable(c.getSimpleName()));
        this.context=context;
    }

}
