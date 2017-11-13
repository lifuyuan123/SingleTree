package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by lifuy on 2017/8/16.
 */

public class BrandBean implements Serializable {

    private int id;
    private String iconUrl;
    private String name;
    private String phone;
    private double latitude;
    private double longitude;
    private int storeStatus;//店铺状态，1待审核，2正常，3关闭
    private int type;//类型:1美食店铺 2 装修建材
    private int areaId;//区域id
    private String adress;
    private int isRecommend;//是否推荐店铺:1-是 2-否
    private String describe;//描述
    private String label;//标签

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public int getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(int storeStatus) {
        this.storeStatus = storeStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(int isRecommend) {
        this.isRecommend = isRecommend;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    @Override
    public String toString() {
        return "BrandBean{" +
                "id=" + id +
                ", iconUrl='" + iconUrl + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", storeStatus=" + storeStatus +
                ", type=" + type +
                ", areaId=" + areaId +
                ", adress='" + adress + '\'' +
                ", isRecommend=" + isRecommend +
                ", describe='" + describe + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
