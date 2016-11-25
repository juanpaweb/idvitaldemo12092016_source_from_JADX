package com.google.zxing.client.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.google.zxing.client.android.PreferencesActivity;

final class CameraConfigurationManager {
    private static final String TAG = "CameraConfiguration";
    private Point cameraResolution;
    private final Context context;
    private Point screenResolution;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    void initFromCameraParameters(Camera camera) {
        Parameters parameters = camera.getParameters();
        Display display = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay();
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);
        this.screenResolution = theScreenResolution;
        Log.i(TAG, "Screen resolution: " + this.screenResolution);
        this.cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, this.screenResolution);
        Log.i(TAG, "Camera resolution: " + this.cameraResolution);
    }

    void setDesiredCameraParameters(Camera camera, boolean safeMode) {
        Parameters parameters = camera.getParameters();
        if (parameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }
        Log.i(TAG, "Initial camera parameters: " + parameters.flatten());
        if (safeMode) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        initializeTorch(parameters, prefs, safeMode);
        CameraConfigurationUtils.setFocus(parameters, prefs.getBoolean(PreferencesActivity.KEY_AUTO_FOCUS, true), prefs.getBoolean(PreferencesActivity.KEY_DISABLE_CONTINUOUS_FOCUS, true), safeMode);
        if (!safeMode) {
            if (prefs.getBoolean(PreferencesActivity.KEY_INVERT_SCAN, false)) {
                CameraConfigurationUtils.setInvertColor(parameters);
            }
            if (!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_BARCODE_SCENE_MODE, true)) {
                CameraConfigurationUtils.setBarcodeSceneMode(parameters);
            }
            if (!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_METERING, true)) {
                CameraConfigurationUtils.setVideoStabilization(parameters);
                CameraConfigurationUtils.setFocusArea(parameters);
                CameraConfigurationUtils.setMetering(parameters);
            }
        }
        parameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
        Log.i(TAG, "Final camera parameters: " + parameters.flatten());
        camera.setParameters(parameters);
        Size afterSize = camera.getParameters().getPreviewSize();
        if (afterSize == null) {
            return;
        }
        if (this.cameraResolution.x != afterSize.width || this.cameraResolution.y != afterSize.height) {
            Log.w(TAG, "Camera said it supported preview size " + this.cameraResolution.x + 'x' + this.cameraResolution.y + ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
            this.cameraResolution.x = afterSize.width;
            this.cameraResolution.y = afterSize.height;
        }
    }

    public void setCameraDisplayOrientation(int cameraId, Camera camera) {
        int result;
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        switch (((WindowManager) this.context.getSystemService("window")).getDefaultDisplay().getRotation()) {
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
            result = (360 - ((info.orientation + degrees) % 360)) % 360;
        } else {
            result = ((info.orientation - degrees) + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public boolean isDisplayRotated() {
        switch (((WindowManager) this.context.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case ItemTouchHelper.ACTION_STATE_IDLE /*0*/:
            case ItemTouchHelper.DOWN /*2*/:
                return true;
            case ItemTouchHelper.UP /*1*/:
            case DrawerLayout.LOCK_MODE_UNDEFINED /*3*/:
                return false;
            default:
                return false;
        }
    }

    Point getCameraResolution() {
        return this.cameraResolution;
    }

    Point getRotatedCameraResolution() {
        if (this.cameraResolution == null) {
            return null;
        }
        if (isDisplayRotated()) {
            return new Point(this.cameraResolution.y, this.cameraResolution.x);
        }
        return this.cameraResolution;
    }

    Point getScreenResolution() {
        return this.screenResolution;
    }

    boolean getTorchState(Camera camera) {
        if (camera == null) {
            return false;
        }
        Parameters parameters = camera.getParameters();
        if (parameters == null) {
            return false;
        }
        String flashMode = parameters.getFlashMode();
        if (flashMode == null) {
            return false;
        }
        if ("on".equals(flashMode) || "torch".equals(flashMode)) {
            return true;
        }
        return false;
    }

    void setTorch(Camera camera, boolean newSetting) {
        Parameters parameters = camera.getParameters();
        doSetTorch(parameters, newSetting, false);
        camera.setParameters(parameters);
    }

    private void initializeTorch(Parameters parameters, SharedPreferences prefs, boolean safeMode) {
        doSetTorch(parameters, FrontLightMode.readPref(prefs) == FrontLightMode.ON, safeMode);
    }

    private void doSetTorch(Parameters parameters, boolean newSetting, boolean safeMode) {
        CameraConfigurationUtils.setTorch(parameters, newSetting);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        if (!safeMode && !prefs.getBoolean(PreferencesActivity.KEY_DISABLE_EXPOSURE, true)) {
            CameraConfigurationUtils.setBestExposure(parameters, newSetting);
        }
    }
}
