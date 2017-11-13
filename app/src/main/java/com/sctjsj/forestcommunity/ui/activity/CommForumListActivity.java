package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.CommFroumLsitAdapters;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/***
 * 社区论坛更多列表
 */
@Route(path = "/main/act/CommForumListActivity")
public class CommForumListActivity extends BaseAppcompatActivity {


    @Autowired(name = "Tag")
    String Tag = "";

    private boolean flag = true; //true 最新

    @BindView(R.id.comm_fourm_list_back)
    RelativeLayout commFourmListBack;
    @BindView(R.id.comm_froum_list_zx)
    RadioButton commFroumListZx;
    @BindView(R.id.comm_froum_list_zr)
    RadioButton commFroumListZr;
    @BindView(R.id.comm_froum_list_rg)
    RadioGroup commFroumListRg;
    @BindView(R.id.comm_forum_list_rv)
    RecyclerView commForumListRv;
    @BindView(R.id.comm_forum_list_qr)
    QRefreshLayout commForumListQr;
    @BindView(R.id.comm_froum_list_b_layout)
    LinearLayout commFroumListBLayout;
    @BindView(R.id.activity_comm_forum_list)
    LinearLayout activityCommForumList;
    @BindView(R.id.froum_lay_nodata)
    LinearLayout nodata;


    private List<FroumPostBean> ZXdata = new ArrayList<>();
    private List<FroumPostBean> ZRdata = new ArrayList<>();

    private CommFroumLsitAdapters adapterZx;
    private CommFroumLsitAdapters adapterZR;

    private HttpServiceImpl service;

    private int pageIndex = 1;

    private boolean isFirstLoading = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        commFroumListZx.setChecked(true);
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        adapterZR = new CommFroumLsitAdapters(this, ZRdata);
        adapterZx = new CommFroumLsitAdapters(this, ZXdata);
        initView();
        initData();


    }

    //获取数据
    private void initData() {

        HashMap<String, String> body = LoadingBody();
        getFroumData(body);

    }

    //判断加载条件
    private HashMap<String, String> LoadingBody() {

        HashMap<String, String> body = new HashMap<>();
        //首先判断是否是标签点进来的
        if (!StringUtil.isBlank(Tag)) {
            //是
            if (flag) {//最新
                body.put("pageIndex", pageIndex + "");
                body.put("ctype", "tribune");
                body.put("cond", "{tags:'" + Tag + "'}");
                body.put("orderby", "insertTime desc");
                body.put("qtype","2");
                body.put("jf", "evaluates|tuser|thumbnail|euser|acclist");

            } else {//最热
                body.put("ctype", "tribune");
                body.put("cond", "{tags:'" + Tag + "'}");
                body.put("orderby", "trihot desc");
                body.put("qtype","2");
                body.put("jf", "evaluates|tuser|thumbnail|euser|acclist");
                body.put("pageIndex", pageIndex + "");
            }
        } else {
            //不是
            if (flag) {//最新
                body.put("pageIndex", pageIndex + "");
                body.put("ctype", "tribune");
                body.put("cond", "{tribune:1}");
                body.put("qtype","2");
                body.put("orderby", "insertTime desc");
                body.put("jf", "evaluates|tuser|thumbnail|euser|acclist");

            } else {//最热
                body.put("ctype", "tribune");
                body.put("cond", "{tribune:1}");
                body.put("qtype","2");
                body.put("orderby", "trihot desc");
                body.put("jf", "evaluates|tuser|thumbnail|euser|acclist");
                body.put("pageIndex", pageIndex + "");
            }
        }
        return body;
    }

    private void initView() {
        commForumListRv.setLayoutManager(new LinearLayoutManager(this));
        setListener();
    }


    //设置其他的监听
    private void setListener() {
        commForumListQr.setLoadMoreEnable(true);
        commForumListQr.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                pageIndex = 1;
                isFirstLoading = false;
                HashMap<String, String> body = LoadingBody();
                getFroumData(body);
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                HashMap<String, String> body = LoadingBody();
                getFroumMoreData(body);
            }
        });

        //radiogroup的监听
        commFroumListRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.comm_froum_list_zx:
                        flag = true;
                        isFirstLoading = true;
                        pageIndex = 1;
                        HashMap<String, String> bodyzx = LoadingBody();
                        getFroumData(bodyzx);
                        break;
                    case R.id.comm_froum_list_zr:
                        flag = false;
                        isFirstLoading = true;
                        pageIndex = 1;
                        HashMap<String, String> bodzry = LoadingBody();
                        getFroumData(bodzry);
                        break;

                }
            }
        });

    }

    @Override
    public int initLayout() {
        return R.layout.activity_comm_forum_list;
    }

    @Override
    public void reloadData() {

    }


    private void getFroumData(HashMap<String, String> body) {

        ZXdata.clear();
        ZRdata.clear();
        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("getFroumData", result.toString());
                pageIndex++;
                if (!StringUtil.isBlank(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            JSONArray arr = object.getJSONArray("resultList");
                            if (null != arr && arr.length() > 0) {
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject froum = arr.getJSONObject(i);
                                    FroumPostBean froumBean = new FroumPostBean();
                                    froumBean.setContent(froum.getString("content"));
                                    froumBean.setId(froum.getInt("id"));
                                    froumBean.setInsertTime(froum.getString("insertTime"));
                                    froumBean.setTags(froum.getString("tags"));
                                    froumBean.setAddress(froum.getString("position"));
                                    //获取图片
                                    List<String> imgList = new ArrayList<>();
                                    JSONArray imgArr = froum.getJSONArray("acclist");
                                    if (null != imgArr && imgArr.length() > 0) {
                                        for (int j = 0; j < imgArr.length(); j++) {
                                            JSONObject img = imgArr.getJSONObject(j);
                                            imgList.add(img.getString("url"));
                                        }
                                        froumBean.setImgList(imgList);
                                    }

                                    //获取发帖人
                                    JSONObject tuser = froum.getJSONObject("tuser");
                                    UserBean tuserBean = new UserBean();
                                    tuserBean.setUserIcon(tuser.getJSONObject("thumbnail").getString("url"));
                                    tuserBean.setUserName(tuser.getString("username"));
                                    froumBean.setBean(tuserBean);
                                    //获取评论列表
                                    JSONArray commArr = froum.getJSONArray("evaluates");
                                    if (null != commArr && commArr.length() > 0) {
                                        List<CommentBean> commList = new ArrayList<>();
                                        for (int k = 0; k < commArr.length(); k++) {
                                            JSONObject connemt = commArr.getJSONObject(k);
                                            CommentBean commBean = new CommentBean();
                                            UserBean commUser = new UserBean();
                                            commBean.setId(connemt.getInt("id"));
                                            commBean.setEvalContent(connemt.getString("evalContent"));
                                            commUser.setUserName(connemt.getJSONObject("euser").getString("username"));
                                            commBean.setBean(commUser);
                                            commList.add(commBean);
                                        }
                                        froumBean.setCommList(commList);
                                    }

                                    if (flag) {
                                        ZXdata.add(froumBean);
                                    } else {
                                        ZRdata.add(froumBean);
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getFroumData", e.toString());

                    } finally {
                        if (flag) {
                            if(ZXdata.size()>0){
                                commForumListQr.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                                commForumListRv.setAdapter(adapterZx);
                            }else {
                                commForumListQr.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            }
                        } else {

                            if(ZXdata.size()>0){
                                commForumListQr.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                                commForumListRv.setAdapter(adapterZR);
                            }else {
                                commForumListQr.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            }

                        }

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
                commForumListQr.refreshComplete();
                commForumListQr.LoadMoreComplete();
                if (isFirstLoading) {
                    dismissLoading();
                }

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                if (isFirstLoading) {
                    showLoading(true, "加载中...");
                }

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });


    }

    private void getFroumMoreData(HashMap<String, String> body) {

        service.doCommonPost(null, MainUrl.basePageQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                pageIndex++;
                if (!StringUtil.isBlank(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            JSONArray arr = object.getJSONArray("resultList");
                            if (null != arr && arr.length() > 0) {
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject froum = arr.getJSONObject(i);
                                    FroumPostBean froumBean = new FroumPostBean();
                                    froumBean.setContent(froum.getString("content"));
                                    froumBean.setId(froum.getInt("id"));
                                    froumBean.setInsertTime(froum.getString("insertTime"));
                                    froumBean.setTags(froum.getString("tags"));
                                    froumBean.setAddress(froum.getString("position"));
                                    //获取图片
                                    List<String> imgList = new ArrayList<>();
                                    JSONArray imgArr = froum.getJSONArray("acclist");
                                    if (null != imgArr && imgArr.length() > 0) {
                                        for (int j = 0; j < imgArr.length(); j++) {
                                            JSONObject img = imgArr.getJSONObject(j);
                                            imgList.add(img.getString("url"));
                                        }
                                        froumBean.setImgList(imgList);
                                    }

                                    //获取发帖人
                                    JSONObject tuser = froum.getJSONObject("tuser");
                                    UserBean tuserBean = new UserBean();
                                    tuserBean.setUserIcon(tuser.getJSONObject("thumbnail").getString("url"));
                                    tuserBean.setUserName(tuser.getString("username"));
                                    froumBean.setBean(tuserBean);
                                    //获取评论列表
                                    JSONArray commArr = froum.getJSONArray("evaluates");
                                    if (null != commArr && commArr.length() > 0) {
                                        List<CommentBean> commList = new ArrayList<>();
                                        for (int k = 0; k < commArr.length(); k++) {
                                            JSONObject connemt = commArr.getJSONObject(k);
                                            CommentBean commBean = new CommentBean();
                                            UserBean commUser = new UserBean();
                                            commBean.setId(connemt.getInt("id"));
                                            commBean.setEvalContent(connemt.getString("evalContent"));
                                            commUser.setUserName(connemt.getJSONObject("euser").getString("username"));
                                            commBean.setBean(commUser);
                                            commList.add(commBean);
                                        }
                                        froumBean.setCommList(commList);
                                    }
                                    if (flag) {
                                        ZXdata.add(froumBean);
                                    } else {
                                        ZRdata.add(froumBean);
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if (flag) {
                            if(ZXdata.size()>0){
                                commForumListQr.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                                commForumListRv.setAdapter(adapterZx);
                            }else {
                                commForumListQr.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            }
                        } else {

                            if(ZXdata.size()>0){
                                commForumListQr.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                                commForumListRv.setAdapter(adapterZR);
                            }else {
                                commForumListQr.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            }
                        }
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
                commForumListQr.refreshComplete();
                commForumListQr.LoadMoreComplete();
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

    @OnClick({R.id.comm_fourm_list_back, R.id.comm_froum_list_b_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.comm_fourm_list_back:
                finish();
                break;
            case R.id.comm_froum_list_b_layout:
                if(UserAuthUtil.isUserLogin()){
                    ARouter.getInstance().build("/main/act/PostToFroum").navigation();
                }else {
                    Toast.makeText(this, "请登录后操作！", Toast.LENGTH_SHORT).show();
                    ARouter.getInstance().build("/main/act/login").navigation();
                }
                break;
        }
    }
}
