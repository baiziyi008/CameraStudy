package com.mfx.camerastudy.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.view.Surface;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 照相机工具类
 */
public class ICamera {

    public Camera mCamera;
    public int cameraWidth;
    public int cameraHeight;
    public int cameraId = 1;//前置摄像头
    public int angle;
    private byte[] mImageCallbackBuffer = new byte[640
            * 480 * 3 / 2];

    private ICameraOpenCallBack mICameraOpenCallBack;
    public interface ICameraOpenCallBack {
        public void cameraOpenState(boolean isSuccess);
    }

    public ICamera() {
    }

    /**
     * 打开相机
     */
    public Camera openCamera(Activity activity, int cameraId) {

        try {
            this.cameraId = cameraId;
            mCamera = Camera.open(cameraId);
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            Camera.Parameters params = mCamera.getParameters();
            Camera.Size bestPreviewSize = calBestPreviewSize(
                    mCamera.getParameters(), 640, 480);
            cameraWidth = bestPreviewSize.width;
            cameraHeight = bestPreviewSize.height;
            params.setPreviewSize(cameraWidth, cameraHeight);
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
//            angle = getCameraAngle(activity);

            mCamera.setDisplayOrientation(angle);
            mCamera.setParameters(params);

            if (mICameraOpenCallBack != null){
                mICameraOpenCallBack.cameraOpenState(true);
            }
            return mCamera;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 通过屏幕参数、相机预览尺寸计算布局参数
    public RelativeLayout.LayoutParams getLayoutParam() {
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        float scale = Math.min(Screen.mWidth * 1.0f / previewSize.height,
                Screen.mHeight * 1.0f / previewSize.width);
        int layout_width = (int) (scale * previewSize.height);
        int layout_height = (int) (scale * previewSize.width);
        ILogPrint.logw("layout_width",layout_width+"");
        ILogPrint.logw("layout_height",layout_height+"");
        RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(
                layout_width, layout_height);

        return layout_params;

    }

    public int getLayoutWidth(){
//        float scale = Math.max(Screen.mWidth * 1.0f / cameraHeight,
//                Screen.mHeight * 1.0f /cameraWidth);
//
//        return (int) (scale *cameraHeight);
        return cameraHeight;
    }

    public int getLayoutHeight(){
//        float scale = Math.max(Screen.mWidth * 1.0f / cameraHeight,
//                Screen.mHeight * 1.0f /cameraWidth);
//
//        return (int) (scale *cameraWidth);
        return cameraWidth;
    }

    /**
     * 开始检测脸
     */
    public void actionDetect(Camera.PreviewCallback mActivity) {
        try {
            if (mCamera != null) {
                ILogPrint.logw("test","Camera.setPreviewCallback");
//                mCamera.setPreviewCallback(mActivity);
                mCamera.addCallbackBuffer(mImageCallbackBuffer);
                mCamera.setPreviewCallbackWithBuffer((Camera.PreviewCallback) mActivity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPreview(SurfaceTexture surfaceTexture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surfaceTexture);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeCamera() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过传入的宽高算出最接近于宽高值的相机大小
     */
    private Camera.Size calBestPreviewSize(Camera.Parameters camPara,
                                           final int width, final int height) {
        List<Camera.Size> allSupportedSize = camPara.getSupportedPreviewSizes();
        ArrayList<Camera.Size> widthLargerSize = new ArrayList<Camera.Size>();
        for (Camera.Size tmpSize : allSupportedSize) {
            if (tmpSize.width > tmpSize.height) {
                widthLargerSize.add(tmpSize);
            }
        }

        Collections.sort(widthLargerSize, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int off_one = Math.abs(lhs.width * lhs.height - width * height);
                int off_two = Math.abs(rhs.width * rhs.height - width * height);
                return off_one - off_two;
            }
        });

        return widthLargerSize.get(0);
    }

    /**
     * 打开前置或后置摄像头
     */
    public Camera getCameraSafely(int cameraId) {
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            camera = null;
        }
        return camera;
    }

    public RelativeLayout.LayoutParams getParams(Camera camera) {
        Camera.Parameters camPara = camera.getParameters();
        // AliLog.w("ceshi", "Screen.mWidth===" + Screen.mWidth +
        // ", Screen.mHeight===" + Screen.mHeight);
        // 注意Screen是否初始化
        Camera.Size bestPreviewSize = calBestPreviewSize(camPara,
                Screen.mWidth, Screen.mHeight);
        cameraWidth = bestPreviewSize.width;
        cameraHeight = bestPreviewSize.height;
        camPara.setPreviewSize(cameraWidth, cameraHeight);
        camera.setParameters(camPara);

        float scale = bestPreviewSize.width / bestPreviewSize.height;

        RelativeLayout.LayoutParams layoutPara = new RelativeLayout.LayoutParams(
                (int) (bestPreviewSize.width),
                (int) (bestPreviewSize.width / scale));

        layoutPara.addRule(RelativeLayout.CENTER_HORIZONTAL);// 设置照相机水平居中
        return layoutPara;
    }

    public Bitmap getBitMap(byte[] data, Camera camera, boolean mIsFrontalCamera) {
        int width = camera.getParameters().getPreviewSize().width;
        int height = camera.getParameters().getPreviewSize().height;
        YuvImage yuvImage = new YuvImage(data, camera.getParameters()
                .getPreviewFormat(), width, height, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 80,
                byteArrayOutputStream);
        byte[] jpegData = byteArrayOutputStream.toByteArray();
        // 获取照相后的bitmap
        Bitmap tmpBitmap = BitmapFactory.decodeByteArray(jpegData, 0,
                jpegData.length);
        Matrix matrix = new Matrix();
        matrix.reset();
        if (mIsFrontalCamera) {
            matrix.setRotate(-90);
        } else {
            matrix.setRotate(90);
        }
        tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(),
                tmpBitmap.getHeight(), matrix, true);
        tmpBitmap = tmpBitmap.copy(Bitmap.Config.ARGB_8888, true);

        int hight = tmpBitmap.getHeight() > tmpBitmap.getWidth() ? tmpBitmap
                .getHeight() : tmpBitmap.getWidth();

        float scale = hight / 800.0f;

        if (scale > 1) {
            tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap,
                    (int) (tmpBitmap.getWidth() / scale),
                    (int) (tmpBitmap.getHeight() / scale), false);
        }
        return tmpBitmap;
    }

    /**
     * 获取照相机旋转角度
     */
    public int getCameraAngle(Activity activity) {
        int rotateAngle = 90;
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            rotateAngle = (info.orientation + degrees) % 360;
            rotateAngle = (360 - rotateAngle) % 360; // compensate the mirror
        } else { // back-facing
            rotateAngle = (info.orientation - degrees + 360) % 360;
        }
        return rotateAngle;
    }

    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        CameraInfo info = new CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCameraPerm() {
        Camera camera = null;
        try {
            camera = Camera.open(0);

            if (camera == null) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (camera != null) {
                try {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                    camera.release();
                    camera = null;
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}