package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.IntegralBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lifuy on 2017/8/14.
 */

public class InregralAdapter extends RecyclerView.Adapter<InregralAdapter.MyViewHolder> {

    private Context context;
    private List<IntegralBean> list = new ArrayList<>();

    public InregralAdapter(Context context, List<IntegralBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.integral_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        IntegralBean bean=list.get(position);
        switch (bean.getType()){
            case 1:
                holder.tvIntegral.setText("+"+bean.getIntegral());
                break;
            case 2:
                holder.tvIntegral.setText("-"+bean.getIntegral());
                break;
        }

        holder.tvName.setText(bean.getName());
        holder.tvTime.setText(bean.getTime());
        PicassoUtil.getPicassoObject().load(bean.getIconUrl())
                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80)).error(R.drawable.icon_user_default)
                .into(holder.ivIcon);
        holder.tvClick.setText(bean.getRemark());
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack!=null){
                    callBack.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvTime;
        TextView tvClick;
        TextView tvIntegral;
        Button btDelete;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivIcon= (ImageView) itemView.findViewById(R.id.iv_icon);
            tvName= (TextView) itemView.findViewById(R.id.tv_inregral_name);
            tvTime= (TextView) itemView.findViewById(R.id.tv_time);
            tvClick= (TextView) itemView.findViewById(R.id.tv_click);
            tvIntegral= (TextView) itemView.findViewById(R.id.tv_integral);
            btDelete= (Button) itemView.findViewById(R.id.bt_delete);
        }
    }

    public interface CallBack{
        void  onClick(int position);
    }
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}
