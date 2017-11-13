package com.sctjsj.basemodule.base;

/**
 * Created by mayikang on 17/3/22.
 */

import com.sctjsj.basemodule.BuildConfig;

import java.io.ObjectStreamException;

/**
 * 利用静态内部类实现单例，解决 DCL 失效问题
 * 但是反序列化时会时效，并且返回一个新的单例
 */
public class Singleton {
    private Singleton(){

    }

    public static Singleton getInstance(){
        return SingletonHolder.instance;

    }

    private static class SingletonHolder{
        private static final Singleton instance=new Singleton();
    }



}
