package com.google.zxing.datamatrix.encoder;

import java.util.Arrays;

public class DefaultPlacement {
    private final byte[] bits;
    private final CharSequence codewords;
    private final int numcols;
    private final int numrows;

    public DefaultPlacement(CharSequence codewords, int numcols, int numrows) {
        this.codewords = codewords;
        this.numcols = numcols;
        this.numrows = numrows;
        this.bits = new byte[(numcols * numrows)];
        Arrays.fill(this.bits, (byte) -1);
    }

    final int getNumrows() {
        return this.numrows;
    }

    final int getNumcols() {
        return this.numcols;
    }

    final byte[] getBits() {
        return this.bits;
    }

    public final boolean getBit(int col, int row) {
        return this.bits[(this.numcols * row) + col] == (byte) 1;
    }

    final void setBit(int col, int row, boolean bit) {
        this.bits[(this.numcols * row) + col] = bit ? (byte) 1 : (byte) 0;
    }

    final boolean hasBit(int col, int row) {
        return this.bits[(this.numcols * row) + col] >= null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void place() {
        /*
        r7 = this;
        r6 = 1;
        r1 = 0;
        r3 = 4;
        r0 = 0;
    L_0x0004:
        r4 = r7.numrows;
        if (r3 != r4) goto L_0x0010;
    L_0x0008:
        if (r0 != 0) goto L_0x0010;
    L_0x000a:
        r2 = r1 + 1;
        r7.corner1(r1);
        r1 = r2;
    L_0x0010:
        r4 = r7.numrows;
        r4 = r4 + -2;
        if (r3 != r4) goto L_0x0024;
    L_0x0016:
        if (r0 != 0) goto L_0x0024;
    L_0x0018:
        r4 = r7.numcols;
        r4 = r4 % 4;
        if (r4 == 0) goto L_0x0024;
    L_0x001e:
        r2 = r1 + 1;
        r7.corner2(r1);
        r1 = r2;
    L_0x0024:
        r4 = r7.numrows;
        r4 = r4 + -2;
        if (r3 != r4) goto L_0x0039;
    L_0x002a:
        if (r0 != 0) goto L_0x0039;
    L_0x002c:
        r4 = r7.numcols;
        r4 = r4 % 8;
        r5 = 4;
        if (r4 != r5) goto L_0x0039;
    L_0x0033:
        r2 = r1 + 1;
        r7.corner3(r1);
        r1 = r2;
    L_0x0039:
        r4 = r7.numrows;
        r4 = r4 + 4;
        if (r3 != r4) goto L_0x004e;
    L_0x003f:
        r4 = 2;
        if (r0 != r4) goto L_0x004e;
    L_0x0042:
        r4 = r7.numcols;
        r4 = r4 % 8;
        if (r4 != 0) goto L_0x004e;
    L_0x0048:
        r2 = r1 + 1;
        r7.corner4(r1);
        r1 = r2;
    L_0x004e:
        r4 = r7.numrows;
        if (r3 >= r4) goto L_0x0060;
    L_0x0052:
        if (r0 < 0) goto L_0x0060;
    L_0x0054:
        r4 = r7.hasBit(r0, r3);
        if (r4 != 0) goto L_0x0060;
    L_0x005a:
        r2 = r1 + 1;
        r7.utah(r3, r0, r1);
        r1 = r2;
    L_0x0060:
        r3 = r3 + -2;
        r0 = r0 + 2;
        if (r3 < 0) goto L_0x006a;
    L_0x0066:
        r4 = r7.numcols;
        if (r0 < r4) goto L_0x004e;
    L_0x006a:
        r3 = r3 + 1;
        r0 = r0 + 3;
        r2 = r1;
    L_0x006f:
        if (r3 < 0) goto L_0x00bd;
    L_0x0071:
        r4 = r7.numcols;
        if (r0 >= r4) goto L_0x00bd;
    L_0x0075:
        r4 = r7.hasBit(r0, r3);
        if (r4 != 0) goto L_0x00bd;
    L_0x007b:
        r1 = r2 + 1;
        r7.utah(r3, r0, r2);
    L_0x0080:
        r3 = r3 + 2;
        r0 = r0 + -2;
        r4 = r7.numrows;
        if (r3 >= r4) goto L_0x008a;
    L_0x0088:
        if (r0 >= 0) goto L_0x00bb;
    L_0x008a:
        r3 = r3 + 3;
        r0 = r0 + 1;
        r4 = r7.numrows;
        if (r3 < r4) goto L_0x0004;
    L_0x0092:
        r4 = r7.numcols;
        if (r0 < r4) goto L_0x0004;
    L_0x0096:
        r4 = r7.numcols;
        r4 = r4 + -1;
        r5 = r7.numrows;
        r5 = r5 + -1;
        r4 = r7.hasBit(r4, r5);
        if (r4 != 0) goto L_0x00ba;
    L_0x00a4:
        r4 = r7.numcols;
        r4 = r4 + -1;
        r5 = r7.numrows;
        r5 = r5 + -1;
        r7.setBit(r4, r5, r6);
        r4 = r7.numcols;
        r4 = r4 + -2;
        r5 = r7.numrows;
        r5 = r5 + -2;
        r7.setBit(r4, r5, r6);
    L_0x00ba:
        return;
    L_0x00bb:
        r2 = r1;
        goto L_0x006f;
    L_0x00bd:
        r1 = r2;
        goto L_0x0080;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.datamatrix.encoder.DefaultPlacement.place():void");
    }

    private void module(int row, int col, int pos, int bit) {
        boolean z = true;
        if (row < 0) {
            row += this.numrows;
            col += 4 - ((this.numrows + 4) % 8);
        }
        if (col < 0) {
            col += this.numcols;
            row += 4 - ((this.numcols + 4) % 8);
        }
        if ((this.codewords.charAt(pos) & (1 << (8 - bit))) == 0) {
            z = false;
        }
        setBit(col, row, z);
    }

    private void utah(int row, int col, int pos) {
        module(row - 2, col - 2, pos, 1);
        module(row - 2, col - 1, pos, 2);
        module(row - 1, col - 2, pos, 3);
        module(row - 1, col - 1, pos, 4);
        module(row - 1, col, pos, 5);
        module(row, col - 2, pos, 6);
        module(row, col - 1, pos, 7);
        module(row, col, pos, 8);
    }

    private void corner1(int pos) {
        module(this.numrows - 1, 0, pos, 1);
        module(this.numrows - 1, 1, pos, 2);
        module(this.numrows - 1, 2, pos, 3);
        module(0, this.numcols - 2, pos, 4);
        module(0, this.numcols - 1, pos, 5);
        module(1, this.numcols - 1, pos, 6);
        module(2, this.numcols - 1, pos, 7);
        module(3, this.numcols - 1, pos, 8);
    }

    private void corner2(int pos) {
        module(this.numrows - 3, 0, pos, 1);
        module(this.numrows - 2, 0, pos, 2);
        module(this.numrows - 1, 0, pos, 3);
        module(0, this.numcols - 4, pos, 4);
        module(0, this.numcols - 3, pos, 5);
        module(0, this.numcols - 2, pos, 6);
        module(0, this.numcols - 1, pos, 7);
        module(1, this.numcols - 1, pos, 8);
    }

    private void corner3(int pos) {
        module(this.numrows - 3, 0, pos, 1);
        module(this.numrows - 2, 0, pos, 2);
        module(this.numrows - 1, 0, pos, 3);
        module(0, this.numcols - 2, pos, 4);
        module(0, this.numcols - 1, pos, 5);
        module(1, this.numcols - 1, pos, 6);
        module(2, this.numcols - 1, pos, 7);
        module(3, this.numcols - 1, pos, 8);
    }

    private void corner4(int pos) {
        module(this.numrows - 1, 0, pos, 1);
        module(this.numrows - 1, this.numcols - 1, pos, 2);
        module(0, this.numcols - 3, pos, 3);
        module(0, this.numcols - 2, pos, 4);
        module(0, this.numcols - 1, pos, 5);
        module(1, this.numcols - 3, pos, 6);
        module(1, this.numcols - 2, pos, 7);
        module(1, this.numcols - 1, pos, 8);
    }
}
