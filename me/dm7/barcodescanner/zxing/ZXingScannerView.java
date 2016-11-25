package me.dm7.barcodescanner.zxing;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.dm7.barcodescanner.core.BarcodeScannerView;
import me.dm7.barcodescanner.core.DisplayUtils;

public class ZXingScannerView extends BarcodeScannerView {
    public static final List<BarcodeFormat> ALL_FORMATS;
    private static final String TAG = "ZXingScannerView";
    private List<BarcodeFormat> mFormats;
    private MultiFormatReader mMultiFormatReader;
    private ResultHandler mResultHandler;

    /* renamed from: me.dm7.barcodescanner.zxing.ZXingScannerView.1 */
    class C02491 implements Runnable {
        final /* synthetic */ Result val$finalRawResult;

        C02491(Result result) {
            this.val$finalRawResult = result;
        }

        public void run() {
            ResultHandler tmpResultHandler = ZXingScannerView.this.mResultHandler;
            ZXingScannerView.this.mResultHandler = null;
            ZXingScannerView.this.stopCameraPreview();
            if (tmpResultHandler != null) {
                tmpResultHandler.handleResult(this.val$finalRawResult);
            }
        }
    }

    public interface ResultHandler {
        void handleResult(Result result);
    }

    static {
        ALL_FORMATS = new ArrayList();
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
    }

    public ZXingScannerView(Context context) {
        super(context);
        initMultiFormatReader();
    }

    public ZXingScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initMultiFormatReader();
    }

    public void setFormats(List<BarcodeFormat> formats) {
        this.mFormats = formats;
        initMultiFormatReader();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if (this.mFormats == null) {
            return ALL_FORMATS;
        }
        return this.mFormats;
    }

    private void initMultiFormatReader() {
        Map<DecodeHintType, Object> hints = new EnumMap(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
        this.mMultiFormatReader = new MultiFormatReader();
        this.mMultiFormatReader.setHints(hints);
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        if (this.mResultHandler != null) {
            Result rawResult;
            try {
                Size size = camera.getParameters().getPreviewSize();
                int width = size.width;
                int height = size.height;
                if (DisplayUtils.getScreenOrientation(getContext()) == 1) {
                    byte[] rotatedData = new byte[data.length];
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            rotatedData[(((x * height) + height) - y) - 1] = data[(y * width) + x];
                        }
                    }
                    int tmp = width;
                    width = height;
                    height = tmp;
                    data = rotatedData;
                }
                rawResult = null;
                PlanarYUVLuminanceSource source = buildLuminanceSource(data, width, height);
                if (source != null) {
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    rawResult = this.mMultiFormatReader.decodeWithState(bitmap);
                    this.mMultiFormatReader.reset();
                }
            } catch (ReaderException e) {
                this.mMultiFormatReader.reset();
            } catch (NullPointerException e2) {
                this.mMultiFormatReader.reset();
            } catch (ArrayIndexOutOfBoundsException e3) {
                this.mMultiFormatReader.reset();
            } catch (RuntimeException e4) {
                Log.e(TAG, e4.toString(), e4);
                return;
            } catch (Throwable th) {
                this.mMultiFormatReader.reset();
            }
            Result finalRawResult = rawResult;
            if (finalRawResult != null) {
                Looper mainLooper = Looper.getMainLooper();
                new Handler(r16).post(new C02491(finalRawResult));
                return;
            }
            camera.setOneShotPreviewCallback(this);
        }
    }

    public void resumeCameraPreview(ResultHandler resultHandler) {
        this.mResultHandler = resultHandler;
        super.resumeCameraPreview();
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview(width, height);
        if (rect == null) {
            return null;
        }
        try {
            return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
        } catch (Exception e) {
            return null;
        }
    }
}
