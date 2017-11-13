package com.sctjsj.forestcommunity.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.forestcommunity.R;

/**
 * Created by haohaoliu on 2017/8/14.
 * explain:
 */

public class ShopPhoneDialog extends Dialog {
    private LayoutInflater inflater;
    private Context mContext;
    private View mView;

    private TextView shop_phone;
    private LinearLayout shop_phone_layout;


    public ShopPhoneDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext=context;
        this.inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    public ShopPhoneDialog(Context context) {
        super(context);
        this.mContext=context;
        this.inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }
    //初始化
    private void init() {
        mView=inflater.inflate(R.layout.dialog_shop_phone,null);
        setContentView(mView);
        shop_phone= (TextView) mView.findViewById(R.id.shop_phone_txt);
        shop_phone_layout= (LinearLayout) mView.findViewById(R.id.shop_phone_layout);
        shop_phone_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack!=null){
                    callBack.click();
                }
            }
        });

    }
    //设置电话号码
    public void setPhone(String phoneNumber){
        if (null!=shop_phone&& !StringUtil.isBlank(phoneNumber)) {
            shop_phone.setText(phoneNumber);
        }
    }

    public interface CallBack{
        void click();
    }
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}
