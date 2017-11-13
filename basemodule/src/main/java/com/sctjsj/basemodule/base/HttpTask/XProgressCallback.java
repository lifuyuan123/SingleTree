package com.sctjsj.basemodule.base.HttpTask;

import org.xutils.common.Callback;

/**
 * Created by mayikang on 17/4/7.
 */

/**
 * 代理回调
 */
public interface XProgressCallback {

    public void onSuccess(String result);

    public void onError(Throwable ex);

    public void onCancelled(Callback.CancelledException cex);

    public void onFinished() ;

    public void onWaiting();

    public void onStarted();

    public void onLoading(long total, long current);

}
