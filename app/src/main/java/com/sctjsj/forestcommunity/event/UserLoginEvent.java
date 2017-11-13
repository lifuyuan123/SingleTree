package com.sctjsj.forestcommunity.event;

/**
 * Created by mayikang on 17/5/23.
 */

public class UserLoginEvent {
    //1.登录成功
    //2.登录失败
    private int status;

    public UserLoginEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
