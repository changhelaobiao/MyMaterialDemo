package com.toby.mymaterialdemo.model;

import java.io.Serializable;

/**
 * Created by Toby on 2016/9/21.
 */
public class MainData implements Serializable{

    private String title;// 文字
    private String imageUrl;// 图片地址

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
