package com.sctjsj.forestcommunity.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;

import com.google.gson.Gson;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.UserBean;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import static com.sctjsj.basemodule.core.config.AsyncNotifyCode.CODE_LOGIN_ERROR;
import static com.sctjsj.basemodule.core.config.AsyncNotifyCode.CODE_LOGIN_FAILED;
import static com.sctjsj.basemodule.core.config.AsyncNotifyCode.CODE_LOGIN_SUCCESS;
import static com.sctjsj.basemodule.core.config.Tag.TAG_LOGIN_RESULT;

/**
 * Created by mayikang on 17/5/31.
 */

public class UserAuthUtil {

    private HttpServiceImpl http;

    public UserAuthUtil(){
        http= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
    }

    /**
     * 判断用户是否已经登录
     * @return
     */
    public static boolean isUserLogin(){
        //1.没有 token
        String token=SPFUtil.get(Tag.TAG_TOKEN,"none").toString();

        if(StringUtil.isBlank(token)||"none".equals(token) ){
            return false;
        }

        //2.没有用户信息
        String data= SPFUtil.get(Tag.TAG_USER,"null").toString();

        if(StringUtil.isBlank(data)|| "null".equals(data)){
            return false;
        }

        UserBean ub=new Gson().fromJson(data,UserBean.class);

        if(ub==null){
            return false;
        }

        if(0==ub.getId()){
            return false;
        }

        if(StringUtil.isBlank(ub.getUserName())){
            return false;
        }

        return true;
    }

    /**
     * 获取用户 id
     * @return
     */
    public static int getUserId(){
        //2.没有用户信息
        String data= SPFUtil.get(Tag.TAG_USER,"null").toString();

        if(StringUtil.isBlank(data)|| "null".equals(data)){
            return -1;
        }

        UserBean ub=new Gson().fromJson(data,UserBean.class);

        return ub.getId();
    }



    /**
     * 保存用户信息
     * @param ub
     */
    public static void saveUserBean(UserBean ub){
        if(ub==null){
            return;
        }

        SPFUtil.put(Tag.TAG_USER,new Gson().toJson(ub));

    }

    public static UserBean getCurrentUser(){
        String data=SPFUtil.get(Tag.TAG_USER,"null").toString();
        if(!data.equals("null")){
            UserBean ub=new Gson().fromJson(data,UserBean.class);
            return ub;
        }
        return null;
    }


    public void doLogin(final Context context, HashMap<String, String> map, final LogInOutListener listener){

        http.doCommonPost(null, MainUrl.LoginUrl, map, new XProgressCallback() {
            @Override
            public void onSuccess(String resultStr) {

                if(!StringUtil.isBlank(resultStr)){
                    try {
                        JSONObject res=new JSONObject(resultStr);
                        boolean result=res.getBoolean("result");
                        String resultMsg=res.getString("resultMsg");

                        //登录成功
                        if(result){
                            //处理登录成功的结果
                            String info=res.getString("info");
                            JSONObject user=new JSONObject(info);

                            int id=user.getInt("id");
                            String phone=user.getString("phone");
                            String url=null;
                            String photo=user.getString("photo");
                            if(!StringUtil.isBlank(photo)){
                                url=user.getJSONObject("photo").getString("url");
                            }
                            String username=user.getString("username");
                            UserBean ub=new UserBean();
                            ub.setEmail(user.getString("email"));

                            ub.setId(id);
                            ub.setPhone(phone);

                            SPFUtil.put(Tag.TAG_USER,new Gson().toJson(ub));

                            //发送用户登录成功
                           // EventBus.getDefault().post(new UserLoginEvent(1));

                            //发送本地广播通知拦截器
                            Intent intent=new Intent(Tag.TAG_LOGIN_FILTER);
                            intent.putExtra(TAG_LOGIN_RESULT,CODE_LOGIN_SUCCESS);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                            listener.onSuccess();
                        }else {
                            //处理登录失败的结果
                            Intent intent=new Intent(Tag.TAG_LOGIN_FILTER);
                            intent.putExtra(TAG_LOGIN_RESULT,CODE_LOGIN_FAILED);
                            Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                            listener.onFailed();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                //处理登录报错的结果

                LogUtil.e(ex.toString());
                Intent intent=new Intent(Tag.TAG_LOGIN_FILTER);
                intent.putExtra(TAG_LOGIN_RESULT,CODE_LOGIN_ERROR);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                listener.onError();
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

   /* public  void doLogout(final Context context, final LogInOutListener listener) {

        http.doCommonPost(null, MainUrl.logoutUrl, null, new XProgressCallback() {
            @Override
            public void onSuccess(String resultStr) {
                LogUtil.e("退出结果",resultStr.toString());
                if (!StringUtil.isBlank(resultStr)) {
                    try {
                        JSONObject obj = new JSONObject(resultStr);
                        boolean result = obj.getBoolean("result");


                        //退出成功
                        if (result) {
                            listener.onSuccess();
                        } else {
                            listener.onFailed();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {
                LogUtil.e("退出报错",ex.toString());
                listener.onError();
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                //清除本地 token
                if (SPFUtil.contains(Tag.TAG_TOKEN)) {
                    SPFUtil.removeOne(Tag.TAG_TOKEN);
                }
                //清除本地用户信息
                if (SPFUtil.contains(Tag.TAG_USER)) {
                    SPFUtil.removeOne(Tag.TAG_USER);
                }
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
*/

    public interface LogInOutListener {
        void onSuccess();
        void onFailed();
        void onError();
    }

}
