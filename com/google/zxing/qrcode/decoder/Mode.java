package com.google.zxing.qrcode.decoder;

import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import idvital1.idvital1.C0239R;

public enum Mode {
    TERMINATOR(new int[]{0, 0, 0}, 0),
    NUMERIC(new int[]{10, 12, 14}, 1),
    ALPHANUMERIC(new int[]{9, 11, 13}, 2),
    STRUCTURED_APPEND(new int[]{0, 0, 0}, 3),
    BYTE(new int[]{8, 16, 16}, 4),
    ECI(new int[]{0, 0, 0}, 7),
    KANJI(new int[]{8, 10, 12}, 8),
    FNC1_FIRST_POSITION(new int[]{0, 0, 0}, 5),
    FNC1_SECOND_POSITION(new int[]{0, 0, 0}, 9),
    HANZI(new int[]{8, 10, 12}, 13);
    
    private final int bits;
    private final int[] characterCountBitsForVersions;

    private Mode(int[] characterCountBitsForVersions, int bits) {
        this.characterCountBitsForVersions = characterCountBitsForVersions;
        this.bits = bits;
    }

    public static Mode forBits(int bits) {
        switch (bits) {
            case ItemTouchHelper.ACTION_STATE_IDLE /*0*/:
                return TERMINATOR;
            case ItemTouchHelper.UP /*1*/:
                return NUMERIC;
            case ItemTouchHelper.DOWN /*2*/:
                return ALPHANUMERIC;
            case DrawerLayout.LOCK_MODE_UNDEFINED /*3*/:
                return STRUCTURED_APPEND;
            case ItemTouchHelper.LEFT /*4*/:
                return BYTE;
            case WearableExtender.SIZE_FULL_SCREEN /*5*/:
                return FNC1_FIRST_POSITION;
            case C0239R.styleable.Toolbar_contentInsetLeft /*7*/:
                return ECI;
            case ItemTouchHelper.RIGHT /*8*/:
                return KANJI;
            case C0239R.styleable.Toolbar_contentInsetStartWithNavigation /*9*/:
                return FNC1_SECOND_POSITION;
            case C0239R.styleable.Toolbar_subtitleTextAppearance /*13*/:
                return HANZI;
            default:
                throw new IllegalArgumentException();
        }
    }

    public int getCharacterCountBits(Version version) {
        int offset;
        int number = version.getVersionNumber();
        if (number <= 9) {
            offset = 0;
        } else if (number <= 26) {
            offset = 1;
        } else {
            offset = 2;
        }
        return this.characterCountBitsForVersions[offset];
    }

    public int getBits() {
        return this.bits;
    }
}
