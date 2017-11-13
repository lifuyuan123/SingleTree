package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by lifuy on 2017/8/25.
 */

public class RentalCenter implements Serializable {

    private int id;
    private String title;
    private String adress;
    private String area;//区域
    private String url;
    private String monthly;//月租
    private String houseType;//户型,2室一厅
    private String acreage;//面积
    private String Orientation;//朝向
    private int type;
    private int issell;//1在售 2在租 3 其他

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;}

    public int getIssell() {
        return issell;
    }

    public void setIssell(int issell) {
        this.issell = issell;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMonthly() {
        return monthly;
    }

    public void setMonthly(String monthly) {
        this.monthly = monthly;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getAcreage() {
        return acreage;
    }

    public void setAcreage(String acreage) {
        this.acreage = acreage;
    }

    public String getOrientation() {
        return Orientation;
    }

    public void setOrientation(String orientation) {
        Orientation = orientation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RentalCenter{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", adress='" + adress + '\'' +
                ", area='" + area + '\'' +
                ", url='" + url + '\'' +
                ", monthly='" + monthly + '\'' +
                ", houseType='" + houseType + '\'' +
                ", acreage='" + acreage + '\'' +
                ", Orientation='" + Orientation + '\'' +
                ", type=" + type +
                ", issell=" + issell +
                '}';
    }
}
