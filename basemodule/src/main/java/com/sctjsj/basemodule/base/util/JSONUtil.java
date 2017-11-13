package com.sctjsj.basemodule.base.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mayikang on 17/5/5.
 */

public class JSONUtil {

    public static Map<String, String> toMap(JSONObject jsonObject)
    {
        Map<String, String> result = new HashMap<String, String>();
        Iterator<String> iterator = jsonObject.keys();
        String key = null;
        String value = null;
        while (iterator.hasNext())
        {
            key = iterator.next();
            try {
                value = jsonObject.getString(key);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            result.put(key, value);
        }
        return result;
    }

}
