package com.sctjsj.basemodule.base.util.setup;

/**
 * Created by Chris-Jason on 2016/10/4.
 */

/**
 * App 信息实体类
 */
public class AppInfo {

    public String appName="";
    public String packageName="";
    public String versionName="";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
