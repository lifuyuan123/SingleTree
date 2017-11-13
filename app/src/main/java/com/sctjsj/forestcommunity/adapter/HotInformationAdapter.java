package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.HotInformationBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lifuy on 2017/8/17.
 */

public class HotInformationAdapter extends RecyclerView.Adapter<HotInformationAdapter.MyViewHolder> {

    private Context context;
    private List<HotInformationBean> list = new ArrayList();

    public HotInformationAdapter(Context context, List<HotInformationBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        //type=1 只有一张图片   type=2 三张图片
        switch (list.get(position).getType()) {
            case 1:
                return 1;
            case 2:
                return 2;
        }
        return super.getItemViewType(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.hot_information_one, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.hot_information_three, parent, false);
                break;
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final HotInformationBean bean=list.get(position);
       switch (getItemViewType(position)){
           case 1:
               holder.lay2.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       ARouter.getInstance().build("/main/act/NewsDetails").withInt("id",bean.getId()).withString("title",bean.getTitle()).navigation();
                   }
               });
               holder.tvHotTitle.setText(bean.getTitle());
               holder.tvCount.setText(bean.getCount()+"");
        PicassoUtil.getPicassoObject().load(bean.getIconUrlList().get(0))
                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                .into(holder.ivHotIcon);
               break;
           case 2:
               //type=2;
               holder.lay1.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       ARouter.getInstance().build("/main/act/NewsDetails").withInt("id",bean.getId()).withString("title",bean.getTitle()).navigation();
                   }
               });
               holder.tvHotTitleOne.setText(bean.getTitle());
               holder.tvCountThree.setText(bean.getCount()+"");
        PicassoUtil.getPicassoObject().load(bean.getIconUrlList().get(0))
                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                .into(holder.ivHotIconOne);
        PicassoUtil.getPicassoObject().load(bean.getIconUrlList().get(1))
                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                .into(holder.ivHotIconTwo);
        PicassoUtil.getPicassoObject().load(bean.getIconUrlList().get(2))
                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                .into(holder.ivHotIconThree);
               break;
       }





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //type=1:
        TextView tvHotTitle;
        TextView tvCount;
        ImageView ivHotIcon;
        LinearLayout lay2;
        //type=2:
        TextView tvHotTitleOne;
        ImageView ivHotIconOne;
        ImageView ivHotIconTwo;
        ImageView ivHotIconThree;
        TextView tvCountThree;
        LinearLayout lay1;
        public MyViewHolder(View itemView) {
            super(itemView);

            //type=1
            tvHotTitle= (TextView) itemView.findViewById(R.id.tv_hot_title);
            tvCount= (TextView) itemView.findViewById(R.id.tv_count);
            ivHotIcon= (ImageView) itemView.findViewById(R.id.iv_hot_icon);
            lay2= (LinearLayout) itemView.findViewById(R.id.lay_2);

            //type=2
            tvHotTitleOne= (TextView) itemView.findViewById(R.id.tv_hot_title_one);
            tvCountThree= (TextView) itemView.findViewById(R.id.tv_count_three);
            ivHotIconOne= (ImageView) itemView.findViewById(R.id.iv_hot_icon_one);
            ivHotIconTwo= (ImageView) itemView.findViewById(R.id.iv_hot_icon_two);
            ivHotIconThree= (ImageView) itemView.findViewById(R.id.iv_hot_icon_three);
            lay1= (LinearLayout) itemView.findViewById(R.id.lay_1);

        }
    }
}
