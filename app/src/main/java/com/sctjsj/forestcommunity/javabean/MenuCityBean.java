package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by XiaoHaoWit on 2017/8/17.
 * dropDownMenu 的城市选项的javabean
 */

public class MenuCityBean implements Serializable{
    private String cyitName;
    private boolean isCheck=false;

    public MenuCityBean() {
    }

    public MenuCityBean(String cyitName, boolean isCheck) {
        this.cyitName = cyitName;
        this.isCheck = isCheck;
    }

    public String getCyitName() {
        return cyitName;
    }

    public void setCyitName(String cyitName) {
        this.cyitName = cyitName;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "MenuCityBean{" +
                "cyitName='" + cyitName + '\'' +
                ", isCheck=" + isCheck +
                '}';
    }
}
