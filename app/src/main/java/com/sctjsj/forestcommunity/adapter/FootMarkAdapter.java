package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.FootMarkBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lifuy on 2017/8/14.
 */

public class FootMarkAdapter extends RecyclerView.Adapter<FootMarkAdapter.MyViewHolder> {

    private Context context;
    private List<FootMarkBean> list = new ArrayList();
    private boolean isShow=false;

    public boolean getShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
        notifyDataSetChanged();
    }

    public List<FootMarkBean> getList() {
        return list;
    }

    public void setList(List<FootMarkBean> list) {
        this.list = list;
    }

    public FootMarkAdapter(Context context, List<FootMarkBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.footmark_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FootMarkBean bean=list.get(position);

        switch (bean.getType()){
            //周边商家
            case 1:
                PicassoUtil.getPicassoObject().load(R.drawable.icon_shops_ic)
                        .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80)).error(R.drawable.icon_user_default)
                        .into(holder.ivFootmarkIcon);
                break;
            //新闻
            case 2:
                PicassoUtil.getPicassoObject().load(R.drawable.icon_news_ic)
                        .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80)).error(R.drawable.icon_user_default)
                        .into(holder.ivFootmarkIcon);
                break;
            //帖子
            case 3:
                PicassoUtil.getPicassoObject().load(R.drawable.icon_froum_ic)
                        .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80)).error(R.drawable.icon_user_default)
                        .into(holder.ivFootmarkIcon);
                break;
        }
        holder.tvFootmarkTitle.setText(bean.getName());
        holder.tvFootmarkTime.setText(bean.getTime());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               bean.setCheck(isChecked);
            }
        });
        if(bean.isCheck()){
            holder.checkbox.setChecked(true);
        }else {
            holder.checkbox.setChecked(false);
        }
        if (isShow){
            holder.checkbox.setVisibility(View.VISIBLE);
        }else {
            holder.checkbox.setVisibility(View.GONE);
        }
        holder.linFootMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack!=null){
                    callBack.onItemClick(position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        ImageView ivFootmarkIcon;
        TextView tvFootmarkTitle;
        TextView tvFootmarkTime;
        LinearLayout linFootMark;
        public MyViewHolder(View itemView) {
            super(itemView);
            checkbox= (CheckBox) itemView.findViewById(R.id.checkbox_footmark);
            ivFootmarkIcon= (ImageView) itemView.findViewById(R.id.iv_footmark_icon);
            tvFootmarkTime= (TextView) itemView.findViewById(R.id.tv_footmark_time);
            tvFootmarkTitle= (TextView) itemView.findViewById(R.id.tv_footmark_title);
            linFootMark= (LinearLayout) itemView.findViewById(R.id.lin_footmark);
        }
    }

    public List<FootMarkBean> delete(){
        List<FootMarkBean> beenMove=new ArrayList<>();
        for (int i = 0; i <list.size(); i++) {
            if(list.get(i).isCheck()){
                beenMove.add(list.get(i));
            }
        }
        if (beenMove.size()==0){
            return null;
        }
        if(list.size()>=beenMove.size()){
            return beenMove;
        }else {
            return null;
        }
    }

   //清除
    public void clearMark(){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).isCheck()){
                list.get(i).setCheck(false);
            }
        }
    }

    //全选
    public void setAllMark(){
        for (int i = 0; i < list.size(); i++) {
            if(!list.get(i).isCheck()){
                list.get(i).setCheck(true);
            }
        }
    }

    public interface CallBack{
        void onItemClick(int position);
    }
    private CallBack callBack;

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}
