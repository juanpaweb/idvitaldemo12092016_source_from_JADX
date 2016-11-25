package com.google.zxing.aztec.encoder;

import android.support.v7.widget.helper.ItemTouchHelper;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import idvital1.idvital1.C0239R;

public final class Encoder {
    public static final int DEFAULT_AZTEC_LAYERS = 0;
    public static final int DEFAULT_EC_PERCENT = 33;
    private static final int MAX_NB_BITS = 32;
    private static final int MAX_NB_BITS_COMPACT = 4;
    private static final int[] WORD_SIZE;

    static {
        WORD_SIZE = new int[]{MAX_NB_BITS_COMPACT, 6, 6, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12};
    }

    private Encoder() {
    }

    public static AztecCode encode(byte[] data) {
        return encode(data, DEFAULT_EC_PERCENT, DEFAULT_AZTEC_LAYERS);
    }

    public static AztecCode encode(byte[] data, int minECCPercent, int userSpecifiedLayers) {
        boolean compact;
        int layers;
        int totalBitsInLayer;
        int wordSize;
        BitArray stuffedBits;
        int matrixSize;
        BitArray bits = new HighLevelEncoder(data).encode();
        int eccBits = ((bits.getSize() * minECCPercent) / 100) + 11;
        int totalSizeBits = bits.getSize() + eccBits;
        if (userSpecifiedLayers != 0) {
            compact = userSpecifiedLayers < 0;
            layers = Math.abs(userSpecifiedLayers);
            if (layers > (compact ? MAX_NB_BITS_COMPACT : MAX_NB_BITS)) {
                throw new IllegalArgumentException(String.format("Illegal value %s for layers", new Object[]{Integer.valueOf(userSpecifiedLayers)}));
            }
            totalBitsInLayer = totalBitsInLayer(layers, compact);
            wordSize = WORD_SIZE[layers];
            int usableBitsInLayers = totalBitsInLayer - (totalBitsInLayer % wordSize);
            stuffedBits = stuffBits(bits, wordSize);
            if (stuffedBits.getSize() + eccBits > usableBitsInLayers) {
                throw new IllegalArgumentException("Data to large for user specified layer");
            } else if (compact && stuffedBits.getSize() > wordSize * 64) {
                throw new IllegalArgumentException("Data to large for user specified layer");
            }
        }
        wordSize = DEFAULT_AZTEC_LAYERS;
        stuffedBits = null;
        int i = DEFAULT_AZTEC_LAYERS;
        while (i <= MAX_NB_BITS) {
            compact = i <= 3;
            if (compact) {
                layers = i + 1;
            } else {
                layers = i;
            }
            totalBitsInLayer = totalBitsInLayer(layers, compact);
            if (totalSizeBits <= totalBitsInLayer) {
                if (wordSize != WORD_SIZE[layers]) {
                    wordSize = WORD_SIZE[layers];
                    stuffedBits = stuffBits(bits, wordSize);
                }
                usableBitsInLayers = totalBitsInLayer - (totalBitsInLayer % wordSize);
                if ((!compact || stuffedBits.getSize() <= wordSize * 64) && stuffedBits.getSize() + eccBits <= usableBitsInLayers) {
                }
            }
            i++;
        }
        throw new IllegalArgumentException("Data too large for an Aztec code");
        BitArray messageBits = generateCheckWords(stuffedBits, totalBitsInLayer, wordSize);
        int messageSizeInWords = stuffedBits.getSize() / wordSize;
        BitArray modeMessage = generateModeMessage(compact, layers, messageSizeInWords);
        int baseMatrixSize = compact ? (layers * MAX_NB_BITS_COMPACT) + 11 : (layers * MAX_NB_BITS_COMPACT) + 14;
        int[] alignmentMap = new int[baseMatrixSize];
        if (compact) {
            matrixSize = baseMatrixSize;
            i = DEFAULT_AZTEC_LAYERS;
            while (true) {
                int length = alignmentMap.length;
                if (i >= r0) {
                    break;
                }
                alignmentMap[i] = i;
                i++;
            }
        } else {
            matrixSize = (baseMatrixSize + 1) + ((((baseMatrixSize / 2) - 1) / 15) * 2);
            int origCenter = baseMatrixSize / 2;
            int center = matrixSize / 2;
            for (i = DEFAULT_AZTEC_LAYERS; i < origCenter; i++) {
                int newOffset = i + (i / 15);
                alignmentMap[(origCenter - i) - 1] = (center - newOffset) - 1;
                alignmentMap[origCenter + i] = (center + newOffset) + 1;
            }
        }
        BitMatrix matrix = new BitMatrix(matrixSize);
        int rowOffset = DEFAULT_AZTEC_LAYERS;
        for (i = DEFAULT_AZTEC_LAYERS; i < layers; i++) {
            int j;
            int rowSize = compact ? ((layers - i) * MAX_NB_BITS_COMPACT) + 9 : ((layers - i) * MAX_NB_BITS_COMPACT) + 12;
            for (j = DEFAULT_AZTEC_LAYERS; j < rowSize; j++) {
                int k;
                int columnOffset = j * 2;
                for (k = DEFAULT_AZTEC_LAYERS; k < 2; k++) {
                    if (messageBits.get((rowOffset + columnOffset) + k)) {
                        matrix.set(alignmentMap[(i * 2) + k], alignmentMap[(i * 2) + j]);
                    }
                    if (messageBits.get((((rowSize * 2) + rowOffset) + columnOffset) + k)) {
                        matrix.set(alignmentMap[(i * 2) + j], alignmentMap[((baseMatrixSize - 1) - (i * 2)) - k]);
                    }
                    if (messageBits.get((((rowSize * MAX_NB_BITS_COMPACT) + rowOffset) + columnOffset) + k)) {
                        matrix.set(alignmentMap[((baseMatrixSize - 1) - (i * 2)) - k], alignmentMap[((baseMatrixSize - 1) - (i * 2)) - j]);
                    }
                    if (messageBits.get((((rowSize * 6) + rowOffset) + columnOffset) + k)) {
                        matrix.set(alignmentMap[((baseMatrixSize - 1) - (i * 2)) - j], alignmentMap[(i * 2) + k]);
                    }
                }
            }
            rowOffset += rowSize * 8;
        }
        drawModeMessage(matrix, compact, matrixSize, modeMessage);
        if (compact) {
            drawBullsEye(matrix, matrixSize / 2, 5);
        } else {
            drawBullsEye(matrix, matrixSize / 2, 7);
            i = DEFAULT_AZTEC_LAYERS;
            j = DEFAULT_AZTEC_LAYERS;
            while (i < (baseMatrixSize / 2) - 1) {
                for (k = (matrixSize / 2) & 1; k < matrixSize; k += 2) {
                    matrix.set((matrixSize / 2) - j, k);
                    matrix.set((matrixSize / 2) + j, k);
                    matrix.set(k, (matrixSize / 2) - j);
                    matrix.set(k, (matrixSize / 2) + j);
                }
                i += 15;
                j += 16;
            }
        }
        AztecCode aztec = new AztecCode();
        aztec.setCompact(compact);
        aztec.setSize(matrixSize);
        aztec.setLayers(layers);
        aztec.setCodeWords(messageSizeInWords);
        aztec.setMatrix(matrix);
        return aztec;
    }

    private static void drawBullsEye(BitMatrix matrix, int center, int size) {
        for (int i = DEFAULT_AZTEC_LAYERS; i < size; i += 2) {
            for (int j = center - i; j <= center + i; j++) {
                matrix.set(j, center - i);
                matrix.set(j, center + i);
                matrix.set(center - i, j);
                matrix.set(center + i, j);
            }
        }
        matrix.set(center - size, center - size);
        matrix.set((center - size) + 1, center - size);
        matrix.set(center - size, (center - size) + 1);
        matrix.set(center + size, center - size);
        matrix.set(center + size, (center - size) + 1);
        matrix.set(center + size, (center + size) - 1);
    }

    static BitArray generateModeMessage(boolean compact, int layers, int messageSizeInWords) {
        BitArray modeMessage = new BitArray();
        if (compact) {
            modeMessage.appendBits(layers - 1, 2);
            modeMessage.appendBits(messageSizeInWords - 1, 6);
            return generateCheckWords(modeMessage, 28, MAX_NB_BITS_COMPACT);
        }
        modeMessage.appendBits(layers - 1, 5);
        modeMessage.appendBits(messageSizeInWords - 1, 11);
        return generateCheckWords(modeMessage, 40, MAX_NB_BITS_COMPACT);
    }

    private static void drawModeMessage(BitMatrix matrix, boolean compact, int matrixSize, BitArray modeMessage) {
        int center = matrixSize / 2;
        int i;
        int offset;
        if (compact) {
            for (i = DEFAULT_AZTEC_LAYERS; i < 7; i++) {
                offset = (center - 3) + i;
                if (modeMessage.get(i)) {
                    matrix.set(offset, center - 5);
                }
                if (modeMessage.get(i + 7)) {
                    matrix.set(center + 5, offset);
                }
                if (modeMessage.get(20 - i)) {
                    matrix.set(offset, center + 5);
                }
                if (modeMessage.get(27 - i)) {
                    matrix.set(center - 5, offset);
                }
            }
            return;
        }
        for (i = DEFAULT_AZTEC_LAYERS; i < 10; i++) {
            offset = ((center - 5) + i) + (i / 5);
            if (modeMessage.get(i)) {
                matrix.set(offset, center - 7);
            }
            if (modeMessage.get(i + 10)) {
                matrix.set(center + 7, offset);
            }
            if (modeMessage.get(29 - i)) {
                matrix.set(offset, center + 7);
            }
            if (modeMessage.get(39 - i)) {
                matrix.set(center - 7, offset);
            }
        }
    }

    private static BitArray generateCheckWords(BitArray bitArray, int totalBits, int wordSize) {
        int i = DEFAULT_AZTEC_LAYERS;
        int messageSizeInWords = bitArray.getSize() / wordSize;
        ReedSolomonEncoder rs = new ReedSolomonEncoder(getGF(wordSize));
        int totalWords = totalBits / wordSize;
        int[] messageWords = bitsToWords(bitArray, wordSize, totalWords);
        rs.encode(messageWords, totalWords - messageSizeInWords);
        int startPad = totalBits % wordSize;
        BitArray messageBits = new BitArray();
        messageBits.appendBits(DEFAULT_AZTEC_LAYERS, startPad);
        int length = messageWords.length;
        while (i < length) {
            messageBits.appendBits(messageWords[i], wordSize);
            i++;
        }
        return messageBits;
    }

    private static int[] bitsToWords(BitArray stuffedBits, int wordSize, int totalWords) {
        int[] message = new int[totalWords];
        int n = stuffedBits.getSize() / wordSize;
        for (int i = DEFAULT_AZTEC_LAYERS; i < n; i++) {
            int value = DEFAULT_AZTEC_LAYERS;
            for (int j = DEFAULT_AZTEC_LAYERS; j < wordSize; j++) {
                value |= stuffedBits.get((i * wordSize) + j) ? 1 << ((wordSize - j) - 1) : DEFAULT_AZTEC_LAYERS;
            }
            message[i] = value;
        }
        return message;
    }

    private static GenericGF getGF(int wordSize) {
        switch (wordSize) {
            case MAX_NB_BITS_COMPACT /*4*/:
                return GenericGF.AZTEC_PARAM;
            case FragmentManagerImpl.ANIM_STYLE_FADE_EXIT /*6*/:
                return GenericGF.AZTEC_DATA_6;
            case ItemTouchHelper.RIGHT /*8*/:
                return GenericGF.AZTEC_DATA_8;
            case C0239R.styleable.Toolbar_contentInsetEndWithActions /*10*/:
                return GenericGF.AZTEC_DATA_10;
            case C0239R.styleable.Toolbar_titleTextAppearance /*12*/:
                return GenericGF.AZTEC_DATA_12;
            default:
                throw new IllegalArgumentException("Unsupported word size " + wordSize);
        }
    }

    static BitArray stuffBits(BitArray bits, int wordSize) {
        BitArray out = new BitArray();
        int n = bits.getSize();
        int mask = (1 << wordSize) - 2;
        int i = DEFAULT_AZTEC_LAYERS;
        while (i < n) {
            int word = DEFAULT_AZTEC_LAYERS;
            int j = DEFAULT_AZTEC_LAYERS;
            while (j < wordSize) {
                if (i + j >= n || bits.get(i + j)) {
                    word |= 1 << ((wordSize - 1) - j);
                }
                j++;
            }
            if ((word & mask) == mask) {
                out.appendBits(word & mask, wordSize);
                i--;
            } else if ((word & mask) == 0) {
                out.appendBits(word | 1, wordSize);
                i--;
            } else {
                out.appendBits(word, wordSize);
            }
            i += wordSize;
        }
        return out;
    }

    private static int totalBitsInLayer(int layers, boolean compact) {
        return ((compact ? 88 : C0239R.styleable.AppCompatTheme_spinnerStyle) + (layers * 16)) * layers;
    }
}
