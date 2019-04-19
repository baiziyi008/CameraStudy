package com.mfx.camerastudy.utils;


import android.util.Log;

public class ILogPrint {
    public final static String TAG = "AllInOne";
    public static boolean ISDEBUG = true;
    private static int LOG_MAXLENGTH = 2000;

    public static void logw(String key, String log) {
        if (ISDEBUG)
            Log.w(TAG, key + " === " + log);
    }

    public static void logw(String log) {
        int strLength = log.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(TAG + i, log.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.e(TAG, log.substring(start, strLength));
                break;
            }
        }
    }

    public static void logv(String key, String log) {
        if (ISDEBUG)
            Log.v(TAG, key + " === " + log);
    }

    public static void logd(String key, String log) {
        if (ISDEBUG)
            Log.d(TAG, key + " === " + log);
    }

    public static void logi(String key, String log) {
        if (ISDEBUG)
            Log.i(TAG, key + " === " + log);
    }

    public static void loge(String key, String log) {
        if (ISDEBUG)
            Log.e(TAG, key + " === " + log);
    }
}
