package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by lifuy on 2017/8/14.
 */

public class IntegralBean implements Serializable {
    private String name;
    private String time;
    private int integral;
    private String iconUrl;
    private String remark;//备注支出还是收入
    private int id;
    private int type;//type=1;收入   type=2支出

    public IntegralBean(String name, String time, int integral, String iconUrl, int id, int type) {
        this.name = name;
        this.time = time;
        this.integral = integral;
        this.iconUrl = iconUrl;
        this.id = id;
        this.type = type;
    }

    public IntegralBean() {
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "IntegralBean{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", integral=" + integral +
                ", iconUrl='" + iconUrl + '\'' +
                ", remark='" + remark + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }
}
