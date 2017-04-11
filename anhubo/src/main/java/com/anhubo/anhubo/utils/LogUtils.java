package com.anhubo.anhubo.utils;


import android.util.Log;

/**
 * Created by luoli on 2016/08/12.
 * Log工具类
 */
public class LogUtils {
    private static final boolean ENABLE = false;

    /**
     * 打印info级别的log
     */
    public static void i(Object objTag, Object objMsg) {
        if (ENABLE) {
            Log.i(getTag(objTag), getMsg(objMsg));
        }
    }

    /**
     * 打印error级别的log
     */
    public static void eNormal(Object objTag, Object objMsg) {
        if (ENABLE) {
            Log.e(getTag(objTag), getMsg(objMsg));
        }
    }

    /**
     * 打印错误信息的log
     */
    public static void e(Object objTag, Object objMsg, Throwable e) {
        if (ENABLE) {
            Log.e(getTag(objTag), getMsg(objMsg), e);
        }
    }

    /**
     * 获取Tag
     */
    private static String getTag(Object objTag) {
        String tag;
        if (objTag == null) {
            tag = "null";
        } else if (objTag instanceof Class<?>) {
            tag = ((Class<?>) objTag).getSimpleName();
        } else if (objTag instanceof String) {
            tag = (String) objTag;
        } else {
            tag = objTag.getClass().getSimpleName();
        }
        return "luoli_" + tag;
    }

    /**
     * 获取要打印的信息
     */
    private static String getMsg(Object objMsg) {
        String msg;
        if (objMsg == null) {
            msg = (String) objMsg;
        } else {
            msg = String.valueOf(objMsg);
        }
        return msg;
    }
}
