package com.sctjsj.forestcommunity.javabean;

import java.io.Serializable;

/**
 * Created by XiaoHaoWit on 2017/8/30.
 * 评论的javabean
 */

public class CommentBean implements Serializable {

    private int type;//1评论主题  2.评论评论
    private UserBean bean;//评论者的javabean
    private UserBean parBean;//type为2的时候一级评论的jababean
    private String evalContent;//评论内容
    private String insertTime;//评论时间
    private int parentid;//父级评论的id
    private int id;//本条评论的id

    public CommentBean() {
    }

    public CommentBean(int type, UserBean bean, UserBean parBean, String evalContent, String insertTime) {
        this.type = type;
        this.bean = bean;
        this.parBean = parBean;
        this.evalContent = evalContent;
        this.insertTime = insertTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UserBean getBean() {
        return bean;
    }

    public void setBean(UserBean bean) {
        this.bean = bean;
    }

    public UserBean getParBean() {
        return parBean;
    }

    public void setParBean(UserBean parBean) {
        this.parBean = parBean;
    }

    public String getEvalContent() {
        return evalContent;
    }

    public void setEvalContent(String evalContent) {
        this.evalContent = evalContent;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommentBean{" +
                "type=" + type +
                ", bean=" + bean +
                ", parBean=" + parBean +
                ", evalContent='" + evalContent + '\'' +
                ", insertTime='" + insertTime + '\'' +
                '}';
    }
}
