package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.forestcommunity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haohaoliu on 2017/8/10.
 * explain:社区论坛分类的适配器
 */

public class FroumSortAdapter extends RecyclerView.Adapter<FroumSortAdapter.FroumSortHolder> {
    private ArrayList<HashMap<String,Object>> data;
    private Context mContext;
    private LayoutInflater inflater;

    public FroumSortAdapter(ArrayList<HashMap<String,Object>> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        this.inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public FroumSortHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.froum_lay_c_item,null);
        return new FroumSortHolder(view);
    }

    @Override
    public void onBindViewHolder(FroumSortHolder holder, int position) {
        HashMap<String, Object> mapData = data.get(position);
        final String tag= (String) mapData.get("tag");
        int id= (int) mapData.get("img");
        holder.froum_lay_c_sort_IMV.setImageResource(id);
        holder.froum_lay_c_sort_txt.setText(tag);
        holder.froum_lay_c_sort_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/main/act/CommForumListActivity").withString("Tag",tag).navigation();
            }
        });


    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    static  class  FroumSortHolder extends RecyclerView.ViewHolder{
        RelativeLayout froum_lay_c_sort_layout;
        ImageView froum_lay_c_sort_IMV;
        TextView froum_lay_c_sort_txt;

        public FroumSortHolder(View itemView) {
            super(itemView);
            froum_lay_c_sort_layout= (RelativeLayout) itemView.findViewById(R.id.froum_lay_c_sort_layout);
            froum_lay_c_sort_IMV= (ImageView) itemView.findViewById(R.id.froum_lay_c_sort_IMV);
            froum_lay_c_sort_txt= (TextView) itemView.findViewById(R.id.froum_lay_c_sort_txt);
        }
    }
}
