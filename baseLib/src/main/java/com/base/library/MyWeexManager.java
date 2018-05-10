package com.base.library;

import android.text.TextUtils;

import com.taobao.weex.WXSDKInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by QYG_XXY on 0012 2018/2/12.
 */

public final class MyWeexManager {
    /**
     * 全局融云连接状态监听器EventName
     */
    public static final String GEN_Connection_Status = "connectionStatus";
    /**
     * 全局融云消息监听器EVENTName
     */
    public static final String GEN_New_Message = "newMessage";
    /**
     * 全局返回键EVENTName
     */
    public static final String GEN_Back = "aBack";

    private Map<String, WXSDKInstance> mWXSDKInstances;
    private static MyWeexManager myWeexManager;

    public WXSDKInstance getWXSDKInstance(String instanceHash) {
        if (TextUtils.isEmpty(instanceHash)) return null;
        for (Map.Entry<String, WXSDKInstance> entry : mWXSDKInstances.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            if (instanceHash.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Map<String, WXSDKInstance> getWXSDKInstances() {
        return mWXSDKInstances;
    }

    /**
     * 默认取obj.toString()作为Key
     *
     * @param mWXSDKInstance
     */
    public void putWXSDKInstance(WXSDKInstance mWXSDKInstance) {
        if (mWXSDKInstance == null) return;
        mWXSDKInstances.put(BeanUtil.getBeanHash(mWXSDKInstance), mWXSDKInstance);
    }

    public void removeWXSDKInstance(String instanceHash) {
        mWXSDKInstances.remove(instanceHash);
    }

    public void removeWXSDKInstance(WXSDKInstance instance) {
        if (instance == null) return;
        mWXSDKInstances.remove(BeanUtil.getBeanHash(instance));
    }

    public void removeAllWXSDKInstance() {
        this.mWXSDKInstances.clear();
    }


    private MyWeexManager() {
        mWXSDKInstances = new HashMap<>();
    }

    public static MyWeexManager getInstance() {
        if (myWeexManager == null) {
            synchronized (MyWeexManager.class) {
                if (myWeexManager == null) {
                    myWeexManager = new MyWeexManager();
                }
            }
        }
        return myWeexManager;
    }
}
