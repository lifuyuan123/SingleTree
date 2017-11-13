package com.sctjsj.basemodule.base.util;

import android.os.Handler;
import android.os.Looper;

public class MainLooper extends Handler {
    //默认实例化主线程
    private static MainLooper instance = new MainLooper(Looper.getMainLooper());

    protected MainLooper(Looper looper) {
        super(looper);
    }

    public static MainLooper getInstance() {
        return instance;
    }

    public static void runOnUiThread(Runnable runnable) {
        //判断当前线程是否为主线程，不是则切换到主线程
        if(Looper.getMainLooper().equals(Looper.myLooper())) {
            runnable.run();
        } else {
            instance.post(runnable);
        }

    }
}
