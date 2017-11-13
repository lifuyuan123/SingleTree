package com.sctjsj.forestcommunity.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
//import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView;
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
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.UserBean;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

@Route(path = "/main/act/user/UserData")
//个人资料
public class UserDataActivity extends BaseAppcompatActivity implements TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.iv_userIcon)
    CircleImageView ivUserIcon;
    @BindView(R.id.tv_userName)
    TextView tvUserName;
    @BindView(R.id.tv_userBirthday)
    TextView tvUserBirthday;
    @BindView(R.id.tv_userHouse)
    TextView tvUserHouse;
    @BindView(R.id.tv_userPhone)
    TextView tvUserPhone;
    //图片相关
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    //时间选择器
    private TimePickerView pvTime;

    private HttpServiceImpl service;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_user_data;
    }

    @Override
    public void reloadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserMessage();
    }
    //获取用户信息
    private void getUserMessage() {
        HashMap<String,String> body=new HashMap<>();
        body.put("id", UserAuthUtil.getUserId()+"");
        body.put("ctype","user");
        body.put("jf","thumbnail");
        service.doCommonPost(null, MainUrl.baseSingleQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("UseronSuccess",result.toString());
                if(!StringUtil.isBlank(result)){
                    try {
                        JSONObject object1=new JSONObject(result);
                        JSONObject object=object1.getJSONObject("data");
                        if(object1.getBoolean("result")){
                            //获取成功
                            UserBean bean=new UserBean();
                            if(object.has("thumbnail")&&object.getJSONObject("thumbnail").has("url")){
                                bean.setUserIcon(object.getJSONObject("thumbnail").getString("url"));
                            }else {
                                bean.setUserIcon("asd");
                            }
                            bean.setUserName(object.getString("realName"));
                            if(object.has("birthday")){
                                String birthday=object.getString("birthday");
                                int lastIndex=birthday.lastIndexOf(" ");
                                bean.setBirthday(birthday.substring(0,lastIndex));
                            }else {
                                bean.setBirthday("暂未设置");
                            }
                            bean.setPhone(object.getString("phone"));
                            initView(bean);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("UserJSONException",e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("UserJSonError",ex.toString());
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


    //适配数据
    private void initView(UserBean bean) {

        if(null!=bean){
            PicassoUtil.getPicassoObject().load(bean.getUserIcon())
                    .resize(DpUtils.dpToPx(this,80),DpUtils.dpToPx(this,80))
                    .error(R.drawable.icon_user_default).into(ivUserIcon);

            tvUserName.setText(bean.getUserName());
            tvUserBirthday.setText(bean.getBirthday());
            tvUserPhone.setText(bean.getPhone());
        }
    }

    @OnClick({R.id.linear_back, R.id.lin_userIcon, R.id.lin_userName, R.id.lin_user_birthday, R.id.lin_userAdress, R.id.lin_user_house, R.id.lin_user_phone, R.id.lin_user_changePsw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //退出
            case R.id.linear_back:
                finish();
                break;
            //头像
            case R.id.lin_userIcon:
                changeIcon();
                break;
            //昵称
            case R.id.lin_userName:
                ARouter.getInstance().build("/main/act/ChangeUserName").withString("name",tvUserName.getText().toString()).navigation();
                break;
            //生日
            case R.id.lin_user_birthday:
                choiceTime();
                break;
            //收货地址
            case R.id.lin_userAdress:
                ARouter.getInstance().build("/main/act/ManageAdress").navigation();
                break;
            //我的房屋
            case R.id.lin_user_house:
                break;
            //绑定电话
            case R.id.lin_user_phone:
                ARouter.getInstance().build("/main/act/ChangePhone").withString("phone",tvUserPhone.getText().toString()).navigation();
                break;
            //修改密码
            case R.id.lin_user_changePsw:
                ARouter.getInstance().build("/main/act/ChangePsw").navigation();
                break;
        }
    }

   //选择生日
    private void choiceTime() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        Calendar startDate = Calendar.getInstance();
        startDate.set(calendar.get(Calendar.YEAR) - 100,0,1);
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                tvUserBirthday.setText(getTime(date));
                changeBirthday(getTime(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
                .setTitleText("选择生日")
                .setRange(calendar.get(Calendar.YEAR) - 100, calendar.get(Calendar.YEAR))
                .setRangDate(startDate,selectedDate)
                .setLabel("年","月","日","","","")
                .isCenterLabel(true)//是否每项item都有label，false代表是
                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();

        pvTime.show();

    }
    //转换时间格式
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    //添加头像
    private void changeIcon() {
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


     // 获取TakePhoto实例
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    //图片保存路径
    public Uri getUri() {
        File file = new File(Environment.getExternalStorageDirectory(), "/forestcommunity/images/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }

    //成功
    @Override
    public void takeSuccess(TResult result) {
        path = result.getImage().getPath();
        if (StringUtil.isBlank(path)) {
            return;
        }
        upFile(new File(path));
    }

    //失败
    @Override
    public void takeFail(TResult result, String msg) {

    }

    //取消
    @Override
    public void takeCancel() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //创建TakePhoto实例
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    // 申请权限
    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    // 权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //处理运行时权限
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    //上传图片文件
    private void upFile(File file) {
        service.uploadFile(null, MainUrl.UpIcon, file, null, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("upfileonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("resultMsg");
                        if(results){
                            String str=object.getString("resultData");
                            if(!StringUtil.isBlank(str)&&str!=null){
                            JSONArray array=new JSONArray(str);
                            if(array!=null&&array.length()>0){
                                int id=array.getJSONObject(0).getInt("acyId");
                                //修改头像
                                upIcon(id);
                            }
                            }
                        }else {
                            Toast.makeText(UserDataActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("upfileJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("upfileonError",ex.toString());
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

    //修改头像
    private void upIcon(int id) {
        HashMap<String,String> map=new HashMap<>();
        map.put("data","{id:"+UserAuthUtil.getUserId()+",thumbnail:{id:"+id+"}}");
        map.put("ctype","user");
        service.doCommonPost(null, MainUrl.baseModifyUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("upIcononSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");
                        if(results){
                            PicassoUtil.getPicassoObject().load(new File(path)).into(ivUserIcon);
                            Toast.makeText(UserDataActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UserDataActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("upIconJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("upIcononError",ex.toString());
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


    //修改生日
    private void changeBirthday(String birthday) {
        final HashMap<String,String> map=new HashMap<>();
        map.put("data","{id:"+ UserAuthUtil.getUserId()+",birthday:"+"\""+birthday+"\""+"}");
        map.put("ctype","user");
        service.doCommonPost(null, MainUrl.baseModifyUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("changebirthonSuccess",result.toString());
                if(!StringUtil.isBlank(result)&&result!=null){
                    try {
                        JSONObject object=new JSONObject(result);
                        boolean results=object.getBoolean("result");
                        String msg=object.getString("msg");
                        if(results){
                            Toast.makeText(UserDataActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UserDataActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("changebirthJSONException",e.toString());
                    }
                }
            }
            @Override
            public void onError(Throwable ex) {
                LogUtil.e("changebirthonError",ex.toString());
            }
            @Override
            public void onCancelled(Callback.CancelledException cex) {}
            @Override
            public void onFinished() {
            }
            @Override
            public void onWaiting() {}
            @Override
            public void onStarted() {}
            @Override
            public void onLoading(long total, long current) {}
        });
    }
}
