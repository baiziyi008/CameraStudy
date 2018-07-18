package com.mfx.camerastudy.activity;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mfx.camerastudy.R;
import com.mfx.camerastudy.utils.CameraInterface;
import com.mfx.camerastudy.utils.DisplayUtil;
import com.mfx.camerastudy.view.CameraTextureView;

/**
 * Created by mfx on 2018/7/15.
 */

public class TextureActivity extends Activity implements CameraInterface.CamOpenOverCallback {

    private CameraTextureView mTextureView;
    private ImageButton shutter;
    private float mPreviewRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(TextureActivity.this);
            }
        });
        thread.start();

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
        p.width = (int) (s.y / ratio);
        mTextureView.setLayoutParams(p);
        mPreviewRate = DisplayUtil.getScreenRate(this);

        ViewGroup.LayoutParams layoutParams = shutter.getLayoutParams();
        layoutParams.width = DisplayUtil.dip2px(this, 80);
        layoutParams.height = DisplayUtil.dip2px(this, 80);
        shutter.setLayoutParams(layoutParams);
    }

    private void initUI() {
        mTextureView = findViewById(R.id.camera_textureview);
        shutter = findViewById(R.id.btn_shutter);
    }

    @Override
    public void cameraHasOpened() {
        SurfaceTexture  texture = mTextureView.getSurfaceTexture();
        CameraInterface.getInstance().doStartPreview(texture, mPreviewRate);
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
}
