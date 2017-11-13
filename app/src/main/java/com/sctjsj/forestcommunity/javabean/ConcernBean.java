package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by lifuy on 2017/8/15.
 */

public class ConcernBean implements Serializable {
    private String name;
    private int id;
    private String content;
    private String iconUrl;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ConcernBean{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", content='" + content + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
