package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Map;

public final class ITFReader extends OneDReader {
    private static final int[] DEFAULT_ALLOWED_LENGTHS;
    private static final int[] END_PATTERN_REVERSED;
    private static final float MAX_AVG_VARIANCE = 0.38f;
    private static final float MAX_INDIVIDUAL_VARIANCE = 0.78f;
    private static final int f19N = 1;
    static final int[][] PATTERNS;
    private static final int[] START_PATTERN;
    private static final int f20W = 3;
    private int narrowLineWidth;

    public ITFReader() {
        this.narrowLineWidth = -1;
    }

    static {
        DEFAULT_ALLOWED_LENGTHS = new int[]{6, 8, 10, 12, 14};
        START_PATTERN = new int[]{f19N, f19N, f19N, f19N};
        END_PATTERN_REVERSED = new int[]{f19N, f19N, f20W};
        PATTERNS = new int[][]{new int[]{f19N, f19N, f20W, f20W, f19N}, new int[]{f20W, f19N, f19N, f19N, f20W}, new int[]{f19N, f20W, f19N, f19N, f20W}, new int[]{f20W, f20W, f19N, f19N, f19N}, new int[]{f19N, f19N, f20W, f19N, f20W}, new int[]{f20W, f19N, f20W, f19N, f19N}, new int[]{f19N, f20W, f20W, f19N, f19N}, new int[]{f19N, f19N, f19N, f20W, f20W}, new int[]{f20W, f19N, f19N, f20W, f19N}, new int[]{f19N, f20W, f19N, f20W, f19N}};
    }

    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws FormatException, NotFoundException {
        int[] startRange = decodeStart(row);
        int[] endRange = decodeEnd(row);
        StringBuilder result = new StringBuilder(20);
        decodeMiddle(row, startRange[f19N], endRange[0], result);
        String resultString = result.toString();
        int[] allowedLengths = null;
        if (hints != null) {
            allowedLengths = (int[]) hints.get(DecodeHintType.ALLOWED_LENGTHS);
        }
        if (allowedLengths == null) {
            allowedLengths = DEFAULT_ALLOWED_LENGTHS;
        }
        int length = resultString.length();
        boolean lengthOK = false;
        int maxAllowedLength = 0;
        int length2 = allowedLengths.length;
        for (int i = 0; i < length2; i += f19N) {
            int allowedLength = allowedLengths[i];
            if (length == allowedLength) {
                lengthOK = true;
                break;
            }
            if (allowedLength > maxAllowedLength) {
                maxAllowedLength = allowedLength;
            }
        }
        if (!lengthOK && length > maxAllowedLength) {
            lengthOK = true;
        }
        if (lengthOK) {
            ResultPoint[] resultPointArr = new ResultPoint[2];
            resultPointArr[0] = new ResultPoint((float) startRange[f19N], (float) rowNumber);
            resultPointArr[f19N] = new ResultPoint((float) endRange[0], (float) rowNumber);
            return new Result(resultString, null, resultPointArr, BarcodeFormat.ITF);
        }
        throw FormatException.getFormatInstance();
    }

    private static void decodeMiddle(BitArray row, int payloadStart, int payloadEnd, StringBuilder resultString) throws NotFoundException {
        int[] counterDigitPair = new int[10];
        int[] counterBlack = new int[5];
        int[] counterWhite = new int[5];
        while (payloadStart < payloadEnd) {
            OneDReader.recordPattern(row, payloadStart, counterDigitPair);
            for (int k = 0; k < 5; k += f19N) {
                int twoK = k * 2;
                counterBlack[k] = counterDigitPair[twoK];
                counterWhite[k] = counterDigitPair[twoK + f19N];
            }
            resultString.append((char) (decodeDigit(counterBlack) + 48));
            resultString.append((char) (decodeDigit(counterWhite) + 48));
            for (int i = 0; i < counterDigitPair.length; i += f19N) {
                payloadStart += counterDigitPair[i];
            }
        }
    }

    int[] decodeStart(BitArray row) throws NotFoundException {
        int[] startPattern = findGuardPattern(row, skipWhiteSpace(row), START_PATTERN);
        this.narrowLineWidth = (startPattern[f19N] - startPattern[0]) / 4;
        validateQuietZone(row, startPattern[0]);
        return startPattern;
    }

    private void validateQuietZone(BitArray row, int startPattern) throws NotFoundException {
        int quietCount = this.narrowLineWidth * 10;
        if (quietCount >= startPattern) {
            quietCount = startPattern;
        }
        int i = startPattern - 1;
        while (quietCount > 0 && i >= 0 && !row.get(i)) {
            quietCount--;
            i--;
        }
        if (quietCount != 0) {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    private static int skipWhiteSpace(BitArray row) throws NotFoundException {
        int width = row.getSize();
        int endStart = row.getNextSet(0);
        if (endStart != width) {
            return endStart;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    int[] decodeEnd(BitArray row) throws NotFoundException {
        row.reverse();
        try {
            int[] endPattern = findGuardPattern(row, skipWhiteSpace(row), END_PATTERN_REVERSED);
            validateQuietZone(row, endPattern[0]);
            int temp = endPattern[0];
            endPattern[0] = row.getSize() - endPattern[f19N];
            endPattern[f19N] = row.getSize() - temp;
            return endPattern;
        } finally {
            row.reverse();
        }
    }

    private static int[] findGuardPattern(BitArray row, int rowOffset, int[] pattern) throws NotFoundException {
        int patternLength = pattern.length;
        int[] counters = new int[patternLength];
        int width = row.getSize();
        boolean isWhite = false;
        int counterPosition = 0;
        int patternStart = rowOffset;
        for (int x = rowOffset; x < width; x += f19N) {
            if ((row.get(x) ^ isWhite) != 0) {
                counters[counterPosition] = counters[counterPosition] + f19N;
            } else {
                if (counterPosition != patternLength - 1) {
                    counterPosition += f19N;
                } else if (OneDReader.patternMatchVariance(counters, pattern, MAX_INDIVIDUAL_VARIANCE) < MAX_AVG_VARIANCE) {
                    return new int[]{patternStart, x};
                } else {
                    patternStart += counters[0] + counters[f19N];
                    System.arraycopy(counters, 2, counters, 0, patternLength - 2);
                    counters[patternLength - 2] = 0;
                    counters[patternLength - 1] = 0;
                    counterPosition--;
                }
                counters[counterPosition] = f19N;
                if (isWhite) {
                    isWhite = false;
                } else {
                    isWhite = true;
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeDigit(int[] counters) throws NotFoundException {
        float bestVariance = MAX_AVG_VARIANCE;
        int bestMatch = -1;
        int max = PATTERNS.length;
        for (int i = 0; i < max; i += f19N) {
            float variance = OneDReader.patternMatchVariance(counters, PATTERNS[i], MAX_INDIVIDUAL_VARIANCE);
            if (variance < bestVariance) {
                bestVariance = variance;
                bestMatch = i;
            }
        }
        if (bestMatch >= 0) {
            return bestMatch;
        }
        throw NotFoundException.getNotFoundInstance();
    }
}
