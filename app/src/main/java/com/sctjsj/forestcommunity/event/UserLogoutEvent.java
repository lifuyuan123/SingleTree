package com.sctjsj.forestcommunity.event;

/**
 * Created by mayikang on 17/5/23.
 */

public class UserLogoutEvent {
    //1.登出成功
    //2.登出失败
    private int status;

    public UserLogoutEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
