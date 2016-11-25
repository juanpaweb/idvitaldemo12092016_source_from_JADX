package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

public final class WhiteRectangleDetector {
    private static final int CORR = 1;
    private static final int INIT_SIZE = 10;
    private final int downInit;
    private final int height;
    private final BitMatrix image;
    private final int leftInit;
    private final int rightInit;
    private final int upInit;
    private final int width;

    public WhiteRectangleDetector(BitMatrix image) throws NotFoundException {
        this(image, INIT_SIZE, image.getWidth() / 2, image.getHeight() / 2);
    }

    public WhiteRectangleDetector(BitMatrix image, int initSize, int x, int y) throws NotFoundException {
        this.image = image;
        this.height = image.getHeight();
        this.width = image.getWidth();
        int halfsize = initSize / 2;
        this.leftInit = x - halfsize;
        this.rightInit = x + halfsize;
        this.upInit = y - halfsize;
        this.downInit = y + halfsize;
        if (this.upInit < 0 || this.leftInit < 0 || this.downInit >= this.height || this.rightInit >= this.width) {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.zxing.ResultPoint[] detect() throws com.google.zxing.NotFoundException {
        /*
        r30 = this;
        r0 = r30;
        r14 = r0.leftInit;
        r0 = r30;
        r0 = r0.rightInit;
        r17 = r0;
        r0 = r30;
        r0 = r0.upInit;
        r22 = r0;
        r0 = r30;
        r12 = r0.downInit;
        r19 = 0;
        r5 = 1;
        r6 = 0;
        r9 = 0;
        r7 = 0;
        r8 = 0;
        r10 = 0;
    L_0x001c:
        if (r5 == 0) goto L_0x0059;
    L_0x001e:
        r5 = 0;
        r18 = 1;
    L_0x0021:
        if (r18 != 0) goto L_0x0025;
    L_0x0023:
        if (r9 != 0) goto L_0x004b;
    L_0x0025:
        r0 = r30;
        r0 = r0.width;
        r26 = r0;
        r0 = r17;
        r1 = r26;
        if (r0 >= r1) goto L_0x004b;
    L_0x0031:
        r26 = 0;
        r0 = r30;
        r1 = r22;
        r2 = r17;
        r3 = r26;
        r18 = r0.containsBlackPoint(r1, r12, r2, r3);
        if (r18 == 0) goto L_0x0046;
    L_0x0041:
        r17 = r17 + 1;
        r5 = 1;
        r9 = 1;
        goto L_0x0021;
    L_0x0046:
        if (r9 != 0) goto L_0x0021;
    L_0x0048:
        r17 = r17 + 1;
        goto L_0x0021;
    L_0x004b:
        r0 = r30;
        r0 = r0.width;
        r26 = r0;
        r0 = r17;
        r1 = r26;
        if (r0 < r1) goto L_0x0091;
    L_0x0057:
        r19 = 1;
    L_0x0059:
        if (r19 != 0) goto L_0x01d1;
    L_0x005b:
        if (r6 == 0) goto L_0x01d1;
    L_0x005d:
        r16 = r17 - r14;
        r25 = 0;
        r13 = 1;
    L_0x0062:
        r0 = r16;
        if (r13 >= r0) goto L_0x008a;
    L_0x0066:
        r0 = (float) r14;
        r26 = r0;
        r27 = r12 - r13;
        r0 = r27;
        r0 = (float) r0;
        r27 = r0;
        r28 = r14 + r13;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = (float) r12;
        r29 = r0;
        r0 = r30;
        r1 = r26;
        r2 = r27;
        r3 = r28;
        r4 = r29;
        r25 = r0.getBlackPointOnSegment(r1, r2, r3, r4);
        if (r25 == 0) goto L_0x0117;
    L_0x008a:
        if (r25 != 0) goto L_0x011b;
    L_0x008c:
        r26 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r26;
    L_0x0091:
        r11 = 1;
    L_0x0092:
        if (r11 != 0) goto L_0x0096;
    L_0x0094:
        if (r7 != 0) goto L_0x00b8;
    L_0x0096:
        r0 = r30;
        r0 = r0.height;
        r26 = r0;
        r0 = r26;
        if (r12 >= r0) goto L_0x00b8;
    L_0x00a0:
        r26 = 1;
        r0 = r30;
        r1 = r17;
        r2 = r26;
        r11 = r0.containsBlackPoint(r14, r1, r12, r2);
        if (r11 == 0) goto L_0x00b3;
    L_0x00ae:
        r12 = r12 + 1;
        r5 = 1;
        r7 = 1;
        goto L_0x0092;
    L_0x00b3:
        if (r7 != 0) goto L_0x0092;
    L_0x00b5:
        r12 = r12 + 1;
        goto L_0x0092;
    L_0x00b8:
        r0 = r30;
        r0 = r0.height;
        r26 = r0;
        r0 = r26;
        if (r12 < r0) goto L_0x00c5;
    L_0x00c2:
        r19 = 1;
        goto L_0x0059;
    L_0x00c5:
        r15 = 1;
    L_0x00c6:
        if (r15 != 0) goto L_0x00ca;
    L_0x00c8:
        if (r8 != 0) goto L_0x00e4;
    L_0x00ca:
        if (r14 < 0) goto L_0x00e4;
    L_0x00cc:
        r26 = 0;
        r0 = r30;
        r1 = r22;
        r2 = r26;
        r15 = r0.containsBlackPoint(r1, r12, r14, r2);
        if (r15 == 0) goto L_0x00df;
    L_0x00da:
        r14 = r14 + -1;
        r5 = 1;
        r8 = 1;
        goto L_0x00c6;
    L_0x00df:
        if (r8 != 0) goto L_0x00c6;
    L_0x00e1:
        r14 = r14 + -1;
        goto L_0x00c6;
    L_0x00e4:
        if (r14 >= 0) goto L_0x00ea;
    L_0x00e6:
        r19 = 1;
        goto L_0x0059;
    L_0x00ea:
        r21 = 1;
    L_0x00ec:
        if (r21 != 0) goto L_0x00f0;
    L_0x00ee:
        if (r10 != 0) goto L_0x010c;
    L_0x00f0:
        if (r22 < 0) goto L_0x010c;
    L_0x00f2:
        r26 = 1;
        r0 = r30;
        r1 = r17;
        r2 = r22;
        r3 = r26;
        r21 = r0.containsBlackPoint(r14, r1, r2, r3);
        if (r21 == 0) goto L_0x0107;
    L_0x0102:
        r22 = r22 + -1;
        r5 = 1;
        r10 = 1;
        goto L_0x00ec;
    L_0x0107:
        if (r10 != 0) goto L_0x00ec;
    L_0x0109:
        r22 = r22 + -1;
        goto L_0x00ec;
    L_0x010c:
        if (r22 >= 0) goto L_0x0112;
    L_0x010e:
        r19 = 1;
        goto L_0x0059;
    L_0x0112:
        if (r5 == 0) goto L_0x001c;
    L_0x0114:
        r6 = 1;
        goto L_0x001c;
    L_0x0117:
        r13 = r13 + 1;
        goto L_0x0062;
    L_0x011b:
        r20 = 0;
        r13 = 1;
    L_0x011e:
        r0 = r16;
        if (r13 >= r0) goto L_0x0148;
    L_0x0122:
        r0 = (float) r14;
        r26 = r0;
        r27 = r22 + r13;
        r0 = r27;
        r0 = (float) r0;
        r27 = r0;
        r28 = r14 + r13;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r22;
        r0 = (float) r0;
        r29 = r0;
        r0 = r30;
        r1 = r26;
        r2 = r27;
        r3 = r28;
        r4 = r29;
        r20 = r0.getBlackPointOnSegment(r1, r2, r3, r4);
        if (r20 == 0) goto L_0x014f;
    L_0x0148:
        if (r20 != 0) goto L_0x0152;
    L_0x014a:
        r26 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r26;
    L_0x014f:
        r13 = r13 + 1;
        goto L_0x011e;
    L_0x0152:
        r23 = 0;
        r13 = 1;
    L_0x0155:
        r0 = r16;
        if (r13 >= r0) goto L_0x0181;
    L_0x0159:
        r0 = r17;
        r0 = (float) r0;
        r26 = r0;
        r27 = r22 + r13;
        r0 = r27;
        r0 = (float) r0;
        r27 = r0;
        r28 = r17 - r13;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r22;
        r0 = (float) r0;
        r29 = r0;
        r0 = r30;
        r1 = r26;
        r2 = r27;
        r3 = r28;
        r4 = r29;
        r23 = r0.getBlackPointOnSegment(r1, r2, r3, r4);
        if (r23 == 0) goto L_0x0188;
    L_0x0181:
        if (r23 != 0) goto L_0x018b;
    L_0x0183:
        r26 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r26;
    L_0x0188:
        r13 = r13 + 1;
        goto L_0x0155;
    L_0x018b:
        r24 = 0;
        r13 = 1;
    L_0x018e:
        r0 = r16;
        if (r13 >= r0) goto L_0x01b8;
    L_0x0192:
        r0 = r17;
        r0 = (float) r0;
        r26 = r0;
        r27 = r12 - r13;
        r0 = r27;
        r0 = (float) r0;
        r27 = r0;
        r28 = r17 - r13;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = (float) r12;
        r29 = r0;
        r0 = r30;
        r1 = r26;
        r2 = r27;
        r3 = r28;
        r4 = r29;
        r24 = r0.getBlackPointOnSegment(r1, r2, r3, r4);
        if (r24 == 0) goto L_0x01bf;
    L_0x01b8:
        if (r24 != 0) goto L_0x01c2;
    L_0x01ba:
        r26 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r26;
    L_0x01bf:
        r13 = r13 + 1;
        goto L_0x018e;
    L_0x01c2:
        r0 = r30;
        r1 = r24;
        r2 = r25;
        r3 = r23;
        r4 = r20;
        r26 = r0.centerEdges(r1, r2, r3, r4);
        return r26;
    L_0x01d1:
        r26 = com.google.zxing.NotFoundException.getNotFoundInstance();
        throw r26;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.common.detector.WhiteRectangleDetector.detect():com.google.zxing.ResultPoint[]");
    }

    private ResultPoint getBlackPointOnSegment(float aX, float aY, float bX, float bY) {
        int dist = MathUtils.round(MathUtils.distance(aX, aY, bX, bY));
        float xStep = (bX - aX) / ((float) dist);
        float yStep = (bY - aY) / ((float) dist);
        for (int i = 0; i < dist; i += CORR) {
            int x = MathUtils.round((((float) i) * xStep) + aX);
            int y = MathUtils.round((((float) i) * yStep) + aY);
            if (this.image.get(x, y)) {
                return new ResultPoint((float) x, (float) y);
            }
        }
        return null;
    }

    private ResultPoint[] centerEdges(ResultPoint y, ResultPoint z, ResultPoint x, ResultPoint t) {
        float yi = y.getX();
        float yj = y.getY();
        float zi = z.getX();
        float zj = z.getY();
        float xi = x.getX();
        float xj = x.getY();
        float ti = t.getX();
        float tj = t.getY();
        if (yi < ((float) this.width) / 2.0f) {
            return new ResultPoint[]{new ResultPoint(ti - 1.0f, 1.0f + tj), new ResultPoint(1.0f + zi, 1.0f + zj), new ResultPoint(xi - 1.0f, xj - 1.0f), new ResultPoint(1.0f + yi, yj - 1.0f)};
        }
        return new ResultPoint[]{new ResultPoint(1.0f + ti, 1.0f + tj), new ResultPoint(1.0f + zi, zj - 1.0f), new ResultPoint(xi - 1.0f, 1.0f + xj), new ResultPoint(yi - 1.0f, yj - 1.0f)};
    }

    private boolean containsBlackPoint(int a, int b, int fixed, boolean horizontal) {
        if (horizontal) {
            for (int x = a; x <= b; x += CORR) {
                if (this.image.get(x, fixed)) {
                    return true;
                }
            }
        } else {
            for (int y = a; y <= b; y += CORR) {
                if (this.image.get(fixed, y)) {
                    return true;
                }
            }
        }
        return false;
    }
}
