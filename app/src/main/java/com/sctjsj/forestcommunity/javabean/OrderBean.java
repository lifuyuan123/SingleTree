package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by XiaoHaoWit on 2017/9/6.
 */

public class OrderBean implements Serializable {

    private int id;
    private double payValue; //支付金额
    private String insertTime;//插入时间
    private int state;//订单状态  订单状态：1失效2待支付3完成
    private PerShopBean shop;//商店实体




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPayValue() {
        return payValue;
    }

    public void setPayValue(double payValue) {
        this.payValue = payValue;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public PerShopBean getShop() {
        return shop;
    }

    public void setShop(PerShopBean shop) {
        this.shop = shop;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "id=" + id +
                ", payValue=" + payValue +
                ", insertTime='" + insertTime + '\'' +
                ", state=" + state +
                ", shop=" + shop +
                '}';
    }
}
