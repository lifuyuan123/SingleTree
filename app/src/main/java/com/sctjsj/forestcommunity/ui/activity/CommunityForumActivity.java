package com.sctjsj.forestcommunity.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.CommunityFroumAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.CommentBean;
import com.sctjsj.forestcommunity.javabean.FroumPostBean;
import com.sctjsj.forestcommunity.javabean.UserBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/*
 社区论坛
 */
@Route(path = "/main/act/CommunityForumActivity")
public class CommunityForumActivity extends BaseAppcompatActivity {

    @BindView(R.id.froum_community_back)
    RelativeLayout froumCommunityBack;
    @BindView(R.id.froum_community_rv)
    RecyclerView froumCommunityRv;
    @BindView(R.id.froum_community_rl)
    QRefreshLayout froumCommunityRl;
    @BindView(R.id.froum_community_edt)
    EditText froumCommunityEdt;
    @BindView(R.id.froum_community_btn)
    TextView froumCommunityBtn;
    @BindView(R.id.activity_community_forum)
    LinearLayout activityCommunityForum;
    @Autowired(name = "froumId")
    int id=0;
    int eid=0;

    private int pageIndex=1;
    private HttpServiceImpl service;


    private CommunityFroumAdapter Type_a,Type_b,Type_c,Type_d;
    private DelegateAdapter adapter;

    private ArrayList<DelegateAdapter.Adapter> adapterList=new ArrayList<>();

    //论坛帖子的主体
    private ArrayList<HashMap<String,Object>> postsData=new ArrayList<>();
    //评论条数和点赞
    private ArrayList<HashMap<String,Object>> loveData=new ArrayList<>();
    //评论条数
    private ArrayList<HashMap<String,Object>> commNumberData=new ArrayList<>();
    //评论的内容
    private ArrayList<HashMap<String,Object>> commentData=new ArrayList<>();

    HashMap<String,Object> commNumberMap=new HashMap<>();

    HashMap<String,Object> loveMap=new HashMap<>();

    InputMethodManager imm;

    private int type=1; //区分 1评论主题  2.评论评论


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                getCommentList();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        imm = (InputMethodManager)CommunityForumActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        initData();
        setListener();


    }
    //初始化数据
    private void initView() {
        //虚拟适配器
        VirtualLayoutManager layoutManager=new VirtualLayoutManager(this);
        froumCommunityRv.setLayoutManager(layoutManager);

        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        froumCommunityRv.setRecycledViewPool(viewPool);

        viewPool.setMaxRecycledViews(6,10);

        initTypeAHelper();
        initTypeBHelper();
        initTypeCHelper();
        initTypeDHelper();

        adapter=new DelegateAdapter(layoutManager,false);
        adapter.setAdapters(adapterList);
        froumCommunityRv.setAdapter(adapter);


    }
    //初始化评论的内容
    private void initTypeDHelper() {
        LinearLayoutHelper linearD=new LinearLayoutHelper();
        Type_d=new CommunityFroumAdapter(commentData,linearD,this,4);
        adapterList.add(Type_d);
    }

    //初始化评论条数
    private void initTypeCHelper() {
        SingleLayoutHelper singC=new SingleLayoutHelper();
        commNumberData.add(commNumberMap);
        Type_c=new CommunityFroumAdapter(commNumberData,singC,this,3);
        adapterList.add(Type_c);
    }


    //初始化评论条数和点赞
    private void initTypeBHelper() {
        SingleLayoutHelper singB=new SingleLayoutHelper();
        loveData.add(loveMap);
        Type_b=new CommunityFroumAdapter(loveData,singB,this,2);
        adapterList.add(Type_b);
    }

    //初始化论坛帖子的主体
    private void initTypeAHelper() {
        SingleLayoutHelper singA=new SingleLayoutHelper();
        Type_a=new CommunityFroumAdapter(postsData,singA,this,1);
        adapterList.add(Type_a);
    }

    //设置监听
    private void setListener() {
        froumCommunityRl.setLoadMoreEnable(true);
        froumCommunityRl.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
               getCommentList();
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
               getCommentMoreList();
            }
        });


        Type_b.setCallBack(new CommunityFroumAdapter.commFroumCallback() {
            @Override
            public void commentLayoutClick() {
                //点击评论
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                type=1;
                froumCommunityEdt.setFocusable(true);//设置输入框可聚集
                froumCommunityEdt.setFocusableInTouchMode(true);//设置触摸聚焦
                froumCommunityEdt.requestFocus();//请求焦点
                froumCommunityEdt.findFocus();//获取焦点
            }

            @Override
            public void commUserItemClick(CommentBean bean) {

            }

            @Override
            public void loveCallback() {
                //点击了赞
                UserLove();
            }
        });


        Type_d.setCallBack(new CommunityFroumAdapter.commFroumCallback() {
            @Override
            public void commentLayoutClick() {

            }

            @Override
            public void commUserItemClick(CommentBean bean) {
                if(null!=bean){
                    if(bean.getBean().getId()== UserAuthUtil.getUserId()){
                        //点击的是自己评论的
                        Toast.makeText(CommunityForumActivity.this, "不能评论自己", Toast.LENGTH_SHORT).show();
                    }else {
                        String text="@"+bean.getBean().getUserName()+":";
                        froumCommunityEdt.setText(text);
                        froumCommunityEdt.setFocusable(true);//设置输入框可聚集
                        froumCommunityEdt.setFocusableInTouchMode(true);//设置触摸聚焦
                        froumCommunityEdt.requestFocus();//请求焦点
                        froumCommunityEdt.setSelection(text.length());
                        froumCommunityEdt.findFocus();//获取焦点
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        type=2;
                        eid=bean.getId();
                    }
                }
            }

            @Override
            public void loveCallback() {

            }
        });


    }

    //初始化布局
    private void initData() {
        getFroumMsg();
        getCommentList();
        UserIsOrNotLove();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_community_forum;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.froum_community_back, R.id.froum_community_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.froum_community_back:
                finish();
                break;
            case R.id.froum_community_btn:
                String edt=froumCommunityEdt.getText().toString();
                imm.hideSoftInputFromWindow(froumCommunityEdt.getWindowToken(), 0);
                froumCommunityEdt.setFocusable(false);
                if(!StringUtil.isBlank(edt)){
                        switch (type){
                            case 1:
                                //评论帖子
                                CommentPost();
                                break;
                            case 2:
                                //评论用户
                                String[] split = froumCommunityEdt.getText().toString().split(":");
                                if(split.length>0&&split[1].length()>0){
                                    CommentUserComm();
                                }else {
                                    Toast.makeText(this, "请输入评论的内容！", Toast.LENGTH_SHORT).show();
                                }
                                break;
                    }
                }else {
                    Toast.makeText(this, "请输入评论的内容！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //获取帖子详情
    private  void  getFroumMsg(){
        HashMap<String,String> body=new HashMap<>();
        body.put("id",id+"");
        body.put("ctype","tribune");
        body.put("jf","acclist|tuser|thumbnail");
        service.doCommonPost(null, MainUrl.baseSingleQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getFroumMsg",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONObject data=object.getJSONObject("data");
                            FroumPostBean bean=new FroumPostBean();
                            JSONArray photoArry=data.getJSONArray("acclist");
                            if(null!=photoArry&&photoArry.length()>0){
                                //获取帖子的图片
                                List<String> photoList=new ArrayList<>();
                                for (int i = 0; i <photoArry.length() ; i++) {
                                    JSONObject imgPhoto=photoArry.getJSONObject(i);
                                    photoList.add(imgPhoto.getString("url"));
                                }
                                bean.setImgList(photoList);
                            }
                            bean.setId(data.getInt("id"));
                            bean.setContent(data.getString("content"));
                            bean.setInsertTime(data.getString("insertTime"));
                            bean.setTags(data.getString("tags"));
                            bean.setAddress(data.getString("position"));
                            //获取发帖人的信息
                            JSONObject tuser=data.getJSONObject("tuser");
                            UserBean tuserBean=new UserBean();
                            tuserBean.setUserIcon(tuser.getJSONObject("thumbnail").getString("url"));
                            tuserBean.setUserName(tuser.getString("username"));

                            bean.setBean(tuserBean);

                            HashMap<String,Object> map=new HashMap<String, Object>();
                            map.put("data",bean);
                            postsData.add(map);
                            Type_a.notifyItemChanged(postsData.size());
                            //上传足迹
                            upData(bean.getContent(),bean.getId());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }


    //获取评论列表
    private void  getCommentList(){
        commentData.clear();
        pageIndex=1;
        HashMap<String,String> body=new HashMap<>();
        body.put("ctype","evaluate");
        body.put("cond","{tribune:{id:"+id+"}}");
        body.put("jf","acclist|euser|parent|thumbnail");
        body.put("pageIndex",pageIndex+"");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                pageIndex++;
                Log.e("getCommentList",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONArray array=object.getJSONArray("resultList");
                            if(null!=array&& array.length()>0){
                                commNumberMap.put("data",array.length());
                                loveMap.put("comment",array.length());
                                for (int i = 0; i <array.length() ; i++) {
                                    CommentBean bean=new CommentBean();
                                    JSONObject comment=array.getJSONObject(i);
                                    if(comment.has("parent")){
                                        //是评论的评论
                                        bean.setType(2);
                                        JSONObject parentObj=comment.getJSONObject("parent");
                                        UserBean parent=new UserBean();
                                        parent.setUserName(parentObj.getJSONObject("euser").getString("username"));
                                        parent.setId(parentObj.getJSONObject("euser").getInt("id"));
                                        bean.setParentid(parentObj.getInt("id"));
                                        bean.setParBean(parent);
                                    }

                                    //存入自己的信息
                                    JSONObject euser=comment.getJSONObject("euser");
                                    UserBean mine=new UserBean();
                                    mine.setId(euser.getInt("id"));
                                    mine.setUserName(euser.getString("username"));
                                    mine.setUserIcon(euser.getJSONObject("thumbnail").getString("url"));
                                    bean.setBean(mine);

                                    //存入评论的信息
                                    bean.setEvalContent(comment.getString("evalContent"));
                                    bean.setInsertTime(comment.getString("insertTime"));
                                    bean.setId(comment.getInt("id"));

                                    HashMap<String,Object> map=new HashMap<String, Object>();
                                    map.put("data",bean);
                                    commentData.add(map);
                                }
                                //刷新适配器
                                Type_b.notifyItemChanged(loveData.size());
                                Type_c.notifyItemChanged(commNumberData.size());
                                //Type_d.notifyDataSetChanged();
                                Type_d.notifyItemChanged(commentData.size());
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getCommentList",e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                froumCommunityRl.refreshComplete();
                froumCommunityRl.LoadMoreComplete();

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }



    //获取评论列表更多
    private void  getCommentMoreList(){
        HashMap<String,String> body=new HashMap<>();
        body.put("ctype","evaluate");
        body.put("cond","{tribune:{id:"+id+"}}");
        body.put("pageIndex",pageIndex+"");
        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getCommentList",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        pageIndex++;
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            JSONArray array=object.getJSONArray("resultList");
                            if(null!=array&& array.length()>0){
                                commNumberMap.put("data",array.length());
                                loveMap.put("comment",array.length());
                                for (int i = 0; i <array.length() ; i++) {
                                    CommentBean bean=new CommentBean();
                                    JSONObject comment=array.getJSONObject(i);
                                    if(comment.has("parent")){
                                        //是评论的评论
                                        bean.setType(2);
                                        JSONObject parentObj=comment.getJSONObject("parent");
                                        UserBean parent=new UserBean();
                                        parent.setUserName(parentObj.getJSONObject("euser").getString("username"));
                                        parent.setId(parentObj.getJSONObject("euser").getInt("id"));
                                        bean.setParentid(parentObj.getInt("id"));
                                        bean.setParBean(parent);
                                    }

                                    //存入自己的信息
                                    JSONObject euser=comment.getJSONObject("euser");
                                    UserBean mine=new UserBean();
                                    mine.setId(euser.getInt("id"));
                                    mine.setUserName(euser.getString("username"));
                                    mine.setUserIcon(euser.getJSONObject("thumbnail").getString("url"));
                                    bean.setBean(mine);

                                    //存入评论的信息
                                    bean.setEvalContent(comment.getString("evalContent"));
                                    bean.setInsertTime(comment.getString("insertTime"));
                                    bean.setId(comment.getInt("id"));

                                    HashMap<String,Object> map=new HashMap<String, Object>();
                                    map.put("data",bean);
                                    commentData.add(map);
                                }
                                //刷新适配器
                                Type_b.notifyDataSetChanged();
                                Type_c.notifyDataSetChanged();
                                //Type_d.notifyDataSetChanged();
                                Type_d.notifyDataSetChanged();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                froumCommunityRl.refreshComplete();
                froumCommunityRl.LoadMoreComplete();

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }


    //评论帖子
    private void CommentPost(){
        HashMap<String,String> body=new HashMap<>();
        body.put("tid",id+"");//帖子的id
        body.put("evalContent",froumCommunityEdt.getText().toString());
        body.put("piclist","");
        service.doCommonPost(null, MainUrl.CommentPostUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            Toast.makeText(CommunityForumActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                            type=1;
                            froumCommunityEdt.setText("");
                            handler.sendEmptyMessage(0);
                        }else {
                            Toast.makeText(CommunityForumActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }

    //评论用户
    private void CommentUserComm(){
        String[] split = froumCommunityEdt.getText().toString().split(":");
        HashMap<String,String> body=new HashMap<>();
        body.put("eid",eid+"");//评论的id
        body.put("evalContent",split[1]);
        body.put("piclist","");
        service.doCommonPost(null, MainUrl.CommentUserUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            Toast.makeText(CommunityForumActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                            froumCommunityEdt.setText("");
                            type=1;
                            handler.sendEmptyMessage(0);
                        }else {
                            Toast.makeText(CommunityForumActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }


    //获取用户是否点赞
    private  void  UserIsOrNotLove(){
        HashMap<String,String> body=new HashMap<>();
        body.put("tid",id+"");
        service.doCommonPost(null, MainUrl.UserIsOrNotLoveUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("UserIsOrNotLove",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            String code=object.getString("code");
                            loveMap.put("code",code);
                            //Type_b.notifyItemChanged(loveData.size());
                            Type_b.notifyDataSetChanged();
                        }else {
                            Toast.makeText(CommunityForumActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }

    //用户取消点赞或者点赞
    private void UserLove(){
        HashMap<String,String> body=new HashMap<>();
        body.put("tid",id+"");

        service.doCommonPost(null, MainUrl.UserloveUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("UserLove",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                           //取消或者点赞成功
                            UserIsOrNotLove();
                        }else {
                            Toast.makeText(CommunityForumActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                Log.e("UserLove",ex.toString());

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }

    //上传足迹
    private void upData(String name,int id) {
        HashMap<String,String> map=new HashMap<>();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        map.put("data","{tuser:{id:"+ UserAuthUtil.getUserId()+"},type:3,tid:"+id+",skimTime:\""+time+"\"}");
//        map.put("ctype","track");
        map.put("type","3");
        map.put("tid",id+"");
        map.put("title",name);
        service.doCommonPost(null, MainUrl.saveFootMark, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("savafootonSuccess",result.toString());
                try {
                    JSONObject object=new JSONObject(result);
                    boolean results=object.getBoolean("result");
                    String msg=object.getString("msg");
                    if(results){
                        LogUtil.e("savafoot",msg);
                    }else {
                        LogUtil.e("savafoot",msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e("savafootJSONException",e.toString());
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("savafootonError",ex.toString());
            }
            @Override
            public void onCancelled(Callback.CancelledException cex) {}
            @Override
            public void onFinished() {}
            @Override
            public void onWaiting() {}
            @Override
            public void onStarted() {}
            @Override
            public void onLoading(long total, long current) {}
        });
    }

}
