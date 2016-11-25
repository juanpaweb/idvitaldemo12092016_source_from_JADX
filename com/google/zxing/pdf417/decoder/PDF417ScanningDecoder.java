package com.google.zxing.pdf417.decoder;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.pdf417.PDF417Common;
import com.google.zxing.pdf417.decoder.ec.ErrorCorrection;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;

public final class PDF417ScanningDecoder {
    private static final int CODEWORD_SKEW_SIZE = 2;
    private static final int MAX_EC_CODEWORDS = 512;
    private static final int MAX_ERRORS = 3;
    private static final ErrorCorrection errorCorrection;

    static {
        errorCorrection = new ErrorCorrection();
    }

    private PDF417ScanningDecoder() {
    }

    public static DecoderResult decode(BitMatrix image, ResultPoint imageTopLeft, ResultPoint imageBottomLeft, ResultPoint imageTopRight, ResultPoint imageBottomRight, int minCodewordWidth, int maxCodewordWidth) throws NotFoundException, FormatException, ChecksumException {
        BoundingBox boundingBox = new BoundingBox(image, imageTopLeft, imageBottomLeft, imageTopRight, imageBottomRight);
        DetectionResultColumn leftRowIndicatorColumn = null;
        DetectionResultColumn rightRowIndicatorColumn = null;
        DetectionResult detectionResult = null;
        int i = 0;
        while (i < CODEWORD_SKEW_SIZE) {
            if (imageTopLeft != null) {
                leftRowIndicatorColumn = getRowIndicatorColumn(image, boundingBox, imageTopLeft, true, minCodewordWidth, maxCodewordWidth);
            }
            if (imageTopRight != null) {
                rightRowIndicatorColumn = getRowIndicatorColumn(image, boundingBox, imageTopRight, false, minCodewordWidth, maxCodewordWidth);
            }
            detectionResult = merge(leftRowIndicatorColumn, rightRowIndicatorColumn);
            if (detectionResult == null) {
                throw NotFoundException.getNotFoundInstance();
            } else if (i != 0 || detectionResult.getBoundingBox() == null || (detectionResult.getBoundingBox().getMinY() >= boundingBox.getMinY() && detectionResult.getBoundingBox().getMaxY() <= boundingBox.getMaxY())) {
                detectionResult.setBoundingBox(boundingBox);
                break;
            } else {
                boundingBox = detectionResult.getBoundingBox();
                i++;
            }
        }
        int maxBarcodeColumn = detectionResult.getBarcodeColumnCount() + 1;
        detectionResult.setDetectionResultColumn(0, leftRowIndicatorColumn);
        detectionResult.setDetectionResultColumn(maxBarcodeColumn, rightRowIndicatorColumn);
        boolean leftToRight = leftRowIndicatorColumn != null;
        int barcodeColumnCount = 1;
        while (barcodeColumnCount <= maxBarcodeColumn) {
            int barcodeColumn = leftToRight ? barcodeColumnCount : maxBarcodeColumn - barcodeColumnCount;
            if (detectionResult.getDetectionResultColumn(barcodeColumn) == null) {
                DetectionResultColumn detectionResultColumn;
                if (barcodeColumn == 0 || barcodeColumn == maxBarcodeColumn) {
                    detectionResultColumn = new DetectionResultRowIndicatorColumn(boundingBox, barcodeColumn == 0);
                } else {
                    detectionResultColumn = new DetectionResultColumn(boundingBox);
                }
                detectionResult.setDetectionResultColumn(barcodeColumn, detectionResultColumn);
                int previousStartColumn = -1;
                for (int imageRow = boundingBox.getMinY(); imageRow <= boundingBox.getMaxY(); imageRow++) {
                    int startColumn = getStartColumn(detectionResult, barcodeColumn, imageRow, leftToRight);
                    if (startColumn < 0 || startColumn > boundingBox.getMaxX()) {
                        if (previousStartColumn == -1) {
                        } else {
                            startColumn = previousStartColumn;
                        }
                    }
                    Codeword codeword = detectCodeword(image, boundingBox.getMinX(), boundingBox.getMaxX(), leftToRight, startColumn, imageRow, minCodewordWidth, maxCodewordWidth);
                    if (codeword != null) {
                        detectionResultColumn.setCodeword(imageRow, codeword);
                        previousStartColumn = startColumn;
                        minCodewordWidth = Math.min(minCodewordWidth, codeword.getWidth());
                        maxCodewordWidth = Math.max(maxCodewordWidth, codeword.getWidth());
                    }
                }
            }
            barcodeColumnCount++;
        }
        return createDecoderResult(detectionResult);
    }

    private static DetectionResult merge(DetectionResultRowIndicatorColumn leftRowIndicatorColumn, DetectionResultRowIndicatorColumn rightRowIndicatorColumn) throws NotFoundException, FormatException {
        if (leftRowIndicatorColumn == null && rightRowIndicatorColumn == null) {
            return null;
        }
        BarcodeMetadata barcodeMetadata = getBarcodeMetadata(leftRowIndicatorColumn, rightRowIndicatorColumn);
        if (barcodeMetadata != null) {
            return new DetectionResult(barcodeMetadata, BoundingBox.merge(adjustBoundingBox(leftRowIndicatorColumn), adjustBoundingBox(rightRowIndicatorColumn)));
        }
        return null;
    }

    private static BoundingBox adjustBoundingBox(DetectionResultRowIndicatorColumn rowIndicatorColumn) throws NotFoundException, FormatException {
        if (rowIndicatorColumn == null) {
            return null;
        }
        int[] rowHeights = rowIndicatorColumn.getRowHeights();
        if (rowHeights == null) {
            return null;
        }
        int maxRowHeight = getMax(rowHeights);
        int missingStartRows = 0;
        for (int rowHeight : rowHeights) {
            missingStartRows += maxRowHeight - rowHeight;
            if (rowHeight > 0) {
                break;
            }
        }
        Codeword[] codewords = rowIndicatorColumn.getCodewords();
        int row = 0;
        while (missingStartRows > 0 && codewords[row] == null) {
            missingStartRows--;
            row++;
        }
        int missingEndRows = 0;
        for (row = rowHeights.length - 1; row >= 0; row--) {
            missingEndRows += maxRowHeight - rowHeights[row];
            if (rowHeights[row] > 0) {
                break;
            }
        }
        row = codewords.length - 1;
        while (missingEndRows > 0 && codewords[row] == null) {
            missingEndRows--;
            row--;
        }
        return rowIndicatorColumn.getBoundingBox().addMissingRows(missingStartRows, missingEndRows, rowIndicatorColumn.isLeft());
    }

    private static int getMax(int[] values) {
        int maxValue = -1;
        for (int value : values) {
            maxValue = Math.max(maxValue, value);
        }
        return maxValue;
    }

    private static BarcodeMetadata getBarcodeMetadata(DetectionResultRowIndicatorColumn leftRowIndicatorColumn, DetectionResultRowIndicatorColumn rightRowIndicatorColumn) {
        if (leftRowIndicatorColumn != null) {
            BarcodeMetadata leftBarcodeMetadata = leftRowIndicatorColumn.getBarcodeMetadata();
            if (leftBarcodeMetadata != null) {
                if (rightRowIndicatorColumn != null) {
                    BarcodeMetadata rightBarcodeMetadata = rightRowIndicatorColumn.getBarcodeMetadata();
                    if (rightBarcodeMetadata != null) {
                        if (leftBarcodeMetadata.getColumnCount() == rightBarcodeMetadata.getColumnCount() || leftBarcodeMetadata.getErrorCorrectionLevel() == rightBarcodeMetadata.getErrorCorrectionLevel() || leftBarcodeMetadata.getRowCount() == rightBarcodeMetadata.getRowCount()) {
                            return leftBarcodeMetadata;
                        }
                        return null;
                    }
                }
                return leftBarcodeMetadata;
            }
        }
        if (rightRowIndicatorColumn == null) {
            return null;
        }
        return rightRowIndicatorColumn.getBarcodeMetadata();
    }

    private static DetectionResultRowIndicatorColumn getRowIndicatorColumn(BitMatrix image, BoundingBox boundingBox, ResultPoint startPoint, boolean leftToRight, int minCodewordWidth, int maxCodewordWidth) {
        DetectionResultRowIndicatorColumn rowIndicatorColumn = new DetectionResultRowIndicatorColumn(boundingBox, leftToRight);
        int i = 0;
        while (i < CODEWORD_SKEW_SIZE) {
            int increment = i == 0 ? 1 : -1;
            int startColumn = (int) startPoint.getX();
            int imageRow = (int) startPoint.getY();
            while (imageRow <= boundingBox.getMaxY() && imageRow >= boundingBox.getMinY()) {
                Codeword codeword = detectCodeword(image, 0, image.getWidth(), leftToRight, startColumn, imageRow, minCodewordWidth, maxCodewordWidth);
                if (codeword != null) {
                    rowIndicatorColumn.setCodeword(imageRow, codeword);
                    if (leftToRight) {
                        startColumn = codeword.getStartX();
                    } else {
                        startColumn = codeword.getEndX();
                    }
                }
                imageRow += increment;
            }
            i++;
        }
        return rowIndicatorColumn;
    }

    private static void adjustCodewordCount(DetectionResult detectionResult, BarcodeValue[][] barcodeMatrix) throws NotFoundException {
        int[] numberOfCodewords = barcodeMatrix[0][1].getValue();
        int calculatedNumberOfCodewords = (detectionResult.getBarcodeColumnCount() * detectionResult.getBarcodeRowCount()) - getNumberOfECCodeWords(detectionResult.getBarcodeECLevel());
        if (numberOfCodewords.length == 0) {
            if (calculatedNumberOfCodewords < 1 || calculatedNumberOfCodewords > PDF417Common.MAX_CODEWORDS_IN_BARCODE) {
                throw NotFoundException.getNotFoundInstance();
            }
            barcodeMatrix[0][1].setValue(calculatedNumberOfCodewords);
        } else if (numberOfCodewords[0] != calculatedNumberOfCodewords) {
            barcodeMatrix[0][1].setValue(calculatedNumberOfCodewords);
        }
    }

    private static DecoderResult createDecoderResult(DetectionResult detectionResult) throws FormatException, ChecksumException, NotFoundException {
        BarcodeValue[][] barcodeMatrix = createBarcodeMatrix(detectionResult);
        adjustCodewordCount(detectionResult, barcodeMatrix);
        Collection<Integer> erasures = new ArrayList();
        int[] codewords = new int[(detectionResult.getBarcodeRowCount() * detectionResult.getBarcodeColumnCount())];
        List<int[]> ambiguousIndexValuesList = new ArrayList();
        List<Integer> ambiguousIndexesList = new ArrayList();
        for (int row = 0; row < detectionResult.getBarcodeRowCount(); row++) {
            for (int column = 0; column < detectionResult.getBarcodeColumnCount(); column++) {
                int[] values = barcodeMatrix[row][column + 1].getValue();
                int codewordIndex = (detectionResult.getBarcodeColumnCount() * row) + column;
                if (values.length == 0) {
                    erasures.add(Integer.valueOf(codewordIndex));
                } else if (values.length == 1) {
                    codewords[codewordIndex] = values[0];
                } else {
                    ambiguousIndexesList.add(Integer.valueOf(codewordIndex));
                    ambiguousIndexValuesList.add(values);
                }
            }
        }
        int[][] ambiguousIndexValues = new int[ambiguousIndexValuesList.size()][];
        for (int i = 0; i < ambiguousIndexValues.length; i++) {
            ambiguousIndexValues[i] = (int[]) ambiguousIndexValuesList.get(i);
        }
        return createDecoderResultFromAmbiguousValues(detectionResult.getBarcodeECLevel(), codewords, PDF417Common.toIntArray(erasures), PDF417Common.toIntArray(ambiguousIndexesList), ambiguousIndexValues);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.google.zxing.common.DecoderResult createDecoderResultFromAmbiguousValues(int r7, int[] r8, int[] r9, int[] r10, int[][] r11) throws com.google.zxing.FormatException, com.google.zxing.ChecksumException {
        /*
        r4 = r10.length;
        r0 = new int[r4];
        r2 = 100;
        r3 = r2;
    L_0x0006:
        r2 = r3 + -1;
        if (r3 <= 0) goto L_0x004e;
    L_0x000a:
        r1 = 0;
    L_0x000b:
        r4 = r0.length;
        if (r1 >= r4) goto L_0x001b;
    L_0x000e:
        r4 = r10[r1];
        r5 = r11[r1];
        r6 = r0[r1];
        r5 = r5[r6];
        r8[r4] = r5;
        r1 = r1 + 1;
        goto L_0x000b;
    L_0x001b:
        r4 = decodeCodewords(r8, r7, r9);	 Catch:{ ChecksumException -> 0x0020 }
        return r4;
    L_0x0020:
        r4 = move-exception;
        r4 = r0.length;
        if (r4 != 0) goto L_0x0029;
    L_0x0024:
        r4 = com.google.zxing.ChecksumException.getChecksumInstance();
        throw r4;
    L_0x0029:
        r1 = 0;
    L_0x002a:
        r4 = r0.length;
        if (r1 >= r4) goto L_0x003c;
    L_0x002d:
        r4 = r0[r1];
        r5 = r11[r1];
        r5 = r5.length;
        r5 = r5 + -1;
        if (r4 >= r5) goto L_0x003e;
    L_0x0036:
        r4 = r0[r1];
        r4 = r4 + 1;
        r0[r1] = r4;
    L_0x003c:
        r3 = r2;
        goto L_0x0006;
    L_0x003e:
        r4 = 0;
        r0[r1] = r4;
        r4 = r0.length;
        r4 = r4 + -1;
        if (r1 != r4) goto L_0x004b;
    L_0x0046:
        r4 = com.google.zxing.ChecksumException.getChecksumInstance();
        throw r4;
    L_0x004b:
        r1 = r1 + 1;
        goto L_0x002a;
    L_0x004e:
        r4 = com.google.zxing.ChecksumException.getChecksumInstance();
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.decoder.PDF417ScanningDecoder.createDecoderResultFromAmbiguousValues(int, int[], int[], int[], int[][]):com.google.zxing.common.DecoderResult");
    }

    private static BarcodeValue[][] createBarcodeMatrix(DetectionResult detectionResult) throws FormatException {
        int column;
        BarcodeValue[][] barcodeMatrix = (BarcodeValue[][]) Array.newInstance(BarcodeValue.class, new int[]{detectionResult.getBarcodeRowCount(), detectionResult.getBarcodeColumnCount() + CODEWORD_SKEW_SIZE});
        for (int row = 0; row < barcodeMatrix.length; row++) {
            for (column = 0; column < barcodeMatrix[row].length; column++) {
                barcodeMatrix[row][column] = new BarcodeValue();
            }
        }
        column = 0;
        for (DetectionResultColumn detectionResultColumn : detectionResult.getDetectionResultColumns()) {
            if (detectionResultColumn != null) {
                for (Codeword codeword : detectionResultColumn.getCodewords()) {
                    if (codeword != null) {
                        int rowNumber = codeword.getRowNumber();
                        if (rowNumber < 0) {
                            continue;
                        } else if (rowNumber >= barcodeMatrix.length) {
                            throw FormatException.getFormatInstance();
                        } else {
                            barcodeMatrix[rowNumber][column].setValue(codeword.getValue());
                        }
                    }
                }
                continue;
            }
            column++;
        }
        return barcodeMatrix;
    }

    private static boolean isValidBarcodeColumn(DetectionResult detectionResult, int barcodeColumn) {
        return barcodeColumn >= 0 && barcodeColumn <= detectionResult.getBarcodeColumnCount() + 1;
    }

    private static int getStartColumn(DetectionResult detectionResult, int barcodeColumn, int imageRow, boolean leftToRight) {
        int offset = leftToRight ? 1 : -1;
        Codeword codeword = null;
        if (isValidBarcodeColumn(detectionResult, barcodeColumn - offset)) {
            codeword = detectionResult.getDetectionResultColumn(barcodeColumn - offset).getCodeword(imageRow);
        }
        if (codeword == null) {
            codeword = detectionResult.getDetectionResultColumn(barcodeColumn).getCodewordNearby(imageRow);
            if (codeword != null) {
                return leftToRight ? codeword.getStartX() : codeword.getEndX();
            } else {
                if (isValidBarcodeColumn(detectionResult, barcodeColumn - offset)) {
                    codeword = detectionResult.getDetectionResultColumn(barcodeColumn - offset).getCodewordNearby(imageRow);
                }
                if (codeword != null) {
                    return leftToRight ? codeword.getEndX() : codeword.getStartX();
                } else {
                    int skippedColumns = 0;
                    while (isValidBarcodeColumn(detectionResult, barcodeColumn - offset)) {
                        barcodeColumn -= offset;
                        for (Codeword previousRowCodeword : detectionResult.getDetectionResultColumn(barcodeColumn).getCodewords()) {
                            if (previousRowCodeword != null) {
                                return (leftToRight ? previousRowCodeword.getEndX() : previousRowCodeword.getStartX()) + ((offset * skippedColumns) * (previousRowCodeword.getEndX() - previousRowCodeword.getStartX()));
                            }
                        }
                        skippedColumns++;
                    }
                    return leftToRight ? detectionResult.getBoundingBox().getMinX() : detectionResult.getBoundingBox().getMaxX();
                }
            }
        } else if (leftToRight) {
            return codeword.getEndX();
        } else {
            return codeword.getStartX();
        }
    }

    private static Codeword detectCodeword(BitMatrix image, int minColumn, int maxColumn, boolean leftToRight, int startColumn, int imageRow, int minCodewordWidth, int maxCodewordWidth) {
        startColumn = adjustCodewordStartColumn(image, minColumn, maxColumn, leftToRight, startColumn, imageRow);
        int[] moduleBitCount = getModuleBitCount(image, minColumn, maxColumn, leftToRight, startColumn, imageRow);
        if (moduleBitCount == null) {
            return null;
        }
        int endColumn;
        int codewordBitCount = PDF417Common.getBitCountSum(moduleBitCount);
        if (leftToRight) {
            endColumn = startColumn + codewordBitCount;
        } else {
            for (int i = 0; i < moduleBitCount.length / CODEWORD_SKEW_SIZE; i++) {
                int tmpCount = moduleBitCount[i];
                moduleBitCount[i] = moduleBitCount[(moduleBitCount.length - 1) - i];
                moduleBitCount[(moduleBitCount.length - 1) - i] = tmpCount;
            }
            endColumn = startColumn;
            startColumn = endColumn - codewordBitCount;
        }
        if (!checkCodewordSkew(codewordBitCount, minCodewordWidth, maxCodewordWidth)) {
            return null;
        }
        int decodedValue = PDF417CodewordDecoder.getDecodedValue(moduleBitCount);
        int codeword = PDF417Common.getCodeword(decodedValue);
        if (codeword == -1) {
            return null;
        }
        return new Codeword(startColumn, endColumn, getCodewordBucketNumber(decodedValue), codeword);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int[] getModuleBitCount(com.google.zxing.common.BitMatrix r7, int r8, int r9, boolean r10, int r11, int r12) {
        /*
        r5 = 1;
        r0 = r11;
        r6 = 8;
        r2 = new int[r6];
        r3 = 0;
        if (r10 == 0) goto L_0x0024;
    L_0x0009:
        r1 = r5;
    L_0x000a:
        r4 = r10;
    L_0x000b:
        if (r10 == 0) goto L_0x000f;
    L_0x000d:
        if (r0 < r9) goto L_0x0013;
    L_0x000f:
        if (r10 != 0) goto L_0x002e;
    L_0x0011:
        if (r0 < r8) goto L_0x002e;
    L_0x0013:
        r6 = r2.length;
        if (r3 >= r6) goto L_0x002e;
    L_0x0016:
        r6 = r7.get(r0, r12);
        if (r6 != r4) goto L_0x0026;
    L_0x001c:
        r6 = r2[r3];
        r6 = r6 + 1;
        r2[r3] = r6;
        r0 = r0 + r1;
        goto L_0x000b;
    L_0x0024:
        r1 = -1;
        goto L_0x000a;
    L_0x0026:
        r3 = r3 + 1;
        if (r4 != 0) goto L_0x002c;
    L_0x002a:
        r4 = r5;
    L_0x002b:
        goto L_0x000b;
    L_0x002c:
        r4 = 0;
        goto L_0x002b;
    L_0x002e:
        r5 = r2.length;
        if (r3 == r5) goto L_0x003e;
    L_0x0031:
        if (r10 == 0) goto L_0x0035;
    L_0x0033:
        if (r0 == r9) goto L_0x0039;
    L_0x0035:
        if (r10 != 0) goto L_0x003f;
    L_0x0037:
        if (r0 != r8) goto L_0x003f;
    L_0x0039:
        r5 = r2.length;
        r5 = r5 + -1;
        if (r3 != r5) goto L_0x003f;
    L_0x003e:
        return r2;
    L_0x003f:
        r2 = 0;
        goto L_0x003e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.decoder.PDF417ScanningDecoder.getModuleBitCount(com.google.zxing.common.BitMatrix, int, int, boolean, int, int):int[]");
    }

    private static int getNumberOfECCodeWords(int barcodeECLevel) {
        return CODEWORD_SKEW_SIZE << barcodeECLevel;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int adjustCodewordStartColumn(com.google.zxing.common.BitMatrix r6, int r7, int r8, boolean r9, int r10, int r11) {
        /*
        r5 = 2;
        r3 = 1;
        r0 = r10;
        if (r9 == 0) goto L_0x0020;
    L_0x0005:
        r2 = -1;
    L_0x0006:
        r1 = 0;
    L_0x0007:
        if (r1 >= r5) goto L_0x002d;
    L_0x0009:
        if (r9 == 0) goto L_0x000d;
    L_0x000b:
        if (r0 >= r7) goto L_0x0011;
    L_0x000d:
        if (r9 != 0) goto L_0x0024;
    L_0x000f:
        if (r0 >= r8) goto L_0x0024;
    L_0x0011:
        r4 = r6.get(r0, r11);
        if (r9 != r4) goto L_0x0024;
    L_0x0017:
        r4 = r10 - r0;
        r4 = java.lang.Math.abs(r4);
        if (r4 <= r5) goto L_0x0022;
    L_0x001f:
        return r10;
    L_0x0020:
        r2 = r3;
        goto L_0x0006;
    L_0x0022:
        r0 = r0 + r2;
        goto L_0x0009;
    L_0x0024:
        r2 = -r2;
        if (r9 != 0) goto L_0x002b;
    L_0x0027:
        r9 = r3;
    L_0x0028:
        r1 = r1 + 1;
        goto L_0x0007;
    L_0x002b:
        r9 = 0;
        goto L_0x0028;
    L_0x002d:
        r10 = r0;
        goto L_0x001f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.decoder.PDF417ScanningDecoder.adjustCodewordStartColumn(com.google.zxing.common.BitMatrix, int, int, boolean, int, int):int");
    }

    private static boolean checkCodewordSkew(int codewordSize, int minCodewordWidth, int maxCodewordWidth) {
        return minCodewordWidth + -2 <= codewordSize && codewordSize <= maxCodewordWidth + CODEWORD_SKEW_SIZE;
    }

    private static DecoderResult decodeCodewords(int[] codewords, int ecLevel, int[] erasures) throws FormatException, ChecksumException {
        if (codewords.length == 0) {
            throw FormatException.getFormatInstance();
        }
        int numECCodewords = 1 << (ecLevel + 1);
        int correctedErrorsCount = correctErrors(codewords, erasures, numECCodewords);
        verifyCodewordCount(codewords, numECCodewords);
        DecoderResult decoderResult = DecodedBitStreamParser.decode(codewords, String.valueOf(ecLevel));
        decoderResult.setErrorsCorrected(Integer.valueOf(correctedErrorsCount));
        decoderResult.setErasures(Integer.valueOf(erasures.length));
        return decoderResult;
    }

    private static int correctErrors(int[] codewords, int[] erasures, int numECCodewords) throws ChecksumException {
        if ((erasures == null || erasures.length <= (numECCodewords / CODEWORD_SKEW_SIZE) + MAX_ERRORS) && numECCodewords >= 0 && numECCodewords <= MAX_EC_CODEWORDS) {
            return errorCorrection.decode(codewords, numECCodewords, erasures);
        }
        throw ChecksumException.getChecksumInstance();
    }

    private static void verifyCodewordCount(int[] codewords, int numECCodewords) throws FormatException {
        if (codewords.length < 4) {
            throw FormatException.getFormatInstance();
        }
        int numberOfCodewords = codewords[0];
        if (numberOfCodewords > codewords.length) {
            throw FormatException.getFormatInstance();
        } else if (numberOfCodewords != 0) {
        } else {
            if (numECCodewords < codewords.length) {
                codewords[0] = codewords.length - numECCodewords;
                return;
            }
            throw FormatException.getFormatInstance();
        }
    }

    private static int[] getBitCountForCodeword(int codeword) {
        int[] result = new int[8];
        int previousValue = 0;
        int i = result.length - 1;
        while (true) {
            if ((codeword & 1) != previousValue) {
                previousValue = codeword & 1;
                i--;
                if (i < 0) {
                    return result;
                }
            }
            result[i] = result[i] + 1;
            codeword >>= 1;
        }
    }

    private static int getCodewordBucketNumber(int codeword) {
        return getCodewordBucketNumber(getBitCountForCodeword(codeword));
    }

    private static int getCodewordBucketNumber(int[] moduleBitCount) {
        return ((((moduleBitCount[0] - moduleBitCount[CODEWORD_SKEW_SIZE]) + moduleBitCount[4]) - moduleBitCount[6]) + 9) % 9;
    }

    public static String toString(BarcodeValue[][] barcodeMatrix) {
        Formatter formatter = new Formatter();
        for (int row = 0; row < barcodeMatrix.length; row++) {
            formatter.format("Row %2d: ", new Object[]{Integer.valueOf(row)});
            for (BarcodeValue barcodeValue : barcodeMatrix[row]) {
                if (barcodeValue.getValue().length == 0) {
                    formatter.format("        ", (Object[]) null);
                } else {
                    Object[] objArr = new Object[CODEWORD_SKEW_SIZE];
                    objArr[0] = Integer.valueOf(barcodeValue.getValue()[0]);
                    objArr[1] = barcodeValue.getConfidence(barcodeValue.getValue()[0]);
                    formatter.format("%4d(%2d)", objArr);
                }
            }
            formatter.format("%n", new Object[0]);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
