package com.sctjsj.basemodule.base.ui.widget.dialog.sweet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sctjsj.basemodule.R;


public class PopListDialog extends Dialog implements View.OnClickListener {
    private PopListCallback callback;
    private Activity mActivity;
    private Context context;
    /**
     * Activity中创建
     * @param mActivity
     */
    public PopListDialog(Activity mActivity) {

        super(mActivity, R.style.pop_list_dialog_style);
        this.mActivity = mActivity;
        setCancelable(false);
    }

    public PopListDialog(Context context) {

        super(context, R.style.pop_list_dialog_style);
        this.context= context;
        setCancelable(false);
    }
    /**
     * Fragment中创建
     * @param mFragment
     */
    public PopListDialog(Fragment mFragment) {
        super(mFragment.getActivity(), R.style.pop_list_dialog_style);
        this.mActivity = mFragment.getActivity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pop_list_dialog);
        initLocation();
        initView();
    }

    private void initLocation(){
        Window Window = this.getWindow();
        //显示位置
        Window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = Window.getAttributes();
        params.x=0;
        params.y=0;
        //params.y = 20;
        Window.setAttributes(params);
    }

    private void initView(){

        findViewById(R.id.rl_choose_from_camera).setOnClickListener(this);
        findViewById(R.id.choose_from_gallery).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        if(view.getId()== R.id.rl_choose_from_camera){
            if(callback!=null){
                callback.callCamera();
            }
        }

        if(view.getId()== R.id.choose_from_gallery){
            if(callback!=null){
                callback.callGallery();
            }
        }

        if(view.getId()== R.id.cancel){
            dismiss();
        }


    }


    public interface PopListCallback{
        void callCamera();
        void callGallery();
    }

    public void setPopListCallback(PopListCallback callback){
        this.callback=callback;
    }

}
