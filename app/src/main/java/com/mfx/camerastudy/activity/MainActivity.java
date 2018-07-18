package com.mfx.camerastudy.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mfx.camerastudy.view.CameraSurfaceView;
import com.mfx.camerastudy.R;
import com.mfx.camerastudy.utils.CameraInterface;
import com.mfx.camerastudy.utils.DisplayUtil;

public class MainActivity extends Activity implements CameraInterface.CamOpenOverCallback{
    private static final String TAG = "MainActivity";
    CameraSurfaceView mCameraSurfaceView;
    ImageButton shutterButton;
    float   previewRate = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(MainActivity.this);
            }
        });
        thread.start();
        setContentView(R.layout.activity_main);
        initUI();
        initParams();
        shutterButton.setOnClickListener(new BtnListeners());
    }

    private void initParams() {
        ViewGroup.LayoutParams params =mCameraSurfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width =  p.x;
        params.height = p.y;
        float ratio = 640f/480f;
        float w = params.height / ratio;
        params.width = (int) w;
        previewRate = DisplayUtil.getScreenRate(this);
        Log.d(TAG, "initParams: surfaceview  width="+params.width + "  height="+params.height);
        mCameraSurfaceView.setLayoutParams(params);

        ViewGroup.LayoutParams p2 = shutterButton.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);
        shutterButton.setLayoutParams(p2);
    }


    private void initUI() {
        mCameraSurfaceView = findViewById(R.id.camera_surfaceview);
        shutterButton = findViewById(R.id.btn_shutter);
    }

    @Override
    public void cameraHasOpened() {
        SurfaceHolder holder = mCameraSurfaceView.getSurfaceHolder();
        CameraInterface.getInstance().doStartPreview(holder, previewRate);
    }
    private class BtnListeners implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(v.getId()){
                case R.id.btn_shutter:
                    CameraInterface.getInstance().doTakePicture();
                    break;
                default:break;
            }
        }

    }


}
