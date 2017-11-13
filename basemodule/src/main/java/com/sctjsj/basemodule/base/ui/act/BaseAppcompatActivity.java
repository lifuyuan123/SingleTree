package com.sctjsj.basemodule.base.ui.act;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.R;
import com.sctjsj.basemodule.base.receiver.NetworkChangeReceiver;
import com.sctjsj.basemodule.base.ui.widget.LoadingDialog;
import com.sctjsj.basemodule.base.ui.widget.NoNetworkRemindView;
import com.sctjsj.basemodule.base.ui.widget.dialog.CommonDialog;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.network.NetUtil;
import com.sctjsj.basemodule.core.event.PushEvent;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by mayikang on 17/2/6.
 */

/**
 * 封装的  AppcompatActivity 基类
 */

public abstract class BaseAppcompatActivity extends AppCompatActivity {
    private Unbinder unbinder;
    /**页面根布局**/
    private View rootView;
    private ViewGroup root;

    /**无网络提示控件**/
    private NoNetworkRemindView noNetworkRemindView;

    /**无网络时弹出的 dialog**/
    private AlertDialog noNetworkRemindDialog;
    private AlertDialog.Builder noNetworkRemindBuilder;

    //网络状态发生变化时，是否需要提示重新加载页面
    private boolean isNeedNetRemind =true;

    /** 日志输出标志 **/
    protected final String TAG = this.getClass().getSimpleName();

    /**判断 activity 是否处于激活状态**/
    private boolean isReady=false;

    /**加载进度对话框**/
    private LoadingDialog dialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(initLayout());
        EventBus.getDefault().register(this);
        LogUtil.e(TAG+":onCreate");
        //锁定屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //ARouter 依赖注入
        ARouter.getInstance().inject(this);

        //注册网络变化监听
        NetworkChangeReceiver.setOnNetworkChangedListener(new NetworkChangeReceiver.OnNetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean isConnected, int type) {
                //无网络
                if(!isConnected){
                    //showNoNetworkRemind();
                    showNoNetworkDialog();
                } else {
                    //dismissNoNetworkRemind();
                    dismissNoNetworkDialog();
                }
            }
        });

        if(!NetUtil.isConnected(this)){
            showNoNetworkDialog();
        }



    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        unbinder=ButterKnife.bind(this);
    }

    public abstract @LayoutRes int initLayout();

    @Override
    protected void onStart() {
        super.onStart();

        isReady=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e(TAG+":onResume");

        if(NetUtil.isConnected(this)){
            // dismissNoNetworkRemind();
            dismissNoNetworkDialog();
        }else {
            // showNoNetworkRemind();
            showNoNetworkDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e(TAG+":onPause");
    }

    @Override
    protected void onStop() {

        super.onStop();
        isReady=false;
        LogUtil.e(TAG+":onStop");
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        //取消注册 EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        LogUtil.e(TAG+":onDestroy");

    }

    /**
     * 判断 activity 是否处于激活状态
     * @return
     */
    public boolean isActReady(){
        return isReady;
    }

    /**
     * 设置是否需要网络变化提示
     * @param b
     */
    public void setNeedNetRemind(boolean b){
        this.isNeedNetRemind =b;
    }

    /**
     * 显示无网络提示,遮盖式
     */
    public void showNoNetworkRemind(){

        if(isNeedNetRemind){
            //获取页面根布局
            rootView=  getWindow().getDecorView().findViewById(android.R.id.content);
            root= (ViewGroup) rootView;

            if(noNetworkRemindView==null){
                noNetworkRemindView=new NoNetworkRemindView(this);
                //设置点击重新加载数据的监听
                noNetworkRemindView.setOnReloadClickListerer(new NoNetworkRemindView.ReloadClickListener() {
                    @Override
                    public void reload() {
                        reloadData();
                    }

                    @Override
                    public void setNetwork() {
                        goToSetNetwork();
                    }
                });

                root.addView(noNetworkRemindView);
            }

            //重新获取一次根布局
            rootView=  getWindow().getDecorView().findViewById(android.R.id.content);
            root= (ViewGroup) rootView;

            //循环遍历根布局下的所有子布局，隐藏其他布局，显示无网络提示
            for (int i=0;i<root.getChildCount();i++){
                if(root.getChildAt(i) instanceof NoNetworkRemindView){
                    root.getChildAt(i).setVisibility(View.VISIBLE);
                }else {
                    root.getChildAt(i).setVisibility(View.GONE);
                }
            }

        }

    }

    /**
     * 关闭无网络提示
     */
    public void dismissNoNetworkRemind(){

        if(isNeedNetRemind){
            //获取页面根布局
            rootView=  getWindow().getDecorView().findViewById(android.R.id.content);
            root= (ViewGroup) rootView;

            //循环遍历根布局下的所有子布局，显示其他布局，隐藏无网络提示
            for (int i=0;i<root.getChildCount();i++){
                if(root.getChildAt(i) instanceof NoNetworkRemindView){
                    root.getChildAt(i).setVisibility(View. GONE);
                }else {
                    root.getChildAt(i).setVisibility(View.VISIBLE);
                }
            }
        }


    }

    /**
     * 显示无网络提示，弹框式
     */
    public void showNoNetworkDialog(){

        if(noNetworkRemindBuilder==null && noNetworkRemindDialog==null){
            noNetworkRemindBuilder=new AlertDialog.Builder(this);
            View remindView= LayoutInflater.from(this).inflate(R.layout.no_network_remind_layout,null);
            //点击重新加载数据
            remindView.findViewById(R.id.no_network_view_tv_reload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reloadData();
                }
            });

            //点击去设置网络
            remindView.findViewById(R.id.no_network_view_tv_set_net).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToSetNetwork();
                }
            });
            noNetworkRemindBuilder.setTitle("网络异常");
            noNetworkRemindBuilder.setView(remindView);
            noNetworkRemindDialog=noNetworkRemindBuilder.create();

        }

        if(!noNetworkRemindDialog.isShowing()&& isReady){
            noNetworkRemindDialog.show();
        }


    }

    /**
     * 关闭无网络提示
     */
    public void dismissNoNetworkDialog(){
        if(noNetworkRemindBuilder==null || noNetworkRemindDialog==null){
            return;
        }
        if(noNetworkRemindDialog.isShowing()){
            noNetworkRemindDialog.dismiss();
        }
    }

    /**
     * 重新加载数据的方法必须重写
     */
    public  abstract void reloadData();

    /**
     * 去设置网络
     */
    public void goToSetNetwork(){
        NetUtil.openSetting(this);
    }

    /**
     * 显示加载dialog
     * @param str
     */
    public void showLoading(boolean b,String str){
        if(dialog==null){
            dialog=new LoadingDialog(this);
        }
        dialog.setCancelable(b);
        dialog.setLoadingStr(str);
        if(!dialog.isShowing() && isReady){
            dialog.show();
        }

    }

    /**
     * 关闭加载 dialog
     */
    public void dismissLoading(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMainEvent(final PushEvent event){
        Log.e("onMainEvent","------------");
        if(event!=null){

            if(1==event.getPb().getType()){

                if(1==event.getPb().getState()){
                    try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                               String content=event.getPb().getContent();
                               final CommonDialog dialog=new CommonDialog(BaseAppcompatActivity.this);
                               dialog.setTitle("下线通知");
                               String str=null;
                               if(content.contains("ANDROID")|| content.contains("android")){
                                   str="ANDROID";
                               }

                               if(content.contains("IOS")|| content.contains("ios")){
                                   str="IOS";
                               }
                               dialog.setContent("您的帐号在另一台"+str+"设备上登录。如非本人操作，密码可能已泄露。");
                               dialog.setCancelable(false);
                               dialog.setCancelClickListener("退出", new CommonDialog.CancelClickListener() {
                                   @Override
                                   public void clickCancel() {
                                       dialog.dismiss();
                                       ARouter.getInstance().build("/main/act/login").navigation();
                                   }
                               });

                               dialog.setConfirmClickListener("重新登录", new CommonDialog.ConfirmClickListener() {
                                   @Override
                                   public void clickConfirm() {
                                       dialog.dismiss();
                                       ARouter.getInstance().build("/main/act/login").navigation();
                                   }
                               });

                               dialog.show();

                        }
                    });

                    }catch (Exception e){
                        e.getStackTrace();
                    }

                }

            }

        }
    }


}
