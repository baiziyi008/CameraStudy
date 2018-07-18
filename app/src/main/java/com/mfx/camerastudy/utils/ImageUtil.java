package com.mfx.camerastudy.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by mfx on 2018/7/14.
 */

public class ImageUtil {

    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegreee){
        Matrix matrix = new Matrix();

        matrix.postRotate(rotateDegreee);

        Bitmap bitmap = Bitmap.createBitmap(b, 0,0,  b.getWidth(), b.getHeight(), matrix, false);

        return bitmap;
    }
}
