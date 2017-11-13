package com.sctjsj.basemodule.core.cookie;


import android.util.Log;

import com.sctjsj.basemodule.base.util.LogUtil;

import org.xutils.http.cookie.DbCookieStore;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * Created by mayikang on 17/4/6.
 */

public class CookieUtil {
    private String TAG="@link com.sctjsj.basemodule.core.cookie.CookieUtil";
    private static CookieUtil instance;
    private CookieUtil(){

    }

    public static CookieUtil instance(){
        if(instance==null){
            instance=new CookieUtil();
        }
        return instance;
    }

    /**
     * xUtils 自动管理的 cookie
     * 获取本地存储的 cookie
     *
     *    该方法慎用
     *
     * @return
     */
    public HttpCookie getLocalCookie(){
        //获取cookie

        DbCookieStore cookieInstance=DbCookieStore.INSTANCE;
        List<HttpCookie> cookies = cookieInstance.getCookies();
        if(cookies!=null && cookies.size()>0){
            HttpCookie hc=cookies.get(0);
            return hc;
        }else {
            return null;
        }



//        for(HttpCookie c:cookies){
//            LogUtil.e("CookieUtil",c.toString());
//            String name=c.getName();
//            String val=c.getValue();
//            if("JSESSIONID".equals(name)){
//                cookie=name+"="+val;
//                break;
//            }
//        }


        //返回完整的 Cookie

//        name:c.getName();
//        value:c.getValue();
//        domain:c.getDomain();
//        path:c.getPath();

    }

    /**
     * 更新本地 cookie
     * @param uri
     * @param cookie
     */
    public void updateCookie(URI uri,HttpCookie cookie){
        DbCookieStore cookieInstance=DbCookieStore.INSTANCE;
        cookieInstance.removeAll();
        cookieInstance.add(uri,cookie);
    }


}
