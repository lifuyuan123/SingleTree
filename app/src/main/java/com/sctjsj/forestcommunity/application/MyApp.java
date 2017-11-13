package com.sctjsj.forestcommunity.application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.sctjsj.basemodule.core.app.BaseApplication;
import com.sctjsj.forestcommunity.db.DBManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by haohaoliu on 2017/8/9.
 * explain:
 */

public class MyApp extends BaseApplication {
    private DBManager dbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "64fd0f40b5", false);
        Beta.autoInit = true;
        Beta.autoCheckUpgrade = true;
        //导入数据库
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
