package com.toby.mymaterialdemo.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.toby.mymaterialdemo.base.BaseApplication;
import com.toby.mymaterialdemo.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Toby on 2016/9/27.
 */
public class Utils {

    private static Context mContext = BaseApplication.appContext;

    public static void copyToClipboard(View view,String info) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("msg", info);
        cm.setPrimaryClip(cd);
        Snackbar.make(view, R.string.notif_info_copied, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 校验用户手机号码格式
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNumber(String phone) {
        boolean flag;
        String regExp = "^((13[0-9])|(15[^4,\\D])|(17[0,6,7,8])|(18[0-9])|(14[5,7]))\\d{8}$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(phone);
        flag = matcher.find();
        return flag;
    }

    /**
     * 校验用户登录密码(6-18位数字或字母组合)
     *
     * @param pwd
     * @return
     */
    public static boolean isRightPwd(String pwd) {
        boolean flag;
        String regExp = "^[0-9A-Za-z]{6,18}$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(pwd);
        flag = matcher.find();
        return flag;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
