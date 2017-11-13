package com.sctjsj.basemodule.core.http;

/**
 * Created by mayikang on 17/5/3.
 */

import org.xutils.http.annotation.HttpResponse;

/**
 * 自定义基础响应类(文件下载除外)
 */
@HttpResponse(parser = NormalResponseParser.class)
public class NormalResponse {
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
       return result;
    }
}

