package com.sctjsj.basemodule.core.router_interceptor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.MainLooper;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.core.config.AsyncNotifyCode;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.config.TargetPage;

/**
 * Created by mayikang on 17/2/8.
 */

/**
 * 用户登录拦截器
 * priority 越小，优先级越高
 * 所有拦截器会在程序初始化的时候完成注入
 */
@Interceptor(priority = 1,name = "login-interceptor")
public class LoginInterceptor implements IInterceptor {
    private final String TAG="LoginInterceptor";
    private Context mContext;
    private InterceptorCallback mCallback;
    private Postcard mPostcard;
    /**
     * 所有路由操作都要进入这个拦截器
     * @param postcard
     * @param callback
     */

    @Override
    public void process(Postcard postcard, final InterceptorCallback callback) {
        LogUtil.e(TAG,"进入登录拦截器"+postcard.getPath());

        /**需要登录的页面**/
        if(postcard.getPath().contains("/user/")){
            mPostcard=postcard;
            mCallback=callback;
            //TODO 判断是否已经登录
            if(SPFUtil.get(Tag.TAG_TOKEN,"none").equals("none")){
                //callback.onInterrupt(new Throwable("页面跳转已被拦截"));
                //切换到主线程
                MainLooper.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext.getApplicationContext(), "请登录后操作", Toast.LENGTH_SHORT).show();
                    }
                });

                ARouter.getInstance().build(TargetPage.PAGE_AFTER_LOGIN_INTERCEPTED).navigation();
            }else {
                //TODO 如果已经登录，直接 callback.onContunue;否则跳转到登录页
                callback.onContinue(postcard);
            }
        }else {
            /**无需登录**/
             callback.onContinue(postcard);
        }
    }

    @Override
    public void init(Context context) {
        mContext=context;

        //注册广播接收器
        AccountReceiver accountReceiver=new AccountReceiver();
        IntentFilter intentFilter=new IntentFilter(Tag.TAG_LOGIN_FILTER);
        LocalBroadcastManager.getInstance(context).registerReceiver(accountReceiver,intentFilter);

    }


    /**
     * 接收用户登录状态的广播
     */
    public class AccountReceiver extends BroadcastReceiver {

        public AccountReceiver(){

        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            LogUtil.e("接收到了本地广播");
            int result=intent.getIntExtra(Tag.TAG_LOGIN_RESULT,0);

            switch (result){
                case AsyncNotifyCode.CODE_LOGIN_SUCCESS:
                    if(mCallback!=null){

                        MainLooper.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                        mCallback.onContinue(mPostcard);
                    }

                    break;
                case AsyncNotifyCode.CODE_LOGIN_FAILED:
                    if(mCallback!=null){

                        MainLooper.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                        mCallback.onInterrupt(new Throwable("用户登录失败"));
                    }

                    break;
                case AsyncNotifyCode.CODE_LOGIN_ERROR:
                    if(mCallback!=null){

                        MainLooper.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "登录错误", Toast.LENGTH_SHORT).show();
                            }
                        });

                        mCallback.onInterrupt(new Throwable("用户登录错误"));
                    }

                    break;

            }
        }
    }


}
