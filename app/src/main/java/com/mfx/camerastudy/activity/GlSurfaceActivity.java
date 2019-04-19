package com.mfx.camerastudy.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.mfx.camerastudy.R;
import com.mfx.camerastudy.utils.CameraInterface;
import com.mfx.camerastudy.utils.DisplayUtil;
import com.mfx.camerastudy.utils.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mfx on 2018/7/21.
 */

public class GlSurfaceActivity extends Activity {
    private static final String TAG = "GlSurfaceActivity";
    private ImageButton shutterButton;;
    private GLSurfaceView   mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurface);
        initUI();
        initParams();
        shutterButton.setOnClickListener(new GlSurfaceActivity.BtnListeners());
    }

    private void initParams() {
        ViewGroup.LayoutParams params =mGLSurfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width =  p.x;
        params.height = p.y;
        float ratio = 640f/480f;
        float w = params.height / ratio;
        params.width = (int) w;
        Log.d(TAG, "initParams: GLsurfaceview  width="+params.width + "  height="+params.height);
        mGLSurfaceView.setLayoutParams(params);

        ViewGroup.LayoutParams p2 = shutterButton.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);
        shutterButton.setLayoutParams(p2);
    }


    private void initUI() {
        mGLSurfaceView = findViewById(R.id.camera_glsurfaceview);
        shutterButton = findViewById(R.id.btn_shutter);
    }

    private class BtnListeners implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(v.getId()){
                case R.id.btn_shutter:
//                    CameraInterface.getInstance().doTakePicture();
//                    Bitmap bmp = capture(GlSurfaceActivity.this);
//                    Bitmap bmp = convertViewToBitmap(getRootView(GlSurfaceActivity.this));
//                    Bitmap bmp = captureScreen(GlSurfaceActivity.this);
//                    Bitmap bmp = loadBitmapFromView(getRootView(GlSurfaceActivity.this));
//                    if (bmp == null){
//                        Log.d(TAG, "onClick: bmp is null");
//                        break;
//                    }
//                    Log.d(TAG, "onClick: bmp length="+bmp.getHeight());
////                    saveImage(GlSurfaceActivity.this, bmp);
//                    FileUtil.saveBitmap(bmp);
                    takeScreenShot();
                    break;
                default:break;
            }
        }

    }


    private Bitmap capture(Activity activity) {
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bmp = activity.getWindow().getDecorView().getDrawingCache();
        return bmp;
    }

    public Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    private Bitmap captureScreen(Activity context) {
        View cv = context.getWindow().getDecorView();

        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bmp = cv.getDrawingCache();
        if (bmp == null) {
            return null;
        }

        bmp.setHasAlpha(false);
        bmp.prepareToDraw();
        return bmp;
    }

    private Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }

    public void takeScreenShot() {
        File file = new File("/mnt/sdcard/dji_picture");
        if (!file.exists()) {
            file.mkdir();
        }
        //String mSavedPath = Environment.getExternalStorageDirectory() + dir.separator + "sreenshot.png";
        String mSavedPath = file.getAbsolutePath()  +  file.separator  + "sreenshot" + ".png";
        try {
            Runtime.getRuntime().exec("screencap -p " + mSavedPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static final String SCREEN_SHOT_FILE = "screen_shot";
    private void saveImage(Activity activity, Bitmap bitmap){
        if (bitmap == null){
            return;
        }

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return;
        }

        File file = activity.getExternalFilesDir(SCREEN_SHOT_FILE);
        if (file.exists()){
            file.delete();
        }
        file.mkdirs();
        if (file.canWrite()){
            new File(file, ".png");
        }
    }
    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private View getRootView(Activity activity){
        return (getWindow().getDecorView().findViewById(android.R.id.content));
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mGLSurfaceView.bringToFront();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
