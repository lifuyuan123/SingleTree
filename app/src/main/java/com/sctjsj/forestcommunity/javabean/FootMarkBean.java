package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by lifuy on 2017/8/14.
 */

public class FootMarkBean implements Serializable {

    private String name;
    private String time;
    private int id; //这一条的id
    private String iconUrl;
    private boolean isCheck;
    private int tid;//item类型id  通过此id查询详情
    private int type;//1周边商家 2新闻资讯 3 帖子

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "FootMarkBean{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", id=" + id +
                ", iconUrl='" + iconUrl + '\'' +
                ", isCheck=" + isCheck +
                ", tid=" + tid +
                ", type=" + type +
                '}';
    }
}
