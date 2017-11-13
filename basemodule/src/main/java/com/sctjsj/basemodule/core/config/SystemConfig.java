package com.sctjsj.basemodule.core.config;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.util.setup.ApplicationUtil;
import org.xutils.x;


/**
 * Created by mayikang on 17/4/7.
 */

/**
 * 该类必须在 application 启动时初始化（逻辑单例），用SingletonManager 来管理
 */
public class SystemConfig {

    private static SystemConfig config;

    private static Context context;

    private SystemConfig(){

    }

    /**
     * 在 Application 的 onCreate 时会注册本类(逻辑单例)，context 为全局的 application context
     * @return
     * @throws Exception
     */
    public  Context getContext() throws Exception {

        if(context==null){
            throw new Exception("@link com.sctjsj.basemodule.core.config.SystemConfig 类尚未初始化");
        }
        return context;
    }

    /**
     * 初始化 context
     * @param c
     * @return
     */
    public static SystemConfig init(Context c){
        context=c;
        if(config==null){
            config=new SystemConfig();
        }

        //初始化 ARouter
        //开启 instant run 的时候，opendebug 必须在 init 前面
        ARouter.openDebug();//开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init((Application) context.getApplicationContext());
        ARouter.openLog();
        ARouter.printStackTrace();

        //初始化 xUtils
        x.Ext.init((Application) context.getApplicationContext());

        return config;
    }

    //系统存储根路径
    private  String SYS_BASE_DIR= Environment.getExternalStorageDirectory().getPath();

    //网络请求缓存路径
    private  final String HTTP_CACHE_DIR=SYS_BASE_DIR+"/"+ ApplicationUtil.getPackageName(context)+"/http_cache";

    //文件下载保存路径
    private final String DOWNLOAD_FILE_DIR=SYS_BASE_DIR+"/"+ApplicationUtil.getPackageName(context)+"/download";

    //数据库保存路径
    private final String DB_DIR=SYS_BASE_DIR+"/"+ApplicationUtil.getPackageName(context)+"/db";

    //系统数据库名
    private final String DB_NAME=ApplicationUtil.getAppName(context)+".db";

    public String getDB_DIR() {

        return DB_DIR;
    }

    public String getDB_NAME() {
        return DB_NAME;
    }

    public String getDOWNLOAD_FILE_DIR() {
        return DOWNLOAD_FILE_DIR;
    }

    public String getHTTP_CACHE_DIR() {
        return HTTP_CACHE_DIR;
    }

    public String getSYS_BASE_DIR() {
        return SYS_BASE_DIR;
    }
}
