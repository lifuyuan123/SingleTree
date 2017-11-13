package com.sctjsj.basemodule.core.event;


import java.io.Serializable;

/**
 * Created by mayikang on 17/6/7.
 */

public class PushEvent {
    private PushBean pb;

    public PushEvent() {

    }

    public PushBean getPb() {
        return pb;
    }

    public void setPb(PushBean pb) {
        this.pb = pb;
    }
}
