package com.google.zxing.client.android;

public final class Intents {

    public static final class Scan {
        public static final String ACTION = "com.google.zxing.client.android.SCAN";
        public static final String AZTEC_MODE = "AZTEC_MODE";
        public static final String CAMERA_ID = "SCAN_CAMERA_ID";
        public static final String CHARACTER_SET = "CHARACTER_SET";
        public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";
        public static final String FORMATS = "SCAN_FORMATS";
        public static final String HEIGHT = "SCAN_HEIGHT";
        public static final String MODE = "SCAN_MODE";
        public static final String ONE_D_MODE = "ONE_D_MODE";
        public static final String ORIENTATION = "SCAN_ORIENTATION";
        public static final String PDF417_MODE = "PDF417_MODE";
        public static final String PRODUCT_MODE = "PRODUCT_MODE";
        public static final String PROMPT_MESSAGE = "PROMPT_MESSAGE";
        public static final String QR_CODE_MODE = "QR_CODE_MODE";
        public static final String RESULT = "SCAN_RESULT";
        public static final String RESULT_BYTES = "SCAN_RESULT_BYTES";
        public static final String RESULT_BYTE_SEGMENTS_PREFIX = "SCAN_RESULT_BYTE_SEGMENTS_";
        public static final String RESULT_DISPLAY_DURATION_MS = "RESULT_DISPLAY_DURATION_MS";
        public static final String RESULT_ERROR_CORRECTION_LEVEL = "SCAN_RESULT_ERROR_CORRECTION_LEVEL";
        public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";
        public static final String RESULT_ORIENTATION = "SCAN_RESULT_ORIENTATION";
        public static final String RESULT_UPC_EAN_EXTENSION = "SCAN_RESULT_UPC_EAN_EXTENSION";
        public static final String WIDE = "SCAN_WIDE";
        public static final String WIDTH = "SCAN_WIDTH";

        private Scan() {
        }
    }

    private Intents() {
    }
}
