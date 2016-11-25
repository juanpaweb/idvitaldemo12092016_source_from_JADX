package com.google.zxing.client.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents.Scan;
import com.google.zxing.client.android.camera.CameraManager;
import idvital1.idvital1.C0239R;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@TargetApi(15)
public final class CaptureActivity extends Activity implements Callback {
    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 0;
    private static final String TAG;
    public static final String ZXING_CAPTURE_LAYOUT_ID_KEY = "ZXING_CAPTURE_LAYOUT_ID_KEY";
    private AmbientLightManager ambientLightManager;
    private BeepManager beepManager;
    private CameraManager cameraManager;
    private String characterSet;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private TextView statusView;
    private ViewfinderView viewfinderView;

    /* renamed from: com.google.zxing.client.android.CaptureActivity.1 */
    class C02211 implements OnClickListener {
        C02211() {
        }

        public void onClick(View view) {
            CaptureActivity.this.setResult(0);
            CaptureActivity.this.finish();
        }
    }

    static {
        TAG = CaptureActivity.class.getSimpleName();
    }

    ViewfinderView getViewfinderView() {
        return this.viewfinderView;
    }

    public Handler getHandler() {
        return this.handler;
    }

    CameraManager getCameraManager() {
        return this.cameraManager;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        Bundle extras = getIntent().getExtras();
        int zxingCaptureLayoutResourceId = C0223R.layout.zxing_capture;
        if (extras != null) {
            zxingCaptureLayoutResourceId = extras.getInt(ZXING_CAPTURE_LAYOUT_ID_KEY, C0223R.layout.zxing_capture);
        }
        setContentView(zxingCaptureLayoutResourceId);
        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(this);
        this.beepManager = new BeepManager(this);
        this.ambientLightManager = new AmbientLightManager(this);
        PreferenceManager.setDefaultValues(this, C0223R.xml.zxing_preferences, false);
        Button cancelButton = (Button) findViewById(C0223R.id.zxing_back_button);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new C02211());
        }
    }

    protected void onResume() {
        super.onResume();
        this.cameraManager = new CameraManager(getApplication());
        this.viewfinderView = (ViewfinderView) findViewById(C0223R.id.zxing_viewfinder_view);
        this.viewfinderView.setCameraManager(this.cameraManager);
        this.statusView = (TextView) findViewById(C0223R.id.zxing_status_view);
        this.handler = null;
        resetStatusView();
        this.beepManager.updatePrefs();
        this.ambientLightManager.start(this.cameraManager);
        this.inactivityTimer.onResume();
        Intent intent = getIntent();
        this.decodeFormats = null;
        this.characterSet = null;
        if (intent != null) {
            if (Scan.ACTION.equals(intent.getAction())) {
                this.decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                this.decodeHints = DecodeHintManager.parseDecodeHints(intent);
                int orientation = intent.getIntExtra(Scan.ORIENTATION, -1);
                if (orientation == -1) {
                    setRequestedOrientation(getCurrentOrientation());
                } else {
                    setRequestedOrientation(orientation);
                }
                if (intent.getBooleanExtra(Scan.WIDE, false)) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point displaySize = new Point();
                    display.getSize(displaySize);
                    this.cameraManager.setManualFramingRect((displaySize.x * 9) / 10, Math.min((displaySize.y * 3) / 4, 400));
                } else if (intent.hasExtra(Scan.WIDTH) && intent.hasExtra(Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        this.cameraManager.setManualFramingRect(width, height);
                    }
                }
                if (intent.hasExtra(Scan.CAMERA_ID)) {
                    int cameraId = intent.getIntExtra(Scan.CAMERA_ID, -1);
                    if (cameraId >= 0) {
                        this.cameraManager.setManualCameraId(cameraId);
                    }
                }
                String customPromptMessage = intent.getStringExtra(Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {
                    this.statusView.setText(customPromptMessage);
                }
            }
            this.characterSet = intent.getStringExtra(Scan.CHARACTER_SET);
            SurfaceHolder surfaceHolder = ((SurfaceView) findViewById(C0223R.id.zxing_preview_view)).getHolder();
            if (this.hasSurface) {
                initCamera(surfaceHolder);
            } else {
                surfaceHolder.addCallback(this);
            }
        }
    }

    private int getCurrentOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case ItemTouchHelper.ACTION_STATE_IDLE /*0*/:
            case ItemTouchHelper.UP /*1*/:
                return 0;
            default:
                return 8;
        }
    }

    protected void onPause() {
        if (this.handler != null) {
            this.handler.quitSynchronously();
            this.handler = null;
        }
        this.inactivityTimer.onPause();
        this.ambientLightManager.stop();
        this.beepManager.close();
        this.cameraManager.closeDriver();
        if (!this.hasSurface) {
            ((SurfaceView) findViewById(C0223R.id.zxing_preview_view)).getHolder().removeCallback(this);
        }
        super.onPause();
    }

    protected void onDestroy() {
        this.inactivityTimer.shutdown();
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case ItemTouchHelper.LEFT /*4*/:
                setResult(0);
                finish();
                return true;
            case C0239R.styleable.Toolbar_navigationIcon /*24*/:
                this.cameraManager.setTorch(true);
                return true;
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
                this.cameraManager.setTorch(false);
                return true;
            case C0239R.styleable.Toolbar_titleTextColor /*27*/:
            case C0239R.styleable.AppCompatTheme_panelMenuListWidth /*80*/:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!this.hasSurface) {
            this.hasSurface = true;
            initCamera(holder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        this.inactivityTimer.onActivity();
        if (barcode != null) {
            this.beepManager.playBeepSoundAndVibrate();
            drawResultPoints(barcode, scaleFactor, rawResult);
        }
        handleDecodeExternally(rawResult, barcode);
    }

    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(C0223R.color.zxing_result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 && (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(point.getX() * scaleFactor, point.getY() * scaleFactor, paint);
                    }
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(), scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
        }
    }

    private void handleDecodeExternally(Result rawResult, Bitmap barcode) {
        if (barcode != null) {
            this.viewfinderView.drawResultBitmap(barcode);
        }
        long resultDurationMS = getIntent().getLongExtra(Scan.RESULT_DISPLAY_DURATION_MS, DEFAULT_INTENT_RESULT_DURATION_MS);
        Intent intent = new Intent(getIntent().getAction());
        intent.addFlags(AccessibilityNodeInfoCompat.ACTION_COLLAPSE);
        intent.putExtra(Scan.RESULT, rawResult.toString());
        intent.putExtra(Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
        byte[] rawBytes = rawResult.getRawBytes();
        if (rawBytes != null && rawBytes.length > 0) {
            intent.putExtra(Scan.RESULT_BYTES, rawBytes);
        }
        Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
        if (metadata != null) {
            if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
                intent.putExtra(Scan.RESULT_UPC_EAN_EXTENSION, metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
            }
            Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
            if (orientation != null) {
                intent.putExtra(Scan.RESULT_ORIENTATION, orientation.intValue());
            }
            String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
            if (ecLevel != null) {
                intent.putExtra(Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
            }
            Iterable<byte[]> byteSegments = (Iterable) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
            if (byteSegments != null) {
                int i = 0;
                for (byte[] byteSegment : byteSegments) {
                    intent.putExtra(Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                    i++;
                }
            }
        }
        sendReplyMessage(C0223R.id.zxing_return_scan_result, intent, resultDurationMS);
    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {
        if (this.handler != null) {
            Message message = Message.obtain(this.handler, id, arg);
            if (delayMS > DEFAULT_INTENT_RESULT_DURATION_MS) {
                this.handler.sendMessageDelayed(message, delayMS);
            } else {
                this.handler.sendMessage(message);
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        } else if (this.cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
        } else {
            try {
                this.cameraManager.openDriver(surfaceHolder);
                if (this.handler == null) {
                    this.handler = new CaptureActivityHandler(this, this.decodeFormats, this.decodeHints, this.characterSet, this.cameraManager);
                }
            } catch (IOException ioe) {
                Log.w(TAG, ioe);
                displayFrameworkBugMessageAndExit();
            } catch (RuntimeException e) {
                Log.w(TAG, "Unexpected error initializing camera", e);
                displayFrameworkBugMessageAndExit();
            }
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(C0223R.string.zxing_app_name));
        builder.setMessage(getString(C0223R.string.zxing_msg_camera_framework_bug));
        builder.setPositiveButton(C0223R.string.zxing_button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    private void resetStatusView() {
        this.statusView.setText(C0223R.string.zxing_msg_default_status);
        this.statusView.setVisibility(0);
        this.viewfinderView.setVisibility(0);
    }

    public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
    }
}
