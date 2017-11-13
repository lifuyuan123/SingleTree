package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.CardBean;
import com.sctjsj.forestcommunity.javabean.DiscountsBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lifuy on 2017/8/15.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private List<DiscountsBean> list = new ArrayList<>();
    private Context context;
    private View footView;
    private int NORMORE = 1;
    private int FOOT = 2;

    public View getFootView() {
        return footView;
    }

    public void setFootView(View footView) {
        this.footView = footView;
        notifyItemInserted(getItemCount() - 1);
    }

    public CardAdapter(List<DiscountsBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < list.size() - 1) {
            return NORMORE;
        } else if (position == getItemCount() - 1) {
            return FOOT;
        }
        return NORMORE;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (footView != null && viewType == FOOT) {
            return new MyViewHolder(footView);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.card_pakage_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //footview布局
        if (getItemViewType(position) == FOOT) {
            if (footView != null) {
                footView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/user/CardInvalid").navigation();
                    }
                });
            } else {
                //没有footview的时候加载的是正常的布局
               DiscountsBean bean=list.get(position);
                switch (bean.getType()) {
                    //有效券
                    case 1:
                        holder.tvCardMoney.setText(bean.getShowPrice()+"");
                        holder.tvCardUllcut.setText(bean.getCondition());
                        holder.tvCardCondition.setText(bean.getContent());
                        holder.tvCardDeductible.setText(bean.getDisName());
                        String[] split1 = bean.getBeginTime().split(" ");
                        String[] split2 = bean.getEndTime().split(" ");
                        holder.tvCardDate.setText(split1[0]+"到"+split2[0]);
                        break;
                    //无效券
                    case 2:
                        holder.tvCardMoney.setTextColor(context.getResources().getColor(R.color.app_txt_fade_gray));
                        holder.tvCardUnit.setTextColor(context.getResources().getColor(R.color.app_txt_fade_gray));
                        holder.tvCardDate.setTextColor(context.getResources().getColor(R.color.app_txt_fade_gray));
                        holder.tvCardMoney.setText(bean.getShowPrice()+"");
                        holder.tvCardUllcut.setText(bean.getCondition());
                        holder.tvCardCondition.setText(bean.getContent());
                        holder.tvCardDeductible.setText(bean.getDisName());
                        String[] splitN1 = bean.getBeginTime().split(" ");
                        String[] splitN2 = bean.getEndTime().split(" ");
                        holder.tvCardDate.setText(splitN1[0]+"到"+splitN2[0]);
                        break;
                }
            }
            return;
            //正常的布局
        } else if (getItemViewType(position) == NORMORE) {
            DiscountsBean bean = list.get(position);
            switch (bean.getType()) {
                //有效券
                case 1:
                    holder.tvCardMoney.setText(bean.getShowPrice()+"");
                    holder.tvCardUllcut.setText(bean.getCondition());
                    holder.tvCardCondition.setText(bean.getContent());
                    holder.tvCardDeductible.setText(bean.getDisName());
                    String[] split1 = bean.getBeginTime().split(" ");
                    String[] split2 = bean.getEndTime().split(" ");
                    holder.tvCardDate.setText(split1[0]+"到"+split2[0]);

                    break;
                //无效券
                case 2:

                    holder.tvCardMoney.setTextColor(context.getResources().getColor(R.color.app_txt_fade_gray));
                    holder.tvCardUnit.setTextColor(context.getResources().getColor(R.color.app_txt_fade_gray));
                    holder.tvCardDate.setTextColor(context.getResources().getColor(R.color.app_txt_fade_gray));
                    holder.tvCardMoney.setText(bean.getShowPrice()+"");
                    holder.tvCardUllcut.setText(bean.getCondition());
                    holder.tvCardCondition.setText(bean.getContent());
                    holder.tvCardDeductible.setText(bean.getDisName());
                    String[] splitN1 = bean.getBeginTime().split(" ");
                    String[] splitN2 = bean.getEndTime().split(" ");
                    holder.tvCardDate.setText(splitN1[0]+"到"+splitN2[0]);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (footView != null) {
            return list.size() + 1;
        }
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_card_money)
        TextView tvCardMoney;
        @BindView(R.id.tv_card_unit)
        TextView tvCardUnit;
        @BindView(R.id.tv_card_ullcut)
        TextView tvCardUllcut;
        @BindView(R.id.tv_card_condition)
        TextView tvCardCondition;
        @BindView(R.id.tv_card_deductible)
        TextView tvCardDeductible;
        @BindView(R.id.tv_card_date)
        TextView tvCardDate;
        @BindView(R.id.lin_card_item)
        LinearLayout linCardItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            if (itemView == footView) {
                return;
            }
            ButterKnife.bind(this, itemView);

        }
    }
}
