package com.base.library;

/**
 * Created by QYG_XXY on 0002 2018/4/2.
 */

public final class BeanUtil {

    public static String getBeanHash(Object obj) {
        if (obj == null) return null;
        return obj.toString();
    }
}
