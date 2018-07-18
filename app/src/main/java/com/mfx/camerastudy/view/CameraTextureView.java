package com.mfx.camerastudy.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.mfx.camerastudy.utils.CameraInterface;

/**
 * Created by mfx on 2018/7/15.
 */

public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = "CameraTextureView";
    private Context mContext;
    SurfaceTexture mSurfaceTexture;


    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable: ");
        mSurfaceTexture = surface;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged: ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed: ");
        CameraInterface.getInstance().doStopCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureUpdated: ");

    }

    public SurfaceTexture getSurfaceTexture(){
        return mSurfaceTexture;
    }
}
