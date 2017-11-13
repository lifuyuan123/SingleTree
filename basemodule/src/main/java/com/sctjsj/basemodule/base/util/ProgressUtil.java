package com.sctjsj.basemodule.base.util;

import android.app.Activity;

import com.sctjsj.basemodule.base.ui.widget.LoadingDialog;


/**
 * Created by mayikang on 17/2/7.
 */

public class ProgressUtil {
    private Activity mActivity;
    private static LoadingDialog dialog;

    public ProgressUtil(Activity activity){
        this.mActivity=activity;
    }


    public  void show( boolean b, String str){

        if(dialog==null){
            dialog=new LoadingDialog(mActivity);
        }
        dialog.setCancelable(b);
        dialog.setLoadingStr(str);
        if(!dialog.isShowing()){
            dialog.show();
        }else {
            return;
        }

    }

    public  void close(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

}
