package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.UpImageBean;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by haohaoliu on 2017/8/11.
 * explain:发帖的适配器
 */

public class PostToFroumAdapter extends DelegateAdapter.Adapter<PostToFroumAdapter.PostToFroumHolder> {

    public static final int TYPE_a = 1;
    public static final int TYPE_b = 2;
    public static final int TYPE_c = 3;
    public static final int TYPE_d = 4;

    private LayoutHelper layoutHelper;
    private RecyclerView.LayoutParams layoutParams;
    private ArrayList<HashMap<String,Object>> data;
    private int type=0;
    private Context mContext;
    private LayoutInflater inflater;
    private TagAdapter<String> tagAdapter;
    private boolean flag=false;//false没有显示删除标签

    private int Max=500;

    private String edtTxt="";

    private String seleTag="";


    public PostToFroumAdapter(LayoutHelper layoutHelper, RecyclerView.LayoutParams layoutParams, ArrayList<HashMap<String, Object>> data, int type, Context mContext) {
        this.layoutHelper = layoutHelper;
        this.layoutParams = layoutParams;
        this.data = data;
        this.type = type;
        this.mContext = mContext;
        this.inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public PostToFroumAdapter(LayoutHelper layoutHelper, ArrayList<HashMap<String, Object>> data, int type, Context mContext){
        this(layoutHelper,new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT),data,type,mContext);
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
    public PostToFroumHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=null;
        switch (viewType){
            case TYPE_a:
                view=inflater.inflate(R.layout.post_to_froum_a,parent,false);
                break;
            case TYPE_b:
                view=inflater.inflate(R.layout.post_to_froum_b,parent,false);
                break;
            case TYPE_c:
                view=inflater.inflate(R.layout.post_to_froum_c,parent,false);
                break;
            case TYPE_d:
                view=inflater.inflate(R.layout.post_to_froum_d,parent,false);
                break;
        }

        return new PostToFroumHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(final PostToFroumHolder holder, int position) {
        int viewType=getItemViewType(position);

        switch (viewType){
            case TYPE_a:
                holder.postToFrom_a_edt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String str = holder.postToFrom_a_edt.getText().toString();
                        if(str.length()<Max){
                            holder.postToFrom_a_Number.setText(str.length()+"/"+Max);
                        }else {
                            String substring = str.substring(0, Max);
                            holder.postToFrom_a_edt.setText(substring);
                            Toast.makeText(mContext, "超过最大长度！", Toast.LENGTH_SHORT).show();
                            holder.postToFrom_a_edt.setSelection(substring.length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                                edtTxt=holder.postToFrom_a_edt.getText().toString();
                    }
                });

                holder.postToFrom_a_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus){
                            //当失去焦点的时候判断
                            if(holder.postToFrom_a_edt.getText().toString().length()<0){
                                holder.postToFrom_a_edt.setError("请输入帖子内容");
                            }
                        }
                    }
                });





                break;
            case TYPE_b:
                HashMap<String, Object> dataMap = data.get(position);
                final UpImageBean imgBean= (UpImageBean) dataMap.get("data");
                if(null!=imgBean){
                    if(imgBean.getFlag()==2){
                        //是+ 图片
                        holder.postToFroum_b_Img.setImageResource(R.drawable.ic_add_img);
                    }else {
                        //是从相册选入的图片
                        holder.postToFroum_b_Img.setImageBitmap(imgBean.getBitmap());
                        //判断是否要显示删除图标
                        if(imgBean.isDel()){
                            //显示
                            holder.post_froum_b_delImg.setVisibility(View.VISIBLE);
                        }else {
                            holder.post_froum_b_delImg.setVisibility(View.GONE);
                        }
                    }
                }

                holder.postToFroum_b_Img.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //图片的长按事件（显示删除按钮）
                        if(imgBean.getFlag()!=2){
                            if(!flag){
                                showDel();
                            }else {
                                goneDel();
                            }
                        }
                        return true;
                    }
                });

                holder.post_froum_b_delImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击删除图标的监听
                        if(null!=callback){
                            callback.delImgClick(imgBean.getId(),holder.getLayoutPosition());
                        }
                    }
                });

                holder.postToFroum_b_Img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(imgBean.getFlag()==2){
                            //点击的添加图片
                            if(null!=callback){
                                callback.addImgClack();
                            }
                        }
                    }
                });

                break;
            case TYPE_c:

                break;
            case TYPE_d:
                final List<String> tagList= (List<String>) data.get(0).get("data");
                if(null==tagAdapter){
                    tagAdapter=new LableAdapter(tagList,mContext);
                    holder.postToFroum_d_tag.setAdapter(tagAdapter);
                }else {
                    tagAdapter.notifyDataChanged();
                }

                holder.postToFroum_d_tag.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                    @Override
                    public void onSelected(Set<Integer> selectPosSet) {
                        String s = selectPosSet.toString();
                        char[] chars = s.toCharArray();
                        String number= String.valueOf(chars[1]);
                        int sel=Integer.valueOf(new String(number));
                        seleTag=tagList.get(sel);
                    }
                });
                break;
        }

    }
    //隐藏删除的图标
    private void goneDel() {
        for (int i = 0; i <data.size() ; i++) {
            HashMap<String, Object> dataMap = data.get(i);
            UpImageBean imgBean= (UpImageBean) dataMap.get("data");
            if(imgBean.getFlag()!=2){
                imgBean.setDel(false);
            }
        }
        notifyDataSetChanged();
    }

    //显示所有的删除的图标
    private void showDel() {
        for (int i = 0; i <data.size() ; i++) {
            HashMap<String, Object> dataMap = data.get(i);
            UpImageBean imgBean= (UpImageBean) dataMap.get("data");
            if(imgBean.getFlag()!=2){
                imgBean.setDel(true);
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    static  class  PostToFroumHolder extends RecyclerView.ViewHolder{
        //a布局的控件
        EditText postToFrom_a_edt;
        TextView postToFrom_a_Number;
        //b的布局
        ImageView postToFroum_b_Img;
        ImageView post_froum_b_delImg;

        //d的布局
        TagFlowLayout postToFroum_d_tag;



        public PostToFroumHolder(View itemView,int viewType) {
            super(itemView);

            switch (viewType){
                case TYPE_a:
                    postToFrom_a_edt= (EditText) itemView.findViewById(R.id.postToFrom_a_edt);
                    postToFrom_a_Number= (TextView) itemView.findViewById(R.id.postToFrom_a_Number);
                    break;
                case TYPE_b:
                    postToFroum_b_Img= (ImageView) itemView.findViewById(R.id.postToFroum_b_Img);
                    post_froum_b_delImg= (ImageView) itemView.findViewById(R.id.post_froum_b_delImg);
                    break;
                case TYPE_c:
                    break;
                case TYPE_d:
                    postToFroum_d_tag= (TagFlowLayout) itemView.findViewById(R.id.postToFroum_d_tag);
                    break;
            }
        }
    }


    //相关的回调
    public  interface  PostFroumCallBack{
        public void delImgClick(int id,int poSition);
        public void addImgClack();
    }

    private PostFroumCallBack callback;

    public void  setAdapterCallBack(PostFroumCallBack callback){
        this.callback=callback;
    }


    public String getEdtTxt(){
        if(edtTxt.length()<0){
            Toast.makeText(mContext, "请填写您的帖子内容！", Toast.LENGTH_SHORT).show();
        }
        return edtTxt;
    }


    public String getTagTxt(){
        if(seleTag.length()<0){
            Toast.makeText(mContext, "请选择一个标签！", Toast.LENGTH_SHORT).show();
        }
        return seleTag;
    }
}
