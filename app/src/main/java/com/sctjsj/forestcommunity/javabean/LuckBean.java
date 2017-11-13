package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by lifuy on 2017/8/15.
 */

public class LuckBean implements Serializable {
    private int id;
    private String userName;
    private String prize;
    private String time;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "LuckBean{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", prize='" + prize + '\'' +
                ", time='" + time + '\'' +
                ", count=" + count +
                '}';
    }
}
