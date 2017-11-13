package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
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
import com.sctjsj.forestcommunity.javabean.CommentBean;
import com.sctjsj.forestcommunity.javabean.FroumPostBean;
import com.sctjsj.forestcommunity.javabean.UserBean;
import com.sctjsj.forestcommunity.util.TextColorUtil;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XiaoHaoWit on 2017/9/1.
 * 社区论坛列表的适配器
 */

public class CommFroumLsitAdapters extends RecyclerView.Adapter<CommFroumLsitAdapters.CommFroumLsitHolder> {

    private List<FroumPostBean> data;
    private Context mContext;
    private LayoutInflater inflater;

    public CommFroumLsitAdapters(Context mContext, List<FroumPostBean> data) {
        this.mContext = mContext;
        this.data = data;
        this.inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public CommFroumLsitHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = inflater.inflate(R.layout.comm_froum_list_a, null);

        return new CommFroumLsitHolder(mView);
    }

    @Override
    public void onBindViewHolder(CommFroumLsitHolder holder, int position) {
            final FroumPostBean bean=data.get(position);
             UserBean postBean = bean.getBean();
        PicassoUtil.getPicassoObject().load(postBean.getUserIcon())
                .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                .into(holder.commFroumListAIcon);
        holder.commFroumListAUserName.setText(postBean.getUserName());
        holder.commFroumListAUserAddress.setText(bean.getAddress()+"");
        String time=bean.getInsertTime().trim();
        String[] split = time.split(" ");
        holder.commFroumListADate.setText(split[0]+"");

        SpannableStringBuilder builder = TextColorUtil.setTVColor(bean.getTags(), bean.getContent(), 2, mContext.getResources().getColor(R.color.color_froum_Tag));
        holder.commFroumListAFroumContent.setText(builder);



        List<String> photo=bean.getImgList();
        if(null!=photo&&photo.size()>0){
            holder.commFroumListAImgLayout.setVisibility(View.VISIBLE);
            if(photo.size()>3){
                //显示三张照片
            }
            if(photo.size()>=3){
                loadImg(photo.get(0),holder.commFroumListAImgA);
                loadImg(photo.get(1),holder.commFroumListAImgB);
                loadImg(photo.get(2),holder.commFroumListAImgC);
            }
            if(photo.size()==2){
                loadImg(photo.get(0),holder.commFroumListAImgA);
                loadImg(photo.get(1),holder.commFroumListAImgB);
                holder.commFroumListAImgC.setVisibility(View.GONE);
            }
            if(photo.size()==1){
                loadImg(photo.get(0),holder.commFroumListAImgA);
                holder.commFroumListAImgB.setVisibility(View.GONE);
                holder.commFroumListAImgC.setVisibility(View.GONE);
            }
        }else {
            holder.commFroumListAImgLayout.setVisibility(View.GONE);
        }

        List<CommentBean> commList = bean.getCommList();
        if(null!=commList&&commList.size()>0){
            holder.commFroumListACommentLayout.setVisibility(View.VISIBLE);
            CommentBean comBean=commList.get(0);
            holder.commFroumListACommentName.setText(comBean.getBean().getUserName()+":");
            holder.commFroumListACommentCont.setText(comBean.getEvalContent());
        }else {
            holder.commFroumListACommentLayout.setVisibility(View.GONE);
        }


        if(position==data.size()-1){
            holder.commFroumListASpa.setVisibility(View.GONE);

        }else {
            holder.commFroumListASpa.setVisibility(View.VISIBLE);
        }


        holder.froum_list_a_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserAuthUtil.isUserLogin()){
                    ARouter.getInstance().build("/main/act/CommunityForumActivity").withInt("froumId",bean.getId()).navigation();
                }else {
                    Toast.makeText(mContext, "请登录后操作！", Toast.LENGTH_SHORT).show();
                    ARouter.getInstance().build("/main/act/login").navigation();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class CommFroumLsitHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comm_froum_list_a_icon)
        CircleImageView commFroumListAIcon;
        @BindView(R.id.comm_froum_list_a_userName)
        TextView commFroumListAUserName;
        @BindView(R.id.comm_froum_list_a_userAddress)
        TextView commFroumListAUserAddress;
        @BindView(R.id.comm_froum_list_a_date)
        TextView commFroumListADate;
      /*  @BindView(R.id.comm_froum_list_a_froumSort)
        TextView commFroumListAFroumSort;*/
        @BindView(R.id.comm_froum_list_a_froumContent)
        TextView commFroumListAFroumContent;
        @BindView(R.id.comm_froum_list_a_ImgA)
        ImageView commFroumListAImgA;
        @BindView(R.id.comm_froum_list_a_ImgB)
        ImageView commFroumListAImgB;
        @BindView(R.id.comm_froum_list_a_ImgC)
        ImageView commFroumListAImgC;
        @BindView(R.id.comm_froum_list_a_ImgLayout)
        LinearLayout commFroumListAImgLayout;
        @BindView(R.id.comm_froum_list_a_commentName)
        TextView commFroumListACommentName;
        @BindView(R.id.comm_froum_list_a_commentCont)
        TextView commFroumListACommentCont;
        @BindView(R.id.comm_froum_list_a_commentLayout)
        LinearLayout commFroumListACommentLayout;
        @BindView(R.id.comm_froum_list_a_spa)
        TextView commFroumListASpa;
        @BindView(R.id.froum_list_a_item)
        LinearLayout froum_list_a_item;

        public CommFroumLsitHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private void loadImg(String url,ImageView img){
        PicassoUtil.getPicassoObject().load(url)
                .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                .into(img);
    }

 }
