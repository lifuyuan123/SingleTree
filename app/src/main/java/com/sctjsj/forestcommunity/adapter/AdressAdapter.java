package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.AdressBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lifuy on 2017/8/11.
 */

public class AdressAdapter extends RecyclerView.Adapter<AdressAdapter.MyViewHolder> {

    private List<AdressBean> list = new ArrayList<>();
    private Context context;

    public AdressAdapter(List<AdressBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adress_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final AdressBean bean=list.get(position);
        //编辑地址
        holder.linEditAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/main/act/AddAdress").withObject("data",bean).withInt("key",2).navigation();
            }
        });
        //删除地址
        holder.linDeleteAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           if(callBack!=null){
               callBack.deleteItem(position);
           }
            }
        });
        if(bean.isDefault()){
            holder.tvDefault.setVisibility(View.VISIBLE);
        }
        holder.tvName.setText(bean.getName());
        holder.tvPhone.setText(bean.getPhone());
        holder.tvAdress.setText(bean.getAdressRegion()+bean.getAdress());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPhone;
        TextView tvDefault;
        TextView tvAdress;
        LinearLayout linEditAdress;
        LinearLayout linDeleteAdress;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName= (TextView) itemView.findViewById(R.id.tv_name);
            tvPhone= (TextView) itemView.findViewById(R.id.tv_phone);
            tvDefault= (TextView) itemView.findViewById(R.id.tv_default);
            tvAdress= (TextView) itemView.findViewById(R.id.tv_adress);
            linEditAdress= (LinearLayout) itemView.findViewById(R.id.lin_editAdress);
            linDeleteAdress= (LinearLayout) itemView.findViewById(R.id.lin_deleteAdress);
        }
    }

    public interface CallBack{
        void deleteItem(int position);
    }
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}
