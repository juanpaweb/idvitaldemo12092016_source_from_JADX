package com.google.zxing;

public final class NotFoundException extends ReaderException {
    private static final NotFoundException INSTANCE;

    static {
        INSTANCE = new NotFoundException();
        INSTANCE.setStackTrace(NO_TRACE);
    }

    private NotFoundException() {
    }

    public static NotFoundException getNotFoundInstance() {
        return INSTANCE;
    }
}
