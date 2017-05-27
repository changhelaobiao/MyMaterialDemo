package com.toby.mymaterialdemo.common;

/**
 * Created by Toby on 2016/11/9.
 */

public class ApiConstants {

    // 头条id
    public static final String HEADLINE_ID = "T1348647909107";
    // 房产id
    public static final String HOUSE_ID = "5YyX5Lqs";

    // 头条TYPE
    public static final String HEADLINE_TYPE = "headline";
    // 房产TYPE
    public static final String HOUSE_TYPE = "house";
    // 其他TYPE
    public static final String OTHER_TYPE = "list";


    /**
     * 新闻id获取类型
     *
     * @param id 新闻id
     * @return 新闻类型
     */
    public static String getType(String id) {
        switch (id) {
            case HEADLINE_ID:
                return HEADLINE_TYPE;
            case HOUSE_ID:
                return HOUSE_TYPE;
            default:
                break;
        }
        return OTHER_TYPE;
    }

    public static final String BAISI_BASE_URL = "http://s.budejie.com/";
    public static final String BAISI__COMMENT_BASE_URL = "http://s.budejie.com/";
    public static final String BAISI__DETAIL_BASE_URL = "http://api.budejie.com/api/";

    // 美女图片
    // http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1
    // 每页10条数据、1表示第一页
    public static final String BASE_URL_BEAUTY = "http://gank.io/api/data/福利/10/";

}
