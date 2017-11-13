package com.sctjsj.basemodule.core.router_service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.sctjsj.basemodule.base.HttpTask.XCacheCallback;
import com.sctjsj.basemodule.base.HttpTask.XDownloadCallback;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;

import org.xutils.common.Callback;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by mayikang on 17/4/7.
 */

public interface IHttpService extends IProvider {
    /**
     * 执行普通的 post 请求，不带缓存
     * @param headers 请求头
     * @param url 请求 url
     * @param pParameters post 参数
     * @param callback 带有加载进度的回调
     * @return
     */
    public void doCommonPost(@Nullable Map<String,String> headers,
                                               @NonNull String url,
                                               Map<String,String> pParameters,
                                               XProgressCallback callback);


    /**
     * 执行普通的 post 请求，携带缓存
     * @param headers 请求头
     * @param url 请求 url
     * @param pParameters post 参数
     * @param maxAge 缓存最大有效时间
     * @param callback 带有加载进度的回调
     *

     * @return
     */

    public void doCommonPostWithCache(@Nullable Map<String,String> headers,
                                               @NonNull String url,
                                               Map<String,String> pParameters,
                                               long maxAge,
                                               XCacheCallback callback);

    /**
     * 单上传文件
     * @param headers
     * @param url
     * @param map
     * @param callback
     */
    public void uploadFile(@Nullable Map<String,String> headers,
                           @NonNull String url,
                           @NonNull  File file,
                           @NonNull Map<String,String> map,
                           @NonNull XProgressCallback callback);

    /**
     * 下载文件
     * @param headers 请求头
     * @param url 请求连接
     * @param map 请求参数
     * @param fileSaveName 保存的文件名
     * @param callback
     */
    public void downloadFile(@Nullable Map<String,String> headers,
                           @NonNull String url,
                           @NonNull Map<String,String> map,
                             @NonNull String fileSaveName,
                           @NonNull XDownloadCallback callback);


//    /**
//     * https post 请求
//     * @param headers
//     * @param url
//     * @param pParameters
//     * @param callback
//     */
//    public void doSSLPost(@Nullable Map<String,String> headers,
//                          @NonNull String url,
//                          Map<String,String> pParameters,
//                          XProgressCallback callback);


}
