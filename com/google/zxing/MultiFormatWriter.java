package com.google.zxing;

import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.CodaBarWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.EAN8Writer;
import com.google.zxing.oned.ITFWriter;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import idvital1.idvital1.C0239R;
import java.util.Map;

public final class MultiFormatWriter implements Writer {

    /* renamed from: com.google.zxing.MultiFormatWriter.1 */
    static /* synthetic */ class C02181 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$BarcodeFormat;

        static {
            $SwitchMap$com$google$zxing$BarcodeFormat = new int[BarcodeFormat.values().length];
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.EAN_8.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.EAN_13.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.UPC_A.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.QR_CODE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.CODE_39.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.CODE_128.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.ITF.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.PDF_417.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.CODABAR.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.DATA_MATRIX.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$zxing$BarcodeFormat[BarcodeFormat.AZTEC.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        return encode(contents, format, width, height, null);
    }

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        Writer writer;
        switch (C02181.$SwitchMap$com$google$zxing$BarcodeFormat[format.ordinal()]) {
            case ItemTouchHelper.UP /*1*/:
                writer = new EAN8Writer();
                break;
            case ItemTouchHelper.DOWN /*2*/:
                writer = new EAN13Writer();
                break;
            case DrawerLayout.LOCK_MODE_UNDEFINED /*3*/:
                writer = new UPCAWriter();
                break;
            case ItemTouchHelper.LEFT /*4*/:
                writer = new QRCodeWriter();
                break;
            case WearableExtender.SIZE_FULL_SCREEN /*5*/:
                writer = new Code39Writer();
                break;
            case FragmentManagerImpl.ANIM_STYLE_FADE_EXIT /*6*/:
                writer = new Code128Writer();
                break;
            case C0239R.styleable.Toolbar_contentInsetLeft /*7*/:
                writer = new ITFWriter();
                break;
            case ItemTouchHelper.RIGHT /*8*/:
                writer = new PDF417Writer();
                break;
            case C0239R.styleable.Toolbar_contentInsetStartWithNavigation /*9*/:
                writer = new CodaBarWriter();
                break;
            case C0239R.styleable.Toolbar_contentInsetEndWithActions /*10*/:
                writer = new DataMatrixWriter();
                break;
            case C0239R.styleable.Toolbar_popupTheme /*11*/:
                writer = new AztecWriter();
                break;
            default:
                throw new IllegalArgumentException("No encoder available for format " + format);
        }
        return writer.encode(contents, format, width, height, hints);
    }
}
