package com.toby.mymaterialdemo.model.event;

/**
 * Created by Toby on 2016/11/1.
 */

public class MessageEvent {

    private int type;// 消息类型
    private Object data;// 携带的数据

    public MessageEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
