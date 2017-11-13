package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.sctjsj.basemodule.base.util.CallUtil;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.MainLooper;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.HotInformationBean;
import com.sctjsj.forestcommunity.ui.widget.dialog.HomeOnlineDialog;
import com.sctjsj.forestcommunity.ui.widget.dialog.OnlineDialog;
import com.sctjsj.forestcommunity.ui.widget.dialog.ShopPhoneDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.sunfusheng.marqueeview.MarqueeView;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;
import com.tmall.ultraviewpager.transformer.UltraVerticalTransformer;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.transformer.DepthPageTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by mayikang on 17/8/10.
 */

public class HomeMultiAdapter extends DelegateAdapter.Adapter<HomeMultiAdapter.HomeHolder> {

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
    private static final int TYPE_3=3;
    private static final int TYPE_4=4;
    private static final int TYPE_5=5;

    public HomeMultiAdapter(Context context, LayoutHelper layoutHelper, int count, @NonNull RecyclerView.LayoutParams layoutParams, ArrayList<HashMap<String, Object>> listItem, int type) {
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
    public HomeMultiAdapter(Context context, LayoutHelper layoutHelper, int count, ArrayList<HashMap<String, Object>> listItem, int type) {
        //宽度占满，高度随意
        this(context, layoutHelper, count, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT), listItem, type);
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return layoutHelper;
    }

    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        switch (viewType){
            case TYPE_1:
                view=inflater.inflate(R.layout.home_lay_1,parent,false);
                break;

            case TYPE_2:
                view=inflater.inflate(R.layout.home_lay_2,parent,false);
                break;

            case TYPE_3:
                view=inflater.inflate(R.layout.home_lay_3,parent,false);
                break;

            case TYPE_4:
                view=inflater.inflate(R.layout.home_lay_4,parent,false);
                break;

            case TYPE_5:
                view=inflater.inflate(R.layout.home_lay_5,parent,false);
                break;
        }


        return new HomeHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(final HomeHolder holder, int position) {
        int type=getItemViewType(position);

        switch (type){
            case 1:

                HashMap<String, Object> map = listItem.get(0);
                if(map.containsKey("banner")){
                 List<String> list= (List<String>) map.get("banner");
                    initBanner(holder.mBanner,list);
                    LogUtil.e("string",list.toString());
                }
                holder.search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //搜索
                        ARouter.getInstance().build("/main/act/Search").navigation();
                    }
                });
                //选择城市
                holder.choice_city.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/ChoiceCity").withString("city",holder.tv_home_city.getText().toString()).navigation();
                    }
                });

                if(map.containsKey("city")){
                    String city= (String) map.get("city");
                    holder.tv_home_city.setText(city);
                }else {
                    holder.tv_home_city.setText((String)SPFUtil.get(Tag.TAG_CITY,"成都"));
                }

                break;

            case 2:
                holder.mLLECard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/user/e_card").navigation();
                    }
                });

                holder.lay_2_perShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //周边商家
                        ARouter.getInstance().build("/main/act/PeripheryShop").navigation();
                    }
                });

                holder.ownerIntegral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //业主积分
                        ARouter.getInstance().build("/main/act/user/OwnerIntegral").navigation();
                    }
                });
                holder.decoration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //装修建材
                        ARouter.getInstance().build("/main/act/Decoration").navigation();
                    }
                });
                holder.more_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //更多内容
                        ARouter.getInstance().build("/main/act/MoreContent").navigation();
                    }
                });
                holder.pay_life.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //生活缴费
                        /*ARouter.getInstance().build("/main/act/PayLife").navigation();*/
                        if(null!=callBack){
                            callBack.PayLifeClick();
                        }
                    }
                });
                holder.online.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HomeOnlineDialog dialog=new HomeOnlineDialog(context);
                        dialog.show();
                    }
                });
                //租售中心
                holder.lin_rentalCenter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/RentalCenter").navigation();
                    }
                });
                break;

            case 3:
                //纵向跑马灯
                HashMap<String,Object> today=new HashMap<>();
                today=listItem.get(0);
                if(today.containsKey("tiltes")){
                    List<String> titles= (List<String>) today.get("tiltes");
                    holder.marqueeView.startWithList(titles);
                    // 在代码里设置自己的动画
                    holder.marqueeView.startWithList(titles, R.anim.anim_bottom_in, R.anim.anim_top_out);

                }
                holder.recommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/HotInformation").withInt("key",6).navigation();
                    }
                });
                holder.marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, TextView textView) {
                        ARouter.getInstance().build("/main/act/HotInformation").withInt("key",6).navigation();
                    }
                });
                //横向 pager
                List<Integer> pics=new ArrayList<>();
                pics.add(R.mipmap.pic_h_1);
                pics.add(R.mipmap.pic_h_2);
                for (int i = 0; i < pics.size(); i++) {
                    //防止多次加载item
                    if(holder.lin_hs.getChildCount()<pics.size()){
                    View view=LayoutInflater.from(context).inflate(R.layout.imageview_item,holder.lin_hs,false);
                    ImageView imageView= (ImageView) view.findViewById(R.id.iv_image);
                    imageView.setImageResource(pics.get(i));
                    imageView.setId(i);
                    holder.lin_hs.addView(view);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()){
                                case 0:
                                    ARouter.getInstance().build("/main/act/user/LuckDraw").navigation();
                                    break;
                                case 1:
                                    Toast.makeText(context, "暂未开放", Toast.LENGTH_SHORT).show();
                                    break;

                            }

                        }
                    });
                    }
                }

                break;

            case 4:
                //热点资讯
                holder.hot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build("/main/act/HotInformation").navigation();
                    }
                });
                break;

            case 5:
                final HotInformationBean bean= (HotInformationBean) listItem.get(position).get("bean");
                LogUtil.e("listItem",listItem.size()+"");
                    if(bean.getType()==1){
                        holder.lay1.setVisibility(View.GONE);
                        holder.title_one.setText(bean.getTitle());
                        holder.count_one.setText(bean.getCount()+"");
                        PicassoUtil.getPicassoObject().load(bean.getIconUrlList().get(0))
                                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                                .into(holder.icon_one);
                        holder.lay2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance().build("/main/act/NewsDetails").withInt("id",bean.getId()).withString("title",bean.getTitle()).navigation();
                            }
                        });
                    }else if(bean.getType()==2){
                        holder.lay2.setVisibility(View.GONE);
                        holder.title_three.setText(bean.getTitle());
                        holder.count_three.setText(bean.getCount()+"");
                        PicassoUtil.getPicassoObject().load(bean.getIconUrlList().get(0))
                                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                                .into(holder.icon_1_three);
                        PicassoUtil.getPicassoObject().load(bean.getIconUrlList().get(1))
                                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                                .into(holder.icon_2_three);
                        PicassoUtil.getPicassoObject().load(bean.getIconUrlList().get(2))
                                .resize(DpUtils.dpToPx(context,80),DpUtils.dpToPx(context,80))
                                .into(holder.icon_3_three);
                        holder.lay1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance().build("/main/act/NewsDetails").withInt("id",bean.getId()).withString("title",bean.getTitle()).navigation();
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
                return TYPE_1;
            case 2:
                return TYPE_2;
            case 3:
                return TYPE_3;
            case 4:
                return TYPE_4;
            case 5:
                return TYPE_5;
            default:
                return -1;
        }
    }

    class HomeHolder extends RecyclerView.ViewHolder{

        //type1
        Banner  mBanner;
        LinearLayout search;
        RelativeLayout choice_city;
        TextView tv_home_city;

        //type2
        LinearLayout mLLECard;
        LinearLayout lay_2_perShop;
        LinearLayout ownerIntegral;
        LinearLayout decoration;
        LinearLayout more_content;
        LinearLayout pay_life;
        LinearLayout online;
        LinearLayout lin_rentalCenter;

        //type3
        MarqueeView marqueeView;
//        UltraViewPager mHorizontalPager;
        LinearLayout recommend,lin_hs;
        HorizontalScrollView hs;

        //type4
        RelativeLayout hot;

        //type=5
        LinearLayout lay1,lay2;
        TextView title_three;
        ImageView icon_1_three;
        ImageView icon_2_three;
        ImageView icon_3_three;
        TextView count_three;

        TextView title_one;
        ImageView icon_one;
        TextView count_one;





        public HomeHolder(View root,int type) {
            super(root);
            switch (type){
                case 1:
                    mBanner= (Banner) root.findViewById(R.id.home_banner);
                    search= (LinearLayout) root.findViewById(R.id.rel_hone_search);
                    choice_city= (RelativeLayout) root.findViewById(R.id.rel_choice_city);
                    tv_home_city= (TextView) root.findViewById(R.id.tv_hone_city);
                    break;

                case 2:
                    mLLECard= (LinearLayout) root.findViewById(R.id.ll_e_card);
                    ownerIntegral= (LinearLayout) root.findViewById(R.id.lin_owner_integral);
                    lay_2_perShop= (LinearLayout) root.findViewById(R.id.lay_2_perShop);
                    decoration= (LinearLayout) root.findViewById(R.id.lin_decoration);
                    more_content= (LinearLayout) root.findViewById(R.id.lin_more_content);
                    pay_life= (LinearLayout) root.findViewById(R.id.lin_pay_life);
                    online= (LinearLayout) root.findViewById(R.id.lin_online);
                    lin_rentalCenter= (LinearLayout) root.findViewById(R.id.lin_rentalCenter);
                    break;

                case 3:
                    marqueeView= (MarqueeView) root.findViewById(R.id.hot_marqueeView);
//                    mHorizontalPager= (UltraViewPager) root.findViewById(R.id.horizontal_pager);
                    hs= (HorizontalScrollView) root.findViewById(R.id.hs);
                    lin_hs= (LinearLayout) root.findViewById(R.id.lin_hs);
                    recommend= (LinearLayout) root.findViewById(R.id.lin_recommend);
                    break;

                case 4:
                    hot= (RelativeLayout) root.findViewById(R.id.rel_hot);
                    break;

                case 5:
                    lay1= (LinearLayout) root.findViewById(R.id.lay_1);
                    lay2= (LinearLayout) root.findViewById(R.id.lay_2);
                    //lay1
                    title_three= (TextView) root.findViewById(R.id.tv_home_hot_title);
                    count_three= (TextView) root.findViewById(R.id.tv_home_hot_count);
                    icon_1_three= (ImageView) root.findViewById(R.id.iv_home_hot_icon1);
                    icon_2_three= (ImageView) root.findViewById(R.id.iv_home_hot_icon2);
                    icon_3_three= (ImageView) root.findViewById(R.id.iv_home_hot_icon3);
                    //lay2
                    title_one= (TextView) root.findViewById(R.id.tv_home_hot_title_one);
                    icon_one= (ImageView) root.findViewById(R.id.iv_home_hot_icon_one);
                    count_one= (TextView) root.findViewById(R.id.tv_home_hot_count_one);
            }


        }

    }


    /**
     * 初始化 banner
     */
    private void initBanner(Banner mBanner,List<?> list){
        //显示圆形指示器
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //指示器居中
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setImages(list);
        mBanner.setImageLoader(new MyImageLoader());
        mBanner.start();
    }

    class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Picasso 加载图片
            //加载本地图片
            if(path instanceof Integer){
                Picasso.with(context).load((int)path).into(imageView);
            }

            //加载网络图片要压缩一下质量
            if(path instanceof String){
                PicassoUtil.getPicassoObject().with(context).load((String) path).memoryPolicy(MemoryPolicy.NO_CACHE).
                        into(imageView);
            }

        }

    }

    public interface HomeAdapterCallBack{
        public void  PayLifeClick();
    }

    private HomeAdapterCallBack callBack;

    public void setAdapterListener(HomeAdapterCallBack callBack){
        this.callBack=callBack;

    }





}
