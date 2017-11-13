package com.sctjsj.basemodule.core.event;

import java.io.Serializable;

/**
 * Created by mayikang on 17/6/7.
 */

public class PushBean implements Serializable{
    //1:系统级推送  2：普通业务推送

    //如果type = 1
    //state 1 强制下线 2 强制跟新

    //如果type = 2
    //state 1 用户推商家 2 商家推用户 3 用户推用户 4 平台推送到用户
    private int type;
    private int state;
    private String content;
    private String dev;

    public PushBean() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }
}
