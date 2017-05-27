package com.toby.mymaterialdemo.view;

/**
 * Created by Toby on 2016/12/21.
 */

public interface BaseView {

    /**
     * 显示正在加载
     */
    void showLoading(String msg);

    /**
     * 隐藏加载进度
     */
    void hideLoading();

    /**
     * 显示错误信息
     */
    void showError(String msg);

    /**
     * 显示异常信息
     */
    void showException(String msg);

    /**
     * 显示网络错误
     */
    void showNetError();

}
