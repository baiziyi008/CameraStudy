package com.mfx.camerastudy.activity;

import android.app.Activity;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mfx.camerastudy.R;
import com.mfx.camerastudy.utils.CameraInterface;
import com.mfx.camerastudy.utils.DisplayUtil;

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
                    CameraInterface.getInstance().doTakePicture();
                    break;
                default:break;
            }
        }

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
