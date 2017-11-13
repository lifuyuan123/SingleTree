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
 * Created by XiaoHaoWit on 2017/8/21.
 */

public class CommunityNewListAdapter extends RecyclerView.Adapter<CommunityNewListAdapter.CommunityNewHolder> {

    private List<HotInformationBean> data=new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

    public CommunityNewListAdapter(List<HotInformationBean> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        this.inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public CommunityNewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = inflater.inflate(R.layout.froum_lay_g,parent, false);
        return new CommunityNewHolder(mView);
    }

    @Override
    public void onBindViewHolder(CommunityNewHolder holder, int position) {
        final HotInformationBean bean=data.get(position);
        holder.froumLayGContent.setText(bean.getTitle());
        holder.froumLayGSort.setText(bean.getKeyWord());
        holder.froumLayGDate.setText(bean.getTime());
        PicassoUtil.getPicassoObject().load(bean.getUrl())
                .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                .into(holder.froumLayGPic);

        holder.froumLayGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/main/act/NewsDetails").withInt("id",bean.getId()).withString("title",bean.getTitle()).navigation();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class CommunityNewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.froum_lay_g_content)
        TextView froumLayGContent;
        @BindView(R.id.froum_lay_g_sort)
        TextView froumLayGSort;
        @BindView(R.id.froum_lay_g_date)
        TextView froumLayGDate;
        @BindView(R.id.froum_lay_g_pic)
        ImageView froumLayGPic;
        @BindView(R.id.froum_lay_g_layout)
        LinearLayout froumLayGLayout;
        public CommunityNewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
