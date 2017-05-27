package com.toby.mymaterialdemo.utils;

import com.orhanobut.logger.Logger;
import com.toby.mymaterialdemo.common.Constants;

/**
 * Created by toby on 15/11/21.
 */
public class LogUtils {

//    private static boolean LOG = true; // true;

    private static String arrayToString(String[] infos) {
        StringBuilder sb = new StringBuilder();
        for (String info : infos) {
            sb.append(info);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static void v(String... s) {
        if (Constants.DEBUG_MODE) {
            Logger.v(arrayToString(s));
        }
    }

    public static void i(String... s) {
        if (Constants.DEBUG_MODE) {
            Logger.i(arrayToString(s));
        }
    }

    public static void d(String... s) {
        if (Constants.DEBUG_MODE) {
            Logger.d(arrayToString(s));
        }
    }

    public static void e(String... s) {
        if (Constants.DEBUG_MODE) {
            Logger.e(arrayToString(s));
        }
    }

    public static void w(String... s) {
        if (Constants.DEBUG_MODE) {
            Logger.w(arrayToString(s));
        }
    }

    public static void logException(Throwable throwable) {
        if (Constants.DEBUG_MODE) {
            Logger.e(throwable, "Throwable", throwable.toString());
        }
    }

}
