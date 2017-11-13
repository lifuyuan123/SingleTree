package com.sctjsj.basemodule.core.http;

import android.util.Log;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.basemodule.core.cookie.CookieUtil;

import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mayikang on 17/5/3.
 */

/**
 * 自定义基础响应转换类，处理响应头相关操作
 */
public class NormalResponseParser implements ResponseParser {
    private String TAG="@link com.sctjsj.basemodule.core.http.NormalResponseParser";

    @Override
    public void checkResponse(UriRequest request) throws Throwable {

        if(request!=null){
            //将返回的 token 保存到本地
            if(request.getResponseHeaders().containsKey("TOKEN")){
                    String TOKEN=request.getResponseHeader("TOKEN");
                    LogUtil.e(TAG,"parser-token:"+TOKEN);
                    SPFUtil.put(Tag.TAG_TOKEN,TOKEN);
                }
            }
    }

    /**
     * 对响应结果做类型转换，比如转 json
     * 或者对特定返回结果进行过滤
     * @param resultType
     * @param resultClass
     * @param result
     * @return
     * @throws Throwable
     */
    @Override
    public Object parse(Type resultType, Class<?> resultClass, String result) throws Throwable {

        if (resultClass == List.class) {
            // 这里只是个示例, 不做json转换.
            List<NormalResponse> list = new ArrayList<NormalResponse>();
            NormalResponse response = new NormalResponse();
            response.setResult(result);
            list.add(response);
            return list;
            // fastJson 解析:
            // return JSON.parseArray(result, (Class<?>) ParameterizedTypeUtil.getParameterizedType(resultType, List.class, 0));
        } else {
            // 这里只是个示例, 不做json转换.
            NormalResponse response = new NormalResponse();
            response.setResult(result);
            return response;
            // fastjson 解析:
            // return JSON.parseObject(result, resultClass);
        }

    }
}
