package com.google.zxing.oned;

import android.support.v4.media.TransportMediator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import idvital1.idvital1.C0239R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Code128Reader extends OneDReader {
    private static final int CODE_CODE_A = 101;
    private static final int CODE_CODE_B = 100;
    private static final int CODE_CODE_C = 99;
    private static final int CODE_FNC_1 = 102;
    private static final int CODE_FNC_2 = 97;
    private static final int CODE_FNC_3 = 96;
    private static final int CODE_FNC_4_A = 101;
    private static final int CODE_FNC_4_B = 100;
    static final int[][] CODE_PATTERNS;
    private static final int CODE_SHIFT = 98;
    private static final int CODE_START_A = 103;
    private static final int CODE_START_B = 104;
    private static final int CODE_START_C = 105;
    private static final int CODE_STOP = 106;
    private static final float MAX_AVG_VARIANCE = 0.25f;
    private static final float MAX_INDIVIDUAL_VARIANCE = 0.7f;

    static {
        int[][] iArr = new int[C0239R.styleable.AppCompatTheme_radioButtonStyle][];
        iArr[0] = new int[]{2, 1, 2, 2, 2, 2};
        iArr[1] = new int[]{2, 2, 2, 1, 2, 2};
        iArr[2] = new int[]{2, 2, 2, 2, 2, 1};
        iArr[3] = new int[]{1, 2, 1, 2, 2, 3};
        iArr[4] = new int[]{1, 2, 1, 3, 2, 2};
        iArr[5] = new int[]{1, 3, 1, 2, 2, 2};
        iArr[6] = new int[]{1, 2, 2, 2, 1, 3};
        iArr[7] = new int[]{1, 2, 2, 3, 1, 2};
        iArr[8] = new int[]{1, 3, 2, 2, 1, 2};
        iArr[9] = new int[]{2, 2, 1, 2, 1, 3};
        iArr[10] = new int[]{2, 2, 1, 3, 1, 2};
        iArr[11] = new int[]{2, 3, 1, 2, 1, 2};
        iArr[12] = new int[]{1, 1, 2, 2, 3, 2};
        iArr[13] = new int[]{1, 2, 2, 1, 3, 2};
        iArr[14] = new int[]{1, 2, 2, 2, 3, 1};
        iArr[15] = new int[]{1, 1, 3, 2, 2, 2};
        iArr[16] = new int[]{1, 2, 3, 1, 2, 2};
        iArr[17] = new int[]{1, 2, 3, 2, 2, 1};
        iArr[18] = new int[]{2, 2, 3, 2, 1, 1};
        iArr[19] = new int[]{2, 2, 1, 1, 3, 2};
        iArr[20] = new int[]{2, 2, 1, 2, 3, 1};
        iArr[21] = new int[]{2, 1, 3, 2, 1, 2};
        iArr[22] = new int[]{2, 2, 3, 1, 1, 2};
        iArr[23] = new int[]{3, 1, 2, 1, 3, 1};
        iArr[24] = new int[]{3, 1, 1, 2, 2, 2};
        iArr[25] = new int[]{3, 2, 1, 1, 2, 2};
        iArr[26] = new int[]{3, 2, 1, 2, 2, 1};
        iArr[27] = new int[]{3, 1, 2, 2, 1, 2};
        iArr[28] = new int[]{3, 2, 2, 1, 1, 2};
        iArr[29] = new int[]{3, 2, 2, 2, 1, 1};
        iArr[30] = new int[]{2, 1, 2, 1, 2, 3};
        iArr[31] = new int[]{2, 1, 2, 3, 2, 1};
        iArr[32] = new int[]{2, 3, 2, 1, 2, 1};
        iArr[33] = new int[]{1, 1, 1, 3, 2, 3};
        iArr[34] = new int[]{1, 3, 1, 1, 2, 3};
        iArr[35] = new int[]{1, 3, 1, 3, 2, 1};
        iArr[36] = new int[]{1, 1, 2, 3, 1, 3};
        iArr[37] = new int[]{1, 3, 2, 1, 1, 3};
        iArr[38] = new int[]{1, 3, 2, 3, 1, 1};
        iArr[39] = new int[]{2, 1, 1, 3, 1, 3};
        iArr[40] = new int[]{2, 3, 1, 1, 1, 3};
        iArr[41] = new int[]{2, 3, 1, 3, 1, 1};
        iArr[42] = new int[]{1, 1, 2, 1, 3, 3};
        iArr[43] = new int[]{1, 1, 2, 3, 3, 1};
        iArr[44] = new int[]{1, 3, 2, 1, 3, 1};
        iArr[45] = new int[]{1, 1, 3, 1, 2, 3};
        iArr[46] = new int[]{1, 1, 3, 3, 2, 1};
        iArr[47] = new int[]{1, 3, 3, 1, 2, 1};
        iArr[48] = new int[]{3, 1, 3, 1, 2, 1};
        iArr[49] = new int[]{2, 1, 1, 3, 3, 1};
        iArr[50] = new int[]{2, 3, 1, 1, 3, 1};
        iArr[51] = new int[]{2, 1, 3, 1, 1, 3};
        iArr[52] = new int[]{2, 1, 3, 3, 1, 1};
        iArr[53] = new int[]{2, 1, 3, 1, 3, 1};
        iArr[54] = new int[]{3, 1, 1, 1, 2, 3};
        iArr[55] = new int[]{3, 1, 1, 3, 2, 1};
        iArr[56] = new int[]{3, 3, 1, 1, 2, 1};
        iArr[57] = new int[]{3, 1, 2, 1, 1, 3};
        iArr[58] = new int[]{3, 1, 2, 3, 1, 1};
        iArr[59] = new int[]{3, 3, 2, 1, 1, 1};
        iArr[60] = new int[]{3, 1, 4, 1, 1, 1};
        iArr[61] = new int[]{2, 2, 1, 4, 1, 1};
        iArr[62] = new int[]{4, 3, 1, 1, 1, 1};
        iArr[63] = new int[]{1, 1, 1, 2, 2, 4};
        iArr[64] = new int[]{1, 1, 1, 4, 2, 2};
        iArr[65] = new int[]{1, 2, 1, 1, 2, 4};
        iArr[66] = new int[]{1, 2, 1, 4, 2, 1};
        iArr[67] = new int[]{1, 4, 1, 1, 2, 2};
        iArr[68] = new int[]{1, 4, 1, 2, 2, 1};
        iArr[69] = new int[]{1, 1, 2, 2, 1, 4};
        iArr[70] = new int[]{1, 1, 2, 4, 1, 2};
        iArr[71] = new int[]{1, 2, 2, 1, 1, 4};
        iArr[72] = new int[]{1, 2, 2, 4, 1, 1};
        iArr[73] = new int[]{1, 4, 2, 1, 1, 2};
        iArr[74] = new int[]{1, 4, 2, 2, 1, 1};
        iArr[75] = new int[]{2, 4, 1, 2, 1, 1};
        iArr[76] = new int[]{2, 2, 1, 1, 1, 4};
        iArr[77] = new int[]{4, 1, 3, 1, 1, 1};
        iArr[78] = new int[]{2, 4, 1, 1, 1, 2};
        iArr[79] = new int[]{1, 3, 4, 1, 1, 1};
        iArr[80] = new int[]{1, 1, 1, 2, 4, 2};
        iArr[81] = new int[]{1, 2, 1, 1, 4, 2};
        iArr[82] = new int[]{1, 2, 1, 2, 4, 1};
        iArr[83] = new int[]{1, 1, 4, 2, 1, 2};
        iArr[84] = new int[]{1, 2, 4, 1, 1, 2};
        iArr[85] = new int[]{1, 2, 4, 2, 1, 1};
        iArr[86] = new int[]{4, 1, 1, 2, 1, 2};
        iArr[87] = new int[]{4, 2, 1, 1, 1, 2};
        iArr[88] = new int[]{4, 2, 1, 2, 1, 1};
        iArr[89] = new int[]{2, 1, 2, 1, 4, 1};
        iArr[90] = new int[]{2, 1, 4, 1, 2, 1};
        iArr[91] = new int[]{4, 1, 2, 1, 2, 1};
        iArr[92] = new int[]{1, 1, 1, 1, 4, 3};
        iArr[93] = new int[]{1, 1, 1, 3, 4, 1};
        iArr[94] = new int[]{1, 3, 1, 1, 4, 1};
        iArr[95] = new int[]{1, 1, 4, 1, 1, 3};
        iArr[CODE_FNC_3] = new int[]{1, 1, 4, 3, 1, 1};
        iArr[CODE_FNC_2] = new int[]{4, 1, 1, 1, 1, 3};
        iArr[CODE_SHIFT] = new int[]{4, 1, 1, 3, 1, 1};
        iArr[CODE_CODE_C] = new int[]{1, 1, 3, 1, 4, 1};
        iArr[CODE_FNC_4_B] = new int[]{1, 1, 4, 1, 3, 1};
        iArr[CODE_FNC_4_A] = new int[]{3, 1, 1, 1, 4, 1};
        iArr[CODE_FNC_1] = new int[]{4, 1, 1, 1, 3, 1};
        iArr[CODE_START_A] = new int[]{2, 1, 1, 4, 1, 2};
        iArr[CODE_START_B] = new int[]{2, 1, 1, 2, 1, 4};
        iArr[CODE_START_C] = new int[]{2, 1, 1, 2, 3, 2};
        iArr[CODE_STOP] = new int[]{2, 3, 3, 1, 1, 1, 2};
        CODE_PATTERNS = iArr;
    }

    private static int[] findStartPattern(BitArray row) throws NotFoundException {
        int width = row.getSize();
        int rowOffset = row.getNextSet(0);
        int counterPosition = 0;
        int[] counters = new int[6];
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;
        int i = rowOffset;
        while (i < width) {
            if ((row.get(i) ^ isWhite) != 0) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition == patternLength - 1) {
                    float bestVariance = MAX_AVG_VARIANCE;
                    int bestMatch = -1;
                    for (int startCode = CODE_START_A; startCode <= CODE_START_C; startCode++) {
                        float variance = OneDReader.patternMatchVariance(counters, CODE_PATTERNS[startCode], MAX_INDIVIDUAL_VARIANCE);
                        if (variance < bestVariance) {
                            bestVariance = variance;
                            bestMatch = startCode;
                        }
                    }
                    if (bestMatch < 0 || !row.isRange(Math.max(0, patternStart - ((i - patternStart) / 2)), patternStart, false)) {
                        patternStart += counters[0] + counters[1];
                        System.arraycopy(counters, 2, counters, 0, patternLength - 2);
                        counters[patternLength - 2] = 0;
                        counters[patternLength - 1] = 0;
                        counterPosition--;
                    } else {
                        return new int[]{patternStart, i, bestMatch};
                    }
                }
                counterPosition++;
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
            i++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeCode(BitArray row, int[] counters, int rowOffset) throws NotFoundException {
        OneDReader.recordPattern(row, rowOffset, counters);
        float bestVariance = MAX_AVG_VARIANCE;
        int bestMatch = -1;
        for (int d = 0; d < CODE_PATTERNS.length; d++) {
            float variance = OneDReader.patternMatchVariance(counters, CODE_PATTERNS[d], MAX_INDIVIDUAL_VARIANCE);
            if (variance < bestVariance) {
                bestVariance = variance;
                bestMatch = d;
            }
        }
        if (bestMatch >= 0) {
            return bestMatch;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
        boolean convertFNC1;
        int[] startPatternInfo;
        int startCode;
        List<Byte> arrayList;
        int codeSet;
        boolean done;
        boolean isNextShifted;
        StringBuilder stringBuilder;
        int lastStart;
        int nextStart;
        int[] counters;
        int lastCode;
        int code;
        int checksumTotal;
        int multiplier;
        boolean lastCharacterWasPrintable;
        boolean upperMode;
        boolean shiftUpperMode;
        boolean unshift;
        int lastPatternSize;
        int resultLength;
        int rawCodesSize;
        byte[] rawBytes;
        int i;
        if (hints != null) {
            if (hints.containsKey(DecodeHintType.ASSUME_GS1)) {
                convertFNC1 = true;
                startPatternInfo = findStartPattern(row);
                startCode = startPatternInfo[2];
                arrayList = new ArrayList(20);
                arrayList.add(Byte.valueOf((byte) startCode));
                switch (startCode) {
                    case CODE_START_A /*103*/:
                        codeSet = CODE_FNC_4_A;
                        break;
                    case CODE_START_B /*104*/:
                        codeSet = CODE_FNC_4_B;
                        break;
                    case CODE_START_C /*105*/:
                        codeSet = CODE_CODE_C;
                        break;
                    default:
                        throw FormatException.getFormatInstance();
                }
                done = false;
                isNextShifted = false;
                stringBuilder = new StringBuilder(20);
                lastStart = startPatternInfo[0];
                nextStart = startPatternInfo[1];
                counters = new int[6];
                lastCode = 0;
                code = 0;
                checksumTotal = startCode;
                multiplier = 0;
                lastCharacterWasPrintable = true;
                upperMode = false;
                shiftUpperMode = false;
                while (!done) {
                    unshift = isNextShifted;
                    isNextShifted = false;
                    lastCode = code;
                    code = decodeCode(row, counters, nextStart);
                    arrayList.add(Byte.valueOf((byte) code));
                    if (code != CODE_STOP) {
                        lastCharacterWasPrintable = true;
                    }
                    if (code != CODE_STOP) {
                        multiplier++;
                        checksumTotal += multiplier * code;
                    }
                    lastStart = nextStart;
                    for (int counter : counters) {
                        nextStart += counter;
                    }
                    switch (code) {
                        case CODE_START_A /*103*/:
                        case CODE_START_B /*104*/:
                        case CODE_START_C /*105*/:
                            throw FormatException.getFormatInstance();
                        default:
                            switch (codeSet) {
                                case CODE_CODE_C /*99*/:
                                    if (code >= CODE_FNC_4_B) {
                                        if (code != CODE_STOP) {
                                            lastCharacterWasPrintable = false;
                                        }
                                        switch (code) {
                                            case CODE_FNC_4_B /*100*/:
                                                codeSet = CODE_FNC_4_B;
                                                break;
                                            case CODE_FNC_4_A /*101*/:
                                                codeSet = CODE_FNC_4_A;
                                                break;
                                            case CODE_FNC_1 /*102*/:
                                                if (convertFNC1) {
                                                    if (stringBuilder.length() != 0) {
                                                        stringBuilder.append('\u001d');
                                                        break;
                                                    }
                                                    stringBuilder.append("]C1");
                                                    break;
                                                }
                                                break;
                                            case CODE_STOP /*106*/:
                                                done = true;
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    if (code < 10) {
                                        stringBuilder.append('0');
                                    }
                                    stringBuilder.append(code);
                                    break;
                                case CODE_FNC_4_B /*100*/:
                                    if (code >= CODE_FNC_3) {
                                        if (code != CODE_STOP) {
                                            lastCharacterWasPrintable = false;
                                        }
                                        switch (code) {
                                            case CODE_FNC_3 /*96*/:
                                            case CODE_FNC_2 /*97*/:
                                                break;
                                            case CODE_SHIFT /*98*/:
                                                isNextShifted = true;
                                                codeSet = CODE_FNC_4_A;
                                                break;
                                            case CODE_CODE_C /*99*/:
                                                codeSet = CODE_CODE_C;
                                                break;
                                            case CODE_FNC_4_B /*100*/:
                                                if (upperMode || !shiftUpperMode) {
                                                    if (!upperMode && shiftUpperMode) {
                                                        upperMode = false;
                                                        shiftUpperMode = false;
                                                        break;
                                                    }
                                                    shiftUpperMode = true;
                                                    break;
                                                }
                                                upperMode = true;
                                                shiftUpperMode = false;
                                                break;
                                                break;
                                            case CODE_FNC_4_A /*101*/:
                                                codeSet = CODE_FNC_4_A;
                                                break;
                                            case CODE_FNC_1 /*102*/:
                                                if (convertFNC1) {
                                                    if (stringBuilder.length() != 0) {
                                                        stringBuilder.append('\u001d');
                                                        break;
                                                    }
                                                    stringBuilder.append("]C1");
                                                    break;
                                                }
                                                break;
                                            case CODE_STOP /*106*/:
                                                done = true;
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    if (shiftUpperMode != upperMode) {
                                        stringBuilder.append((char) (code + 32));
                                    } else {
                                        stringBuilder.append((char) ((code + 32) + TransportMediator.FLAG_KEY_MEDIA_NEXT));
                                    }
                                    shiftUpperMode = false;
                                    break;
                                case CODE_FNC_4_A /*101*/:
                                    if (code < 64) {
                                        if (code >= CODE_FNC_3) {
                                            if (code != CODE_STOP) {
                                                lastCharacterWasPrintable = false;
                                            }
                                            switch (code) {
                                                case CODE_FNC_3 /*96*/:
                                                case CODE_FNC_2 /*97*/:
                                                    break;
                                                case CODE_SHIFT /*98*/:
                                                    isNextShifted = true;
                                                    codeSet = CODE_FNC_4_B;
                                                    break;
                                                case CODE_CODE_C /*99*/:
                                                    codeSet = CODE_CODE_C;
                                                    break;
                                                case CODE_FNC_4_B /*100*/:
                                                    codeSet = CODE_FNC_4_B;
                                                    break;
                                                case CODE_FNC_4_A /*101*/:
                                                    if (upperMode || !shiftUpperMode) {
                                                        if (!upperMode && shiftUpperMode) {
                                                            upperMode = false;
                                                            shiftUpperMode = false;
                                                            break;
                                                        }
                                                        shiftUpperMode = true;
                                                        break;
                                                    }
                                                    upperMode = true;
                                                    shiftUpperMode = false;
                                                    break;
                                                    break;
                                                case CODE_FNC_1 /*102*/:
                                                    if (convertFNC1) {
                                                        if (stringBuilder.length() != 0) {
                                                            stringBuilder.append('\u001d');
                                                            break;
                                                        }
                                                        stringBuilder.append("]C1");
                                                        break;
                                                    }
                                                    break;
                                                case CODE_STOP /*106*/:
                                                    done = true;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        if (shiftUpperMode != upperMode) {
                                            stringBuilder.append((char) (code - 64));
                                        } else {
                                            stringBuilder.append((char) (code + 64));
                                        }
                                        shiftUpperMode = false;
                                        break;
                                    }
                                    if (shiftUpperMode != upperMode) {
                                        stringBuilder.append((char) (code + 32));
                                    } else {
                                        stringBuilder.append((char) ((code + 32) + TransportMediator.FLAG_KEY_MEDIA_NEXT));
                                    }
                                    shiftUpperMode = false;
                                    break;
                                    break;
                            }
                            if (!unshift) {
                                if (codeSet != CODE_FNC_4_A) {
                                    codeSet = CODE_FNC_4_B;
                                } else {
                                    codeSet = CODE_FNC_4_A;
                                }
                            }
                            break;
                    }
                }
                lastPatternSize = nextStart - lastStart;
                nextStart = row.getNextUnset(nextStart);
                if (!row.isRange(nextStart, Math.min(row.getSize(), ((nextStart - lastStart) / 2) + nextStart), false)) {
                    throw NotFoundException.getNotFoundInstance();
                } else if ((checksumTotal - (multiplier * lastCode)) % CODE_START_A == lastCode) {
                    throw ChecksumException.getChecksumInstance();
                } else {
                    resultLength = stringBuilder.length();
                    if (resultLength != 0) {
                        throw NotFoundException.getNotFoundInstance();
                    }
                    if (resultLength > 0 && lastCharacterWasPrintable) {
                        if (codeSet != CODE_CODE_C) {
                            stringBuilder.delete(resultLength - 2, resultLength);
                        } else {
                            stringBuilder.delete(resultLength - 1, resultLength);
                        }
                    }
                    float left = ((float) (startPatternInfo[1] + startPatternInfo[0])) / 2.0f;
                    float right = ((float) lastStart) + (((float) lastPatternSize) / 2.0f);
                    rawCodesSize = arrayList.size();
                    rawBytes = new byte[rawCodesSize];
                    for (i = 0; i < rawCodesSize; i++) {
                        rawBytes[i] = ((Byte) arrayList.get(i)).byteValue();
                    }
                    String stringBuilder2 = stringBuilder.toString();
                    r34 = new ResultPoint[2];
                    r34[0] = new ResultPoint(left, (float) rowNumber);
                    r34[1] = new ResultPoint(right, (float) rowNumber);
                    return new Result(stringBuilder2, rawBytes, r34, BarcodeFormat.CODE_128);
                }
            }
        }
        convertFNC1 = false;
        startPatternInfo = findStartPattern(row);
        startCode = startPatternInfo[2];
        arrayList = new ArrayList(20);
        arrayList.add(Byte.valueOf((byte) startCode));
        switch (startCode) {
            case CODE_START_A /*103*/:
                codeSet = CODE_FNC_4_A;
                break;
            case CODE_START_B /*104*/:
                codeSet = CODE_FNC_4_B;
                break;
            case CODE_START_C /*105*/:
                codeSet = CODE_CODE_C;
                break;
            default:
                throw FormatException.getFormatInstance();
        }
        done = false;
        isNextShifted = false;
        stringBuilder = new StringBuilder(20);
        lastStart = startPatternInfo[0];
        nextStart = startPatternInfo[1];
        counters = new int[6];
        lastCode = 0;
        code = 0;
        checksumTotal = startCode;
        multiplier = 0;
        lastCharacterWasPrintable = true;
        upperMode = false;
        shiftUpperMode = false;
        while (!done) {
            unshift = isNextShifted;
            isNextShifted = false;
            lastCode = code;
            code = decodeCode(row, counters, nextStart);
            arrayList.add(Byte.valueOf((byte) code));
            if (code != CODE_STOP) {
                lastCharacterWasPrintable = true;
            }
            if (code != CODE_STOP) {
                multiplier++;
                checksumTotal += multiplier * code;
            }
            lastStart = nextStart;
            while (r32 < counters.length) {
                nextStart += counter;
            }
            switch (code) {
                case CODE_START_A /*103*/:
                case CODE_START_B /*104*/:
                case CODE_START_C /*105*/:
                    throw FormatException.getFormatInstance();
                default:
                    switch (codeSet) {
                        case CODE_CODE_C /*99*/:
                            if (code >= CODE_FNC_4_B) {
                                if (code < 10) {
                                    stringBuilder.append('0');
                                }
                                stringBuilder.append(code);
                                break;
                            }
                            if (code != CODE_STOP) {
                                lastCharacterWasPrintable = false;
                            }
                            switch (code) {
                                case CODE_FNC_4_B /*100*/:
                                    codeSet = CODE_FNC_4_B;
                                    break;
                                case CODE_FNC_4_A /*101*/:
                                    codeSet = CODE_FNC_4_A;
                                    break;
                                case CODE_FNC_1 /*102*/:
                                    if (convertFNC1) {
                                        if (stringBuilder.length() != 0) {
                                            stringBuilder.append("]C1");
                                            break;
                                        }
                                        stringBuilder.append('\u001d');
                                        break;
                                    }
                                    break;
                                case CODE_STOP /*106*/:
                                    done = true;
                                    break;
                                default:
                                    break;
                            }
                        case CODE_FNC_4_B /*100*/:
                            if (code >= CODE_FNC_3) {
                                if (shiftUpperMode != upperMode) {
                                    stringBuilder.append((char) ((code + 32) + TransportMediator.FLAG_KEY_MEDIA_NEXT));
                                } else {
                                    stringBuilder.append((char) (code + 32));
                                }
                                shiftUpperMode = false;
                                break;
                            }
                            if (code != CODE_STOP) {
                                lastCharacterWasPrintable = false;
                            }
                            switch (code) {
                                case CODE_FNC_3 /*96*/:
                                case CODE_FNC_2 /*97*/:
                                    break;
                                case CODE_SHIFT /*98*/:
                                    isNextShifted = true;
                                    codeSet = CODE_FNC_4_A;
                                    break;
                                case CODE_CODE_C /*99*/:
                                    codeSet = CODE_CODE_C;
                                    break;
                                case CODE_FNC_4_B /*100*/:
                                    if (!upperMode) {
                                    }
                                    if (!upperMode) {
                                    }
                                    shiftUpperMode = true;
                                    break;
                                case CODE_FNC_4_A /*101*/:
                                    codeSet = CODE_FNC_4_A;
                                    break;
                                case CODE_FNC_1 /*102*/:
                                    if (convertFNC1) {
                                        if (stringBuilder.length() != 0) {
                                            stringBuilder.append("]C1");
                                            break;
                                        }
                                        stringBuilder.append('\u001d');
                                        break;
                                    }
                                    break;
                                case CODE_STOP /*106*/:
                                    done = true;
                                    break;
                                default:
                                    break;
                            }
                        case CODE_FNC_4_A /*101*/:
                            if (code < 64) {
                                if (code >= CODE_FNC_3) {
                                    if (shiftUpperMode != upperMode) {
                                        stringBuilder.append((char) (code + 64));
                                    } else {
                                        stringBuilder.append((char) (code - 64));
                                    }
                                    shiftUpperMode = false;
                                    break;
                                }
                                if (code != CODE_STOP) {
                                    lastCharacterWasPrintable = false;
                                }
                                switch (code) {
                                    case CODE_FNC_3 /*96*/:
                                    case CODE_FNC_2 /*97*/:
                                        break;
                                    case CODE_SHIFT /*98*/:
                                        isNextShifted = true;
                                        codeSet = CODE_FNC_4_B;
                                        break;
                                    case CODE_CODE_C /*99*/:
                                        codeSet = CODE_CODE_C;
                                        break;
                                    case CODE_FNC_4_B /*100*/:
                                        codeSet = CODE_FNC_4_B;
                                        break;
                                    case CODE_FNC_4_A /*101*/:
                                        if (!upperMode) {
                                        }
                                        if (!upperMode) {
                                        }
                                        shiftUpperMode = true;
                                        break;
                                    case CODE_FNC_1 /*102*/:
                                        if (convertFNC1) {
                                            if (stringBuilder.length() != 0) {
                                                stringBuilder.append("]C1");
                                                break;
                                            }
                                            stringBuilder.append('\u001d');
                                            break;
                                        }
                                        break;
                                    case CODE_STOP /*106*/:
                                        done = true;
                                        break;
                                    default:
                                        break;
                                }
                            }
                            if (shiftUpperMode != upperMode) {
                                stringBuilder.append((char) ((code + 32) + TransportMediator.FLAG_KEY_MEDIA_NEXT));
                            } else {
                                stringBuilder.append((char) (code + 32));
                            }
                            shiftUpperMode = false;
                            break;
                            break;
                    }
                    if (!unshift) {
                        if (codeSet != CODE_FNC_4_A) {
                            codeSet = CODE_FNC_4_A;
                        } else {
                            codeSet = CODE_FNC_4_B;
                        }
                    }
                    break;
            }
        }
        lastPatternSize = nextStart - lastStart;
        nextStart = row.getNextUnset(nextStart);
        if (!row.isRange(nextStart, Math.min(row.getSize(), ((nextStart - lastStart) / 2) + nextStart), false)) {
            throw NotFoundException.getNotFoundInstance();
        } else if ((checksumTotal - (multiplier * lastCode)) % CODE_START_A == lastCode) {
            resultLength = stringBuilder.length();
            if (resultLength != 0) {
                if (codeSet != CODE_CODE_C) {
                    stringBuilder.delete(resultLength - 1, resultLength);
                } else {
                    stringBuilder.delete(resultLength - 2, resultLength);
                }
                float left2 = ((float) (startPatternInfo[1] + startPatternInfo[0])) / 2.0f;
                float right2 = ((float) lastStart) + (((float) lastPatternSize) / 2.0f);
                rawCodesSize = arrayList.size();
                rawBytes = new byte[rawCodesSize];
                for (i = 0; i < rawCodesSize; i++) {
                    rawBytes[i] = ((Byte) arrayList.get(i)).byteValue();
                }
                String stringBuilder22 = stringBuilder.toString();
                r34 = new ResultPoint[2];
                r34[0] = new ResultPoint(left2, (float) rowNumber);
                r34[1] = new ResultPoint(right2, (float) rowNumber);
                return new Result(stringBuilder22, rawBytes, r34, BarcodeFormat.CODE_128);
            }
            throw NotFoundException.getNotFoundInstance();
        } else {
            throw ChecksumException.getChecksumInstance();
        }
    }
}
