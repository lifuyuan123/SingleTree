package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by lifuy on 2017/8/15.
 */

public class CardBean implements Serializable {

    private int type;//2.失效，1.未失效
    private int id;
    private double price;
    private double conditionPrice;//满多少可用

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getConditionPrice() {
        return conditionPrice;
    }

    public void setConditionPrice(double conditionPrice) {
        this.conditionPrice = conditionPrice;
    }

    @Override
    public String toString() {
        return "CardBean{" +
                "type=" + type +
                ", id=" + id +
                ", price=" + price +
                ", conditionPrice=" + conditionPrice +
                '}';
    }
}
