package com.google.zxing.common.reedsolomon;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.RecyclerView.ItemAnimator;

public final class GenericGF {
    public static final GenericGF AZTEC_DATA_10;
    public static final GenericGF AZTEC_DATA_12;
    public static final GenericGF AZTEC_DATA_6;
    public static final GenericGF AZTEC_DATA_8;
    public static final GenericGF AZTEC_PARAM;
    public static final GenericGF DATA_MATRIX_FIELD_256;
    public static final GenericGF MAXICODE_FIELD_64;
    public static final GenericGF QR_CODE_FIELD_256;
    private final int[] expTable;
    private final int generatorBase;
    private final int[] logTable;
    private final GenericGFPoly one;
    private final int primitive;
    private final int size;
    private final GenericGFPoly zero;

    static {
        AZTEC_DATA_12 = new GenericGF(4201, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, 1);
        AZTEC_DATA_10 = new GenericGF(1033, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, 1);
        AZTEC_DATA_6 = new GenericGF(67, 64, 1);
        AZTEC_PARAM = new GenericGF(19, 16, 1);
        QR_CODE_FIELD_256 = new GenericGF(285, AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY, 0);
        DATA_MATRIX_FIELD_256 = new GenericGF(301, AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY, 1);
        AZTEC_DATA_8 = DATA_MATRIX_FIELD_256;
        MAXICODE_FIELD_64 = AZTEC_DATA_6;
    }

    public GenericGF(int primitive, int size, int b) {
        int i;
        this.primitive = primitive;
        this.size = size;
        this.generatorBase = b;
        this.expTable = new int[size];
        this.logTable = new int[size];
        int x = 1;
        for (i = 0; i < size; i++) {
            this.expTable[i] = x;
            x *= 2;
            if (x >= size) {
                x = (x ^ primitive) & (size - 1);
            }
        }
        for (i = 0; i < size - 1; i++) {
            this.logTable[this.expTable[i]] = i;
        }
        this.zero = new GenericGFPoly(this, new int[]{0});
        this.one = new GenericGFPoly(this, new int[]{1});
    }

    GenericGFPoly getZero() {
        return this.zero;
    }

    GenericGFPoly getOne() {
        return this.one;
    }

    GenericGFPoly buildMonomial(int degree, int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        } else if (coefficient == 0) {
            return this.zero;
        } else {
            int[] coefficients = new int[(degree + 1)];
            coefficients[0] = coefficient;
            return new GenericGFPoly(this, coefficients);
        }
    }

    static int addOrSubtract(int a, int b) {
        return a ^ b;
    }

    int exp(int a) {
        return this.expTable[a];
    }

    int log(int a) {
        if (a != 0) {
            return this.logTable[a];
        }
        throw new IllegalArgumentException();
    }

    int inverse(int a) {
        if (a != 0) {
            return this.expTable[(this.size - this.logTable[a]) - 1];
        }
        throw new ArithmeticException();
    }

    int multiply(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return this.expTable[(this.logTable[a] + this.logTable[b]) % (this.size - 1)];
    }

    public int getSize() {
        return this.size;
    }

    public int getGeneratorBase() {
        return this.generatorBase;
    }

    public String toString() {
        return "GF(0x" + Integer.toHexString(this.primitive) + ',' + this.size + ')';
    }
}
