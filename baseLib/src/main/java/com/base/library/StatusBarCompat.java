package com.base.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;



/**
 * Created by zhy on 15/9/21.
 * "For this to take effect, the window must be drawing the system bar backgrounds with FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS and FLAG_TRANSLUCENT_STATUS must not be set.
 */
public final class StatusBarCompat {
    private static int statusBarHeight = -1;

    /**
     * 设置状态栏颜色
     *
     * @param activity
     * @param statusColor
     */
    public static void setStatusBarColor(Activity activity, @ColorRes int statusColor) {
        int color = activity.getResources().getColor(statusColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);//5.0 以上直接设置状态栏颜色,只有通过代码设置状态栏透明才会生效，style设置透明时不会生效
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置状态栏透明
            View statusBarView = new View(activity);
            statusBarView.setBackgroundColor(color);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusBarView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity)));
        }
    }

    /**
     * 设置状态栏颜色R.color.colorPrimary
     */
    public static void setStatusBarColor(Activity activity) {
        setStatusBarColor(activity, R.color.base_colorPrimary);
    }

    /**
     * 设置主布局padding
     *
     * @param activity
     */
    public static void setContentViewPadding(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);//设置 paddingTop
        rootView.setPadding(0, getStatusBarHeight(activity), 0, 0);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight <= 0) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    /**
     * 通过设置全屏，设置状态栏透明
     * 取代xml文件设置主题
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //导航栏颜色也可以正常设置
//            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//            attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            window.setAttributes(attributes);
        }
    }

    /**
     * 设置页面最外层布局 FitsSystemWindows 属性
     * 弹出输入法会有问题
     *
     * @param activity
     */
    public static void setFitsSystemWindows(Activity activity) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        View parentView = contentView.getChildAt(0);
        if (parentView != null && Build.VERSION.SDK_INT >= 14) {
            //布局预留状态栏高度的 padding
            parentView.setFitsSystemWindows(true);
            if (parentView instanceof DrawerLayout) {
                DrawerLayout drawer = (DrawerLayout) parentView;
                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
                drawer.setClipToPadding(false);
            }
        }
    }

    /**
     * 为DrawerLayout添加状态栏占位视图
     *
     * @param activity
     */
    public void addDrawerLayoutStatusViewWithColor(Activity activity, @ColorRes int colorId, @IdRes int drawerContentId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            View statusBarView = new View(activity);
            statusBarView.setBackgroundColor(activity.getResources().getColor(colorId));
            //添加占位状态栏到线性布局中
            linearLayout.addView(statusBarView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity)));

            ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);//要在内容布局增加状态栏，否则会盖在侧滑菜单上
            DrawerLayout parentView = (DrawerLayout) rootView.getChildAt(0);//DrawerLayout 则需要在第一个子视图即内容试图中添加padding

            //内容视图
            View content = activity.findViewById(drawerContentId);
            //将内容视图从 DrawerLayout 中移除
            parentView.removeView(content);
            //添加内容视图
            linearLayout.addView(content, content.getLayoutParams());
            //将带有占位状态栏的新的内容视图设置给 DrawerLayout
            parentView.addView(linearLayout, 0);
        }
    }


}
