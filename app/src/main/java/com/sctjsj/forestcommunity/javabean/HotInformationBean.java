package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by lifuy on 2017/8/17.
 */

public class HotInformationBean implements Serializable {
    private String title;
    private int type;  //type=1 只有一张图片   type=2 三张图片
    private int id;
    private List<String> iconUrlList;
    private int count;
    private String url;
    private String keyWord;
    private String time;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getIconUrlList() {
        return iconUrlList;
    }

    public void setIconUrlList(List<String> iconUrlList) {
        this.iconUrlList = iconUrlList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "HotInformationBean{" +
                "title='" + title + '\'' +
                ", type=" + type +
                ", id=" + id +
                ", iconUrlList=" + iconUrlList +
                ", count=" + count +
                ", url='" + url + '\'' +
                ", keyWord='" + keyWord + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
