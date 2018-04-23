package com.base.library;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

/**
 * 序列化对象持久化工具类
 * Created by xxy on 2016/8/15 0015.
 */
public class LastingUtils {
    private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间
    private static final String mmm = "jp12s%29ks%:";

    /**
     * 判断缓存数据是否可读
     *
     * @param cachefile
     * @return
     */
    public static boolean isReadDataCache(Context context, String cachefile) {
        return readObject(context, cachefile) != null;
    }

    /**
     * 判断缓存是否存在
     *
     * @param cacheFile
     * @return
     */
    private static boolean isExistDataCache(Context context, String cacheFile) {
        boolean exist = false;
        File data = context.getFileStreamPath(cacheFile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 判断缓存是否失效
     *
     * @param cacheFile
     * @return
     */
    public static boolean isCacheDataFailure(Context context, String cacheFile) {
        boolean failure = false;
        File data = context.getFileStreamPath(cacheFile);
        if (data.exists()
                && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     */
    public static Serializable readObject(Context context, String file) {
        if (!isExistDataCache(context, file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 清除持久缓存
     *
     * @param context
     * @param file
     * @return
     */
    public static boolean delete(Context context, String file) {
        return context.getFileStreamPath(file).delete();
    }

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     */
    public static boolean saveObject(Context context, Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Application.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    private static boolean buildKey(Context context) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("DESede");//  //指定算法,这里为DES
            kg.init(192); //指定密钥长度,长度越高,加密强度越大
            Key k = kg.generateKey();//产生密钥
            saveObject(context, k, mmm);
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Key readKey(Context context) {
        Key key = (Key) readObject(context, mmm);
        if (key == null && buildKey(context)) {
            key = (Key) readObject(context, mmm);
            Log.e("-", "Create K-K !!!");
        }
        return key;
    }

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     */
    public static boolean saveObjectDES(Context context, Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Application.MODE_PRIVATE);
            //加密要用Cipher来实现
            Cipher cipher = Cipher.getInstance("DESede");
            Key k = readKey(context);//产生密钥
            cipher.init(Cipher.ENCRYPT_MODE, k);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(fos, cipher);
            oos = new ObjectOutputStream(cipherOutputStream);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     */
    public static Serializable readObjectDES(Context context, String file) {
        if (!isExistDataCache(context, file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            Cipher cipher = Cipher.getInstance("DESede");
            Key k = readKey(context);
            cipher.init(Cipher.DECRYPT_MODE, k);
            CipherInputStream cipherOutputStream = new CipherInputStream(fis, cipher);
            ois = new ObjectInputStream(cipherOutputStream);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }
}
