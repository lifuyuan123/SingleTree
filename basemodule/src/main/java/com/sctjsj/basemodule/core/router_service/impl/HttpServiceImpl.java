package com.sctjsj.basemodule.core.router_service.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.android.arouter.utils.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.sctjsj.basemodule.base.HttpTask.XCacheCallback;
import com.sctjsj.basemodule.base.HttpTask.XDownloadCallback;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.SingletonManager;
import com.sctjsj.basemodule.base.db.entity.HttpCacheTbl;
import com.sctjsj.basemodule.base.util.FileUtil;
import com.sctjsj.basemodule.base.util.JSONUtil;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.TimeUtil;
import com.sctjsj.basemodule.base.util.network.NetUtil;
import com.sctjsj.basemodule.base.util.setup.DeviceUtil;
import com.sctjsj.basemodule.core.config.SystemConfig;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.config.TargetPage;
import com.sctjsj.basemodule.core.cookie.CookieUtil;
import com.sctjsj.basemodule.core.http.NormalResponse;
import com.sctjsj.basemodule.core.router_service.IHttpService;

import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.http.app.RequestInterceptListener;
import org.xutils.http.app.RequestTracker;
import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;
import org.xutils.x;

import java.io.File;
import java.security.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mayikang on 17/4/7.
 */
/**
 * 基于 xutils3封装的网络请求 service
 */

@Route(path = "/basemodule/service/http")

public class HttpServiceImpl implements IHttpService {
    private final String TAG=getClass().getSimpleName().toString();
    private CacheServiceImpl cacheService;
    private SystemConfig config;

    /**
     * 一般 post 请求，不带缓存
     * @param headers 请求头
     * @param url 请求 url
     * @param pParameters post 参数
     * @param callback 带有加载进度的回调
     */
    @Override
    public void doCommonPost(@Nullable Map<String,String> headers, @NonNull String url, Map<String, String> pParameters, final XProgressCallback callback) {
        final RequestParams requestParams=new RequestParams(url);
        //默认自带 cookie
        requestParams.setUseCookie(true);

        //最大重连次数
        requestParams.setMaxRetryCount(3);
        //连接超时
        requestParams.setConnectTimeout(5000);
        /**
         * 迭代请求头 headers
         */
        if(headers!=null && headers.size()>0){
            Set<String> hSet=headers.keySet();
            Iterator<String> hIterator=hSet.iterator();
            while (hIterator.hasNext()){
                String hKey=hIterator.next();
                requestParams.setHeader(hKey,headers.get(hKey));
            }
        }

        /**
         * 添加默认的请求头
         * 设备唯一识别号
         */
        try {
            requestParams.setHeader("DEV-ID", "ANDROID_"+DeviceUtil.getDeviceId(config.getContext()));
            requestParams.setHeader("TOKEN",SPFUtil.get(Tag.TAG_TOKEN,"default").toString());
            requestParams.setHeader("MODEL",DeviceUtil.getPhoneModel());
            requestParams.setHeader("OS_VERSION",DeviceUtil.getPhoneOSVersion());


        } catch (Exception e) {
            e.printStackTrace();

        }

        /**
         * 迭代 Post 参数
         */
        if(pParameters!=null && pParameters.size()>0){
            Set<String> set=pParameters.keySet();
            Iterator<String> iterator=set.iterator();
            while (iterator.hasNext()){
                String key=iterator.next();
                requestParams.addBodyParameter(key,pParameters.get(key));
            }
        }

        Callback.Cancelable cancelable = x.http().post(requestParams, new Callback.ProgressCallback<NormalResponse>() {
            @Override
            public void onSuccess(NormalResponse result) {

                if(result!=null){
                    try {
                        org.json.JSONObject obj=new org.json.JSONObject(result.toString());
                        Map<String,String> map= JSONUtil.toMap(obj);
                        /*if(map.containsKey("status")){
                            String status=obj.getString("status");
                            if("AUTH-FAILURE".equals(status)){
                                ARouter.getInstance().build(TargetPage.PAGE_AFTER_LOGIN_INTERCEPTED).navigation();
                            }
                        }else {
                            callback.onSuccess(result.toString());

                        }*/

                        callback.onSuccess(result.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onError(ex);

            }

            @Override
            public void onCancelled(CancelledException cex) {
                callback.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                callback.onFinished();
            }

            @Override
            public void onWaiting() {
                callback.onWaiting();
            }

            @Override
            public void onStarted() {
                callback.onStarted();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                callback.onLoading(total,current);
            }
        });

    }

    /**
     * 带缓存 post 请求
     * @param headers 请求头
     * @param url 请求 url
     * @param pParameters post 参数
     * @param callback 带有加载进度的回调
     *                 通过 DB 模块自己缓存
     */
    @Override
    public void  doCommonPostWithCache(@Nullable Map<String, String> headers,
                                       @NonNull String url,
                                       Map<String, String> pParameters,
                                       final long maxAge,
                                       final XCacheCallback callback) {
        //获取缓存服务
        cacheService= (CacheServiceImpl) ARouter.getInstance().build("/basemodule/service/cache").navigation();
        //是否使用缓存的标志 false：不使用 true：使用，默认不使用
        boolean useCacheFlag=false;
        //拼接参数
        final RequestParams requestParams=new RequestParams(url);
        //默认自带 cookie
        requestParams.setUseCookie(true);
        //最大重连次数
        requestParams.setMaxRetryCount(3);
        //连接超时
        requestParams.setConnectTimeout(1000*5);
        requestParams.setMethod(HttpMethod.POST);
        //请求追踪


        /**
         * 迭代请求头 headers
         */
        if(headers!=null && headers.size()>0){
            Set<String> hSet=headers.keySet();
            Iterator<String> hIterator=hSet.iterator();
            while (hIterator.hasNext()){
                String hKey=hIterator.next();
                requestParams.setHeader(hKey,headers.get(hKey));
            }
        }
        /**
         * 添加默认的请求头
         * 设备唯一识别号
         * 设备型号
         * 操作系统
         */
        try {
            requestParams.setHeader("DEV-ID", "ANDROID_"+DeviceUtil.getDeviceId(config.getContext()));
            requestParams.setHeader("TOKEN",SPFUtil.get(Tag.TAG_TOKEN,"default").toString());
            requestParams.setHeader("MODEL",DeviceUtil.getPhoneModel());
            requestParams.setHeader("OS_VERSION",DeviceUtil.getPhoneOSVersion());

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 迭代 Post 参数
         */
        if(pParameters!=null && pParameters.size()>0){
            Set<String> set=pParameters.keySet();
            Iterator<String> iterator=set.iterator();
            while (iterator.hasNext()){
                String key=iterator.next();
                requestParams.addBodyParameter(key,pParameters.get(key));
            }
        }

        /**
         * 先判断本地有无缓存
         */
        //缓存为空或者缓存过期，不能使用
        final HttpCacheTbl cacheTbl=cacheService.getCacheContentByUrl(getFullUrl(url,requestParams));

        try {
            //无网络时，只要有缓存，不考虑过期时间
            if(!NetUtil.isConnected(config.getContext())){
                 if(cacheTbl!= null && cacheTbl.getContent()!=null){
                     useCacheFlag=true;
                 }
            }else {

                //有网络时要考虑过期时间
                if(cacheTbl== null || cacheTbl.getContent()==null
                        || cacheTbl.getContent().isEmpty()
                        || !TimeUtil.isAvailable(cacheTbl.getUpdateTime(),maxAge,TimeUtil.getTimestamp())){
                    useCacheFlag=false;
                }else {
                    useCacheFlag=true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 返回缓存的内容
         */
        if(useCacheFlag){

            String content=cacheTbl.getContent();
            if(content!=null){
                LogUtil.e(TAG,"使用了缓存");
                callback.onSuccess(content);
            }

        }else {
            LogUtil.e(TAG,"不使用缓存");

            Callback.Cancelable cancelable = x.http().post(requestParams, new Callback.ProgressCallback<NormalResponse>() {
                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {
                    callback.onStart();
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    callback.onLoading(total,current);
                }

                /**
                 * 请求成功后，缓存数据
                 * @param result
                 */
                @Override
                public void onSuccess(NormalResponse result) {

                    //处理认证失败的情况
                    if(result!=null){
                        try {
                            org.json.JSONObject obj=new org.json.JSONObject(result.toString());
                            Map<String,String> map= JSONUtil.toMap(obj);
                            if(map.containsKey("status")){
                                String status=obj.getString("status");
                                if("AUTH-FAILURE".equals(status)){
                                    ARouter.getInstance().build(TargetPage.PAGE_AFTER_LOGIN_INTERCEPTED).navigation();
                                }
                            }else {
                                HttpCacheTbl cache=new HttpCacheTbl();
                                //缓存 url
                                cache.setUrl(getFullUrl(requestParams.getUri().toString(),requestParams));
                                //url 请求结果
                                cache.setContent(result.toString());
                                //缓存插入时间
                                cache.setInsertTime(TimeUtil.getTimestamp());
                                //更新时间
                                cache.setUpdateTime(TimeUtil.getTimestamp());
                                //缓存最大生命周期
                                cache.setMaxAge(maxAge);
                                cacheService.saveCache(cache);
                                callback.onSuccess(result.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    callback.onError(ex);

                }

                @Override
                public void onCancelled(CancelledException cex) {
                    callback.onCancelled(cex);
                }

                @Override
                public void onFinished() {
                    callback.onFinished();
                }

            });

        }

    }

    /**
     * 上传单个文件
     * @param headers
     * @param url
     * @param file
     * @param pParameters
     * @param callback
     */
    @Override
    public void uploadFile(@Nullable Map<String,String> headers,
                           @NonNull String url,
                           @NonNull File file,
                           @NonNull Map<String,String> pParameters,
                           @NonNull final XProgressCallback callback) {

        RequestParams requestParams=new RequestParams(url);
        /**
         * 添加默认的请求头
         * 设备唯一识别号
         * 设备型号
         * 操作系统
         */
        try {
            requestParams.setHeader("DEV-ID", "ANDROID_"+DeviceUtil.getDeviceId(config.getContext()));
            requestParams.setHeader("TOKEN",SPFUtil.get(Tag.TAG_TOKEN,"none-token").toString());
            requestParams.setHeader("MODEL",DeviceUtil.getPhoneModel());
            requestParams.setHeader("OS_VERSION",DeviceUtil.getPhoneOSVersion());

        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认自带 cookie
        requestParams.setUseCookie(true);
        //最大重连次数
        requestParams.setMaxRetryCount(3);
        //连接超时
        requestParams.setConnectTimeout(1000*5);
        //多表单上传
        requestParams.setMultipart(true);

        if(file!=null){
            requestParams.addBodyParameter("file",file);
        }else {

            try {
                Toast.makeText(config.getContext(), "上传文件为空", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**
         * 迭代请求头 headers
         */
        if(headers!=null && headers.size()>0){
            Set<String> hSet=headers.keySet();
            Iterator<String> hIterator=hSet.iterator();
            while (hIterator.hasNext()){
                String hKey=hIterator.next();
                //用 setHeader 比 addHeader 好，多了一步重名请求头校验
                requestParams.setHeader(hKey,headers.get(hKey));

            }
        }

        /**
         * 迭代 Post 参数
         */
        if(pParameters!=null && pParameters.size()>0){
            Set<String> set=pParameters.keySet();
            Iterator<String> iterator=set.iterator();
            while (iterator.hasNext()){
                String key=iterator.next();
                requestParams.addBodyParameter(key,pParameters.get(key));
            }
        }

        Callback.Cancelable cancelable = x.http().post(requestParams, new Callback.ProgressCallback<NormalResponse>() {
            @Override
            public void onSuccess(NormalResponse result) {
                //处理认证失败的情况
                if(result!=null){
                    try {
                        org.json.JSONObject obj=new org.json.JSONObject(result.toString());
                        Map<String,String> map= JSONUtil.toMap(obj);
                        if(map.containsKey("status")){
                            String status=obj.getString("status");
                            if("AUTH-FAILURE".equals(status)){
                                ARouter.getInstance().build(TargetPage.PAGE_AFTER_LOGIN_INTERCEPTED).navigation();
                            }
                        }else {
                            callback.onSuccess(result.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                callback.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                callback.onFinished();
            }

            @Override
            public void onWaiting() {
                callback.onWaiting();
            }

            @Override
            public void onStarted() {
                callback.onStarted();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                callback.onLoading(total,current);
            }
        });


    }


    @Override
    public void downloadFile(@Nullable Map<String, String> headers, @NonNull String url, @NonNull Map<String, String> pParams,  @NonNull String fileSaveName, @NonNull final XDownloadCallback callback) {

        RequestParams requestParams=new RequestParams(url);

        //设置文件名
        if(fileSaveName==null ||TextUtils.isEmpty(fileSaveName)){
            fileSaveName=String.valueOf(TimeUtil.getTimestamp());
        }
        String realExtention= FileUtil.getFileExtensionFromUrl(url);

        //设置文件类型
        if(realExtention==null || TextUtils.isEmpty(realExtention)){
            realExtention="tmp";
        }

        //设置下载保存路径
        requestParams.setSaveFilePath(config.getDOWNLOAD_FILE_DIR()+"/"+ TimeUtil.getTimestamp()+"."+realExtention);

        //设置自动断点续传
        requestParams.setAutoResume(true);

        //连接超时
        requestParams.setConnectTimeout(5*1000);
        //文件自动重命名
        requestParams.setAutoRename(false);


        /**
         * 迭代请求头 headers
         */
        if(headers!=null && headers.size()>0){
            Set<String> hSet=headers.keySet();
            Iterator<String> hIterator=hSet.iterator();
            while (hIterator.hasNext()){
                String hKey=hIterator.next();
                requestParams.setHeader(hKey,headers.get(hKey));
            }
        }
        /**
         * 添加默认的请求头
         * 设备唯一识别号
         * 设备型号
         * 操作系统
         */
        try {
            requestParams.setHeader("DEV-ID", "ANDROID_"+DeviceUtil.getDeviceId(config.getContext()));
            requestParams.setHeader("TOKEN",SPFUtil.get(Tag.TAG_TOKEN,"none-token").toString());
            requestParams.setHeader("MODEL",DeviceUtil.getPhoneModel());
            requestParams.setHeader("OS_VERSION",DeviceUtil.getPhoneOSVersion());

        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * 迭代 Post 参数
         */
        if(pParams!=null && pParams.size()>0){
            Set<String> set=pParams.keySet();
            Iterator<String> iterator=set.iterator();
            while (iterator.hasNext()){
                String key=iterator.next();
                requestParams.addBodyParameter(key,pParams.get(key));
            }
        }


        Callback.Cancelable cancelable =x.http().post(requestParams, new FileCallback<File> (){
            @Override
            public void beforeRequest(UriRequest request) throws Throwable {

            }

            @Override
            public void afterRequest(UriRequest request) throws Throwable {

                if(request!=null){
                    //将返回的 token 保存到本地
                    if(request.getResponseHeaders().containsKey("TOKEN")){
                        String TOKEN=request.getResponseHeader("TOKEN");
                        SPFUtil.put(Tag.TAG_TOKEN,TOKEN);
                    }
                }
            }

            @Override
            public void onSuccess(File result) {
                    callback.onDownloadComplete(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                callback.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                callback.onFinished();

            }

            @Override
            public void onWaiting() {
                callback.onWaiting();
            }

            @Override
            public void onStarted() {
                callback.onStarted();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                callback.onDownloading(total,current);
            }
        });

    }



    @Override
    public void init(Context context) {
        Object obj= SingletonManager.INSTANCE.getObj(Tag.TAG_SINGLETON_SYSTEM_CONFIG_HOLDER);
        if(obj instanceof SystemConfig){
            config=(SystemConfig) obj;
        }

    }

    /**
     * 检查指定路径的文件夹是否存在，不存在就创建
     * @param path
     */
    private void checkFileFolderPath(String path){
        if(path==null){
            return;
        }
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /**
     * 根据主连接和请求参数体，拼接完整连接
     * @param mainUrl
     * @param params
     * @return
     */
    private String getFullUrl(String mainUrl,RequestParams params){
        String full="";
        //如果主连接以？结尾，去掉
         if(mainUrl.endsWith("?")){
             int index=mainUrl.lastIndexOf("?");
             mainUrl=mainUrl.replace(mainUrl.substring(index,index+1),"");
         }

        //处理参数体
        List<KeyValue> list=params.getBodyParams();
        String extra="";
        for (int i=0;i<list.size();i++){
            String key=list.get(i).key;
            String value=list.get(i).getValueStr();
            extra=extra+"&"+key+"="+value;
        }
        extra=extra.replaceFirst("&","");

        //拼接完整连接
        full=mainUrl+"?"+extra;

        return full;
    }

    /**
     * 文件下载时，泛型回调接口
     */
    public interface FileCallback<File> extends Callback.ProgressCallback<File>, RequestInterceptListener {

    }


}
