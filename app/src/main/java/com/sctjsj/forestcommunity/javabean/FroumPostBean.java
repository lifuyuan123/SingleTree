package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by XiaoHaoWit on 2017/8/30.
 * 社区帖子的javabean
 */

public class FroumPostBean implements Serializable {
    private int id;
    private UserBean bean;//发帖的用户
    private String tags;//帖子标签
    private String insertTime;//发帖时间
    private String content;//内容
    private List<String> imgList;//图片集合
    private List<CommentBean> commList;//评论的集合
    private String address;

    public FroumPostBean() {
    }

    public FroumPostBean(int id, UserBean bean, String tags, String insertTime, String content, List<String> imgList) {
        this.id = id;
        this.bean = bean;
        this.tags = tags;
        this.insertTime = insertTime;
        this.content = content;
        this.imgList = imgList;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<CommentBean> getCommList() {
        return commList;
    }

    public void setCommList(List<CommentBean> commList) {
        this.commList = commList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserBean getBean() {
        return bean;
    }

    public void setBean(UserBean bean) {
        this.bean = bean;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    @Override
    public String toString() {
        return "FroumPostBean{" +
                "id=" + id +
                ", bean=" + bean +
                ", tags='" + tags + '\'' +
                ", insertTime='" + insertTime + '\'' +
                ", content='" + content + '\'' +
                ", imgList=" + imgList +
                '}';
    }
}
