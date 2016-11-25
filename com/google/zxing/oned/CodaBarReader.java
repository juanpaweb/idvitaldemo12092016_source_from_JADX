package com.google.zxing.oned;

import android.support.v4.media.TransportMediator;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class CodaBarReader extends OneDReader {
    static final char[] ALPHABET;
    private static final String ALPHABET_STRING = "0123456789-$:/.+ABCD";
    static final int[] CHARACTER_ENCODINGS;
    private static final float MAX_ACCEPTABLE = 2.0f;
    private static final int MIN_CHARACTER_LENGTH = 3;
    private static final float PADDING = 1.5f;
    private static final char[] STARTEND_ENCODING;
    private int counterLength;
    private int[] counters;
    private final StringBuilder decodeRowResult;

    static {
        ALPHABET = ALPHABET_STRING.toCharArray();
        CHARACTER_ENCODINGS = new int[]{MIN_CHARACTER_LENGTH, 6, 9, 96, 18, 66, 33, 36, 48, 72, 12, 24, 69, 81, 84, 21, 26, 41, 11, 14};
        STARTEND_ENCODING = new char[]{'A', 'B', 'C', 'D'};
    }

    public CodaBarReader() {
        this.decodeRowResult = new StringBuilder(20);
        this.counters = new int[80];
        this.counterLength = 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.zxing.Result decodeRow(int r21, com.google.zxing.common.BitArray r22, java.util.Map<com.google.zxing.DecodeHintType, ?> r23) throws com.google.zxing.NotFoundException {
        /*
        r20 = this;
        r0 = r20;
        r13 = r0.counters;
        r14 = 0;
        java.util.Arrays.fill(r13, r14);
        r0 = r20;
        r1 = r22;
        r0.setCounters(r1);
        r10 = r20.findStartPattern();
        r7 = r10;
        r0 = r20;
        r13 = r0.decodeRowResult;
        r14 = 0;
        r13.setLength(r14);
    L_0x001c:
        r0 = r20;
        r2 = r0.toNarrowWidePattern(r7);
        r13 = -1;
        if (r2 != r13) goto L_0x002a;
    L_0x0025:
        r13 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r13;
    L_0x002a:
        r0 = r20;
        r13 = r0.decodeRowResult;
        r14 = (char) r2;
        r13.append(r14);
        r7 = r7 + 8;
        r0 = r20;
        r13 = r0.decodeRowResult;
        r13 = r13.length();
        r14 = 1;
        if (r13 <= r14) goto L_0x0064;
    L_0x003f:
        r13 = STARTEND_ENCODING;
        r14 = ALPHABET;
        r14 = r14[r2];
        r13 = arrayContains(r13, r14);
        if (r13 == 0) goto L_0x0064;
    L_0x004b:
        r0 = r20;
        r13 = r0.counters;
        r14 = r7 + -1;
        r12 = r13[r14];
        r5 = 0;
        r4 = -8;
    L_0x0055:
        r13 = -1;
        if (r4 >= r13) goto L_0x006b;
    L_0x0058:
        r0 = r20;
        r13 = r0.counters;
        r14 = r7 + r4;
        r13 = r13[r14];
        r5 = r5 + r13;
        r4 = r4 + 1;
        goto L_0x0055;
    L_0x0064:
        r0 = r20;
        r13 = r0.counterLength;
        if (r7 < r13) goto L_0x001c;
    L_0x006a:
        goto L_0x004b;
    L_0x006b:
        r0 = r20;
        r13 = r0.counterLength;
        if (r7 >= r13) goto L_0x007a;
    L_0x0071:
        r13 = r5 / 2;
        if (r12 >= r13) goto L_0x007a;
    L_0x0075:
        r13 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r13;
    L_0x007a:
        r0 = r20;
        r0.validatePattern(r10);
        r4 = 0;
    L_0x0080:
        r0 = r20;
        r13 = r0.decodeRowResult;
        r13 = r13.length();
        if (r4 >= r13) goto L_0x00a0;
    L_0x008a:
        r0 = r20;
        r13 = r0.decodeRowResult;
        r14 = ALPHABET;
        r0 = r20;
        r15 = r0.decodeRowResult;
        r15 = r15.charAt(r4);
        r14 = r14[r15];
        r13.setCharAt(r4, r14);
        r4 = r4 + 1;
        goto L_0x0080;
    L_0x00a0:
        r0 = r20;
        r13 = r0.decodeRowResult;
        r14 = 0;
        r11 = r13.charAt(r14);
        r13 = STARTEND_ENCODING;
        r13 = arrayContains(r13, r11);
        if (r13 != 0) goto L_0x00b6;
    L_0x00b1:
        r13 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r13;
    L_0x00b6:
        r0 = r20;
        r13 = r0.decodeRowResult;
        r0 = r20;
        r14 = r0.decodeRowResult;
        r14 = r14.length();
        r14 = r14 + -1;
        r3 = r13.charAt(r14);
        r13 = STARTEND_ENCODING;
        r13 = arrayContains(r13, r3);
        if (r13 != 0) goto L_0x00d5;
    L_0x00d0:
        r13 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r13;
    L_0x00d5:
        r0 = r20;
        r13 = r0.decodeRowResult;
        r13 = r13.length();
        r14 = 3;
        if (r13 > r14) goto L_0x00e5;
    L_0x00e0:
        r13 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r13;
    L_0x00e5:
        if (r23 == 0) goto L_0x00f1;
    L_0x00e7:
        r13 = com.google.zxing.DecodeHintType.RETURN_CODABAR_START_END;
        r0 = r23;
        r13 = r0.containsKey(r13);
        if (r13 != 0) goto L_0x010a;
    L_0x00f1:
        r0 = r20;
        r13 = r0.decodeRowResult;
        r0 = r20;
        r14 = r0.decodeRowResult;
        r14 = r14.length();
        r14 = r14 + -1;
        r13.deleteCharAt(r14);
        r0 = r20;
        r13 = r0.decodeRowResult;
        r14 = 0;
        r13.deleteCharAt(r14);
    L_0x010a:
        r9 = 0;
        r4 = 0;
    L_0x010c:
        if (r4 >= r10) goto L_0x0118;
    L_0x010e:
        r0 = r20;
        r13 = r0.counters;
        r13 = r13[r4];
        r9 = r9 + r13;
        r4 = r4 + 1;
        goto L_0x010c;
    L_0x0118:
        r6 = (float) r9;
        r4 = r10;
    L_0x011a:
        r13 = r7 + -1;
        if (r4 >= r13) goto L_0x0128;
    L_0x011e:
        r0 = r20;
        r13 = r0.counters;
        r13 = r13[r4];
        r9 = r9 + r13;
        r4 = r4 + 1;
        goto L_0x011a;
    L_0x0128:
        r8 = (float) r9;
        r13 = new com.google.zxing.Result;
        r0 = r20;
        r14 = r0.decodeRowResult;
        r14 = r14.toString();
        r15 = 0;
        r16 = 2;
        r0 = r16;
        r0 = new com.google.zxing.ResultPoint[r0];
        r16 = r0;
        r17 = 0;
        r18 = new com.google.zxing.ResultPoint;
        r0 = r21;
        r0 = (float) r0;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        r0.<init>(r6, r1);
        r16[r17] = r18;
        r17 = 1;
        r18 = new com.google.zxing.ResultPoint;
        r0 = r21;
        r0 = (float) r0;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        r0.<init>(r8, r1);
        r16[r17] = r18;
        r17 = com.google.zxing.BarcodeFormat.CODABAR;
        r13.<init>(r14, r15, r16, r17);
        return r13;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.CodaBarReader.decodeRow(int, com.google.zxing.common.BitArray, java.util.Map):com.google.zxing.Result");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void validatePattern(int r16) throws com.google.zxing.NotFoundException {
        /*
        r15 = this;
        r11 = 4;
        r10 = new int[r11];
        r10 = {0, 0, 0, 0};
        r11 = 4;
        r1 = new int[r11];
        r1 = {0, 0, 0, 0};
        r11 = r15.decodeRowResult;
        r11 = r11.length();
        r2 = r11 + -1;
        r8 = r16;
        r3 = 0;
    L_0x0017:
        r11 = CHARACTER_ENCODINGS;
        r12 = r15.decodeRowResult;
        r12 = r12.charAt(r3);
        r7 = r11[r12];
        r4 = 6;
    L_0x0022:
        if (r4 < 0) goto L_0x0042;
    L_0x0024:
        r11 = r4 & 1;
        r12 = r7 & 1;
        r12 = r12 * 2;
        r0 = r11 + r12;
        r11 = r10[r0];
        r12 = r15.counters;
        r13 = r8 + r4;
        r12 = r12[r13];
        r11 = r11 + r12;
        r10[r0] = r11;
        r11 = r1[r0];
        r11 = r11 + 1;
        r1[r0] = r11;
        r7 = r7 >> 1;
        r4 = r4 + -1;
        goto L_0x0022;
    L_0x0042:
        if (r3 < r2) goto L_0x0089;
    L_0x0044:
        r11 = 4;
        r5 = new float[r11];
        r11 = 4;
        r6 = new float[r11];
        r3 = 0;
    L_0x004b:
        r11 = 2;
        if (r3 >= r11) goto L_0x008e;
    L_0x004e:
        r11 = 0;
        r6[r3] = r11;
        r11 = r3 + 2;
        r12 = r10[r3];
        r12 = (float) r12;
        r13 = r1[r3];
        r13 = (float) r13;
        r12 = r12 / r13;
        r13 = r3 + 2;
        r13 = r10[r13];
        r13 = (float) r13;
        r14 = r3 + 2;
        r14 = r1[r14];
        r14 = (float) r14;
        r13 = r13 / r14;
        r12 = r12 + r13;
        r13 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r12 = r12 / r13;
        r6[r11] = r12;
        r11 = r3 + 2;
        r11 = r6[r11];
        r5[r3] = r11;
        r11 = r3 + 2;
        r12 = r3 + 2;
        r12 = r10[r12];
        r12 = (float) r12;
        r13 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r12 = r12 * r13;
        r13 = 1069547520; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r12 = r12 + r13;
        r13 = r3 + 2;
        r13 = r1[r13];
        r13 = (float) r13;
        r12 = r12 / r13;
        r5[r11] = r12;
        r3 = r3 + 1;
        goto L_0x004b;
    L_0x0089:
        r8 = r8 + 8;
        r3 = r3 + 1;
        goto L_0x0017;
    L_0x008e:
        r8 = r16;
        r3 = 0;
    L_0x0091:
        r11 = CHARACTER_ENCODINGS;
        r12 = r15.decodeRowResult;
        r12 = r12.charAt(r3);
        r7 = r11[r12];
        r4 = 6;
    L_0x009c:
        if (r4 < 0) goto L_0x00c4;
    L_0x009e:
        r11 = r4 & 1;
        r12 = r7 & 1;
        r12 = r12 * 2;
        r0 = r11 + r12;
        r11 = r15.counters;
        r12 = r8 + r4;
        r9 = r11[r12];
        r11 = (float) r9;
        r12 = r6[r0];
        r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1));
        if (r11 < 0) goto L_0x00ba;
    L_0x00b3:
        r11 = (float) r9;
        r12 = r5[r0];
        r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1));
        if (r11 <= 0) goto L_0x00bf;
    L_0x00ba:
        r11 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r11;
    L_0x00bf:
        r7 = r7 >> 1;
        r4 = r4 + -1;
        goto L_0x009c;
    L_0x00c4:
        if (r3 < r2) goto L_0x00c7;
    L_0x00c6:
        return;
    L_0x00c7:
        r8 = r8 + 8;
        r3 = r3 + 1;
        goto L_0x0091;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.CodaBarReader.validatePattern(int):void");
    }

    private void setCounters(BitArray row) throws NotFoundException {
        this.counterLength = 0;
        int i = row.getNextUnset(0);
        int end = row.getSize();
        if (i >= end) {
            throw NotFoundException.getNotFoundInstance();
        }
        boolean isWhite = true;
        int count = 0;
        while (i < end) {
            if ((row.get(i) ^ isWhite) != 0) {
                count++;
            } else {
                counterAppend(count);
                count = 1;
                isWhite = !isWhite;
            }
            i++;
        }
        counterAppend(count);
    }

    private void counterAppend(int e) {
        this.counters[this.counterLength] = e;
        this.counterLength++;
        if (this.counterLength >= this.counters.length) {
            int[] temp = new int[(this.counterLength * 2)];
            System.arraycopy(this.counters, 0, temp, 0, this.counterLength);
            this.counters = temp;
        }
    }

    private int findStartPattern() throws NotFoundException {
        int i = 1;
        while (i < this.counterLength) {
            int charOffset = toNarrowWidePattern(i);
            if (charOffset != -1 && arrayContains(STARTEND_ENCODING, ALPHABET[charOffset])) {
                int patternSize = 0;
                for (int j = i; j < i + 7; j++) {
                    patternSize += this.counters[j];
                }
                if (i == 1 || this.counters[i - 1] >= patternSize / 2) {
                    return i;
                }
            }
            i += 2;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    static boolean arrayContains(char[] array, char key) {
        if (array == null) {
            return false;
        }
        for (char c : array) {
            if (c == key) {
                return true;
            }
        }
        return false;
    }

    private int toNarrowWidePattern(int position) {
        int end = position + 7;
        if (end >= this.counterLength) {
            return -1;
        }
        int j;
        int i;
        int[] theCounters = this.counters;
        int maxBar = 0;
        int minBar = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (j = position; j < end; j += 2) {
            int currentCounter = theCounters[j];
            if (currentCounter < minBar) {
                minBar = currentCounter;
            }
            if (currentCounter > maxBar) {
                maxBar = currentCounter;
            }
        }
        int thresholdBar = (minBar + maxBar) / 2;
        int maxSpace = 0;
        int minSpace = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (j = position + 1; j < end; j += 2) {
            currentCounter = theCounters[j];
            if (currentCounter < minSpace) {
                minSpace = currentCounter;
            }
            if (currentCounter > maxSpace) {
                maxSpace = currentCounter;
            }
        }
        int thresholdSpace = (minSpace + maxSpace) / 2;
        int bitmask = TransportMediator.FLAG_KEY_MEDIA_NEXT;
        int pattern = 0;
        for (i = 0; i < 7; i++) {
            int threshold;
            if ((i & 1) == 0) {
                threshold = thresholdBar;
            } else {
                threshold = thresholdSpace;
            }
            bitmask >>= 1;
            if (theCounters[position + i] > threshold) {
                pattern |= bitmask;
            }
        }
        for (i = 0; i < CHARACTER_ENCODINGS.length; i++) {
            if (CHARACTER_ENCODINGS[i] == pattern) {
                return i;
            }
        }
        return -1;
    }
}
