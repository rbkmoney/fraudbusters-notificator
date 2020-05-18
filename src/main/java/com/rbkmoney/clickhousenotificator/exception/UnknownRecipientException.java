package com.rbkmoney.clickhousenotificator.exception;

public class UnknownRecipientException extends RuntimeException {

    public UnknownRecipientException() {
    }

    public UnknownRecipientException(String message) {
        super(message);
    }

    public UnknownRecipientException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownRecipientException(Throwable cause) {
        super(cause);
    }

    public UnknownRecipientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
