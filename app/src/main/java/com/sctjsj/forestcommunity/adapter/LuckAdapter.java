package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.LuckBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifuy on 2017/8/15.
 */

public class LuckAdapter extends RecyclerView.Adapter<LuckAdapter.MyViewHolder> {

    private List<LuckBean> list=new ArrayList<>();
    private Context context;

    public LuckAdapter(List<LuckBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public LuckAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.luck_item,parent,false));
    }

    @Override
    public void onBindViewHolder(LuckAdapter.MyViewHolder holder, int position) {
        LuckBean bean=list.get(position);
        holder.textView.setText(bean.getUserName()+"抽中了"+bean.getCount()+"台"+bean.getPrize());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView= (TextView) itemView.findViewById(R.id.tv_luck_content);
        }
    }
}
