package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;

import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.DiscountsBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mayikang on 17/8/11.
 */

public class ECardMultiAdapter extends DelegateAdapter.Adapter<ECardMultiAdapter.CardHolder> {
// 使用DelegateAdapter首先就是要自定义一个它的内部类Adapter，让LayoutHelper和需要绑定的数据传进去
    // 此处的Adapter和普通RecyclerView定义的Adapter只相差了一个onCreateLayoutHelper()方法，其他的都是一样的做法.

    private ArrayList<HashMap<String, Object>> listItem;
    private Context context;
    private LayoutInflater inflater;
    private LayoutHelper layoutHelper;
    private RecyclerView.LayoutParams layoutParams;
    private int count = 0;
    private int type = 0;//本 item 的类型

    //数据类型
    private static final int TYPE_1=1;
    private static final int TYPE_2=2;


    public ECardMultiAdapter(Context context, LayoutHelper layoutHelper, int count, @NonNull RecyclerView.LayoutParams layoutParams, ArrayList<HashMap<String, Object>> listItem, int type) {
        this.context = context;
        this.layoutHelper = layoutHelper;
        this.count = count;
        this.layoutParams = layoutParams;
        this.listItem = listItem;
        this.type = type;

        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
    }

    //构造函数(传入每个的数据列表 & 展示的Item数量)
    public ECardMultiAdapter(Context context, LayoutHelper layoutHelper, int count, ArrayList<HashMap<String, Object>> listItem, int type) {
        //宽度占满，高度随意
        this(context, layoutHelper, count, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT), listItem, type);
    }


    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return layoutHelper;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        switch (viewType){
            case 1:
                view=inflater.inflate(R.layout.e_card_lay_1,parent,false);
                break;
            case 2:
                view=inflater.inflate(R.layout.e_card_lay_2,parent,false);
                break;
        }
        return new CardHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        int type=getItemViewType(position);
        switch (type){
            case 1:
                HashMap<String, Object> titleMap = listItem.get(0);
                if(titleMap.containsKey("data")){
                    int number= (int) titleMap.get("data");
                  holder.e_card_1_number.setText("商城劵("+number+")");
                }
                break;

            case 2:
                holder.mTVCondition.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
                HashMap<String, Object> dataMap = listItem.get(position);
                if(dataMap.containsKey("data")){
                    final DiscountsBean bean= (DiscountsBean) dataMap.get("data");
                    holder.e_card_2_price.setText(bean.getShowPrice()+"");
                    holder.mTVCondition.setText(bean.getCondition());
                    holder.e_card_lay_2_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ARouter.getInstance().build("/main/act/ShopDetail").withInt("storeId",bean.getShop().getId()).navigation();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listItem==null?0:listItem.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (type){
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                return -1;
        }
    }

    class CardHolder extends RecyclerView.ViewHolder{
        //type1
        TextView e_card_1_number;
        //type2
        TextView mTVCondition;
        TextView e_card_2_price;
        RelativeLayout e_card_lay_2_layout;

        public CardHolder(View root,int type) {
            super(root);
            switch (type){
                case 2:
                    mTVCondition= (TextView) root.findViewById(R.id.tv_condition);
                    e_card_2_price= (TextView) root.findViewById(R.id.e_card_2_price);
                    e_card_lay_2_layout= (RelativeLayout) root.findViewById(R.id.e_card_lay_2_layout);
                    break;
                case 1:
                    e_card_1_number= (TextView) root.findViewById(R.id.e_card_1_number);
                    break;
            }
        }
    }
}
