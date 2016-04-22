package com.miriamlaurel.jcarb.common;

public class StoppedException extends RuntimeException {
    public StoppedException() {
        super();
    }

    public StoppedException(String message) {
        super(message);
    }

    public StoppedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoppedException(Throwable cause) {
        super(cause);
    }

    protected StoppedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
