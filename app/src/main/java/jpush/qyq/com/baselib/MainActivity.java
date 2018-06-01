package jpush.qyq.com.baselib;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.base.library.StatusBarCompat;

public class MainActivity extends AppCompatActivity {
    private int mStatusBarHeight;
    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();
        StatusBarCompat.setFullScreen(this);
        StatusBarCompat.setStatusBarColor(this, R.color.colorPrimaryDark);
        mStatusBarHeight = getInternalDimensionSize(res, STATUS_BAR_HEIGHT_RES_NAME);
        Log.e("111", "status:" + mStatusBarHeight);
        Log.e("222", "status:" + StatusBarCompat.getStatusBarHeight(this));

//        View view = findViewById(R.id.tv);
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
//        layoutParams.height = StatusBarCompat.getStatusBarHeight(this);
//        view.setLayoutParams(layoutParams);

    }


    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int resourceId = Integer.parseInt(clazz.getField(key).get(object).toString());
            if (resourceId > 0)
                result = res.getDimensionPixelSize(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
