package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by lifuy on 2017/8/11.
 */

public class AdressBean implements Serializable {
    private String name;
    private String phone;
    //地区
    private String adressRegion;
    private String province;//省
    private String city;//市
    private String area;//区
    private int areaId;//区域id，用于修改和新增时上传
    //地址
    private String adress;
    private int id;
    private boolean isDefault;

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAdressRegion() {
        return adressRegion;
    }

    public void setAdressRegion(String adressRegion) {
        this.adressRegion = adressRegion;
    }

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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "AdressBean{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", adressRegion='" + adressRegion + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", areaId=" + areaId +
                ", adress='" + adress + '\'' +
                ", id=" + id +
                ", isDefault=" + isDefault +
                '}';
    }
}
