package com.toby.mymaterialdemo.model;

import java.util.List;

/**
 * Created by Toby on 2016/11/7.
 */

public class PictureAndWordsEntity {

    private String title;
    private String type;
    private String date;
    private String des;
    private List<PictureAndWordsDetailImg> imgList;
    private List<PictureAndWordsDetailDes> detailDes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public List<PictureAndWordsDetailImg> getImgList() {
        return imgList;
    }

    public void setImgList(List<PictureAndWordsDetailImg> imgList) {
        this.imgList = imgList;
    }

    public List<PictureAndWordsDetailDes> getDetailDes() {
        return detailDes;
    }

    public void setDetailDes(List<PictureAndWordsDetailDes> detailDes) {
        this.detailDes = detailDes;
    }
}
