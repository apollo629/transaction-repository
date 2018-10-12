package com.byinal.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler to manage all rest exceptions on one hand.
 */
@RestControllerAdvice
public class RestControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

    @ExceptionHandler({InvalidRequestException.class, FutureTransactionException.class})
    public ResponseEntity handleRequestValidationException(Exception e) {
        logger.warn("A request validation exception occurred.", e); // client-based error so log level can be warn
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    @ExceptionHandler(TransactionTooOldException.class)
    public ResponseEntity handleTransactionTooOldException(TransactionTooOldException e) {
        logger.warn("Transaction too old exception occurred.", e); // client-based error so log level can be warn
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.warn("A http request method not supported exception occurred", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.warn("A http message not readable exception occurred", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}