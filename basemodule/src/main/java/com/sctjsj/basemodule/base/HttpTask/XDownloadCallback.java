package com.sctjsj.basemodule.base.HttpTask;

/**
 * Created by mayikang on 17/4/7.
 */

import org.xutils.common.Callback;

import java.io.File;

/**
 * 下载文件回调
 */
public  interface XDownloadCallback {

    public void onDownloadComplete(File result);

    public void onError(Throwable ex);

    public void onCancelled(Callback.CancelledException cex);

    public void onFinished() ;

    public void onWaiting();

    public void onStarted();

    public void onDownloading(long total, long current);



}
