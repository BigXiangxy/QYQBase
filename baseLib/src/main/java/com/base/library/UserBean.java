package com.base.library;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.io.Serializable;

/**
 * 登录用户
 * Created by xxy on 2016/7/21.
 */
public class UserBean implements Serializable {

    private String obj;
    private String token;
    private String id;

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /*---------------------------------------------------------------------------------*/
    private static final String user_key = "user_qyq_key";
    private static UserBean _install;

    /**
     * 获取实例,如果有缓存系统将获取缓存中的对象
     *
     * @return
     */
    public static UserBean getInstall(Context context) {
        if (_install == null) {
            try {
                if (context == null)
                    throw new RuntimeException("UserBean.class ::: appContext is null !!!");
                _install = (UserBean) LastingUtils.readObjectDES(context, user_key);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return _install;
    }

    /**
     * 获取实例,如果有缓存系统将获取缓存中的对象
     *
     * @return
     */
    public static UserBean getInstall(Activity activity) {
        Application appContext = null;
        if (activity != null)
            appContext = activity.getApplication();
        return getInstall(appContext);
    }

    /**
     * 保存当前用户信息
     *
     * @param context
     * @return
     */
    public boolean saveInfo(Context context) {
        boolean bol = LastingUtils.saveObjectDES(context, this, user_key);
        if (bol) _install = this;
        return bol;
    }

    /**
     * 保存当前用户信息
     */
    public boolean saveInfo(Activity activity) {
        return saveInfo(activity.getApplication());
    }

    /**
     * 清空存储
     *
     * @param context
     * @return
     */
    public static boolean delete(Context context) {
        boolean bol = LastingUtils.delete(context, user_key);
        if (bol) _install = null;
        return bol;
    }
}
