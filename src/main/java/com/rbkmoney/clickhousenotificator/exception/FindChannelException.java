package com.rbkmoney.clickhousenotificator.exception;

public class FindChannelException extends RuntimeException {

    public FindChannelException() {
    }

    public FindChannelException(String message) {
        super(message);
    }

    public FindChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindChannelException(Throwable cause) {
        super(cause);
    }

    public FindChannelException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
