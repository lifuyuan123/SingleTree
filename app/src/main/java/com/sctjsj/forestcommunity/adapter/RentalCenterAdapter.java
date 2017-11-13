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
import com.sctjsj.forestcommunity.javabean.RentalCenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by XiaoHaoWit on 2017/8/18.
 * 租售中心的适配器
 */

public class RentalCenterAdapter extends RecyclerView.Adapter<RentalCenterAdapter.RentalCenteHolder> {

    private List<RentalCenter> data=new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

    public RentalCenterAdapter(List<RentalCenter> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        this.inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RentalCenteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = inflater.inflate(R.layout.item_rental_center,parent,false);
        return new RentalCenteHolder(mView);
    }

    @Override
    public void onBindViewHolder(RentalCenteHolder holder, final int position) {
        final RentalCenter bean=data.get(position);
        int type=bean.getType();
        holder.itemRentalCenterTitle.setText(bean.getTitle());
        holder.itemRentalCenterAddress.setText(bean.getArea());
        holder.itemRentalCenterMoney.setText(bean.getMonthly()+"元/月");
        holder.itemRentalCenterTxt1.setText(bean.getHouseType());
        holder.itemRentalCenterTxt2.setText(bean.getAcreage()+"");
        holder.itemRentalCenterTxt3.setText(bean.getOrientation());
        PicassoUtil.getPicassoObject().load(bean.getUrl())
                .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                .into(holder.itemRentalCenterIcon);
        holder.itemRentalCenterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/main/act/RentalDetails").withObject("bean",bean).navigation();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class RentalCenteHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_rental_center_icon)
        ImageView itemRentalCenterIcon;
        @BindView(R.id.item_rental_center_title)
        TextView itemRentalCenterTitle;
        @BindView(R.id.item_rental_center_txt1)
        TextView itemRentalCenterTxt1;
        @BindView(R.id.item_rental_center_txt2)
        TextView itemRentalCenterTxt2;
        @BindView(R.id.item_rental_center_txt3)
        TextView itemRentalCenterTxt3;
        @BindView(R.id.item_rental_center_money)
        TextView itemRentalCenterMoney;
        @BindView(R.id.item_rental_center_address)
        TextView itemRentalCenterAddress;
        @BindView(R.id.item_rental_center_layout)
        LinearLayout itemRentalCenterLayout;

        public RentalCenteHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
