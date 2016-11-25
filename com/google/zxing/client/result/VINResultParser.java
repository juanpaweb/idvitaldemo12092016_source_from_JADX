package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import idvital1.idvital1.C0239R;
import java.util.regex.Pattern;
import me.dm7.barcodescanner.zxing.BuildConfig;

public final class VINResultParser extends ResultParser {
    private static final Pattern AZ09;
    private static final Pattern IOQ;

    static {
        IOQ = Pattern.compile("[IOQ]");
        AZ09 = Pattern.compile("[A-Z0-9]{17}");
    }

    public VINParsedResult parse(Result result) {
        if (result.getBarcodeFormat() != BarcodeFormat.CODE_39) {
            return null;
        }
        String rawText = IOQ.matcher(result.getText()).replaceAll(BuildConfig.FLAVOR).trim();
        if (!AZ09.matcher(rawText).matches()) {
            return null;
        }
        try {
            if (!checkChecksum(rawText)) {
                return null;
            }
            String wmi = rawText.substring(0, 3);
            return new VINParsedResult(rawText, wmi, rawText.substring(3, 9), rawText.substring(9, 17), countryCode(wmi), rawText.substring(3, 8), modelYear(rawText.charAt(9)), rawText.charAt(10), rawText.substring(11));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean checkChecksum(CharSequence vin) {
        int sum = 0;
        for (int i = 0; i < vin.length(); i++) {
            sum += vinPositionWeight(i + 1) * vinCharValue(vin.charAt(i));
        }
        return vin.charAt(8) == checkChar(sum % 11);
    }

    private static int vinCharValue(char c) {
        if (c >= 'A' && c <= 'I') {
            return (c - 65) + 1;
        }
        if (c >= 'J' && c <= 'R') {
            return (c - 74) + 1;
        }
        if (c >= 'S' && c <= 'Z') {
            return (c - 83) + 2;
        }
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        throw new IllegalArgumentException();
    }

    private static int vinPositionWeight(int position) {
        if (position >= 1 && position <= 7) {
            return 9 - position;
        }
        if (position == 8) {
            return 10;
        }
        if (position == 9) {
            return 0;
        }
        if (position >= 10 && position <= 17) {
            return 19 - position;
        }
        throw new IllegalArgumentException();
    }

    private static char checkChar(int remainder) {
        if (remainder < 10) {
            return (char) (remainder + 48);
        }
        if (remainder == 10) {
            return 'X';
        }
        throw new IllegalArgumentException();
    }

    private static int modelYear(char c) {
        if (c >= 'E' && c <= 'H') {
            return (c - 69) + 1984;
        }
        if (c >= 'J' && c <= 'N') {
            return (c - 74) + 1988;
        }
        if (c == 'P') {
            return 1993;
        }
        if (c >= 'R' && c <= 'T') {
            return (c - 82) + 1994;
        }
        if (c >= 'V' && c <= 'Y') {
            return (c - 86) + 1997;
        }
        if (c >= '1' && c <= '9') {
            return (c - 49) + 2001;
        }
        if (c >= 'A' && c <= 'D') {
            return (c - 65) + 2010;
        }
        throw new IllegalArgumentException();
    }

    private static String countryCode(CharSequence wmi) {
        char c1 = wmi.charAt(0);
        char c2 = wmi.charAt(1);
        switch (c1) {
            case C0239R.styleable.AppCompatTheme_homeAsUpIndicator /*49*/:
            case C0239R.styleable.AppCompatTheme_buttonBarButtonStyle /*52*/:
            case C0239R.styleable.AppCompatTheme_selectableItemBackground /*53*/:
                return "US";
            case C0239R.styleable.AppCompatTheme_actionButtonStyle /*50*/:
                return "CA";
            case C0239R.styleable.AppCompatTheme_buttonBarStyle /*51*/:
                if (c2 >= 'A' && c2 <= 'W') {
                    return "MX";
                }
            case C0239R.styleable.AppCompatTheme_dividerHorizontal /*57*/:
                if ((c2 >= 'A' && c2 <= 'E') || (c2 >= '3' && c2 <= '9')) {
                    return "BR";
                }
            case C0239R.styleable.AppCompatTheme_listPreferredItemPaddingRight /*74*/:
                if (c2 >= 'A' && c2 <= 'T') {
                    return "JP";
                }
            case C0239R.styleable.AppCompatTheme_dropDownListViewStyle /*75*/:
                if (c2 >= 'L' && c2 <= 'R') {
                    return "KO";
                }
            case C0239R.styleable.AppCompatTheme_listPopupWindowStyle /*76*/:
                return "CN";
            case C0239R.styleable.AppCompatTheme_textAppearanceListItem /*77*/:
                if (c2 >= 'A' && c2 <= 'E') {
                    return "IN";
                }
            case C0239R.styleable.AppCompatTheme_colorPrimary /*83*/:
                if (c2 >= 'A' && c2 <= 'M') {
                    return "UK";
                }
                if (c2 >= 'N' && c2 <= 'T') {
                    return "DE";
                }
                break;
            case C0239R.styleable.AppCompatTheme_colorControlNormal /*86*/:
                if (c2 >= 'F' && c2 <= 'R') {
                    return "FR";
                }
                if (c2 >= 'S' && c2 <= 'W') {
                    return "ES";
                }
                break;
            case C0239R.styleable.AppCompatTheme_colorControlActivated /*87*/:
                return "DE";
            case C0239R.styleable.AppCompatTheme_colorControlHighlight /*88*/:
                if (c2 == '0' || (c2 >= '3' && c2 <= '9')) {
                    return "RU";
                }
            case C0239R.styleable.AppCompatTheme_colorSwitchThumbNormal /*90*/:
                if (c2 >= 'A' && c2 <= 'R') {
                    return "IT";
                }
        }
        return null;
    }
}
