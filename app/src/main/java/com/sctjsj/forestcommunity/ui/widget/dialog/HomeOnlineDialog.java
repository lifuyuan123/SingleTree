package com.sctjsj.forestcommunity.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.basemodule.base.util.CallUtil;
import com.sctjsj.forestcommunity.R;

/**
 * Created by XiaoHaoWit on 2017/10/12.
 */

public class HomeOnlineDialog extends Dialog {

    private Context mContext;
    private LayoutInflater inflater;
    private View root;
    private LinearLayout dialog_online_qq;
    private LinearLayout dialog_online_phone;
    private TextView dialog_online_cancle;
    private  MyListener listener;


    public HomeOnlineDialog(Context context) {
        super(context, R.style.All_dialog);
        this.mContext=context;
        this.inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }



    private void init() {
        findView();

    }




    //查找控件
    private void findView() {
        listener=new MyListener();
        root=inflater.inflate(R.layout.dialog_online_layout,null);
        dialog_online_qq= (LinearLayout) root.findViewById(R.id.dialog_online_qq);
        dialog_online_qq.setOnClickListener(listener);
        dialog_online_phone= (LinearLayout) root.findViewById(R.id.dialog_online_phone);
        dialog_online_phone.setOnClickListener(listener);
        dialog_online_cancle= (TextView) root.findViewById(R.id.dialog_online_cancle);
        dialog_online_cancle.setOnClickListener(listener);
        setContentView(root);

    }


    private class  MyListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.dialog_online_qq:
                    String url="mqqwpa://im/chat?chat_type=wpa&uin=1035854824";
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    break;
                case  R.id.dialog_online_phone:
                    CallUtil.makeCall(mContext, "18783834552");
                    break;
                case R.id.dialog_online_cancle:
                    dismiss();
                    break;
            }
        }
    }

}
