package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.ConcernBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lifuy on 2017/8/15.
 */

public class ConcernAdapter extends RecyclerView.Adapter<ConcernAdapter.MyViewHolder> {


    private Context context;
    private List<ConcernBean> list = new ArrayList<>();

    public ConcernAdapter(Context context, List<ConcernBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.concern_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ConcernBean bean=list.get(position);
        PicassoUtil.getPicassoObject().load(bean.getIconUrl())
                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80)).error(R.drawable.icon_user_default)
                .into(holder.ivConcern);
        holder.tvConcernTitle.setText(bean.getName());
        holder.tvConcernDescribe.setText(bean.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivConcern;
        TextView tvConcernTitle;
        TextView tvConcernDescribe;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivConcern= (CircleImageView) itemView.findViewById(R.id.iv_concern);
            tvConcernTitle= (TextView) itemView.findViewById(R.id.tv_concern_title);
            tvConcernDescribe= (TextView) itemView.findViewById(R.id.tv_concern_describe);
        }
    }
}
