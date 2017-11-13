package com.sctjsj.basemodule.core.img_load;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.setup.DeviceUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.cookie.CookieUtil;
import com.sctjsj.basemodule.core.exception.DuplicateInitException;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.xutils.http.cookie.DbCookieStore;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mayikang on 17/3/23.
 */

/**
 * Picasso 工具类
 */
public class  PicassoUtil {
    private static PicassoUtil picassoUtil;
    //context 为 application
    private static Context context;

    private static Picasso picasso;

    private static Picasso.Builder builder;

    private static OkHttpClient httpClient;



    private PicassoUtil(){

    }

    /**
     * 在 Application 中初始化
     * @param context
     * @return
     */
    public static PicassoUtil init(final Application context) throws Exception {

        /**
         * builder 只能初始化一次
         */
        if(builder!=null){
            throw new DuplicateInitException(context,PicassoUtil.class);
        }

        builder = new Picasso.Builder(context);


        httpClient=new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                Log.e("save","进入");
                Cookie cookie=cookies.get(0);
                Log.e("picasso-获取-cookie",cookie.toString());
                Log.e("picasso-获取-name",cookie.name());
                Log.e("picasso-获取-domain",cookie.domain());
                Log.e("picasso-获取-path",cookie.path());
                Log.e("picasso-获取-value",cookie.value());
                Log.e("picasso","---------------------");

                HttpCookie hc=new HttpCookie(cookie.name(),cookie.value());
                hc.setDomain(cookie.domain());
                hc.setPath(cookie.path());
                Log.e("hc-cookie",hc.toString());
                Log.e("hc-value",hc.getValue());
                Log.e("hc-name",hc.getName());
                Log.e("hc-domain",hc.getDomain());
                Log.e("hc-path",hc.getPath());
                Log.e("hc","---------------------");
                //更新本地 cookie
                CookieUtil.instance().updateCookie(URI.create(url.toString()),hc);

            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {

                List<Cookie> cookieList = new ArrayList<>();
                //获取本地持久化 cookie
                HttpCookie hc=CookieUtil.instance().getLocalCookie();
                //本地无 Cookie 时返回空
                if(hc!=null){
                    Cookie cookie=new Cookie.Builder()
                            .name(hc.getName())
                            .value(hc.getValue())
                            .domain(hc.getDomain())
                            .path(hc.getPath())
                            .build();
                    cookieList.add(cookie);
                }

                return cookieList;
            }
        })
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder request = chain.request().newBuilder();
                //注入 token
                request.addHeader("TOKEN", SPFUtil.get(Tag.TAG_TOKEN,"none-token").toString());

                return chain.proceed(request.build());


            }
        }).build();

        //配置下载器
        builder.downloader(new OkHttp3Downloader(httpClient));

        //配置线程池
        ExecutorService executorService = Executors.newFixedThreadPool(DeviceUtil.getCpuCoreNum()+1);
        builder.executor(executorService);

        //构造一个Picasso
        picasso = builder.build();

        // 设置全局单列instance
        Picasso.setSingletonInstance(picasso);


        return picassoUtil;
    }



    /**
     * 注册 Picassoutil类之后，获取 Picasso 对象
     * @return
     */
    public static Picasso getPicassoObject(){
        return Picasso.with(context);
    }





}
