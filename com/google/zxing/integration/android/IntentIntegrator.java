package com.google.zxing.integration.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents.Scan;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IntentIntegrator {
    public static final Collection<String> ALL_CODE_TYPES;
    public static final Collection<String> DATA_MATRIX_TYPES;
    private static final boolean HAVE_LEGACY_SCANNER;
    private static final boolean HAVE_STANDARD_SCANNER;
    private static final String LEGACY_PACKAGE_NAME = "com.google.zxing.client.androidlegacy";
    public static final Collection<String> ONE_D_CODE_TYPES;
    public static final Collection<String> PRODUCT_CODE_TYPES;
    public static final Collection<String> QR_CODE_TYPES;
    public static final int REQUEST_CODE = 49374;
    private static final String STANDARD_PACKAGE_NAME = "com.google.zxing.client.android";
    private static final String TAG;
    private final Activity activity;
    private Collection<String> desiredBarcodeFormats;
    private Fragment fragment;
    private final Map<String, Object> moreExtras;
    private android.support.v4.app.Fragment supportFragment;

    static {
        TAG = IntentIntegrator.class.getSimpleName();
        PRODUCT_CODE_TYPES = list("UPC_A", "UPC_E", "EAN_8", "EAN_13", "RSS_14");
        ONE_D_CODE_TYPES = list("UPC_A", "UPC_E", "EAN_8", "EAN_13", "CODE_39", "CODE_93", "CODE_128", "ITF", "RSS_14", "RSS_EXPANDED");
        QR_CODE_TYPES = Collections.singleton("QR_CODE");
        DATA_MATRIX_TYPES = Collections.singleton("DATA_MATRIX");
        ALL_CODE_TYPES = null;
        boolean test1 = HAVE_STANDARD_SCANNER;
        try {
            Class.forName("com.google.zxing.client.android.CaptureActivity");
            test1 = true;
        } catch (ClassNotFoundException e) {
        }
        HAVE_STANDARD_SCANNER = test1;
        boolean test2 = HAVE_STANDARD_SCANNER;
        try {
            Class.forName("com.google.zxing.client.androidlegacy.CaptureActivity");
            test2 = true;
        } catch (ClassNotFoundException e2) {
        }
        HAVE_LEGACY_SCANNER = test2;
    }

    protected Class<?> getCaptureActivity() {
        try {
            return Class.forName(getScannerPackage() + ".CaptureActivity");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find CaptureActivity. Make sure one of the zxing-android libraries are loaded.", e);
        }
    }

    private static String getScannerPackage() {
        if (HAVE_STANDARD_SCANNER && VERSION.SDK_INT >= 15) {
            return STANDARD_PACKAGE_NAME;
        }
        if (HAVE_LEGACY_SCANNER) {
            return LEGACY_PACKAGE_NAME;
        }
        return STANDARD_PACKAGE_NAME;
    }

    public IntentIntegrator(Activity activity) {
        this.moreExtras = new HashMap(3);
        this.activity = activity;
    }

    public static IntentIntegrator forSupportFragment(android.support.v4.app.Fragment fragment) {
        IntentIntegrator integrator = new IntentIntegrator(fragment.getActivity());
        integrator.supportFragment = fragment;
        return integrator;
    }

    @TargetApi(11)
    public static IntentIntegrator forFragment(Fragment fragment) {
        IntentIntegrator integrator = new IntentIntegrator(fragment.getActivity());
        integrator.fragment = fragment;
        return integrator;
    }

    public Map<String, ?> getMoreExtras() {
        return this.moreExtras;
    }

    public final IntentIntegrator addExtra(String key, Object value) {
        this.moreExtras.put(key, value);
        return this;
    }

    public final IntentIntegrator setCaptureLayout(int resourceId) {
        addExtra(CaptureActivity.ZXING_CAPTURE_LAYOUT_ID_KEY, Integer.valueOf(resourceId));
        return this;
    }

    public final IntentIntegrator setLegacyCaptureLayout(int resourceId) {
        addExtra("ZXINGLEGACY_CAPTURE_LAYOUT_ID_KEY", Integer.valueOf(resourceId));
        return this;
    }

    public final IntentIntegrator setPrompt(String prompt) {
        if (prompt != null) {
            addExtra(Scan.PROMPT_MESSAGE, prompt);
        }
        return this;
    }

    public final IntentIntegrator setResultDisplayDuration(long ms) {
        addExtra(Scan.RESULT_DISPLAY_DURATION_MS, Long.valueOf(ms));
        return this;
    }

    public final IntentIntegrator setScanningRectangle(int desiredWidth, int desiredHeight) {
        addExtra(Scan.WIDTH, Integer.valueOf(desiredWidth));
        addExtra(Scan.HEIGHT, Integer.valueOf(desiredHeight));
        return this;
    }

    public void setOrientation(int orientation) {
        addExtra(Scan.ORIENTATION, Integer.valueOf(orientation));
    }

    public void setWide() {
        addExtra(Scan.WIDE, Boolean.valueOf(true));
        Display display = this.activity.getWindowManager().getDefaultDisplay();
        int displayWidth = display.getWidth();
        int displayHeight = display.getHeight();
        if (displayHeight > displayWidth) {
            int temp = displayWidth;
            displayWidth = displayHeight;
            displayHeight = temp;
        }
        setScanningRectangle((displayWidth * 9) / 10, Math.min((displayHeight * 3) / 4, 400));
    }

    public static boolean shouldBeWide(Collection<String> desiredBarcodeFormats) {
        boolean scan1d = HAVE_STANDARD_SCANNER;
        boolean scan2d = HAVE_STANDARD_SCANNER;
        for (String format : desiredBarcodeFormats) {
            if (ONE_D_CODE_TYPES.contains(format)) {
                scan1d = true;
            }
            if (QR_CODE_TYPES.contains(format) || DATA_MATRIX_TYPES.contains(format)) {
                scan2d = true;
            }
        }
        return (!scan1d || scan2d) ? HAVE_STANDARD_SCANNER : true;
    }

    public IntentIntegrator autoWide() {
        if (this.desiredBarcodeFormats != null && shouldBeWide(this.desiredBarcodeFormats)) {
            setWide();
        }
        return this;
    }

    public IntentIntegrator setCameraId(int cameraId) {
        if (cameraId >= 0) {
            addExtra(Scan.CAMERA_ID, Integer.valueOf(cameraId));
        }
        return this;
    }

    public IntentIntegrator setDesiredBarcodeFormats(Collection<String> desiredBarcodeFormats) {
        this.desiredBarcodeFormats = desiredBarcodeFormats;
        return this;
    }

    public final void initiateScan() {
        startActivityForResult(createScanIntent(), REQUEST_CODE);
    }

    public Intent createScanIntent() {
        Intent intentScan = new Intent(this.activity, getCaptureActivity());
        intentScan.setAction(Scan.ACTION);
        if (this.desiredBarcodeFormats != null) {
            StringBuilder joinedByComma = new StringBuilder();
            for (String format : this.desiredBarcodeFormats) {
                if (joinedByComma.length() > 0) {
                    joinedByComma.append(',');
                }
                joinedByComma.append(format);
            }
            intentScan.putExtra(Scan.FORMATS, joinedByComma.toString());
        }
        intentScan.addFlags(67108864);
        intentScan.addFlags(AccessibilityNodeInfoCompat.ACTION_COLLAPSE);
        attachMoreExtras(intentScan);
        return intentScan;
    }

    public final void initiateScan(Collection<String> desiredBarcodeFormats) {
        setDesiredBarcodeFormats(desiredBarcodeFormats);
        initiateScan();
    }

    protected void startActivityForResult(Intent intent, int code) {
        if (this.fragment != null) {
            if (VERSION.SDK_INT >= 11) {
                this.fragment.startActivityForResult(intent, code);
            }
        } else if (this.supportFragment != null) {
            this.supportFragment.startActivityForResult(intent, code);
        } else {
            this.activity.startActivityForResult(intent, code);
        }
    }

    protected void startActivity(Intent intent) {
        if (this.fragment != null) {
            if (VERSION.SDK_INT >= 11) {
                this.fragment.startActivity(intent);
            }
        } else if (this.supportFragment != null) {
            this.supportFragment.startActivity(intent);
        } else {
            this.activity.startActivity(intent);
        }
    }

    public static IntentResult parseActivityResult(int requestCode, int resultCode, Intent intent) {
        Integer orientation = null;
        if (requestCode != REQUEST_CODE) {
            return null;
        }
        if (resultCode != -1) {
            return new IntentResult();
        }
        String contents = intent.getStringExtra(Scan.RESULT);
        String formatName = intent.getStringExtra(Scan.RESULT_FORMAT);
        byte[] rawBytes = intent.getByteArrayExtra(Scan.RESULT_BYTES);
        int intentOrientation = intent.getIntExtra(Scan.RESULT_ORIENTATION, LinearLayoutManager.INVALID_OFFSET);
        if (intentOrientation != LinearLayoutManager.INVALID_OFFSET) {
            orientation = Integer.valueOf(intentOrientation);
        }
        return new IntentResult(contents, formatName, rawBytes, orientation, intent.getStringExtra(Scan.RESULT_ERROR_CORRECTION_LEVEL));
    }

    private static List<String> list(String... values) {
        return Collections.unmodifiableList(Arrays.asList(values));
    }

    private void attachMoreExtras(Intent intent) {
        for (Entry<String, Object> entry : this.moreExtras.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Integer) {
                intent.putExtra(key, (Integer) value);
            } else if (value instanceof Long) {
                intent.putExtra(key, (Long) value);
            } else if (value instanceof Boolean) {
                intent.putExtra(key, (Boolean) value);
            } else if (value instanceof Double) {
                intent.putExtra(key, (Double) value);
            } else if (value instanceof Float) {
                intent.putExtra(key, (Float) value);
            } else if (value instanceof Bundle) {
                intent.putExtra(key, (Bundle) value);
            } else {
                intent.putExtra(key, value.toString());
            }
        }
    }
}
