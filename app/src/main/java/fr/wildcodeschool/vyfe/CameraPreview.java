package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private SurfaceCallback mCallback;

    public CameraPreview(Context context, Camera camera, SurfaceCallback callback) {
        super(context);
        mCamera = camera;
        mCallback = callback;

        // permet d'être notifié quand la surface est créée et détruite
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCallback.onSurfaceCreated();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            // la surface n'existe pas
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.getMessage();
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public interface SurfaceCallback {
        void onSurfaceCreated();
    }
}
