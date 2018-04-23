package com.base.library;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QYG_XXY on 0011 2018/4/11.
 */
public abstract class PermissionUtil {
    String[] ps;
    int requestCode;
    Activity activity;
    List<String> passList;

    public PermissionUtil(Activity activity, String[] permissions, int requestCode) {
        this.ps = permissions;
        this.requestCode = requestCode;
        this.activity = activity;
        passList = new ArrayList<>();
    }

    /**
     */
    public void request() {
        List<String> unPassList = new ArrayList<>();
        for (int i = 0; i < ps.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, ps[i]) != PackageManager.PERMISSION_GRANTED) {
                unPassList.add(ps[i]);
            } else {
                passList.add(ps[i]);
            }
        }

        if (unPassList.isEmpty()) {//未授予的权限为空，表示都授予了
            onRequestResult(passList.toArray(new String[passList.size()]), unPassList.toArray(new String[unPassList.size()]), requestCode);
        } else {//请求权限方法
            String[] requestPermissions = unPassList.toArray(new String[unPassList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == this.requestCode) {
            List<String> noPass = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {//没有被允许
                    //判断是否勾选禁止后不再询问
//                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i]);
//                        if (showRequestPermission) {//
//                            judgePermission();//重新申请权限
//                            return;
//                        } else {
//                            mShowRequestPermission = false;//已经禁止
//                        }
                    noPass.add(permissions[i]);
                } else {
                    passList.add(permissions[i]);
                }
            }
            onRequestResult(passList.toArray(new String[passList.size()]), noPass.toArray(new String[noPass.size()]), requestCode);
        }
    }

    public abstract void onRequestResult(String[] successPermissions, String[] failurePermissions, int requestCode);

}
