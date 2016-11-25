package me.dm7.barcodescanner.core;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import java.util.List;

public class CameraPreview extends SurfaceView implements Callback {
    private static final String TAG = "CameraPreview";
    AutoFocusCallback autoFocusCB;
    private Runnable doAutoFocus;
    private boolean mAutoFocus;
    private Handler mAutoFocusHandler;
    private Camera mCamera;
    private PreviewCallback mPreviewCallback;
    private boolean mPreviewing;
    private boolean mSurfaceCreated;

    /* renamed from: me.dm7.barcodescanner.core.CameraPreview.1 */
    class C02451 implements Runnable {
        C02451() {
        }

        public void run() {
            if (CameraPreview.this.mCamera != null && CameraPreview.this.mPreviewing && CameraPreview.this.mAutoFocus && CameraPreview.this.mSurfaceCreated) {
                CameraPreview.this.safeAutoFocus();
            }
        }
    }

    /* renamed from: me.dm7.barcodescanner.core.CameraPreview.2 */
    class C02462 implements AutoFocusCallback {
        C02462() {
        }

        public void onAutoFocus(boolean success, Camera camera) {
            CameraPreview.this.scheduleAutoFocus();
        }
    }

    public CameraPreview(Context context, Camera camera, PreviewCallback previewCallback) {
        super(context);
        this.mPreviewing = true;
        this.mAutoFocus = true;
        this.mSurfaceCreated = false;
        this.doAutoFocus = new C02451();
        this.autoFocusCB = new C02462();
        init(camera, previewCallback);
    }

    public CameraPreview(Context context, AttributeSet attrs, Camera camera, PreviewCallback previewCallback) {
        super(context, attrs);
        this.mPreviewing = true;
        this.mAutoFocus = true;
        this.mSurfaceCreated = false;
        this.doAutoFocus = new C02451();
        this.autoFocusCB = new C02462();
        init(camera, previewCallback);
    }

    public void init(Camera camera, PreviewCallback previewCallback) {
        setCamera(camera, previewCallback);
        this.mAutoFocusHandler = new Handler();
        getHolder().addCallback(this);
        getHolder().setType(3);
    }

    public void setCamera(Camera camera, PreviewCallback previewCallback) {
        this.mCamera = camera;
        this.mPreviewCallback = previewCallback;
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mSurfaceCreated = true;
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (surfaceHolder.getSurface() != null) {
            stopCameraPreview();
            showCameraPreview();
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mSurfaceCreated = false;
        stopCameraPreview();
    }

    public void showCameraPreview() {
        if (this.mCamera != null) {
            try {
                getHolder().addCallback(this);
                this.mPreviewing = true;
                setupCameraParameters();
                this.mCamera.setPreviewDisplay(getHolder());
                this.mCamera.setDisplayOrientation(getDisplayOrientation());
                this.mCamera.setOneShotPreviewCallback(this.mPreviewCallback);
                this.mCamera.startPreview();
                if (!this.mAutoFocus) {
                    return;
                }
                if (this.mSurfaceCreated) {
                    safeAutoFocus();
                } else {
                    scheduleAutoFocus();
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public void safeAutoFocus() {
        try {
            this.mCamera.autoFocus(this.autoFocusCB);
        } catch (RuntimeException e) {
            scheduleAutoFocus();
        }
    }

    public void stopCameraPreview() {
        if (this.mCamera != null) {
            try {
                this.mPreviewing = false;
                getHolder().removeCallback(this);
                this.mCamera.cancelAutoFocus();
                this.mCamera.setOneShotPreviewCallback(null);
                this.mCamera.stopPreview();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public void setupCameraParameters() {
        Size optimalSize = getOptimalPreviewSize();
        Parameters parameters = this.mCamera.getParameters();
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        this.mCamera.setParameters(parameters);
        adjustViewSize(optimalSize);
    }

    private void adjustViewSize(Size cameraSize) {
        Point previewSize = convertSizeToLandscapeOrientation(new Point(getWidth(), getHeight()));
        float cameraRatio = ((float) cameraSize.width) / ((float) cameraSize.height);
        if (((float) previewSize.x) / ((float) previewSize.y) > cameraRatio) {
            setViewSize((int) (((float) previewSize.y) * cameraRatio), previewSize.y);
        } else {
            setViewSize(previewSize.x, (int) (((float) previewSize.x) / cameraRatio));
        }
    }

    private Point convertSizeToLandscapeOrientation(Point size) {
        return getDisplayOrientation() % 180 == 0 ? size : new Point(size.y, size.x);
    }

    private void setViewSize(int width, int height) {
        LayoutParams layoutParams = getLayoutParams();
        if (getDisplayOrientation() % 180 == 0) {
            layoutParams.width = width;
            layoutParams.height = height;
        } else {
            layoutParams.width = height;
            layoutParams.height = width;
        }
        setLayoutParams(layoutParams);
    }

    public int getDisplayOrientation() {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(0, info);
        int degrees = 0;
        switch (((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRotation()) {
            case ItemTouchHelper.ACTION_STATE_IDLE /*0*/:
                degrees = 0;
                break;
            case ItemTouchHelper.UP /*1*/:
                degrees = 90;
                break;
            case ItemTouchHelper.DOWN /*2*/:
                degrees = 180;
                break;
            case DrawerLayout.LOCK_MODE_UNDEFINED /*3*/:
                degrees = 270;
                break;
        }
        if (info.facing == 1) {
            return (360 - ((info.orientation + degrees) % 360)) % 360;
        }
        return ((info.orientation - degrees) + 360) % 360;
    }

    private Size getOptimalPreviewSize() {
        if (this.mCamera == null) {
            return null;
        }
        List<Size> sizes = this.mCamera.getParameters().getSupportedPreviewSizes();
        int w = getWidth();
        int h = getHeight();
        if (DisplayUtils.getScreenOrientation(getContext()) == 1) {
            int portraitWidth = h;
            h = w;
            w = portraitWidth;
        }
        double targetRatio = ((double) w) / ((double) h);
        if (sizes == null) {
            return null;
        }
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Size size : sizes) {
            if (Math.abs((((double) size.width) / ((double) size.height)) - targetRatio) <= 0.1d) {
                if (((double) Math.abs(size.height - targetHeight)) < minDiff) {
                    optimalSize = size;
                    minDiff = (double) Math.abs(size.height - targetHeight);
                }
            }
        }
        if (optimalSize != null) {
            return optimalSize;
        }
        minDiff = Double.MAX_VALUE;
        for (Size size2 : sizes) {
            if (((double) Math.abs(size2.height - targetHeight)) < minDiff) {
                optimalSize = size2;
                minDiff = (double) Math.abs(size2.height - targetHeight);
            }
        }
        return optimalSize;
    }

    public void setAutoFocus(boolean state) {
        if (this.mCamera != null && this.mPreviewing && state != this.mAutoFocus) {
            this.mAutoFocus = state;
            if (!this.mAutoFocus) {
                Log.v(TAG, "Cancelling autofocus");
                this.mCamera.cancelAutoFocus();
            } else if (this.mSurfaceCreated) {
                Log.v(TAG, "Starting autofocus");
                safeAutoFocus();
            } else {
                scheduleAutoFocus();
            }
        }
    }

    private void scheduleAutoFocus() {
        this.mAutoFocusHandler.postDelayed(this.doAutoFocus, 1000);
    }
}
