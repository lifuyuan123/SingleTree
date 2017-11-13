package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.PerShopBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by haohaoliu on 2017/8/14.
 * explain:周边商家的适配器
 */

public class PeripheryShopAdapter extends RecyclerView.Adapter<PeripheryShopAdapter.PeripheryShopHolder> {

    private List<PerShopBean> data;
    private Context mContext;
    private LayoutInflater inflater;
    private PerShoCallBack callBack;

    public PeripheryShopAdapter(List<PerShopBean> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        this.inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public PeripheryShopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_periphery_shop, null);
        return new PeripheryShopHolder(view);
    }

    @Override
    public void onBindViewHolder(PeripheryShopHolder holder, int position) {

        final PerShopBean bean=data.get(position);
        PicassoUtil.getPicassoObject().load(bean.getStoreLogo())
                .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80)).into(holder.itemPeripheryShopIcon);
        holder.itemPeripheryShopName.setText(bean.getStoreName());
        holder.itemPeripheryShopAddress.setText(bean.getStoreAddress());




        holder.itemPeripheryShopPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=callBack){
                    callBack.callPhoneClick(bean.getTelephone());
                }
            }
        });

        holder.item_periphery_shop_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/main/act/ShopDetail").withInt("storeId",bean.getId()).navigation();
            }
        });

        holder.itemPeripheryShopGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=callBack){
                    callBack.gotoStoreClick(bean);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();

    }

    static class PeripheryShopHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_periphery_shop_icon)
        ImageView itemPeripheryShopIcon;
        @BindView(R.id.item_periphery_shop_Name)
        TextView itemPeripheryShopName;
        @BindView(R.id.item_periphery_shop_address)
        TextView itemPeripheryShopAddress;
        @BindView(R.id.item_periphery_shop_goto)
        LinearLayout itemPeripheryShopGoto;
        @BindView(R.id.item_periphery_shop_phone)
        RelativeLayout itemPeripheryShopPhone;
        @BindView(R.id.item_periphery_shop_lay)
        LinearLayout item_periphery_shop_lay;
        public PeripheryShopHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    //点击事件的回调接口
   public interface  PerShoCallBack{
        public void callPhoneClick(String phone);
        public void gotoStoreClick(PerShopBean bean);
    }


    //设置回调监听的方法
    public void setAdapterCallBack(PerShoCallBack callBack){
        this.callBack=callBack;
    }


}
