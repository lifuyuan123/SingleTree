package com.sctjsj.basemodule.core.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.SingletonManager;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.core.config.SystemConfig;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.basemodule.core.router_service.impl.SearchRecordServiceImpl;

/**
 * Created by Chris-Jason on 2016/10/31.
 */
public class BaseApplication extends Application {
    private String TAG="@link com.sctjsj.basemodule.core.app.BaseApplication";
    public SingletonManager singletonManager=SingletonManager.INSTANCE;
    public int activityCount;//activity的count数


    public ActivityLifecycleCallbacks lifecycleCallbacks=new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityCount++;
            if(activityCount>0){
                sendMsg(1);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCount--;
            if(activityCount<0){
                activityCount=0;
            }
            if(activityCount==0){
                sendMsg(0);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        /**尽量逻辑单例持有各大对象**/


        //初始化系统配置
        singletonManager.registerObj(Tag.TAG_SINGLETON_SYSTEM_CONFIG_HOLDER,SystemConfig.init(this));

        //初始化 Picasso 配置
        try {
            singletonManager.registerObj(Tag.TAG_SINGLETON_PICASSO_CONFIG_HOLDER, PicassoUtil.init(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //初始化 SharedPreference 存储
        SPFUtil.init(this);

        //注册 app 生命周期监听
        registerActivityLifecycleCallbacks(lifecycleCallbacks);


    }

    /**
     * 对整体生命周期做处理
     * @param op
     */
    private void sendMsg(int op){
        switch (op){
            case 0:
                LogUtil.e(TAG,"程序已经处于后台");
                break;
            case 1:
                LogUtil.e(TAG,"程序已经处于前台");
                break;
        }

    }


}
