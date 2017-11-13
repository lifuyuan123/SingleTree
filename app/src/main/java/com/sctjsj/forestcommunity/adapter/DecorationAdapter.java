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


import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.DecorationBean;
import com.sctjsj.forestcommunity.ui.widget.dialog.DecorationDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lifuy on 2017/8/15.
 */

public class DecorationAdapter extends RecyclerView.Adapter<DecorationAdapter.MyViewHolder> {

    private List<DecorationBean> list = new ArrayList<>();
    private Context context;

    public DecorationAdapter(List<DecorationBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.decoration_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DecorationBean bean=list.get(position);
        holder.tvDecorationName.setText(bean.getName());
        PicassoUtil.getPicassoObject().load(bean.getIconUrl())
               .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                .error(R.drawable.icon_user_default)
                .into(holder.ivDecoration);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecorationDialog dialog=new DecorationDialog(context);
                dialog.setTitle(bean.getName());
                dialog.setSdistance("距离 "+calDistance(bean.getLongitude(),bean.getLatitude())+"Km");
                dialog.setSphone("电话 "+bean.getPhoneNumber());
                dialog.setSintroduce(bean.getIntroduce());
                dialog.setOnCallback(new DecorationDialog.OnCallback() {
                    @Override
                    public void onClick() {
                        //Toast.makeText(context, "跳转", Toast.LENGTH_SHORT).show();
                        if(callBack!=null){
                            callBack.dialogGoToCallBack(bean);
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDecoration;
        TextView tvDecorationName;
        LinearLayout item;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivDecoration= (ImageView) itemView.findViewById(R.id.iv_decoration);
            tvDecorationName= (TextView) itemView.findViewById(R.id.tv_decoration_name);
            item= (LinearLayout) itemView.findViewById(R.id.lin_decoration_item);
        }
    }


    private String calDistance(double longt,double lat){

        LatLng latLng=new LatLng(Double.valueOf((String) SPFUtil.get(Tag.TAG_LATITUDE,"0")),Double.valueOf((String)SPFUtil.get(Tag.TAG_LONGITUDE,"0")));
        float distance = AMapUtils.calculateLineDistance(latLng ,new LatLng(lat,longt))/1000;
        String str=String.valueOf(distance);
        str=str.substring(0,str.indexOf(".")+2);
        return str;
    }

    public interface DecorationCallBack{
        public void dialogGoToCallBack(DecorationBean bean);
    }
    private  DecorationCallBack callBack;
    public void setDecorCallBack(DecorationCallBack callBack){
        this.callBack=callBack;

    }
}
