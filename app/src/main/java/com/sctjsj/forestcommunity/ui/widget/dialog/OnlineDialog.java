package com.sctjsj.forestcommunity.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.forestcommunity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lifuy on 2017/8/17.
 */

public class OnlineDialog extends Dialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.lin_phone)
    LinearLayout linPhone;

    private String title;
    private String phone;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public OnlineDialog(@NonNull Context context) {
        super(context, R.style.All_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_dialog);
        ButterKnife.bind(this);
        if(title!=null){
            tvTitle.setText(title);
        }
        if(phone!=null){
            tvPhone.setText(phone);
        }
        linPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack!=null){
                    callBack.onClick();
                }
            }
        });
    }

    public interface OnCallBack{
        void onClick();
    }
    private OnCallBack callBack;

    public void setCallBack(OnCallBack callBack) {
        this.callBack = callBack;
    }
}
