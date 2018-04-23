package com.base.library;

/**
 * Created by QYG_XXY on 0004 2018/4/4.
 */

public class FileUtil {

    /**
     * Java文件操作 获取文件扩展名
     * <p>
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }
}
