package com.google.zxing.qrcode.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FinderPatternFinder {
    private static final int CENTER_QUORUM = 2;
    protected static final int MAX_MODULES = 57;
    protected static final int MIN_SKIP = 3;
    private final int[] crossCheckStateCount;
    private boolean hasSkipped;
    private final BitMatrix image;
    private final List<FinderPattern> possibleCenters;
    private final ResultPointCallback resultPointCallback;

    private static final class CenterComparator implements Comparator<FinderPattern>, Serializable {
        private final float average;

        private CenterComparator(float f) {
            this.average = f;
        }

        public int compare(FinderPattern center1, FinderPattern center2) {
            if (center2.getCount() != center1.getCount()) {
                return center2.getCount() - center1.getCount();
            }
            float dA = Math.abs(center2.getEstimatedModuleSize() - this.average);
            float dB = Math.abs(center1.getEstimatedModuleSize() - this.average);
            if (dA < dB) {
                return 1;
            }
            return dA == dB ? 0 : -1;
        }
    }

    private static final class FurthestFromAverageComparator implements Comparator<FinderPattern>, Serializable {
        private final float average;

        private FurthestFromAverageComparator(float f) {
            this.average = f;
        }

        public int compare(FinderPattern center1, FinderPattern center2) {
            float dA = Math.abs(center2.getEstimatedModuleSize() - this.average);
            float dB = Math.abs(center1.getEstimatedModuleSize() - this.average);
            if (dA < dB) {
                return -1;
            }
            return dA == dB ? 0 : 1;
        }
    }

    public FinderPatternFinder(BitMatrix image) {
        this(image, null);
    }

    public FinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
        this.image = image;
        this.possibleCenters = new ArrayList();
        this.crossCheckStateCount = new int[5];
        this.resultPointCallback = resultPointCallback;
    }

    protected final BitMatrix getImage() {
        return this.image;
    }

    protected final List<FinderPattern> getPossibleCenters() {
        return this.possibleCenters;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final com.google.zxing.qrcode.detector.FinderPatternInfo find(java.util.Map<com.google.zxing.DecodeHintType, ?> r17) throws com.google.zxing.NotFoundException {
        /*
        r16 = this;
        if (r17 == 0) goto L_0x0070;
    L_0x0002:
        r14 = com.google.zxing.DecodeHintType.TRY_HARDER;
        r0 = r17;
        r14 = r0.containsKey(r14);
        if (r14 == 0) goto L_0x0070;
    L_0x000c:
        r13 = 1;
    L_0x000d:
        if (r17 == 0) goto L_0x0072;
    L_0x000f:
        r14 = com.google.zxing.DecodeHintType.PURE_BARCODE;
        r0 = r17;
        r14 = r0.containsKey(r14);
        if (r14 == 0) goto L_0x0072;
    L_0x0019:
        r10 = 1;
    L_0x001a:
        r0 = r16;
        r14 = r0.image;
        r7 = r14.getHeight();
        r0 = r16;
        r14 = r0.image;
        r8 = r14.getWidth();
        r14 = r7 * 3;
        r5 = r14 / 228;
        r14 = 3;
        if (r5 < r14) goto L_0x0033;
    L_0x0031:
        if (r13 == 0) goto L_0x0034;
    L_0x0033:
        r5 = 3;
    L_0x0034:
        r3 = 0;
        r14 = 5;
        r12 = new int[r14];
        r4 = r5 + -1;
    L_0x003a:
        if (r4 >= r7) goto L_0x0126;
    L_0x003c:
        if (r3 != 0) goto L_0x0126;
    L_0x003e:
        r14 = 0;
        r15 = 0;
        r12[r14] = r15;
        r14 = 1;
        r15 = 0;
        r12[r14] = r15;
        r14 = 2;
        r15 = 0;
        r12[r14] = r15;
        r14 = 3;
        r15 = 0;
        r12[r14] = r15;
        r14 = 4;
        r15 = 0;
        r12[r14] = r15;
        r2 = 0;
        r6 = 0;
    L_0x0054:
        if (r6 >= r8) goto L_0x0108;
    L_0x0056:
        r0 = r16;
        r14 = r0.image;
        r14 = r14.get(r6, r4);
        if (r14 == 0) goto L_0x0074;
    L_0x0060:
        r14 = r2 & 1;
        r15 = 1;
        if (r14 != r15) goto L_0x0067;
    L_0x0065:
        r2 = r2 + 1;
    L_0x0067:
        r14 = r12[r2];
        r14 = r14 + 1;
        r12[r2] = r14;
    L_0x006d:
        r6 = r6 + 1;
        goto L_0x0054;
    L_0x0070:
        r13 = 0;
        goto L_0x000d;
    L_0x0072:
        r10 = 0;
        goto L_0x001a;
    L_0x0074:
        r14 = r2 & 1;
        if (r14 != 0) goto L_0x0100;
    L_0x0078:
        r14 = 4;
        if (r2 != r14) goto L_0x00f6;
    L_0x007b:
        r14 = foundPatternCross(r12);
        if (r14 == 0) goto L_0x00d9;
    L_0x0081:
        r0 = r16;
        r1 = r0.handlePossibleCenter(r12, r4, r6, r10);
        if (r1 == 0) goto L_0x00bd;
    L_0x0089:
        r5 = 2;
        r0 = r16;
        r14 = r0.hasSkipped;
        if (r14 == 0) goto L_0x00aa;
    L_0x0090:
        r3 = r16.haveMultiplyConfirmedCenters();
    L_0x0094:
        r2 = 0;
        r14 = 0;
        r15 = 0;
        r12[r14] = r15;
        r14 = 1;
        r15 = 0;
        r12[r14] = r15;
        r14 = 2;
        r15 = 0;
        r12[r14] = r15;
        r14 = 3;
        r15 = 0;
        r12[r14] = r15;
        r14 = 4;
        r15 = 0;
        r12[r14] = r15;
        goto L_0x006d;
    L_0x00aa:
        r11 = r16.findRowSkip();
        r14 = 2;
        r14 = r12[r14];
        if (r11 <= r14) goto L_0x0094;
    L_0x00b3:
        r14 = 2;
        r14 = r12[r14];
        r14 = r11 - r14;
        r14 = r14 - r5;
        r4 = r4 + r14;
        r6 = r8 + -1;
        goto L_0x0094;
    L_0x00bd:
        r14 = 0;
        r15 = 2;
        r15 = r12[r15];
        r12[r14] = r15;
        r14 = 1;
        r15 = 3;
        r15 = r12[r15];
        r12[r14] = r15;
        r14 = 2;
        r15 = 4;
        r15 = r12[r15];
        r12[r14] = r15;
        r14 = 3;
        r15 = 1;
        r12[r14] = r15;
        r14 = 4;
        r15 = 0;
        r12[r14] = r15;
        r2 = 3;
        goto L_0x006d;
    L_0x00d9:
        r14 = 0;
        r15 = 2;
        r15 = r12[r15];
        r12[r14] = r15;
        r14 = 1;
        r15 = 3;
        r15 = r12[r15];
        r12[r14] = r15;
        r14 = 2;
        r15 = 4;
        r15 = r12[r15];
        r12[r14] = r15;
        r14 = 3;
        r15 = 1;
        r12[r14] = r15;
        r14 = 4;
        r15 = 0;
        r12[r14] = r15;
        r2 = 3;
        goto L_0x006d;
    L_0x00f6:
        r2 = r2 + 1;
        r14 = r12[r2];
        r14 = r14 + 1;
        r12[r2] = r14;
        goto L_0x006d;
    L_0x0100:
        r14 = r12[r2];
        r14 = r14 + 1;
        r12[r2] = r14;
        goto L_0x006d;
    L_0x0108:
        r14 = foundPatternCross(r12);
        if (r14 == 0) goto L_0x0123;
    L_0x010e:
        r0 = r16;
        r1 = r0.handlePossibleCenter(r12, r4, r8, r10);
        if (r1 == 0) goto L_0x0123;
    L_0x0116:
        r14 = 0;
        r5 = r12[r14];
        r0 = r16;
        r14 = r0.hasSkipped;
        if (r14 == 0) goto L_0x0123;
    L_0x011f:
        r3 = r16.haveMultiplyConfirmedCenters();
    L_0x0123:
        r4 = r4 + r5;
        goto L_0x003a;
    L_0x0126:
        r9 = r16.selectBestPatterns();
        com.google.zxing.ResultPoint.orderBestPatterns(r9);
        r14 = new com.google.zxing.qrcode.detector.FinderPatternInfo;
        r14.<init>(r9);
        return r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.FinderPatternFinder.find(java.util.Map):com.google.zxing.qrcode.detector.FinderPatternInfo");
    }

    private static float centerFromEnd(int[] stateCount, int end) {
        return ((float) ((end - stateCount[4]) - stateCount[MIN_SKIP])) - (((float) stateCount[CENTER_QUORUM]) / 2.0f);
    }

    protected static boolean foundPatternCross(int[] stateCount) {
        boolean z = true;
        int totalModuleSize = 0;
        for (int i = 0; i < 5; i++) {
            int count = stateCount[i];
            if (count == 0) {
                return false;
            }
            totalModuleSize += count;
        }
        if (totalModuleSize < 7) {
            return false;
        }
        float moduleSize = ((float) totalModuleSize) / 7.0f;
        float maxVariance = moduleSize / 2.0f;
        if (Math.abs(moduleSize - ((float) stateCount[0])) >= maxVariance || Math.abs(moduleSize - ((float) stateCount[1])) >= maxVariance || Math.abs((3.0f * moduleSize) - ((float) stateCount[CENTER_QUORUM])) >= 3.0f * maxVariance || Math.abs(moduleSize - ((float) stateCount[MIN_SKIP])) >= maxVariance || Math.abs(moduleSize - ((float) stateCount[4])) >= maxVariance) {
            z = false;
        }
        return z;
    }

    private int[] getCrossCheckStateCount() {
        this.crossCheckStateCount[0] = 0;
        this.crossCheckStateCount[1] = 0;
        this.crossCheckStateCount[CENTER_QUORUM] = 0;
        this.crossCheckStateCount[MIN_SKIP] = 0;
        this.crossCheckStateCount[4] = 0;
        return this.crossCheckStateCount;
    }

    private boolean crossCheckDiagonal(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
        int[] stateCount = getCrossCheckStateCount();
        int i = 0;
        while (startI >= i && centerJ >= i && this.image.get(centerJ - i, startI - i)) {
            stateCount[CENTER_QUORUM] = stateCount[CENTER_QUORUM] + 1;
            i++;
        }
        if (startI < i || centerJ < i) {
            return false;
        }
        while (startI >= i && centerJ >= i && !this.image.get(centerJ - i, startI - i) && stateCount[1] <= maxCount) {
            stateCount[1] = stateCount[1] + 1;
            i++;
        }
        if (startI < i || centerJ < i || stateCount[1] > maxCount) {
            return false;
        }
        while (startI >= i && centerJ >= i && this.image.get(centerJ - i, startI - i) && stateCount[0] <= maxCount) {
            stateCount[0] = stateCount[0] + 1;
            i++;
        }
        if (stateCount[0] > maxCount) {
            return false;
        }
        int maxI = this.image.getHeight();
        int maxJ = this.image.getWidth();
        i = 1;
        while (startI + i < maxI && centerJ + i < maxJ && this.image.get(centerJ + i, startI + i)) {
            stateCount[CENTER_QUORUM] = stateCount[CENTER_QUORUM] + 1;
            i++;
        }
        if (startI + i >= maxI || centerJ + i >= maxJ) {
            return false;
        }
        while (startI + i < maxI && centerJ + i < maxJ && !this.image.get(centerJ + i, startI + i) && stateCount[MIN_SKIP] < maxCount) {
            stateCount[MIN_SKIP] = stateCount[MIN_SKIP] + 1;
            i++;
        }
        if (startI + i >= maxI || centerJ + i >= maxJ || stateCount[MIN_SKIP] >= maxCount) {
            return false;
        }
        while (startI + i < maxI && centerJ + i < maxJ && this.image.get(centerJ + i, startI + i) && stateCount[4] < maxCount) {
            stateCount[4] = stateCount[4] + 1;
            i++;
        }
        if (stateCount[4] >= maxCount) {
            return false;
        }
        return Math.abs(((((stateCount[0] + stateCount[1]) + stateCount[CENTER_QUORUM]) + stateCount[MIN_SKIP]) + stateCount[4]) - originalStateCountTotal) < originalStateCountTotal * CENTER_QUORUM && foundPatternCross(stateCount);
    }

    private float crossCheckVertical(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
        BitMatrix image = this.image;
        int maxI = image.getHeight();
        int[] stateCount = getCrossCheckStateCount();
        int i = startI;
        while (i >= 0 && image.get(centerJ, i)) {
            stateCount[CENTER_QUORUM] = stateCount[CENTER_QUORUM] + 1;
            i--;
        }
        if (i < 0) {
            return Float.NaN;
        }
        while (i >= 0 && !image.get(centerJ, i) && stateCount[1] <= maxCount) {
            stateCount[1] = stateCount[1] + 1;
            i--;
        }
        if (i < 0 || stateCount[1] > maxCount) {
            return Float.NaN;
        }
        while (i >= 0 && image.get(centerJ, i) && stateCount[0] <= maxCount) {
            stateCount[0] = stateCount[0] + 1;
            i--;
        }
        if (stateCount[0] > maxCount) {
            return Float.NaN;
        }
        i = startI + 1;
        while (i < maxI && image.get(centerJ, i)) {
            stateCount[CENTER_QUORUM] = stateCount[CENTER_QUORUM] + 1;
            i++;
        }
        if (i == maxI) {
            return Float.NaN;
        }
        while (i < maxI && !image.get(centerJ, i) && stateCount[MIN_SKIP] < maxCount) {
            stateCount[MIN_SKIP] = stateCount[MIN_SKIP] + 1;
            i++;
        }
        if (i == maxI || stateCount[MIN_SKIP] >= maxCount) {
            return Float.NaN;
        }
        while (i < maxI && image.get(centerJ, i) && stateCount[4] < maxCount) {
            stateCount[4] = stateCount[4] + 1;
            i++;
        }
        if (stateCount[4] >= maxCount) {
            return Float.NaN;
        }
        if (Math.abs(((((stateCount[0] + stateCount[1]) + stateCount[CENTER_QUORUM]) + stateCount[MIN_SKIP]) + stateCount[4]) - originalStateCountTotal) * 5 >= originalStateCountTotal * CENTER_QUORUM) {
            return Float.NaN;
        }
        return foundPatternCross(stateCount) ? centerFromEnd(stateCount, i) : Float.NaN;
    }

    private float crossCheckHorizontal(int startJ, int centerI, int maxCount, int originalStateCountTotal) {
        BitMatrix image = this.image;
        int maxJ = image.getWidth();
        int[] stateCount = getCrossCheckStateCount();
        int j = startJ;
        while (j >= 0 && image.get(j, centerI)) {
            stateCount[CENTER_QUORUM] = stateCount[CENTER_QUORUM] + 1;
            j--;
        }
        if (j < 0) {
            return Float.NaN;
        }
        while (j >= 0 && !image.get(j, centerI) && stateCount[1] <= maxCount) {
            stateCount[1] = stateCount[1] + 1;
            j--;
        }
        if (j < 0 || stateCount[1] > maxCount) {
            return Float.NaN;
        }
        while (j >= 0 && image.get(j, centerI) && stateCount[0] <= maxCount) {
            stateCount[0] = stateCount[0] + 1;
            j--;
        }
        if (stateCount[0] > maxCount) {
            return Float.NaN;
        }
        j = startJ + 1;
        while (j < maxJ && image.get(j, centerI)) {
            stateCount[CENTER_QUORUM] = stateCount[CENTER_QUORUM] + 1;
            j++;
        }
        if (j == maxJ) {
            return Float.NaN;
        }
        while (j < maxJ && !image.get(j, centerI) && stateCount[MIN_SKIP] < maxCount) {
            stateCount[MIN_SKIP] = stateCount[MIN_SKIP] + 1;
            j++;
        }
        if (j == maxJ || stateCount[MIN_SKIP] >= maxCount) {
            return Float.NaN;
        }
        while (j < maxJ && image.get(j, centerI) && stateCount[4] < maxCount) {
            stateCount[4] = stateCount[4] + 1;
            j++;
        }
        if (stateCount[4] >= maxCount) {
            return Float.NaN;
        }
        if (Math.abs(((((stateCount[0] + stateCount[1]) + stateCount[CENTER_QUORUM]) + stateCount[MIN_SKIP]) + stateCount[4]) - originalStateCountTotal) * 5 >= originalStateCountTotal) {
            return Float.NaN;
        }
        return foundPatternCross(stateCount) ? centerFromEnd(stateCount, j) : Float.NaN;
    }

    protected final boolean handlePossibleCenter(int[] stateCount, int i, int j, boolean pureBarcode) {
        int stateCountTotal = (((stateCount[0] + stateCount[1]) + stateCount[CENTER_QUORUM]) + stateCount[MIN_SKIP]) + stateCount[4];
        float centerJ = centerFromEnd(stateCount, j);
        float centerI = crossCheckVertical(i, (int) centerJ, stateCount[CENTER_QUORUM], stateCountTotal);
        if (!Float.isNaN(centerI)) {
            centerJ = crossCheckHorizontal((int) centerJ, (int) centerI, stateCount[CENTER_QUORUM], stateCountTotal);
            if (!Float.isNaN(centerJ) && (!pureBarcode || crossCheckDiagonal((int) centerI, (int) centerJ, stateCount[CENTER_QUORUM], stateCountTotal))) {
                float estimatedModuleSize = ((float) stateCountTotal) / 7.0f;
                boolean found = false;
                for (int index = 0; index < this.possibleCenters.size(); index++) {
                    FinderPattern center = (FinderPattern) this.possibleCenters.get(index);
                    if (center.aboutEquals(estimatedModuleSize, centerI, centerJ)) {
                        this.possibleCenters.set(index, center.combineEstimate(centerI, centerJ, estimatedModuleSize));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    FinderPattern point = new FinderPattern(centerJ, centerI, estimatedModuleSize);
                    this.possibleCenters.add(point);
                    if (this.resultPointCallback != null) {
                        this.resultPointCallback.foundPossibleResultPoint(point);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private int findRowSkip() {
        if (this.possibleCenters.size() <= 1) {
            return 0;
        }
        ResultPoint firstConfirmedCenter = null;
        for (ResultPoint center : this.possibleCenters) {
            if (center.getCount() >= CENTER_QUORUM) {
                if (firstConfirmedCenter == null) {
                    firstConfirmedCenter = center;
                } else {
                    this.hasSkipped = true;
                    return ((int) (Math.abs(firstConfirmedCenter.getX() - center.getX()) - Math.abs(firstConfirmedCenter.getY() - center.getY()))) / CENTER_QUORUM;
                }
            }
        }
        return 0;
    }

    private boolean haveMultiplyConfirmedCenters() {
        int confirmedCount = 0;
        float totalModuleSize = 0.0f;
        int max = this.possibleCenters.size();
        for (FinderPattern pattern : this.possibleCenters) {
            if (pattern.getCount() >= CENTER_QUORUM) {
                confirmedCount++;
                totalModuleSize += pattern.getEstimatedModuleSize();
            }
        }
        if (confirmedCount < MIN_SKIP) {
            return false;
        }
        float average = totalModuleSize / ((float) max);
        float totalDeviation = 0.0f;
        for (FinderPattern pattern2 : this.possibleCenters) {
            totalDeviation += Math.abs(pattern2.getEstimatedModuleSize() - average);
        }
        if (totalDeviation <= 0.05f * totalModuleSize) {
            return true;
        }
        return false;
    }

    private FinderPattern[] selectBestPatterns() throws NotFoundException {
        int startSize = this.possibleCenters.size();
        if (startSize < MIN_SKIP) {
            throw NotFoundException.getNotFoundInstance();
        }
        float totalModuleSize;
        if (startSize > MIN_SKIP) {
            totalModuleSize = 0.0f;
            float square = 0.0f;
            for (FinderPattern center : this.possibleCenters) {
                float size = center.getEstimatedModuleSize();
                totalModuleSize += size;
                square += size * size;
            }
            float average = totalModuleSize / ((float) startSize);
            float stdDev = (float) Math.sqrt((double) ((square / ((float) startSize)) - (average * average)));
            Collections.sort(this.possibleCenters, new FurthestFromAverageComparator(null));
            float limit = Math.max(0.2f * average, stdDev);
            int i = 0;
            while (i < this.possibleCenters.size() && this.possibleCenters.size() > MIN_SKIP) {
                if (Math.abs(((FinderPattern) this.possibleCenters.get(i)).getEstimatedModuleSize() - average) > limit) {
                    this.possibleCenters.remove(i);
                    i--;
                }
                i++;
            }
        }
        if (this.possibleCenters.size() > MIN_SKIP) {
            totalModuleSize = 0.0f;
            for (FinderPattern possibleCenter : this.possibleCenters) {
                totalModuleSize += possibleCenter.getEstimatedModuleSize();
            }
            Collections.sort(this.possibleCenters, new CenterComparator(null));
            this.possibleCenters.subList(MIN_SKIP, this.possibleCenters.size()).clear();
        }
        FinderPattern[] finderPatternArr = new FinderPattern[MIN_SKIP];
        finderPatternArr[0] = (FinderPattern) this.possibleCenters.get(0);
        finderPatternArr[1] = (FinderPattern) this.possibleCenters.get(1);
        finderPatternArr[CENTER_QUORUM] = (FinderPattern) this.possibleCenters.get(CENTER_QUORUM);
        return finderPatternArr;
    }
}
