package com.base.library;

import android.util.Log;


/**
 * Created by xxy on 2016/8/23 0023.
 */
public class LogX {

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.e(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.w(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.d(tag, msg);
    }

    public static void e(Class tag, String msg) {
        e(tag.getSimpleName(), msg);
    }

    public static void i(Class tag, String msg) {
        i(tag.getSimpleName(), msg);
    }

    public static void w(Class tag, String msg) {
        w(tag.getSimpleName(), msg);
    }

    public static void v(Class tag, String msg) {
        v(tag.getSimpleName(), msg);
    }

    public static void d(Class tag, String msg) {
        d(tag.getSimpleName(), msg);
    }
}
