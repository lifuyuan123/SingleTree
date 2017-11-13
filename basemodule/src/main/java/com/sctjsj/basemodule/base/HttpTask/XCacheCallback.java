package com.sctjsj.basemodule.base.HttpTask;

import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.network.NetUtil;
import com.sctjsj.basemodule.core.config.SystemConfig;

import org.xutils.common.Callback;

/**
 * Created by mayikang on 17/4/7.
 */

public interface XCacheCallback {

    public void onSuccess(String result);

    public void onError(Throwable ex);

    public void onCancelled(Callback.CancelledException cex);

    public void onFinished();

    public boolean onCache(Object result);

    public void onStart();

    public void onLoading(long total,long current);

}
