package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.CommentBean;
import com.sctjsj.forestcommunity.javabean.FroumPostBean;
import com.sctjsj.forestcommunity.javabean.UserBean;
import com.sctjsj.forestcommunity.util.TextColorUtil;
import com.sctjsj.forestcommunity.util.TimeIntervalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by haohaoliu on 2017/8/10.
 * explain:社区论坛的适配器
 */

public class CommunityFroumAdapter extends DelegateAdapter.Adapter<CommunityFroumAdapter.CommunityFroumHolder> {
    public static final int TYPE_a = 1;
    public static final int TYPE_b = 2;
    public static final int TYPE_c = 3;
    public static final int TYPE_d = 4;

    private ArrayList<HashMap<String,Object>> data;
    private Context mContext;
    private LayoutHelper layoutHelper;
    private LayoutInflater inflater;
    private RecyclerView.LayoutParams params;
    private int type=0;

    public CommunityFroumAdapter(ArrayList<HashMap<String, Object>> data, LayoutHelper layoutHelper, Context mContext, RecyclerView.LayoutParams params, int type) {
        this.data = data;
        this.layoutHelper = layoutHelper;
        this.mContext = mContext;
        this.params = params;
        this.type = type;
        this.inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CommunityFroumAdapter(ArrayList<HashMap<String, Object>> data, LayoutHelper layoutHelper, Context mContext, int type){
        this(data,layoutHelper,mContext, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT),type);
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
            default:
                return -1;
        }

    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return layoutHelper;
    }

    @Override
    public CommunityFroumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
       switch (viewType){
           case TYPE_a:
               view=inflater.inflate(R.layout.community_froum_a,parent,false);
               break;
           case TYPE_b:
               view=inflater.inflate(R.layout.community_froum_b,parent,false);
               break;
           case TYPE_c:
               view=inflater.inflate(R.layout.community_froum_c,parent,false);
               break;
           case TYPE_d:
               view=inflater.inflate(R.layout.community_froum_d,parent,false);
               break;
       }

        return new CommunityFroumHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(CommunityFroumHolder holder, int position) {

        int viewType=getItemViewType(position);
        switch (viewType) {
            case TYPE_a:
                if (data.size() > 0) {
                    HashMap<String, Object> dataMap = data.get(0);
                    FroumPostBean bean = (FroumPostBean) dataMap.get("data");
                    if (null != bean) {
                        UserBean user=bean.getBean();
                        PicassoUtil.getPicassoObject().load(user.getUserIcon())
                                .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                                .into(holder.community_froum_a_icon);
                        holder.community_froum_a_userName.setText(user.getUserName());
                        holder.community_froum_a_userAddress.setText(bean.getAddress());
                        String time=bean.getInsertTime().trim();
                        String[] split = time.split(" ");
                        holder.community_froum_a_date.setText(split[0]+"");
                        SpannableStringBuilder builder = TextColorUtil.setTVColor(bean.getTags(), bean.getContent(), 2, mContext.getResources().getColor(R.color.color_froum_Tag));
                        holder.community_froum_d_froumContent.setText(builder);

                        List<String> photo=bean.getImgList();
                        if(null!=photo){
                            holder.comm_forum_a_imgLayout.setVisibility(View.VISIBLE);
                            if(photo.size()>3){
                                //显示三张照片
                            }
                            if(photo.size()>=3){
                                loadImg(photo.get(0),holder.community_froum_a_ImgA);
                                loadImg(photo.get(1),holder.community_froum_a_ImgB);
                                loadImg(photo.get(2),holder.community_froum_a_ImgC);
                            }
                            if(photo.size()==2){
                                loadImg(photo.get(0),holder.community_froum_a_ImgA);
                                loadImg(photo.get(1),holder.community_froum_a_ImgB);
                                holder.community_froum_a_ImgC.setVisibility(View.GONE);
                            }
                            if(photo.size()==1){
                                loadImg(photo.get(0),holder.community_froum_a_ImgA);
                                holder.community_froum_a_ImgB.setVisibility(View.GONE);
                                holder.community_froum_a_ImgC.setVisibility(View.GONE);
                            }
                        }else {
                            holder.comm_forum_a_imgLayout.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            case TYPE_b:

               if(data.size()>0){
                   HashMap<String, Object> dataMap = data.get(0);
                   if(dataMap.containsKey("comment")){
                       Log.e("adapter","=======");
                       int number= (int) dataMap.get("comment");
                       if(number==0){
                           holder.community_froum_b_commNumber.setText(0+"");
                       }else {
                           holder.community_froum_b_commNumber.setText(number+"");
                       }
                   }

                   if(dataMap.containsKey("code")){
                       String code= (String) dataMap.get("code");
                       switch (code){
                           case "1"://点赞
                               holder.community_froum_b_loveImg.setImageResource(R.drawable.ic_love_sure);
                               holder.community_froum_b_loveTxt.setText("已赞");
                               break;
                           case "2"://未点赞
                               holder.community_froum_b_loveTxt.setText("赞");
                               holder.community_froum_b_loveImg.setImageResource(R.mipmap.ic_froum_love);
                               break;
                       }
                   }

               }


                holder.community_froum_b_commitLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=callback){
                            callback.commentLayoutClick();
                        }
                    }
                });

                holder.community_froum_b_loveLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=callback){
                            callback.loveCallback();
                        }
                    }
                });


                break;
            case TYPE_c:
               if(data.size()>0){
                   HashMap<String, Object> Map = data.get(0);
                   if(Map.containsKey("data")){
                       holder.community_froum_c_nodata.setVisibility(View.GONE);
                       int commNumber= (int) Map.get("data");
                       if(commNumber==0){
                           holder.community_froum_c_commitNumber.setText("暂无评论");

                       }else {
                           holder.community_froum_c_commitNumber.setText("全部评论("+commNumber+")");
                       }
                   }else {
                       //没有评论
                       holder.community_froum_c_nodata.setVisibility(View.VISIBLE);
                   }
               }

                break;
            case TYPE_d:
               if(data.size()>0){
                   HashMap<String, Object> commntMap = data.get(position);
                   final CommentBean bean=(CommentBean) commntMap.get("data");
                   if(null!=bean) {
                       loadImg(bean.getBean().getUserIcon(), holder.community_froum_d_userIcon);
                       holder.community_froum_d_userName.setText(bean.getBean().getUserName());
                       String interval = TimeIntervalUtil.getInterval(bean.getInsertTime());
                       if (null != interval) {
                           holder.community_froum_d_date.setText(interval);
                       } else {
                           holder.community_froum_d_date.setText("未知");
                       }

                      if(bean.getType()==2){
                          StringBuffer buffer=new StringBuffer();
                          buffer.append("回复"+bean.getParBean().getUserName()+":");
                          SpannableStringBuilder spannableStringBuilder =
                                  TextColorUtil.setTVColor(buffer.toString().trim(), bean.getEvalContent(), 2, mContext.getResources().getColor(R.color.color_comm_froum_contenttxt));
                          holder.community_froum_d_content.setText(spannableStringBuilder);
                      }else {
                          holder.community_froum_d_content.setText(bean.getEvalContent());
                      }

                       holder.comm_froum_d_layout.setOnLongClickListener(new View.OnLongClickListener() {
                           @Override
                           public boolean onLongClick(View v) {
                               if(null!=callback&&null!=bean){
                                   callback.commUserItemClick(bean);
                               }
                               return true;
                           }
                       });
                   }
               }

                break;
        }

    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    static class CommunityFroumHolder extends RecyclerView.ViewHolder{
        //a的布局控件
        CircleImageView community_froum_a_icon;
        TextView community_froum_a_userName;
        TextView community_froum_a_userAddress;
        TextView community_froum_a_date;
        LinearLayout comm_forum_a_imgLayout;
        //TextView community_froum_a_froumSort;
        TextView community_froum_d_froumContent;
        ImageView community_froum_a_ImgA;
        ImageView community_froum_a_ImgB;
        ImageView community_froum_a_ImgC;
        //b的布局控件
        LinearLayout community_froum_b_loveLayout;
        ImageView community_froum_b_loveImg;
        LinearLayout community_froum_b_commitLayout;
        TextView community_froum_b_commNumber;
        TextView community_froum_b_loveTxt;
        //c的布局控件
        TextView community_froum_c_commitNumber;
        LinearLayout community_froum_c_nodata;
        //d的布局控件
        LinearLayout comm_froum_d_layout;
        CircleImageView community_froum_d_userIcon;
        TextView community_froum_d_userName;
        TextView community_froum_d_date;
        TextView community_froum_d_content;



        public CommunityFroumHolder(View itemView,int viewType) {
            super(itemView);
            switch (viewType){
                case TYPE_a:
                    comm_forum_a_imgLayout= (LinearLayout) itemView.findViewById(R.id.comm_forum_a_imgLayout);
                    community_froum_a_icon= (CircleImageView) itemView.findViewById(R.id.community_froum_a_icon);
                    community_froum_a_userName= (TextView) itemView.findViewById(R.id.community_froum_a_userName);
                    community_froum_a_userAddress= (TextView) itemView.findViewById(R.id.community_froum_a_userAddress);
                    community_froum_a_date= (TextView) itemView.findViewById(R.id.community_froum_a_date);
                    //community_froum_a_froumSort= (TextView) itemView.findViewById(R.id.community_froum_a_froumSort);
                    community_froum_d_froumContent= (TextView) itemView.findViewById(R.id.community_froum_d_froumContent);
                    community_froum_a_ImgA= (ImageView) itemView.findViewById(R.id.community_froum_a_ImgA);
                    community_froum_a_ImgB= (ImageView) itemView.findViewById(R.id.community_froum_a_ImgB);
                    community_froum_a_ImgC= (ImageView) itemView.findViewById(R.id.community_froum_a_ImgC);
                    break;
                case TYPE_b:
                    community_froum_b_loveLayout= (LinearLayout) itemView.findViewById(R.id.community_froum_b_loveLayout);
                    community_froum_b_loveImg= (ImageView) itemView.findViewById(R.id.community_froum_b_loveImg);
                    community_froum_b_commitLayout= (LinearLayout) itemView.findViewById(R.id.community_froum_b_commitLayout);
                    community_froum_b_commNumber= (TextView) itemView.findViewById(R.id.community_froum_b_commNumber);
                    community_froum_b_loveTxt= (TextView) itemView.findViewById(R.id.community_froum_b_loveTxt);
                    break;
                case TYPE_c:
                    community_froum_c_commitNumber= (TextView) itemView.findViewById(R.id.community_froum_c_commitNumber);
                    community_froum_c_nodata= (LinearLayout) itemView.findViewById(R.id.community_froum_c_nodata);
                    break;
                case TYPE_d:
                    comm_froum_d_layout= (LinearLayout) itemView.findViewById(R.id.comm_froum_d_layout);
                    community_froum_d_userIcon= (CircleImageView) itemView.findViewById(R.id.community_froum_d_userIcon);
                    community_froum_d_userName= (TextView) itemView.findViewById(R.id.community_froum_d_userName);
                    community_froum_d_date= (TextView) itemView.findViewById(R.id.community_froum_d_date);
                    community_froum_d_content= (TextView) itemView.findViewById(R.id.community_froum_d_content);
                    break;
            }
        }
    }

    private void loadImg(String url,ImageView img){
        PicassoUtil.getPicassoObject().load(url)
                .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                .into(img);
    }

    //回调接口
    public interface  commFroumCallback{
        public void  commentLayoutClick();
        public void  commUserItemClick(CommentBean bean);
        public void  loveCallback();

    }

    private commFroumCallback callback;

    public void setCallBack(commFroumCallback callback){
        this.callback=callback;
    }


}
