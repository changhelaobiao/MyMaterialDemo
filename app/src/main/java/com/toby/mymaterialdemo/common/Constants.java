package com.toby.mymaterialdemo.common;

/**
 * Created by Toby on 2016/10/29.
 */

public class Constants {

    public static final boolean DEBUG_MODE = true;// 用于区分是否是测试环境(主要用于日志打印)

    public static final int REQUEST_CODE_REGISTER = 100;
    public static final int RESULT_CODE_REGISTER = 101;

    public static final int REQUEST_CODE_SETTINGS = 200;
    public static final int RESULT_CODE_LOGOUT = 201;

    /* 新闻*/
    public static final String NEWS_ID = "news_id";
    public static final String NEWS_TYPE = "news_type";
    public static final String CHANNEL_POSITION = "channel_position";
    public static String NEWS_POST_ID = "NEWS_POST_ID";//新闻详情id
    public static String NEWS_LINK = "NEWS_LINK";
    public static String NEWS_TITLE = "NEWS_TITLE";
    public static final String NEWS_IMG_RES = "news_img_res";
    public static final String TRANSITION_ANIMATION_NEWS_PHOTOS = "transition_animation_news_photos";

    public static final String PHOTO_DETAIL_IMGSRC = "photo_detail_imgsrc";
    public static final String PHOTO_DETAIL = "photo_detail";
    public static final String PHOTO_TAB_CLICK = "PHOTO_TAB_CLICK";
}
