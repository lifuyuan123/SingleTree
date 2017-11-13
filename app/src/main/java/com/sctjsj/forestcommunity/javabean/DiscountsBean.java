package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by XiaoHaoWit on 2017/8/28.
 * 商家优惠（活动） 的实体
 */

public class DiscountsBean implements Serializable {
    private int id;
    private  String  disName;//优惠名称
    private  String beginTime;//活动开始时间
    private  String endTime;//活动结束时间
    private  String content;//活动介绍
    private String condition;//使用条件
    private double price;//售价
    private String storeImg;//店铺的url
    private  double showPrice;//展示的钱
    private  PerShopBean  shop;
    private  int Type;//1有效 2.过期


    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public PerShopBean getShop() {
        return shop;
    }

    public void setShop(PerShopBean shop) {
        this.shop = shop;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(double showPrice) {
        this.showPrice = showPrice;
    }

    public String getStoreImg() {
        return storeImg;
    }

    public void setStoreImg(String storeImg) {
        this.storeImg = storeImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "DiscountsBean{" +
                "disName='" + disName + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", content='" + content + '\'' +
                ", condition='" + condition + '\'' +
                ", price=" + price +
                '}';
    }
}
