package com.mfx.camerastudy.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Formatter;

/**
 * Created by mfx on 2018/7/14.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final File PARENTPATH = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "PlayCamera";

    /*
    初始化path
     */
    private static String initPath(){
        if (storagePath.equals("")){
            storagePath = PARENTPATH.getAbsolutePath() + "/"+ DST_FOLDER_NAME;
            File f = new File(storagePath);
            if (!f.exists()){
                f.mkdir();
            }
        }
        return storagePath;
    }

    public static void saveBitmap(Bitmap b){
        String path = initPath();
        long dataTake = System.currentTimeMillis();
        String timeTake= new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(dataTake);
        String jpgName = path + "/" + timeTake + ".jpg";

        Log.i(TAG, "saveBitmap:jpegName = " + jpgName);
        try {
            FileOutputStream fos = new FileOutputStream(jpgName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
