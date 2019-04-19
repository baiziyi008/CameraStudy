package com.mfx.camerastudy.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.mfx.camerastudy.view.CameraTextureView;

import java.io.IOException;
import java.util.List;

/**
 * Created by mfx on 2018/7/5.
 */

public class CameraInterface {

    private static final String TAG = "CameraInterface";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviewRate = -1f;
    private Camera.PreviewCallback mPreviewCallback;
    private byte[] mImageCallbackBuffer = new byte[640
            * 480 * 3 / 2];

    public boolean isPreviewing() {
        return isPreviewing;
    }

    private static CameraInterface mCameraInterface;

    public interface CamOpenOverCallback {
        public void cameraHasOpened();
    }

    private CameraInterface() {
    }


    public synchronized static CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }

        return mCameraInterface;
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }

    public void doOpenCamera(CamOpenOverCallback callback) {
        Log.d(TAG, "doOpenCamera: do camera open .....");
        mCamera = Camera.open(0);
        Log.d(TAG, "doOpenCamera: do camera  open over");

        if (callback != null) {
            callback.cameraHasOpened();
        }
    }

    public void doStartPreview(SurfaceHolder holder, float previewRate) {
        Log.d(TAG, "doStartPreview: .....");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {

            try {
                mCamera.setPreviewDisplay(holder);
//                mCamera.addCallbackBuffer(mImageCallbackBuffer);
//                mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initCamera(previewRate);
        }
    }

    public void doStartPreview(SurfaceTexture texture, float previewRate ){
        Log.d(TAG, "doStartPreview: ");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null){
            try {
                mCamera.setPreviewTexture(texture);
//                mCamera.addCallbackBuffer(mImageCallbackBuffer);
//                mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initCamera(previewRate);
        }
    }

    private void initCamera(float previewRate){
        mParams = mCamera.getParameters();
        mParams.setPictureFormat(ImageFormat.JPEG);
        CamParaUtil.getInstance().printSupportPreviewSize(mParams);
        CamParaUtil.getInstance().printSupportPictureSize(mParams);
        //set previewsize and picturesize
        Camera.Size previewSize = CamParaUtil.getInstance().getProPreviewSize(mParams.getSupportedPreviewSizes(), previewRate, 800);
//            mParams.setPreviewSize(previewSize.width, previewSize.height);
        Log.d(TAG, "doStartPreview:  camera size width="+previewSize.width + "  height="+previewSize.height);
        mParams.setPreviewSize(640, 480);
        Camera.Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(mParams.getSupportedPictureSizes(), previewRate, 800);
        mParams.setPictureSize(pictureSize.width, pictureSize.height);

        CamParaUtil.getInstance().printSupportFocusMode(mParams);
        List<String> modes = mParams.getSupportedFocusModes();
        if (modes.contains("continuous-video")) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(mParams);

            mCamera.startPreview();

        isPreviewing = true;
        mPreviewRate = previewRate;

        mParams = mCamera.getParameters();
        Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                + "Height = " + mParams.getPreviewSize().height);
        Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                + "Height = " + mParams.getPictureSize().height);
    }

    public void doStopCamera() {
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviewRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }


    public void doTakePicture(){
        if(isPreviewing && (mCamera != null)){
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
            //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    Camera.PictureCallback mRawCallback = new Camera.PictureCallback()
            // 拍摄的未压缩原数据的回调,可以为null
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };

    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback()
            //对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if(null != data){
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
            }
            //保存图片到sdcard
            if(null != b)
            {
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                FileUtil.saveBitmap(rotaBitmap);
            }
            //再次进入预览
            mCamera.startPreview();
            isPreviewing = true;
        }
    };

}
