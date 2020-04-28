package com.rbkmoney.clickhousenotificator.exception;

public class ValidationNotificationException extends RuntimeException {
    public ValidationNotificationException() {
    }

    public ValidationNotificationException(String message) {
        super(message);
    }

    public ValidationNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationNotificationException(Throwable cause) {
        super(cause);
    }

    public ValidationNotificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
