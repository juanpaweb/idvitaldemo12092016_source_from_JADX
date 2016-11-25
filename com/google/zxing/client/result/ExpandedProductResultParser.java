package com.google.zxing.client.result;

import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import idvital1.idvital1.C0239R;
import java.util.HashMap;
import java.util.Map;

public final class ExpandedProductResultParser extends ResultParser {
    public ExpandedProductParsedResult parse(Result result) {
        if (result.getBarcodeFormat() != BarcodeFormat.RSS_EXPANDED) {
            return null;
        }
        String rawText = ResultParser.getMassagedText(result);
        String productID = null;
        String sscc = null;
        String lotNumber = null;
        String productionDate = null;
        String packagingDate = null;
        String bestBeforeDate = null;
        String expirationDate = null;
        String weight = null;
        String weightType = null;
        String weightIncrement = null;
        String price = null;
        String priceIncrement = null;
        String priceCurrency = null;
        Map<String, String> uncommonAIs = new HashMap();
        int i = 0;
        while (i < rawText.length()) {
            String ai = findAIvalue(i, rawText);
            if (ai != null) {
                i += ai.length() + 2;
                String value = findValue(i, rawText);
                i += value.length();
                Object obj = -1;
                switch (ai.hashCode()) {
                    case 1536:
                        if (ai.equals("00")) {
                            obj = null;
                            break;
                        }
                        break;
                    case 1537:
                        if (ai.equals("01")) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 1567:
                        if (ai.equals("10")) {
                            obj = 2;
                            break;
                        }
                        break;
                    case 1568:
                        if (ai.equals("11")) {
                            obj = 3;
                            break;
                        }
                        break;
                    case 1570:
                        if (ai.equals("13")) {
                            obj = 4;
                            break;
                        }
                        break;
                    case 1572:
                        if (ai.equals("15")) {
                            obj = 5;
                            break;
                        }
                        break;
                    case 1574:
                        if (ai.equals("17")) {
                            obj = 6;
                            break;
                        }
                        break;
                    case 1567966:
                        if (ai.equals("3100")) {
                            obj = 7;
                            break;
                        }
                        break;
                    case 1567967:
                        if (ai.equals("3101")) {
                            obj = 8;
                            break;
                        }
                        break;
                    case 1567968:
                        if (ai.equals("3102")) {
                            obj = 9;
                            break;
                        }
                        break;
                    case 1567969:
                        if (ai.equals("3103")) {
                            obj = 10;
                            break;
                        }
                        break;
                    case 1567970:
                        if (ai.equals("3104")) {
                            obj = 11;
                            break;
                        }
                        break;
                    case 1567971:
                        if (ai.equals("3105")) {
                            obj = 12;
                            break;
                        }
                        break;
                    case 1567972:
                        if (ai.equals("3106")) {
                            obj = 13;
                            break;
                        }
                        break;
                    case 1567973:
                        if (ai.equals("3107")) {
                            obj = 14;
                            break;
                        }
                        break;
                    case 1567974:
                        if (ai.equals("3108")) {
                            obj = 15;
                            break;
                        }
                        break;
                    case 1567975:
                        if (ai.equals("3109")) {
                            obj = 16;
                            break;
                        }
                        break;
                    case 1568927:
                        if (ai.equals("3200")) {
                            obj = 17;
                            break;
                        }
                        break;
                    case 1568928:
                        if (ai.equals("3201")) {
                            obj = 18;
                            break;
                        }
                        break;
                    case 1568929:
                        if (ai.equals("3202")) {
                            obj = 19;
                            break;
                        }
                        break;
                    case 1568930:
                        if (ai.equals("3203")) {
                            obj = 20;
                            break;
                        }
                        break;
                    case 1568931:
                        if (ai.equals("3204")) {
                            obj = 21;
                            break;
                        }
                        break;
                    case 1568932:
                        if (ai.equals("3205")) {
                            obj = 22;
                            break;
                        }
                        break;
                    case 1568933:
                        if (ai.equals("3206")) {
                            obj = 23;
                            break;
                        }
                        break;
                    case 1568934:
                        if (ai.equals("3207")) {
                            obj = 24;
                            break;
                        }
                        break;
                    case 1568935:
                        if (ai.equals("3208")) {
                            obj = 25;
                            break;
                        }
                        break;
                    case 1568936:
                        if (ai.equals("3209")) {
                            obj = 26;
                            break;
                        }
                        break;
                    case 1575716:
                        if (ai.equals("3920")) {
                            obj = 27;
                            break;
                        }
                        break;
                    case 1575717:
                        if (ai.equals("3921")) {
                            obj = 28;
                            break;
                        }
                        break;
                    case 1575718:
                        if (ai.equals("3922")) {
                            obj = 29;
                            break;
                        }
                        break;
                    case 1575719:
                        if (ai.equals("3923")) {
                            obj = 30;
                            break;
                        }
                        break;
                    case 1575747:
                        if (ai.equals("3930")) {
                            obj = 31;
                            break;
                        }
                        break;
                    case 1575748:
                        if (ai.equals("3931")) {
                            obj = 32;
                            break;
                        }
                        break;
                    case 1575749:
                        if (ai.equals("3932")) {
                            obj = 33;
                            break;
                        }
                        break;
                    case 1575750:
                        if (ai.equals("3933")) {
                            obj = 34;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case ItemTouchHelper.ACTION_STATE_IDLE /*0*/:
                        sscc = value;
                        break;
                    case ItemTouchHelper.UP /*1*/:
                        productID = value;
                        break;
                    case ItemTouchHelper.DOWN /*2*/:
                        lotNumber = value;
                        break;
                    case DrawerLayout.LOCK_MODE_UNDEFINED /*3*/:
                        productionDate = value;
                        break;
                    case ItemTouchHelper.LEFT /*4*/:
                        packagingDate = value;
                        break;
                    case WearableExtender.SIZE_FULL_SCREEN /*5*/:
                        bestBeforeDate = value;
                        break;
                    case FragmentManagerImpl.ANIM_STYLE_FADE_EXIT /*6*/:
                        expirationDate = value;
                        break;
                    case C0239R.styleable.Toolbar_contentInsetLeft /*7*/:
                    case ItemTouchHelper.RIGHT /*8*/:
                    case C0239R.styleable.Toolbar_contentInsetStartWithNavigation /*9*/:
                    case C0239R.styleable.Toolbar_contentInsetEndWithActions /*10*/:
                    case C0239R.styleable.Toolbar_popupTheme /*11*/:
                    case C0239R.styleable.Toolbar_titleTextAppearance /*12*/:
                    case C0239R.styleable.Toolbar_subtitleTextAppearance /*13*/:
                    case C0239R.styleable.Toolbar_titleMargin /*14*/:
                    case C0239R.styleable.Toolbar_titleMarginStart /*15*/:
                    case ItemTouchHelper.START /*16*/:
                        weight = value;
                        weightType = ExpandedProductParsedResult.KILOGRAM;
                        weightIncrement = ai.substring(3);
                        break;
                    case C0239R.styleable.Toolbar_titleMarginTop /*17*/:
                    case C0239R.styleable.Toolbar_titleMarginBottom /*18*/:
                    case C0239R.styleable.Toolbar_titleMargins /*19*/:
                    case C0239R.styleable.Toolbar_maxButtonHeight /*20*/:
                    case C0239R.styleable.Toolbar_buttonGravity /*21*/:
                    case C0239R.styleable.Toolbar_collapseIcon /*22*/:
                    case C0239R.styleable.Toolbar_collapseContentDescription /*23*/:
                    case C0239R.styleable.Toolbar_navigationIcon /*24*/:
                    case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
                    case C0239R.styleable.Toolbar_logoDescription /*26*/:
                        weight = value;
                        weightType = ExpandedProductParsedResult.POUND;
                        weightIncrement = ai.substring(3);
                        break;
                    case C0239R.styleable.Toolbar_titleTextColor /*27*/:
                    case C0239R.styleable.Toolbar_subtitleTextColor /*28*/:
                    case C0239R.styleable.AppCompatTheme_actionModeBackground /*29*/:
                    case C0239R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
                        price = value;
                        priceIncrement = ai.substring(3);
                        break;
                    case C0239R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
                    case ItemTouchHelper.END /*32*/:
                    case C0239R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
                    case C0239R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
                        if (value.length() >= 4) {
                            price = value.substring(3);
                            priceCurrency = value.substring(0, 3);
                            priceIncrement = ai.substring(3);
                            break;
                        }
                        return null;
                    default:
                        uncommonAIs.put(ai, value);
                        break;
                }
            }
            return null;
        }
        return new ExpandedProductParsedResult(rawText, productID, sscc, lotNumber, productionDate, packagingDate, bestBeforeDate, expirationDate, weight, weightType, weightIncrement, price, priceIncrement, priceCurrency, uncommonAIs);
    }

    private static String findAIvalue(int i, String rawText) {
        if (rawText.charAt(i) != '(') {
            return null;
        }
        CharSequence rawTextAux = rawText.substring(i + 1);
        StringBuilder buf = new StringBuilder();
        for (int index = 0; index < rawTextAux.length(); index++) {
            char currentChar = rawTextAux.charAt(index);
            if (currentChar == ')') {
                return buf.toString();
            }
            if (currentChar < '0' || currentChar > '9') {
                return null;
            }
            buf.append(currentChar);
        }
        return buf.toString();
    }

    private static String findValue(int i, String rawText) {
        StringBuilder buf = new StringBuilder();
        String rawTextAux = rawText.substring(i);
        for (int index = 0; index < rawTextAux.length(); index++) {
            char c = rawTextAux.charAt(index);
            if (c == '(') {
                if (findAIvalue(index, rawTextAux) != null) {
                    break;
                }
                buf.append('(');
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
