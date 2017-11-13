package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.FroumPostBean;
import com.sctjsj.forestcommunity.javabean.HotInformationBean;
import com.sctjsj.forestcommunity.util.TextColorUtil;
import com.sctjsj.forestcommunity.util.UserAuthUtil;
import com.soundcloud.android.crop.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by haohaoliu on 2017/8/10.
 * explain:论坛的复杂布局的适配器
 */

public class FroumLayAdapter extends DelegateAdapter.Adapter<FroumLayAdapter.FroumLayHolder> {

    /**
     * 论坛的7种布局，对应的
     */
    public static final int TYPE_a = 1;
    public static final int TYPE_b = 2;
    public static final int TYPE_c = 3;
    public static final int TYPE_d = 4;
    public static final int TYPE_e = 5;
    public static final int TYPE_f = 6;
    public static final int TYPE_g = 7;

    private ArrayList<HashMap<String, Object>> data;
    private Context mContext;
    private LayoutInflater inflater;
    private RecyclerView.LayoutParams layoutParams;
    private LayoutHelper layoutHelper;
    private int type = 0;//item的类型


    private FroumSortAdapter adapter;


    public FroumLayAdapter(ArrayList<HashMap<String, Object>> data, Context mContext, RecyclerView.LayoutParams layoutParams, LayoutHelper layoutHelper, int type) {
        this.data = data;
        this.mContext = mContext;
        this.layoutParams = layoutParams;
        this.layoutHelper = layoutHelper;
        this.type = type;
        this.inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public FroumLayAdapter(ArrayList<HashMap<String, Object>> data, Context mContext, LayoutHelper layoutHelper, int type) {

        this(data, mContext,
                new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT), layoutHelper, type);
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
            case 7:
                return TYPE_g;
            default:
                return -1;
        }
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return layoutHelper;
    }



    @Override
    public FroumLayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_a:
                view = inflater.inflate(R.layout.froum_lay_a, parent, false);
                break;
            case TYPE_b:
                view = inflater.inflate(R.layout.froum_lay_b, parent, false);
                break;
            case TYPE_c:
                view = inflater.inflate(R.layout.froum_lay_c, parent, false);
                break;
            case TYPE_d:
                view = inflater.inflate(R.layout.froum_lay_d, parent, false);
                break;
            case TYPE_e:
                view = inflater.inflate(R.layout.froum_lay_e, parent, false);
                break;
            case TYPE_f:
                view = inflater.inflate(R.layout.froum_lay_f, parent, false);
                break;
            case TYPE_g:
                view = inflater.inflate(R.layout.froum_lay_g, parent, false);
                break;
        }
        return new FroumLayHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(FroumLayHolder holder, int position) {
        int type=getItemViewType(position);
        switch (type){
            case  TYPE_a:
                break;
            case TYPE_b:
                holder.froum_lay_b_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/CommForumListActivity").navigation();
                    }
                });

                break;
            case TYPE_c:
                if(adapter==null){
                    adapter=new FroumSortAdapter(data,mContext);
                    holder.froum_lay_c_rv.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
                    holder.froum_lay_c_rv.setAdapter(adapter);
                }else {
                    adapter.notifyDataSetChanged();
                }
                break;
            case TYPE_d:
                if(data.size()>0){
                    HashMap<String, Object> dataMap = data.get(position);
                    final FroumPostBean bean= (FroumPostBean) dataMap.get("data");
                    PicassoUtil.getPicassoObject().load(bean.getBean().getUserIcon())
                            .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                            .into(holder.froum_lay_d_userIcon);
                    String time=bean.getInsertTime().trim();
                    String[] split = time.split(" ");
                    holder.froum_lay_b_date.setText(split[0]+"");
                    holder.froum_lay_b_userName.setText(bean.getBean().getUserName());
                    /*holder.froum_lay_b_sort.setText(bean.getTags());
                    holder.froum_lay_b_content.setText("                  "+bean.getContent());*/
                    SpannableStringBuilder builder = TextColorUtil.setTVColor(bean.getTags(), bean.getContent(), 2, mContext.getResources().getColor(R.color.color_froum_Tag));
                    holder.froum_lay_b_content.setText(builder);


                    holder.froum_lay_d_layout.setOnClickListener(new View.OnClickListener() {
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




                break;
            case TYPE_e:
                holder.froum_lay_e_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(UserAuthUtil.isUserLogin()){
                           ARouter.getInstance().build("/main/act/PostToFroum").navigation();
                       }else {
                           Toast.makeText(mContext, "请登录后操作！", Toast.LENGTH_SHORT).show();
                           ARouter.getInstance().build("/main/act/login").navigation();
                       }
                    }
                });
                break;
            case TYPE_f:
                holder.froum_lay_f_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/CommunityNewsList").navigation();
                    }
                });
                break;
            case TYPE_g:
                final HotInformationBean bean= (HotInformationBean) data.get(position).get("bean");
                holder.froum_lay_g_content.setText(bean.getTitle());
                holder.froum_lay_g_sort.setText(bean.getKeyWord());
                holder.froum_lay_g_date.setText(bean.getTime());
                PicassoUtil.getPicassoObject().load(bean.getUrl())
                        .resize(DpUtils.dpToPx(mContext,80),DpUtils.dpToPx(mContext,80))
                        .into(holder.froum_lay_g_pic);
                holder.froum_lay_g_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/NewsDetails").withInt("id",bean.getId()).withString("title",bean.getTitle()).navigation();
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class FroumLayHolder extends RecyclerView.ViewHolder {
        //b布局
        LinearLayout froum_lay_b_more;
        //c布局
        RecyclerView froum_lay_c_rv;
        //d布局
        LinearLayout froum_lay_d_layout;
        CircleImageView froum_lay_d_userIcon;
        TextView froum_lay_b_userName;
        TextView froum_lay_b_date;
        //TextView froum_lay_b_sort;
        TextView froum_lay_b_content;
        //e布局
        LinearLayout froum_lay_e_layout;
        //f布局
        LinearLayout froum_lay_f_more;
        //g布局
        LinearLayout froum_lay_g_layout;
        TextView froum_lay_g_content;
        TextView froum_lay_g_sort;
        TextView froum_lay_g_date;
        ImageView froum_lay_g_pic;



        public FroumLayHolder(View itemView, int viewType) {
            super(itemView);
            switch (viewType){
                case TYPE_a:
                    break;
                case TYPE_b:
                    froum_lay_b_more= (LinearLayout) itemView.findViewById(R.id.froum_lay_b_more);
                    break;
                case TYPE_c:
                    froum_lay_c_rv= (RecyclerView) itemView.findViewById(R.id.froum_lay_c_rv);
                    break;
                case TYPE_d:
                    froum_lay_d_layout= (LinearLayout) itemView.findViewById(R.id.froum_lay_d_layout);
                    froum_lay_d_userIcon= (CircleImageView) itemView.findViewById(R.id.froum_lay_d_userIcon);
                    froum_lay_b_userName= (TextView) itemView.findViewById(R.id.froum_lay_b_userName);
                    froum_lay_b_date= (TextView) itemView.findViewById(R.id.froum_lay_b_date);
                    //froum_lay_b_sort= (TextView) itemView.findViewById(R.id.froum_lay_b_sort);
                    froum_lay_b_content= (TextView) itemView.findViewById(R.id.froum_lay_b_content);
                    break;
                case TYPE_e:
                    froum_lay_e_layout= (LinearLayout) itemView.findViewById(R.id.froum_lay_e_layout);
                    break;
                case TYPE_f:
                    froum_lay_f_more= (LinearLayout) itemView.findViewById(R.id.froum_lay_f_more);
                    break;
                case TYPE_g:
                    froum_lay_g_layout= (LinearLayout) itemView.findViewById(R.id.froum_lay_g_layout);
                    froum_lay_g_content= (TextView) itemView.findViewById(R.id.froum_lay_g_content);
                    froum_lay_g_sort= (TextView) itemView.findViewById(R.id.froum_lay_g_sort);
                    froum_lay_g_date= (TextView) itemView.findViewById(R.id.froum_lay_g_date);
                    froum_lay_g_pic= (ImageView) itemView.findViewById(R.id.froum_lay_g_pic);
                    break;
            }
        }
    }


}
