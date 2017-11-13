package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.sctjsj.basemodule.base.util.CallUtil;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.DiscountsBean;
import com.sctjsj.forestcommunity.javabean.PerShopBean;
import com.sctjsj.forestcommunity.ui.widget.dialog.OnlineDialog;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by haohaoliu on 2017/8/15.
 * explain:商家详情的代理适配器
 */

public class ShopDetailMultiAdapter extends DelegateAdapter.Adapter<ShopDetailMultiAdapter.ShopDetailHolder> {
    //对应商家详情的六种布局
    public static final int TYPE_a = 1;
    public static final int TYPE_b = 2;
    public static final int TYPE_c = 3;
    public static final int TYPE_d = 4;
    public static final int TYPE_e = 5;
    public static final int TYPE_f = 6;

    private ArrayList<HashMap<String,Object>> data;
    private Context mContext;
    private LayoutInflater inflater;
    private LayoutHelper layoutHelper;
    private RecyclerView.LayoutParams layoutParams;
    private int type;

    public ShopDetailMultiAdapter(ArrayList<HashMap<String, Object>> data, Context mContext, int type, RecyclerView.LayoutParams layoutParams, LayoutHelper layoutHelper) {
        this.data = data;
        this.mContext = mContext;
        this.type = type;
        this.layoutParams = layoutParams;
        this.layoutHelper = layoutHelper;
        this.inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ShopDetailMultiAdapter(ArrayList<HashMap<String, Object>> data, Context mContext, int type, LayoutHelper layoutHelper) {
       this(data,mContext,type, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.WRAP_CONTENT),layoutHelper);
    }


    @Override
    public int getItemViewType(int position) {
        switch (type){
            case 1:
                return TYPE_a;
            case 2:
                return TYPE_b;
            case 3:
                return TYPE_c;
            case 4:
                return TYPE_d;
            case 5:
                return TYPE_e;
            case 6:
                return TYPE_f;
            default:
                return -1;
        }
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return layoutHelper;
    }

    @Override
    public ShopDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView=null;
        switch (viewType){
            case TYPE_a:
                mView=inflater.inflate(R.layout.shop_lay_a,parent,false);
                break;
            case TYPE_b:
                mView=inflater.inflate(R.layout.shop_lay_b,parent,false);
                break;
            case TYPE_c:
                mView=inflater.inflate(R.layout.shop_lay_c,parent,false);
                break;
            case TYPE_d:
                mView=inflater.inflate(R.layout.shop_lay_d,parent,false);
                break;
            case TYPE_e:
                mView=inflater.inflate(R.layout.shop_lay_e,parent,false);
                break;
            case TYPE_f:
                mView=inflater.inflate(R.layout.shop_lay_f,parent,false);
                break;

        }
        return new ShopDetailHolder(mView,viewType);
    }

    @Override
    public void onBindViewHolder(ShopDetailHolder holder, int position) {

        int itemViewType = getItemViewType(position);
        switch (itemViewType){
            case TYPE_a:
                HashMap<String, Object> beanMap = data.get(position);
                final PerShopBean bean= (PerShopBean) beanMap.get("data");
                if(null!=bean){
                    PicassoUtil.getPicassoObject().load(bean.getStoreLogo())
                            .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                            .into(holder.shop_lay_a_icon);
                    holder.shop_lay_a_shopName.setText(bean.getStoreName());
                    holder.shop_lay_a_Dist.setText(calDistance(bean.getLongitude(),bean.getLatitude())+"Km");
                    holder.shop_lay_a_address.setText(bean.getAreaName());
                    holder.shop_lay_a_sort.setText(bean.getLable());
                    holder.shop_lay_a_AddressMsg.setText(bean.getStoreAddress());
                    holder.shop_lay_a_PhoneNumber.setText(bean.getTelephone());
                }
                holder.shop_lay_a_goto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //导航去商家
                       if(null!=callBack){
                           callBack.gotoLayoutClick(bean);
                       }
                    }
                });
                holder.shop_lay_a_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //在线咨询
                        final OnlineDialog dialog=  new OnlineDialog(mContext);
                        dialog.setTitle("咨询热线");
                        dialog.setPhone("028-82726574");
                        dialog.setCallBack(new OnlineDialog.OnCallBack() {
                            @Override
                            public void onClick() {
                                CallUtil.makeCall(mContext, bean.getTelephone());
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                break;
            case TYPE_b:

                break;
            case TYPE_c:
                HashMap<String,Object> dis=data.get(position);

                if(dis.containsKey("noData")){
                    //没有数据的情况
                    holder.no_data_layout.setVisibility(View.VISIBLE);
                    holder.shop_layc_layout.setVisibility(View.GONE);
                }

                if(dis.containsKey("dis")){
                    //有数据的情况
                    holder.no_data_layout.setVisibility(View.GONE);
                    holder.shop_layc_layout.setVisibility(View.VISIBLE);
                    final DiscountsBean disbean= (DiscountsBean) dis.get("dis");
                    if(null!=disbean){
                        PicassoUtil.getPicassoObject().load(disbean.getStoreImg())
                                .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                                .into(holder.shop_lay_c_CardImg);
                        holder.shop_lay_c_content.setText(disbean.getContent());

                        holder.shop_lay_c_price.setText("￥"+disbean.getPrice()+"");

                    }

                    if(position==data.size()-1){
                        holder.shop_lay_c_line.setVisibility(View.GONE);
                    }else {
                        holder.shop_lay_c_line.setVisibility(View.VISIBLE);
                    }

                    holder.shop_layc_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //跳转到详情页
                            ARouter.getInstance().build("/main/act/CouponMessageActivity").withInt("id",disbean.getId()).navigation();
                        }
                    });
                }



                break;
            case TYPE_d:

                break;
            case TYPE_e:
                HashMap<String,Object> dataMap=data.get(position);
                PerShopBean perbean= (PerShopBean) dataMap.get("data");
                if(null!=perbean){
                    WebView webView=new WebView(mContext);
                    WebSettings setting=webView.getSettings();
                    setting.setJavaScriptEnabled(true);
                    webView.setWebViewClient(new WebViewClient(){
                        @Override
                        public boolean shouldOverrideKeyEvent(WebView webView, KeyEvent keyEvent) {
                            return false;
                        }
                    });
                    webView.loadData(perbean.getDescribe(),"text/html;charset=utf-8",null);
                    holder.shop_lay_e_layout.addView(webView);
                }
                break;
        }


    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    static class ShopDetailHolder extends RecyclerView.ViewHolder{
        //a的布局控件
        ImageView shop_lay_a_icon;
        TextView shop_lay_a_shopName;
        TextView shop_lay_a_Dist;
        TextView shop_lay_a_address;
        TextView shop_lay_a_sort;
        TextView shop_lay_a_AddressMsg;
        RelativeLayout shop_lay_a_goto;
        TextView shop_lay_a_PhoneNumber;
        RelativeLayout shop_lay_a_call;
        //b的布局
        //c的布局
        LinearLayout shop_layc_layout;
        ImageView shop_lay_c_CardImg;
        TextView shop_lay_c_content;
        TextView shop_lay_c_sellNumber;
        TextView shop_lay_c_price;
        LinearLayout no_data_layout;
        TextView shop_lay_c_line;


        //e的布局控件
        WebView shop_lay_e_web;
        LinearLayout shop_lay_e_layout;

        //f的布局
        Button shop_lay_f_btn;



        public ShopDetailHolder(View itemView,int viewType) {
            super(itemView);
            switch (viewType){
                case TYPE_a:
                    shop_lay_a_icon= (ImageView) itemView.findViewById(R.id.shop_lay_a_icon);
                    shop_lay_a_shopName= (TextView) itemView.findViewById(R.id.shop_lay_a_shopName);
                    shop_lay_a_Dist= (TextView) itemView.findViewById(R.id.shop_lay_a_Dist);
                    shop_lay_a_address= (TextView) itemView.findViewById(R.id.shop_lay_a_address);
                    shop_lay_a_sort= (TextView) itemView.findViewById(R.id.shop_lay_a_sort);
                    shop_lay_a_AddressMsg= (TextView) itemView.findViewById(R.id.shop_lay_a_AddressMsg);
                    shop_lay_a_PhoneNumber= (TextView) itemView.findViewById(R.id.shop_lay_a_PhoneNumber);
                    shop_lay_a_goto= (RelativeLayout) itemView.findViewById(R.id.shop_lay_a_goto);
                    shop_lay_a_call= (RelativeLayout) itemView.findViewById(R.id.shop_lay_a_call);
                    break;
                case TYPE_b:
                    break;
                case TYPE_c:
                    shop_layc_layout= (LinearLayout) itemView.findViewById(R.id.shop_layc_layout);
                    shop_lay_c_CardImg= (ImageView) itemView.findViewById(R.id.shop_lay_c_CardImg);
                    shop_lay_c_content= (TextView) itemView.findViewById(R.id.shop_lay_c_content);
                    shop_lay_c_sellNumber= (TextView) itemView.findViewById(R.id.shop_lay_c_sellNumber);
                    shop_lay_c_price= (TextView) itemView.findViewById(R.id.shop_lay_c_price);
                    no_data_layout= (LinearLayout) itemView.findViewById(R.id.no_data_layout);
                    shop_lay_c_line= (TextView) itemView.findViewById(R.id.shop_lay_c_line);
                    break;
                case TYPE_d:
                    break;
                case TYPE_e:
                    shop_lay_e_layout= (LinearLayout) itemView.findViewById(R.id.shop_lay_e_layout);

                    break;
                case TYPE_f:
                    shop_lay_f_btn= (Button) itemView.findViewById(R.id.shop_lay_f_btn);
                    break;
            }

        }
    }



    private String calDistance(double longt,double lat){

        LatLng latLng=new LatLng(Double.valueOf((String) SPFUtil.get(Tag.TAG_LATITUDE,"0")),Double.valueOf((String)SPFUtil.get(Tag.TAG_LONGITUDE,"0")));
        float distance = AMapUtils.calculateLineDistance(latLng ,new LatLng(lat,longt))/1000;
        String str=String.valueOf(distance);
        str=str.substring(0,str.indexOf(".")+2);
        return str;
    }


    public interface ShopDetailCallBack{
        public void gotoLayoutClick(PerShopBean bean);
    }

    private ShopDetailCallBack callBack;

    public void setCallBack(ShopDetailCallBack callBack){
        this.callBack=callBack;

    }
}
