package com.sctjsj.basemodule.base.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sctjsj.basemodule.R;

/**
 * 菊花 加载进度对话框
 */
public class PDialog extends Dialog{
    private Activity mActivity;
    private Context mContext;
    private TextView mTVStr;
    private ImageView mIV;

    private  AnimationDrawable drawable;
    /**
     * Activity中创建
     * @param mActivity
     */
    public PDialog(Activity mActivity) {
        super(mActivity, R.style.loading_dialog);
        this.mActivity = mActivity;
        setContentView(R.layout.p_dialog_layout);
        initLocation();
        initView();

    }

    public PDialog(Context context){
        super(context,R.style.loading_dialog);
        mContext=context;
        setContentView(R.layout.p_dialog_layout);
        initLocation();
        initView();

    }

    /**
     * Fragment中创建
     * @param mFragment
     */
    public PDialog(Fragment mFragment) {
        super(mFragment.getActivity(), R.style.loading_dialog);
        this.mActivity = mFragment.getActivity();
        setContentView(R.layout.p_dialog_layout);
        initLocation();
        initView();
    }

    private void initLocation(){
        Window Window = this.getWindow();
        //显示位置
        Window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = Window.getAttributes();
        params.y = 20;
        Window.setAttributes(params);
    }

    private void initView(){
        mTVStr= (TextView) findViewById(R.id.tv);
        mIV= (ImageView) findViewById(R.id.iv);
        drawable= (AnimationDrawable) mActivity.getResources().getDrawable(R.drawable.loading_mum_anim_style_a);
        mIV.setBackgroundDrawable(drawable);

    }

    public void showPD(){
        show();
        if(drawable!=null && !drawable.isRunning()){
            drawable.start();
        }
    }

    public void dismissPD(){
        dismiss();
        if(drawable!=null && drawable.isRunning()){
            drawable.stop();
        }
    }

    /**
     * 设置加载中的提示文字
     * @param str
     */
    public void setLoadingStr(String str){
        if(!TextUtils.isEmpty(str)){
            mTVStr.setText(str);
            mTVStr.setVisibility(View.VISIBLE);
        }else {
            mTVStr.setVisibility(View.GONE);
        }
    }

}
