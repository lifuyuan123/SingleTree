package com.sctjsj.forestcommunity.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.forestcommunity.R;

/**
 * Created by lifuy on 2017/8/15.
 */

public class DecorationDialog extends Dialog {

    private TextView title;//标题控件
    private TextView  distance;//距离
    private TextView phone;//号码
    private  TextView introduce;//介绍
    private LinearLayout go,cancel;//确认按钮

    public void setTitle(TextView title) {
        this.title = title;
    }

    public void setStitle(String stitle) {
        Stitle = stitle;
    }

    public void setSdistance(String sdistance) {
        Sdistance = sdistance;
    }

    public void setSphone(String sphone) {
        Sphone = sphone;
    }

    public void setSintroduce(String sintroduce) {
        Sintroduce = sintroduce;
    }

    private String Stitle,Sdistance,Sphone,Sintroduce;//外界获取的信息


    public DecorationDialog(@NonNull Context context) {
        super(context, R.style.All_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_decoration);
          //按空白处取消动画  
          setCanceledOnTouchOutside(false);
        initView();
        initData();
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCallback!=null){
                    onCallback.onClick();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    //设置数据
    private void initData() {
        if(Stitle!=null){
            title.setText(Stitle);
        }
        if(Sphone!=null){
            phone.setText(Sphone);
        }
        if(Sdistance!=null){
            distance.setText(Sdistance);
        }
        if(Sintroduce!=null){
            introduce.setText(Sintroduce);
        }
    }

    //初始化控件
    private void initView() {
        title= (TextView) findViewById(R.id.tv_dialog_decoration_title);
        distance= (TextView) findViewById(R.id.tv_dialog_decoration_distance);
        phone= (TextView) findViewById(R.id.tv_dialog_decoration_phone);
        introduce= (TextView) findViewById(R.id.tv_dialog_decoration_introduce);
        go= (LinearLayout) findViewById(R.id.lin_dialog_decoration_go);
        cancel= (LinearLayout) findViewById(R.id.lin_cancel);
    }

    public interface OnCallback{
        void onClick();
    }
    private OnCallback onCallback;

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }
}
