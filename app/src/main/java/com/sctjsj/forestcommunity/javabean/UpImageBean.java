package com.sctjsj.forestcommunity.javabean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by XiaoHaoWit on 2017/8/29.
 * 上传图片的通用图片对象
 */

public class UpImageBean implements Serializable {
    private int id;
    private int flag=1;
    private Bitmap bitmap;
    private boolean del=false;//删除的按钮（默认不显示）

    public UpImageBean() {
    }

    public UpImageBean(int flag) {
        this.flag = flag;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "UpImageBean{" +
                "id=" + id +
                ", flag=" + flag +
                ", bitmap=" + bitmap +
                '}';
    }
}
