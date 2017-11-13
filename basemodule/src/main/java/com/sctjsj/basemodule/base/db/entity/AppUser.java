package com.sctjsj.basemodule.base.db.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by mayikang on 17/4/10.
 */
@Table(name = "sm_user_tbl")
public class AppUser {
    //用户表主键 id
    @Column(name = "id",isId = true,autoGen = true)
    private int id;

    //用户 id
    @Column(name = "user_id",property = "not null")
    private String userId;

    //用户登录后的 token
    @Column(name = "token",property = "unique")
    private String token;

    public AppUser(){

    }

    public AppUser(String userId){
        this.userId=userId;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
