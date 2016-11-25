package com.google.zxing.oned.rss.expanded;

import android.support.v4.media.TransportMediator;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import com.google.zxing.oned.rss.AbstractRSSReader;
import com.google.zxing.oned.rss.DataCharacter;
import com.google.zxing.oned.rss.FinderPattern;
import com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder;
import idvital1.idvital1.C0239R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.dm7.barcodescanner.zxing.BuildConfig;

public final class RSSExpandedReader extends AbstractRSSReader {
    private static final int[] EVEN_TOTAL_SUBSET;
    private static final int[][] FINDER_PATTERNS;
    private static final int[][] FINDER_PATTERN_SEQUENCES;
    private static final int FINDER_PAT_A = 0;
    private static final int FINDER_PAT_B = 1;
    private static final int FINDER_PAT_C = 2;
    private static final int FINDER_PAT_D = 3;
    private static final int FINDER_PAT_E = 4;
    private static final int FINDER_PAT_F = 5;
    private static final int[] GSUM;
    private static final int MAX_PAIRS = 11;
    private static final int[] SYMBOL_WIDEST;
    private static final int[][] WEIGHTS;
    private final List<ExpandedPair> pairs;
    private final List<ExpandedRow> rows;
    private final int[] startEnd;
    private boolean startFromEven;

    public RSSExpandedReader() {
        this.pairs = new ArrayList(MAX_PAIRS);
        this.rows = new ArrayList();
        this.startEnd = new int[FINDER_PAT_C];
    }

    static {
        SYMBOL_WIDEST = new int[]{7, FINDER_PAT_F, FINDER_PAT_E, FINDER_PAT_D, FINDER_PAT_B};
        EVEN_TOTAL_SUBSET = new int[]{FINDER_PAT_E, 20, 52, C0239R.styleable.AppCompatTheme_checkboxStyle, 204};
        GSUM = new int[]{FINDER_PAT_A, 348, 1388, 2948, 3988};
        FINDER_PATTERNS = new int[][]{new int[]{FINDER_PAT_B, 8, FINDER_PAT_E, FINDER_PAT_B}, new int[]{FINDER_PAT_D, 6, FINDER_PAT_E, FINDER_PAT_B}, new int[]{FINDER_PAT_D, FINDER_PAT_E, 6, FINDER_PAT_B}, new int[]{FINDER_PAT_D, FINDER_PAT_C, 8, FINDER_PAT_B}, new int[]{FINDER_PAT_C, 6, FINDER_PAT_F, FINDER_PAT_B}, new int[]{FINDER_PAT_C, FINDER_PAT_C, 9, FINDER_PAT_B}};
        WEIGHTS = new int[][]{new int[]{FINDER_PAT_B, FINDER_PAT_D, 9, 27, 81, 32, 96, 77}, new int[]{20, 60, 180, 118, 143, 7, 21, 63}, new int[]{189, 145, 13, 39, 117, 140, 209, 205}, new int[]{193, 157, 49, 147, 19, 57, 171, 91}, new int[]{62, 186, 136, 197, 169, 85, 44, 132}, new int[]{185, 133, 188, 142, FINDER_PAT_E, 12, 36, C0239R.styleable.AppCompatTheme_ratingBarStyle}, new int[]{C0239R.styleable.AppCompatTheme_switchStyle, TransportMediator.FLAG_KEY_MEDIA_NEXT, 173, 97, 80, 29, 87, 50}, new int[]{150, 28, 84, 41, 123, 158, 52, 156}, new int[]{46, 138, 203, 187, 139, 206, 196, 166}, new int[]{76, 17, 51, 153, 37, C0239R.styleable.AppCompatTheme_seekBarStyle, 122, 155}, new int[]{43, 129, 176, C0239R.styleable.AppCompatTheme_editTextStyle, C0239R.styleable.AppCompatTheme_radioButtonStyle, C0239R.styleable.AppCompatTheme_ratingBarStyleSmall, 119, 146}, new int[]{16, 48, 144, 10, 30, 90, 59, 177}, new int[]{C0239R.styleable.AppCompatTheme_ratingBarStyleIndicator, 116, 137, Callback.DEFAULT_DRAG_ANIMATION_DURATION, 178, C0239R.styleable.AppCompatTheme_spinnerStyle, 125, 164}, new int[]{70, 210, 208, 202, BuildConfig.VERSION_CODE, TransportMediator.KEYCODE_MEDIA_RECORD, 179, 115}, new int[]{134, 191, 151, 31, 93, 68, 204, 190}, new int[]{148, 22, 66, 198, 172, 94, 71, FINDER_PAT_C}, new int[]{6, 18, 54, 162, 64, 192, 154, 40}, new int[]{120, 149, 25, 75, 14, 42, TransportMediator.KEYCODE_MEDIA_PLAY, 167}, new int[]{79, 26, 78, 23, 69, 207, 199, 175}, new int[]{C0239R.styleable.AppCompatTheme_buttonStyleSmall, 98, 83, 38, C0239R.styleable.AppCompatTheme_listMenuViewStyle, 131, 182, 124}, new int[]{161, 61, 183, TransportMediator.KEYCODE_MEDIA_PAUSE, 170, 88, 53, 159}, new int[]{55, 165, 73, 8, 24, 72, FINDER_PAT_F, 15}, new int[]{45, 135, 194, 160, 58, 174, 100, 89}};
        FINDER_PATTERN_SEQUENCES = new int[][]{new int[]{FINDER_PAT_A, FINDER_PAT_A}, new int[]{FINDER_PAT_A, FINDER_PAT_B, FINDER_PAT_B}, new int[]{FINDER_PAT_A, FINDER_PAT_C, FINDER_PAT_B, FINDER_PAT_D}, new int[]{FINDER_PAT_A, FINDER_PAT_E, FINDER_PAT_B, FINDER_PAT_D, FINDER_PAT_C}, new int[]{FINDER_PAT_A, FINDER_PAT_E, FINDER_PAT_B, FINDER_PAT_D, FINDER_PAT_D, FINDER_PAT_F}, new int[]{FINDER_PAT_A, FINDER_PAT_E, FINDER_PAT_B, FINDER_PAT_D, FINDER_PAT_E, FINDER_PAT_F, FINDER_PAT_F}, new int[]{FINDER_PAT_A, FINDER_PAT_A, FINDER_PAT_B, FINDER_PAT_B, FINDER_PAT_C, FINDER_PAT_C, FINDER_PAT_D, FINDER_PAT_D}, new int[]{FINDER_PAT_A, FINDER_PAT_A, FINDER_PAT_B, FINDER_PAT_B, FINDER_PAT_C, FINDER_PAT_C, FINDER_PAT_D, FINDER_PAT_E, FINDER_PAT_E}, new int[]{FINDER_PAT_A, FINDER_PAT_A, FINDER_PAT_B, FINDER_PAT_B, FINDER_PAT_C, FINDER_PAT_C, FINDER_PAT_D, FINDER_PAT_E, FINDER_PAT_F, FINDER_PAT_F}, new int[]{FINDER_PAT_A, FINDER_PAT_A, FINDER_PAT_B, FINDER_PAT_B, FINDER_PAT_C, FINDER_PAT_D, FINDER_PAT_D, FINDER_PAT_E, FINDER_PAT_E, FINDER_PAT_F, FINDER_PAT_F}};
    }

    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> map) throws NotFoundException, FormatException {
        this.pairs.clear();
        this.startFromEven = false;
        try {
            return constructResult(decodeRow2pairs(rowNumber, row));
        } catch (NotFoundException e) {
            this.pairs.clear();
            this.startFromEven = true;
            return constructResult(decodeRow2pairs(rowNumber, row));
        }
    }

    public void reset() {
        this.pairs.clear();
        this.rows.clear();
    }

    List<ExpandedPair> decodeRow2pairs(int rowNumber, BitArray row) throws NotFoundException {
        while (true) {
            try {
                this.pairs.add(retrieveNextPair(row, this.pairs, rowNumber));
            } catch (NotFoundException nfe) {
                if (this.pairs.isEmpty()) {
                    throw nfe;
                } else if (checkChecksum()) {
                    return this.pairs;
                } else {
                    boolean tryStackedDecode;
                    if (this.rows.isEmpty()) {
                        tryStackedDecode = false;
                    } else {
                        tryStackedDecode = true;
                    }
                    storeRow(rowNumber, false);
                    if (tryStackedDecode) {
                        List<ExpandedPair> ps = checkRows(false);
                        if (ps != null) {
                            return ps;
                        }
                        ps = checkRows(true);
                        if (ps != null) {
                            return ps;
                        }
                    }
                    throw NotFoundException.getNotFoundInstance();
                }
            }
        }
    }

    private List<ExpandedPair> checkRows(boolean reverse) {
        if (this.rows.size() > 25) {
            this.rows.clear();
            return null;
        }
        this.pairs.clear();
        if (reverse) {
            Collections.reverse(this.rows);
        }
        List<ExpandedPair> ps = null;
        try {
            ps = checkRows(new ArrayList(), FINDER_PAT_A);
        } catch (NotFoundException e) {
        }
        if (!reverse) {
            return ps;
        }
        Collections.reverse(this.rows);
        return ps;
    }

    private List<ExpandedPair> checkRows(List<ExpandedRow> collectedRows, int currentRow) throws NotFoundException {
        for (int i = currentRow; i < this.rows.size(); i += FINDER_PAT_B) {
            ExpandedRow row = (ExpandedRow) this.rows.get(i);
            this.pairs.clear();
            int size = collectedRows.size();
            for (int j = FINDER_PAT_A; j < size; j += FINDER_PAT_B) {
                this.pairs.addAll(((ExpandedRow) collectedRows.get(j)).getPairs());
            }
            this.pairs.addAll(row.getPairs());
            if (isValidSequence(this.pairs)) {
                if (checkChecksum()) {
                    return this.pairs;
                }
                List<ExpandedRow> rs = new ArrayList();
                rs.addAll(collectedRows);
                rs.add(row);
                try {
                    return checkRows(rs, i + FINDER_PAT_B);
                } catch (NotFoundException e) {
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static boolean isValidSequence(List<ExpandedPair> pairs) {
        int[][] iArr = FINDER_PATTERN_SEQUENCES;
        int length = iArr.length;
        for (int i = FINDER_PAT_A; i < length; i += FINDER_PAT_B) {
            int[] sequence = iArr[i];
            if (pairs.size() <= sequence.length) {
                boolean stop = true;
                for (int j = FINDER_PAT_A; j < pairs.size(); j += FINDER_PAT_B) {
                    if (((ExpandedPair) pairs.get(j)).getFinderPattern().getValue() != sequence[j]) {
                        stop = false;
                        break;
                    }
                }
                if (stop) {
                    return true;
                }
            }
        }
        return false;
    }

    private void storeRow(int rowNumber, boolean wasReversed) {
        int insertPos = FINDER_PAT_A;
        boolean prevIsSame = false;
        boolean nextIsSame = false;
        while (insertPos < this.rows.size()) {
            ExpandedRow erow = (ExpandedRow) this.rows.get(insertPos);
            if (erow.getRowNumber() > rowNumber) {
                nextIsSame = erow.isEquivalent(this.pairs);
                break;
            } else {
                prevIsSame = erow.isEquivalent(this.pairs);
                insertPos += FINDER_PAT_B;
            }
        }
        if (!nextIsSame && !prevIsSame && !isPartialRow(this.pairs, this.rows)) {
            this.rows.add(insertPos, new ExpandedRow(this.pairs, rowNumber, wasReversed));
            removePartialRows(this.pairs, this.rows);
        }
    }

    private static void removePartialRows(List<ExpandedPair> pairs, List<ExpandedRow> rows) {
        Iterator<ExpandedRow> iterator = rows.iterator();
        while (iterator.hasNext()) {
            ExpandedRow r = (ExpandedRow) iterator.next();
            if (r.getPairs().size() != pairs.size()) {
                boolean allFound = true;
                for (ExpandedPair p : r.getPairs()) {
                    boolean found = false;
                    for (ExpandedPair pp : pairs) {
                        if (p.equals(pp)) {
                            found = true;
                            break;
                            continue;
                        }
                    }
                    if (!found) {
                        allFound = false;
                        break;
                    }
                }
                if (allFound) {
                    iterator.remove();
                }
            }
        }
    }

    private static boolean isPartialRow(Iterable<ExpandedPair> pairs, Iterable<ExpandedRow> rows) {
        for (ExpandedRow r : rows) {
            boolean allFound = true;
            for (ExpandedPair p : pairs) {
                boolean found = false;
                for (ExpandedPair pp : r.getPairs()) {
                    if (p.equals(pp)) {
                        found = true;
                        break;
                        continue;
                    }
                }
                if (!found) {
                    allFound = false;
                    break;
                    continue;
                }
            }
            if (allFound) {
                return true;
            }
        }
        return false;
    }

    List<ExpandedRow> getRows() {
        return this.rows;
    }

    static Result constructResult(List<ExpandedPair> pairs) throws NotFoundException, FormatException {
        String resultingString = AbstractExpandedDecoder.createDecoder(BitArrayBuilder.buildBitArray(pairs)).parseInformation();
        ResultPoint[] firstPoints = ((ExpandedPair) pairs.get(FINDER_PAT_A)).getFinderPattern().getResultPoints();
        ResultPoint[] lastPoints = ((ExpandedPair) pairs.get(pairs.size() - 1)).getFinderPattern().getResultPoints();
        ResultPoint[] resultPointArr = new ResultPoint[FINDER_PAT_E];
        resultPointArr[FINDER_PAT_A] = firstPoints[FINDER_PAT_A];
        resultPointArr[FINDER_PAT_B] = firstPoints[FINDER_PAT_B];
        resultPointArr[FINDER_PAT_C] = lastPoints[FINDER_PAT_A];
        resultPointArr[FINDER_PAT_D] = lastPoints[FINDER_PAT_B];
        return new Result(resultingString, null, resultPointArr, BarcodeFormat.RSS_EXPANDED);
    }

    private boolean checkChecksum() {
        ExpandedPair firstPair = (ExpandedPair) this.pairs.get(FINDER_PAT_A);
        DataCharacter checkCharacter = firstPair.getLeftChar();
        DataCharacter firstCharacter = firstPair.getRightChar();
        if (firstCharacter == null) {
            return false;
        }
        int checksum = firstCharacter.getChecksumPortion();
        int s = FINDER_PAT_C;
        for (int i = FINDER_PAT_B; i < this.pairs.size(); i += FINDER_PAT_B) {
            ExpandedPair currentPair = (ExpandedPair) this.pairs.get(i);
            checksum += currentPair.getLeftChar().getChecksumPortion();
            s += FINDER_PAT_B;
            DataCharacter currentRightChar = currentPair.getRightChar();
            if (currentRightChar != null) {
                checksum += currentRightChar.getChecksumPortion();
                s += FINDER_PAT_B;
            }
        }
        if (((s - 4) * 211) + (checksum % 211) == checkCharacter.getValue()) {
            return true;
        }
        return false;
    }

    private static int getNextSecondBar(BitArray row, int initialPos) {
        if (row.get(initialPos)) {
            return row.getNextSet(row.getNextUnset(initialPos));
        }
        return row.getNextUnset(row.getNextSet(initialPos));
    }

    ExpandedPair retrieveNextPair(BitArray row, List<ExpandedPair> previousPairs, int rowNumber) throws NotFoundException {
        boolean isOddPattern;
        FinderPattern pattern;
        if (previousPairs.size() % FINDER_PAT_C == 0) {
            isOddPattern = true;
        } else {
            isOddPattern = false;
        }
        if (this.startFromEven) {
            if (isOddPattern) {
                isOddPattern = false;
            } else {
                isOddPattern = true;
            }
        }
        boolean keepFinding = true;
        int forcedOffset = -1;
        do {
            findNextPair(row, previousPairs, forcedOffset);
            pattern = parseFoundFinderPattern(row, rowNumber, isOddPattern);
            if (pattern == null) {
                forcedOffset = getNextSecondBar(row, this.startEnd[FINDER_PAT_A]);
                continue;
            } else {
                keepFinding = false;
                continue;
            }
        } while (keepFinding);
        DataCharacter leftChar = decodeDataCharacter(row, pattern, isOddPattern, true);
        if (previousPairs.isEmpty() || !((ExpandedPair) previousPairs.get(previousPairs.size() - 1)).mustBeLast()) {
            DataCharacter rightChar;
            try {
                rightChar = decodeDataCharacter(row, pattern, isOddPattern, false);
            } catch (NotFoundException e) {
                rightChar = null;
            }
            return new ExpandedPair(leftChar, rightChar, pattern, true);
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private void findNextPair(BitArray row, List<ExpandedPair> previousPairs, int forcedOffset) throws NotFoundException {
        int rowOffset;
        int[] counters = getDecodeFinderCounters();
        counters[FINDER_PAT_A] = FINDER_PAT_A;
        counters[FINDER_PAT_B] = FINDER_PAT_A;
        counters[FINDER_PAT_C] = FINDER_PAT_A;
        counters[FINDER_PAT_D] = FINDER_PAT_A;
        int width = row.getSize();
        if (forcedOffset >= 0) {
            rowOffset = forcedOffset;
        } else if (previousPairs.isEmpty()) {
            rowOffset = FINDER_PAT_A;
        } else {
            rowOffset = ((ExpandedPair) previousPairs.get(previousPairs.size() - 1)).getFinderPattern().getStartEnd()[FINDER_PAT_B];
        }
        boolean searchingEvenPair = previousPairs.size() % FINDER_PAT_C != 0;
        if (this.startFromEven) {
            searchingEvenPair = !searchingEvenPair;
        }
        boolean isWhite = false;
        while (rowOffset < width) {
            isWhite = !row.get(rowOffset);
            if (!isWhite) {
                break;
            }
            rowOffset += FINDER_PAT_B;
        }
        int counterPosition = FINDER_PAT_A;
        int patternStart = rowOffset;
        for (int x = rowOffset; x < width; x += FINDER_PAT_B) {
            if ((row.get(x) ^ isWhite) != 0) {
                counters[counterPosition] = counters[counterPosition] + FINDER_PAT_B;
            } else {
                if (counterPosition == FINDER_PAT_D) {
                    if (searchingEvenPair) {
                        reverseCounters(counters);
                    }
                    if (AbstractRSSReader.isFinderPattern(counters)) {
                        this.startEnd[FINDER_PAT_A] = patternStart;
                        this.startEnd[FINDER_PAT_B] = x;
                        return;
                    }
                    if (searchingEvenPair) {
                        reverseCounters(counters);
                    }
                    patternStart += counters[FINDER_PAT_A] + counters[FINDER_PAT_B];
                    counters[FINDER_PAT_A] = counters[FINDER_PAT_C];
                    counters[FINDER_PAT_B] = counters[FINDER_PAT_D];
                    counters[FINDER_PAT_C] = FINDER_PAT_A;
                    counters[FINDER_PAT_D] = FINDER_PAT_A;
                    counterPosition--;
                } else {
                    counterPosition += FINDER_PAT_B;
                }
                counters[counterPosition] = FINDER_PAT_B;
                isWhite = !isWhite;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static void reverseCounters(int[] counters) {
        int length = counters.length;
        for (int i = FINDER_PAT_A; i < length / FINDER_PAT_C; i += FINDER_PAT_B) {
            int tmp = counters[i];
            counters[i] = counters[(length - i) - 1];
            counters[(length - i) - 1] = tmp;
        }
    }

    private FinderPattern parseFoundFinderPattern(BitArray row, int rowNumber, boolean oddPattern) {
        int firstCounter;
        int start;
        int end;
        if (oddPattern) {
            int firstElementStart = this.startEnd[FINDER_PAT_A] - 1;
            while (firstElementStart >= 0 && !row.get(firstElementStart)) {
                firstElementStart--;
            }
            firstElementStart += FINDER_PAT_B;
            firstCounter = this.startEnd[FINDER_PAT_A] - firstElementStart;
            start = firstElementStart;
            end = this.startEnd[FINDER_PAT_B];
        } else {
            start = this.startEnd[FINDER_PAT_A];
            end = row.getNextUnset(this.startEnd[FINDER_PAT_B] + FINDER_PAT_B);
            firstCounter = end - this.startEnd[FINDER_PAT_B];
        }
        int[] counters = getDecodeFinderCounters();
        System.arraycopy(counters, FINDER_PAT_A, counters, FINDER_PAT_B, counters.length - 1);
        counters[FINDER_PAT_A] = firstCounter;
        try {
            int value = AbstractRSSReader.parseFinderValue(counters, FINDER_PATTERNS);
            int[] iArr = new int[FINDER_PAT_C];
            iArr[FINDER_PAT_A] = start;
            iArr[FINDER_PAT_B] = end;
            return new FinderPattern(value, iArr, start, end, rowNumber);
        } catch (NotFoundException e) {
            return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    com.google.zxing.oned.rss.DataCharacter decodeDataCharacter(com.google.zxing.common.BitArray r34, com.google.zxing.oned.rss.FinderPattern r35, boolean r36, boolean r37) throws com.google.zxing.NotFoundException {
        /*
        r33 = this;
        r5 = r33.getDataCharacterCounters();
        r30 = 0;
        r31 = 0;
        r5[r30] = r31;
        r30 = 1;
        r31 = 0;
        r5[r30] = r31;
        r30 = 2;
        r31 = 0;
        r5[r30] = r31;
        r30 = 3;
        r31 = 0;
        r5[r30] = r31;
        r30 = 4;
        r31 = 0;
        r5[r30] = r31;
        r30 = 5;
        r31 = 0;
        r5[r30] = r31;
        r30 = 6;
        r31 = 0;
        r5[r30] = r31;
        r30 = 7;
        r31 = 0;
        r5[r30] = r31;
        if (r37 == 0) goto L_0x0086;
    L_0x0036:
        r30 = r35.getStartEnd();
        r31 = 0;
        r30 = r30[r31];
        r0 = r34;
        r1 = r30;
        com.google.zxing.oned.OneDReader.recordPatternInReverse(r0, r1, r5);
    L_0x0045:
        r16 = 17;
        r30 = com.google.zxing.oned.rss.AbstractRSSReader.count(r5);
        r0 = r30;
        r0 = (float) r0;
        r30 = r0;
        r0 = r16;
        r0 = (float) r0;
        r31 = r0;
        r6 = r30 / r31;
        r30 = r35.getStartEnd();
        r31 = 1;
        r30 = r30[r31];
        r31 = r35.getStartEnd();
        r32 = 0;
        r31 = r31[r32];
        r30 = r30 - r31;
        r0 = r30;
        r0 = (float) r0;
        r30 = r0;
        r31 = 1097859072; // 0x41700000 float:15.0 double:5.424144515E-315;
        r11 = r30 / r31;
        r30 = r6 - r11;
        r30 = java.lang.Math.abs(r30);
        r30 = r30 / r11;
        r31 = 1050253722; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r30 = (r30 > r31 ? 1 : (r30 == r31 ? 0 : -1));
        if (r30 <= 0) goto L_0x00aa;
    L_0x0081:
        r30 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r30;
    L_0x0086:
        r30 = r35.getStartEnd();
        r31 = 1;
        r30 = r30[r31];
        r0 = r34;
        r1 = r30;
        com.google.zxing.oned.OneDReader.recordPattern(r0, r1, r5);
        r14 = 0;
        r0 = r5.length;
        r30 = r0;
        r15 = r30 + -1;
    L_0x009b:
        if (r14 >= r15) goto L_0x0045;
    L_0x009d:
        r24 = r5[r14];
        r30 = r5[r15];
        r5[r14] = r30;
        r5[r15] = r24;
        r14 = r14 + 1;
        r15 = r15 + -1;
        goto L_0x009b;
    L_0x00aa:
        r18 = r33.getOddCounts();
        r8 = r33.getEvenCounts();
        r19 = r33.getOddRoundingErrors();
        r9 = r33.getEvenRoundingErrors();
        r14 = 0;
    L_0x00bb:
        r0 = r5.length;
        r30 = r0;
        r0 = r30;
        if (r14 >= r0) goto L_0x011a;
    L_0x00c2:
        r30 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = r5[r14];
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r30 = r30 * r31;
        r27 = r30 / r6;
        r30 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r30 = r30 + r27;
        r0 = r30;
        r4 = (int) r0;
        r30 = 1;
        r0 = r30;
        if (r4 >= r0) goto L_0x00fb;
    L_0x00dc:
        r30 = 1050253722; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r30 = (r27 > r30 ? 1 : (r27 == r30 ? 0 : -1));
        if (r30 >= 0) goto L_0x00e8;
    L_0x00e3:
        r30 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r30;
    L_0x00e8:
        r4 = 1;
    L_0x00e9:
        r22 = r14 / 2;
        r30 = r14 & 1;
        if (r30 != 0) goto L_0x0110;
    L_0x00ef:
        r18[r22] = r4;
        r0 = (float) r4;
        r30 = r0;
        r30 = r27 - r30;
        r19[r22] = r30;
    L_0x00f8:
        r14 = r14 + 1;
        goto L_0x00bb;
    L_0x00fb:
        r30 = 8;
        r0 = r30;
        if (r4 <= r0) goto L_0x00e9;
    L_0x0101:
        r30 = 1091253043; // 0x410b3333 float:8.7 double:5.391506395E-315;
        r30 = (r27 > r30 ? 1 : (r27 == r30 ? 0 : -1));
        if (r30 <= 0) goto L_0x010d;
    L_0x0108:
        r30 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r30;
    L_0x010d:
        r4 = 8;
        goto L_0x00e9;
    L_0x0110:
        r8[r22] = r4;
        r0 = (float) r4;
        r30 = r0;
        r30 = r27 - r30;
        r9[r22] = r30;
        goto L_0x00f8;
    L_0x011a:
        r0 = r33;
        r1 = r16;
        r0.adjustOddEvenCounts(r1);
        r30 = r35.getValue();
        r31 = r30 * 4;
        if (r36 == 0) goto L_0x015d;
    L_0x0129:
        r30 = 0;
    L_0x012b:
        r31 = r31 + r30;
        if (r37 == 0) goto L_0x0160;
    L_0x012f:
        r30 = 0;
    L_0x0131:
        r30 = r30 + r31;
        r29 = r30 + -1;
        r20 = 0;
        r17 = 0;
        r0 = r18;
        r0 = r0.length;
        r30 = r0;
        r14 = r30 + -1;
    L_0x0140:
        if (r14 < 0) goto L_0x0163;
    L_0x0142:
        r30 = isNotA1left(r35, r36, r37);
        if (r30 == 0) goto L_0x0156;
    L_0x0148:
        r30 = WEIGHTS;
        r30 = r30[r29];
        r31 = r14 * 2;
        r28 = r30[r31];
        r30 = r18[r14];
        r30 = r30 * r28;
        r17 = r17 + r30;
    L_0x0156:
        r30 = r18[r14];
        r20 = r20 + r30;
        r14 = r14 + -1;
        goto L_0x0140;
    L_0x015d:
        r30 = 2;
        goto L_0x012b;
    L_0x0160:
        r30 = 1;
        goto L_0x0131;
    L_0x0163:
        r7 = 0;
        r0 = r8.length;
        r30 = r0;
        r14 = r30 + -1;
    L_0x0169:
        if (r14 < 0) goto L_0x0184;
    L_0x016b:
        r30 = isNotA1left(r35, r36, r37);
        if (r30 == 0) goto L_0x0181;
    L_0x0171:
        r30 = WEIGHTS;
        r30 = r30[r29];
        r31 = r14 * 2;
        r31 = r31 + 1;
        r28 = r30[r31];
        r30 = r8[r14];
        r30 = r30 * r28;
        r7 = r7 + r30;
    L_0x0181:
        r14 = r14 + -1;
        goto L_0x0169;
    L_0x0184:
        r3 = r17 + r7;
        r30 = r20 & 1;
        if (r30 != 0) goto L_0x019a;
    L_0x018a:
        r30 = 13;
        r0 = r20;
        r1 = r30;
        if (r0 > r1) goto L_0x019a;
    L_0x0192:
        r30 = 4;
        r0 = r20;
        r1 = r30;
        if (r0 >= r1) goto L_0x019f;
    L_0x019a:
        r30 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r30;
    L_0x019f:
        r30 = 13 - r20;
        r13 = r30 / 2;
        r30 = SYMBOL_WIDEST;
        r21 = r30[r13];
        r10 = 9 - r21;
        r30 = 1;
        r0 = r18;
        r1 = r21;
        r2 = r30;
        r26 = com.google.zxing.oned.rss.RSSUtils.getRSSvalue(r0, r1, r2);
        r30 = 0;
        r0 = r30;
        r25 = com.google.zxing.oned.rss.RSSUtils.getRSSvalue(r8, r10, r0);
        r30 = EVEN_TOTAL_SUBSET;
        r23 = r30[r13];
        r30 = GSUM;
        r12 = r30[r13];
        r30 = r26 * r23;
        r30 = r30 + r25;
        r27 = r30 + r12;
        r30 = new com.google.zxing.oned.rss.DataCharacter;
        r0 = r30;
        r1 = r27;
        r0.<init>(r1, r3);
        return r30;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.rss.expanded.RSSExpandedReader.decodeDataCharacter(com.google.zxing.common.BitArray, com.google.zxing.oned.rss.FinderPattern, boolean, boolean):com.google.zxing.oned.rss.DataCharacter");
    }

    private static boolean isNotA1left(FinderPattern pattern, boolean isOddPattern, boolean leftChar) {
        return (pattern.getValue() == 0 && isOddPattern && leftChar) ? false : true;
    }

    private void adjustOddEvenCounts(int numModules) throws NotFoundException {
        boolean oddParityBad;
        boolean evenParityBad = false;
        int oddSum = AbstractRSSReader.count(getOddCounts());
        int evenSum = AbstractRSSReader.count(getEvenCounts());
        int mismatch = (oddSum + evenSum) - numModules;
        if ((oddSum & FINDER_PAT_B) == FINDER_PAT_B) {
            oddParityBad = true;
        } else {
            oddParityBad = false;
        }
        if ((evenSum & FINDER_PAT_B) == 0) {
            evenParityBad = true;
        }
        boolean incrementOdd = false;
        boolean decrementOdd = false;
        if (oddSum > 13) {
            decrementOdd = true;
        } else if (oddSum < FINDER_PAT_E) {
            incrementOdd = true;
        }
        boolean incrementEven = false;
        boolean decrementEven = false;
        if (evenSum > 13) {
            decrementEven = true;
        } else if (evenSum < FINDER_PAT_E) {
            incrementEven = true;
        }
        if (mismatch == FINDER_PAT_B) {
            if (oddParityBad) {
                if (evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                decrementOdd = true;
            } else if (evenParityBad) {
                decrementEven = true;
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        } else if (mismatch == -1) {
            if (oddParityBad) {
                if (evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                incrementOdd = true;
            } else if (evenParityBad) {
                incrementEven = true;
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        } else if (mismatch != 0) {
            throw NotFoundException.getNotFoundInstance();
        } else if (oddParityBad) {
            if (!evenParityBad) {
                throw NotFoundException.getNotFoundInstance();
            } else if (oddSum < evenSum) {
                incrementOdd = true;
                decrementEven = true;
            } else {
                decrementOdd = true;
                incrementEven = true;
            }
        } else if (evenParityBad) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (incrementOdd) {
            if (decrementOdd) {
                throw NotFoundException.getNotFoundInstance();
            }
            AbstractRSSReader.increment(getOddCounts(), getOddRoundingErrors());
        }
        if (decrementOdd) {
            AbstractRSSReader.decrement(getOddCounts(), getOddRoundingErrors());
        }
        if (incrementEven) {
            if (decrementEven) {
                throw NotFoundException.getNotFoundInstance();
            }
            AbstractRSSReader.increment(getEvenCounts(), getOddRoundingErrors());
        }
        if (decrementEven) {
            AbstractRSSReader.decrement(getEvenCounts(), getEvenRoundingErrors());
        }
    }
}
