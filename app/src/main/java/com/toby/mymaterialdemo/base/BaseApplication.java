package com.toby.mymaterialdemo.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.Stack;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackManager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by Toby on 2016/9/27.
 */
public class BaseApplication extends Application {

    public static Context appContext = null;
    private static BaseApplication instance;
    private static Stack<Activity> activityStack;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();
        // 默认初始化Bmob
        Bmob.initialize(appContext, "9bc69ff802b0dfc6ca11511a58073c4c");
//        BmobUpdateAgent.initAppVersion();// 初始化服务器端的版本升级表，只需要执行一次
//        BGASwipeBackManager.getInstance().init(this);
    }

    public static Context getContext() {
        return appContext;
    }

    /**
     * 添加Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 删除Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
