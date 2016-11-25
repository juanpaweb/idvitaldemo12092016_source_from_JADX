package me.dm7.barcodescanner.core;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public abstract class BarcodeScannerView extends FrameLayout implements PreviewCallback {
    private boolean mAutofocusState;
    private Camera mCamera;
    private CameraHandlerThread mCameraHandlerThread;
    private Boolean mFlashState;
    private Rect mFramingRectInPreview;
    private CameraPreview mPreview;
    private IViewFinder mViewFinderView;

    public BarcodeScannerView(Context context) {
        super(context);
        this.mAutofocusState = true;
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAutofocusState = true;
    }

    public final void setupLayout(Camera camera) {
        removeAllViews();
        this.mPreview = new CameraPreview(getContext(), camera, this);
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setGravity(17);
        relativeLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        relativeLayout.addView(this.mPreview);
        addView(relativeLayout);
        this.mViewFinderView = createViewFinderView(getContext());
        if (this.mViewFinderView instanceof View) {
            addView((View) this.mViewFinderView);
            return;
        }
        throw new IllegalArgumentException("IViewFinder object returned by 'createViewFinderView()' should be instance of android.view.View");
    }

    protected IViewFinder createViewFinderView(Context context) {
        return new ViewFinderView(context);
    }

    public void startCamera(int cameraId) {
        if (this.mCameraHandlerThread == null) {
            this.mCameraHandlerThread = new CameraHandlerThread(this);
        }
        this.mCameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(Camera camera) {
        this.mCamera = camera;
        if (this.mCamera != null) {
            setupLayout(this.mCamera);
            this.mViewFinderView.setupViewFinder();
            if (this.mFlashState != null) {
                setFlash(this.mFlashState.booleanValue());
            }
            setAutoFocus(this.mAutofocusState);
        }
    }

    public void startCamera() {
        startCamera(-1);
    }

    public void stopCamera() {
        if (this.mCamera != null) {
            this.mPreview.stopCameraPreview();
            this.mPreview.setCamera(null, null);
            this.mCamera.release();
            this.mCamera = null;
        }
        if (this.mCameraHandlerThread != null) {
            this.mCameraHandlerThread.quit();
            this.mCameraHandlerThread = null;
        }
    }

    public void stopCameraPreview() {
        if (this.mPreview != null) {
            this.mPreview.stopCameraPreview();
        }
    }

    protected void resumeCameraPreview() {
        if (this.mPreview != null) {
            this.mPreview.showCameraPreview();
        }
    }

    public synchronized Rect getFramingRectInPreview(int previewWidth, int previewHeight) {
        Rect rect;
        if (this.mFramingRectInPreview == null) {
            Rect framingRect = this.mViewFinderView.getFramingRect();
            int viewFinderViewWidth = this.mViewFinderView.getWidth();
            int viewFinderViewHeight = this.mViewFinderView.getHeight();
            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                rect = null;
            } else {
                Rect rect2 = new Rect(framingRect);
                rect2.left = (rect2.left * previewWidth) / viewFinderViewWidth;
                rect2.right = (rect2.right * previewWidth) / viewFinderViewWidth;
                rect2.top = (rect2.top * previewHeight) / viewFinderViewHeight;
                rect2.bottom = (rect2.bottom * previewHeight) / viewFinderViewHeight;
                this.mFramingRectInPreview = rect2;
            }
        }
        rect = this.mFramingRectInPreview;
        return rect;
    }

    public void setFlash(boolean flag) {
        this.mFlashState = Boolean.valueOf(flag);
        if (this.mCamera != null && CameraUtils.isFlashSupported(this.mCamera)) {
            Parameters parameters = this.mCamera.getParameters();
            if (flag) {
                if (!parameters.getFlashMode().equals("torch")) {
                    parameters.setFlashMode("torch");
                } else {
                    return;
                }
            } else if (!parameters.getFlashMode().equals("off")) {
                parameters.setFlashMode("off");
            } else {
                return;
            }
            this.mCamera.setParameters(parameters);
        }
    }

    public boolean getFlash() {
        if (this.mCamera != null && CameraUtils.isFlashSupported(this.mCamera) && this.mCamera.getParameters().getFlashMode().equals("torch")) {
            return true;
        }
        return false;
    }

    public void toggleFlash() {
        if (this.mCamera != null && CameraUtils.isFlashSupported(this.mCamera)) {
            Parameters parameters = this.mCamera.getParameters();
            if (parameters.getFlashMode().equals("torch")) {
                parameters.setFlashMode("off");
            } else {
                parameters.setFlashMode("torch");
            }
            this.mCamera.setParameters(parameters);
        }
    }

    public void setAutoFocus(boolean state) {
        this.mAutofocusState = state;
        if (this.mPreview != null) {
            this.mPreview.setAutoFocus(state);
        }
    }
}
