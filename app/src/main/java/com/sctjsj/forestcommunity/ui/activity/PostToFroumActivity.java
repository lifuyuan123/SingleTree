package com.sctjsj.forestcommunity.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.ui.widget.dialog.sweet.PopListDialog;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.PostToFroumAdapter;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.UpImageBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/***
 * 编辑帖子
 */
@Route(path = "/main/act/PostToFroum")
public class PostToFroumActivity extends BaseAppcompatActivity implements TakePhoto.TakeResultListener, InvokeListener {


    @BindView(R.id.postToFroum_back)
    RelativeLayout postToFroumBack;
    @BindView(R.id.post_to_Froum_rv)
    RecyclerView postToFroumRv;
    @BindView(R.id.activity_post_to_froum)
    LinearLayout activityPostToFroum;

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private File file;

    private PostToFroumAdapter Type_a, Type_b, Type_c, Type_d;
    private DelegateAdapter adapter;
    private ArrayList<DelegateAdapter.Adapter> adapterList = new ArrayList<>();

    //选择照片的集合
    private ArrayList<HashMap<String, Object>> photoData = new ArrayList<>();

    private Bitmap mBitmap;

    private HttpServiceImpl service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initView();
    }

    //初始化布局
    private void initView() {

        VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
        postToFroumRv.setLayoutManager(layoutManager);

        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        postToFroumRv.setRecycledViewPool(viewPool);

        initTypeAHelper();
        initTypeBHelper();
        initTypeCHelper();
        initTypeDHelper();

        adapter = new DelegateAdapter(layoutManager, false);
        adapter.setAdapters(adapterList);
        postToFroumRv.setAdapter(adapter);


    }

    //初始化选择标签页面
    private void initTypeDHelper() {
        SingleLayoutHelper singD = new SingleLayoutHelper();
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        HashMap<String, Object> datas = new HashMap<>();
        List<String> lableList = new ArrayList<>();
        lableList.add("#生活杂谈#");
        lableList.add("#社区爆料#");
        lableList.add("#求助#");
        lableList.add("#二手闲置#");
        lableList.add("#好人好事#");
        lableList.add("#好人#");
        datas.put("data", lableList);
        data.add(datas);
        Type_d = new PostToFroumAdapter(singD, data, 4, this);
        adapterList.add(Type_d);
        setAdapterListener();
    }

    //设置适配器的监听
    private void setAdapterListener() {
        Type_b.setAdapterCallBack(new PostToFroumAdapter.PostFroumCallBack() {
            @Override
            public void delImgClick(int id, int poSition) {
                //删除的回调
                delImg( id,  poSition);

            }

            @Override
            public void addImgClack() {
                //添加的回调
                addImg();
            }
        });

    }


    //点击删除图片的方法
    private void delImg(int id, int poSition) {
        for (int i = 0; i <photoData.size() ; i++) {
            HashMap<String,Object> map=photoData.get(i);
            UpImageBean imgBean= (UpImageBean) map.get("data");
            if(id==imgBean.getId()){
                photoData.remove(i);
                Type_b.notifyItemRemoved(poSition);
                break;
            }
        }
    }


    //初始化标题
    private void initTypeCHelper() {
        SingleLayoutHelper singC = new SingleLayoutHelper();
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        data.add(new HashMap<String, Object>());
        Type_c = new PostToFroumAdapter(singC, data, 3, this);
        adapterList.add(Type_c);
    }

    private void initTypeBHelper() {
        GridLayoutHelper gridB = new GridLayoutHelper(3);
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", new UpImageBean(2));
        photoData.add(map);
        Type_b = new PostToFroumAdapter(gridB, photoData, 2, this);
        adapterList.add(Type_b);
    }

    //初始化输入
    private void initTypeAHelper() {
        SingleLayoutHelper singA = new SingleLayoutHelper();
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        data.add(new HashMap<String, Object>());
        Type_a = new PostToFroumAdapter(singA, data, 1, this);
        adapterList.add(Type_a);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_post_to_froum;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.postToFroum_back, R.id.post_froum_Send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.postToFroum_back:
                finish();
                break;
            case R.id.post_froum_Send:
                sendPostToFroum();
                break;
        }
    }


    //发表帖子
    private void sendPostToFroum() {
        String edtTxt = Type_a.getEdtTxt();
        if(StringUtil.isBlank(edtTxt)){
            Toast.makeText(this, "请输入帖子内容！", Toast.LENGTH_SHORT).show();
            return;
        }
        String tagTxt = Type_d.getTagTxt();
        if(StringUtil.isBlank(tagTxt)){
            Toast.makeText(this, "请选择一个标签！", Toast.LENGTH_SHORT).show();
            return;
        }

        //拼接字符串
        StringBuffer piclist=new StringBuffer();
        for (int i = 0; i <photoData.size() ; i++) {
            HashMap<String,Object> map=photoData.get(i);
            UpImageBean imgBean= (UpImageBean) map.get("data");
            if(imgBean.getFlag()==1){
                int id = imgBean.getId();
                if(i==photoData.size()-2){
                    piclist.append(id);
                }else {
                    piclist.append(id+",");
                }
            }
        }


        HashMap<String,String> body=new HashMap<>();
        body.put("piclist",piclist.toString());
        body.put("content",edtTxt);
        body.put("tags",tagTxt);
        String city= (String) SPFUtil.get(Tag.TAG_CITY,"成都");
        body.put("position",city);

        service.doCommonPost(null, MainUrl.UserPublishPostUrl,body , new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("sendPostToFroum",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            Toast.makeText(PostToFroumActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                            //跳转到帖子详情
                            finish();
                            
                        }else {
                            Toast.makeText(PostToFroumActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
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


    //添加头像
    private void addImg() {
        final PopListDialog dialog = new PopListDialog(this);
        dialog.setPopListCallback(new PopListDialog.PopListCallback() {
            @Override
            public void callCamera() {
                dialog.dismiss();
                //裁剪参数
                CropOptions cropOptions = new CropOptions.Builder().
                        setWithOwnCrop(false).create();
                getTakePhoto().onPickFromCaptureWithCrop(getUri(), cropOptions);
            }

            @Override
            public void callGallery() {
                dialog.dismiss();
                //裁剪参数
                CropOptions cropOptions1 = new CropOptions.Builder()
                        .setWithOwnCrop(false).create();

                getTakePhoto().
                        onPickFromGalleryWithCrop(getUri(), cropOptions1);

            }
        });
        dialog.show();
    }


    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    /**
     * 图片保存路径
     *
     * @return
     */
    private Uri getUri() {
        file = new File(Environment.getExternalStorageDirectory(), "/wowallet/images/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }

    /**
     * 页面跳转回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 权限申请回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //处理运行时权限
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }


    @Override
    public void takeFail(TResult result, String msg) {

    }


    @Override
    public void takeSuccess(TResult result) {
        Log.e("takeSuccess","照片成功");
        mBitmap = BitmapFactory.decodeFile(result.getImage().getPath());
        if (null != file) {
            UpLoadingImg();
            Log.e("takeSuccess","照片成功s");
        }
    }


    //上传图片的网络操作
    private void UpLoadingImg() {
        service.uploadFile(null, MainUrl.UpIcon, file, null, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if (!StringUtil.isBlank(result)) {
                    try {
                        Log.e("UpLoadingImg",result);
                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            if (!StringUtil.isBlank(object.getString("resultData"))) {
                                JSONArray arr = new JSONArray(object.getString("resultData"));
                                JSONObject data = arr.getJSONObject(0);
                                int ImgId = data.getInt("acyId");
                                UpImageBean imgBean = new UpImageBean();
                                imgBean.setId(ImgId);
                                imgBean.setBitmap(mBitmap);
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("data", imgBean);
                                photoData.add(0,map);
                                Type_b.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(PostToFroumActivity.this, object.getString("resultMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                Log.e("UpLoadingImg",ex.toString());


            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dismissLoading();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                showLoading(true,"上传中....");

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });

    }

    @Override
    public void takeCancel() {

    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }


}
