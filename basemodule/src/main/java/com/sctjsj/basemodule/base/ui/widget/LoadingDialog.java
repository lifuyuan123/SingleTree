package com.sctjsj.basemodule.base.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sctjsj.basemodule.R;


/**
 * Created by liudezhi on 16/8/19.
 */
public class LoadingDialog extends Dialog{

    private Activity mActivity;
    private Context mContext;
    private TextView mTVStr;

    /**
     * Activity中创建
     * @param mActivity
     */
    public LoadingDialog(Activity mActivity) {
        super(mActivity, R.style.loading_dialog);
        this.mActivity = mActivity;
        setContentView(R.layout.loading_dialog);
        initLocation();
        initView();

    }

    public LoadingDialog(Context context){
        super(context,R.style.loading_dialog);
        mContext=context;
        setContentView(R.layout.loading_dialog);
        initLocation();
        initView();

    }

    /**
     * Fragment中创建
     * @param mFragment
     */
    public LoadingDialog(Fragment mFragment) {
        super(mFragment.getActivity(), R.style.loading_dialog);
        this.mActivity = mFragment.getActivity();
        setContentView(R.layout.loading_dialog);
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
        mTVStr= (TextView) findViewById(R.id.loading_dialog_tv_str);
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
