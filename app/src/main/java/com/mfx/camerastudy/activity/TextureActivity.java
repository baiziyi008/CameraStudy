package com.mfx.camerastudy.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.mfx.camerastudy.R;
import com.mfx.camerastudy.rencoder.MediaMuxerRunnable;
import com.mfx.camerastudy.utils.CameraInterface;
import com.mfx.camerastudy.utils.DisplayUtil;
import com.mfx.camerastudy.utils.ICamera;
import com.mfx.camerastudy.utils.ILogPrint;
import com.mfx.camerastudy.utils.RotaterUtil;
import com.mfx.camerastudy.view.CameraTextureView;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by mfx on 2018/7/15.
 */

public class TextureActivity extends Activity implements Camera.PreviewCallback, ICamera.ICameraOpenCallBack, TextureView.SurfaceTextureListener {

    private CameraTextureView mTextureView;
    private ImageButton shutter;
    private float mPreviewRate;
    private boolean mIsRecording = false;
    private Button mStart, mStop;
    private ICamera mICamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mICamera = new ICamera();
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mICamera.openCamera(TextureActivity.this, 1);
//            }
//        });
//        thread.start();
        requestWriteExternalPerm();

        setContentView(R.layout.activity_texture);
        initUI();
        initParams();
        shutter.setOnClickListener(new BtnListeners());
    }

    private void initParams() {
        ViewGroup.LayoutParams p = mTextureView.getLayoutParams();
        Point s = DisplayUtil.getScreenMetrics(this);
        p.width = s.x;
        p.height = s.y;
        float ratio = 640f / 480f;
        p.height = (int) (s.x * ratio);
        mTextureView.setLayoutParams(p);
        mPreviewRate = DisplayUtil.getScreenRate(this);

        ViewGroup.LayoutParams layoutParams = shutter.getLayoutParams();
        layoutParams.width = DisplayUtil.dip2px(this, 80);
        layoutParams.height = DisplayUtil.dip2px(this, 80);
        shutter.setLayoutParams(layoutParams);
    }

    private void initUI() {
        mStart = findViewById(R.id.start);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    MediaMuxerRunnable.startMuxer();
                if (!mIsRecording) {
                    mIsRecording = true;
                }
            }
        });
        mStop = findViewById(R.id.stop);
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    MediaMuxerRunnable.stopMuxer();
                    finish();
                if (mIsRecording) {
                    mIsRecording = false;
                }
            }
        });
        mTextureView = findViewById(R.id.camera_textureview);
        mTextureView.setSurfaceTextureListener(this);
        shutter = findViewById(R.id.btn_shutter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mICamera != null) {
            mICamera.closeCamera();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        MediaMuxerRunnable.addVideoFrameData(data);
        camera.addCallbackBuffer(data);
        if (mIsRecording) {
        }
    }

    @Override
    public void cameraOpenState(boolean isSuccess) {
//        SurfaceTexture texture = mTextureView.getSurfaceTexture();
//        mICamera.actionDetect(this);
//        mICamera.startPreview(texture);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mICamera.openCamera(this, 1);
        mICamera.actionDetect(this);
        mICamera.startPreview(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mICamera.closeCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private class BtnListeners implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_shutter:
                    CameraInterface.getInstance().doTakePicture();
                    break;
                default:
                    break;
            }
        }

    }

    private void enterNextPage() {


    }

    private void requestWriteExternalPerm() {
        if (android.os.Build.VERSION.SDK_INT >= M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            } else {
                requestCameraPerm();
            }
        } else {
            enterNextPage();
        }
    }


    private void requestCameraPerm() {
        if (android.os.Build.VERSION.SDK_INT >= M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        2);
            } else {
                requestAudioPerm();
            }
        } else {
            enterNextPage();
        }
    }

    private void requestAudioPerm() {
        if (android.os.Build.VERSION.SDK_INT >= M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        3);
            } else {
                enterNextPage();
            }
        } else {
            enterNextPage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            } else {
                requestAudioPerm();
            }
        } else if (requestCode == 1) {
            if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            } else {
                requestCameraPerm();
            }
        } else if (requestCode == 3) {
            if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            } else {
                enterNextPage();
            }
        }
    }
}
