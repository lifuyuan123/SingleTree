package com.sctjsj.basemodule.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mayikang on 17/3/22.
 */

/**
 * 单例对象管理器
 * 在程序初始化时，初始化所有需要的单例对象
 */
public enum  SingletonManager {
    INSTANCE;
    private Map<String,Object> objectMap=new HashMap<>();

    /**
     * 注入单例对象
     * @param key
     * @param obj
     */
    public void registerObj(String key,Object obj){
        if(!objectMap.containsKey(key)){
            objectMap.put(key,obj);
        }

    }

    /**
     * 获取单例对象
     * @param key
     * @return
     */
    public Object getObj(String key){
        return objectMap.get(key);
    }

}
