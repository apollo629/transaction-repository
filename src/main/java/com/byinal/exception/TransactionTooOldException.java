package com.byinal.exception;

public class TransactionTooOldException extends RuntimeException {

    public TransactionTooOldException(String message) {
        super(message);
    }
}