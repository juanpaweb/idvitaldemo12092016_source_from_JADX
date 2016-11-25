package me.dm7.barcodescanner.core;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class CameraHandlerThread extends HandlerThread {
    private static final String LOG_TAG = "CameraHandlerThread";
    private BarcodeScannerView mScannerView;

    /* renamed from: me.dm7.barcodescanner.core.CameraHandlerThread.1 */
    class C02441 implements Runnable {
        final /* synthetic */ int val$cameraId;

        /* renamed from: me.dm7.barcodescanner.core.CameraHandlerThread.1.1 */
        class C02431 implements Runnable {
            final /* synthetic */ Camera val$camera;

            C02431(Camera camera) {
                this.val$camera = camera;
            }

            public void run() {
                CameraHandlerThread.this.mScannerView.setupCameraPreview(this.val$camera);
            }
        }

        C02441(int i) {
            this.val$cameraId = i;
        }

        public void run() {
            new Handler(Looper.getMainLooper()).post(new C02431(CameraUtils.getCameraInstance(this.val$cameraId)));
        }
    }

    public CameraHandlerThread(BarcodeScannerView scannerView) {
        super(LOG_TAG);
        this.mScannerView = scannerView;
        start();
    }

    public void startCamera(int cameraId) {
        new Handler(getLooper()).post(new C02441(cameraId));
    }
}
