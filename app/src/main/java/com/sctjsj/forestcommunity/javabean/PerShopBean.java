package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by XiaoHaoWit on 2017/8/25.
 * 周边商家的实体
 */

public class PerShopBean implements Serializable{
    private int id;
    private String storeName;
    private  String describe;//商家介绍
    private  int isRecommend;//是否推荐店铺:1-是 2-否
    private  double latitude;//纬度
    private  double longitude;//经度
    private  String storeAddress;//地址
    private  String telephone;//电话
    private String storeLogo;
    private  String lable;//店铺类型
    private String areaName;//地区
    private ArrayList<HashMap<String,Object>> disList;//活动列表


    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public ArrayList<HashMap<String, Object>> getDisList() {
        return disList;
    }

    public void setDisList(ArrayList<HashMap<String, Object>> disList) {
        this.disList = disList;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(int isRecommend) {
        this.isRecommend = isRecommend;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "PerShopBean{" +
                "id=" + id +
                ", storeName='" + storeName + '\'' +
                ", describe='" + describe + '\'' +
                ", isRecommend=" + isRecommend +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", storeAddress='" + storeAddress + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
