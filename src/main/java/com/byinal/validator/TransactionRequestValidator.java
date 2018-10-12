package com.byinal.validator;

import com.byinal.exception.FutureTransactionException;
import com.byinal.exception.InvalidRequestException;
import com.byinal.exception.TransactionTooOldException;
import com.byinal.model.request.TransactionRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Accept only valid requests for your business,
 * others will be responded with a clear message
 */
@Component
public class TransactionRequestValidator {

    private static final long TIMESTAMP_ACCEPTANCE_THRESHOLD_IN_MILLISECONDS = TimeUnit.SECONDS.toMillis(60);

    public void validate(TransactionRequest transactionRequest) {
        validateIfPresentOrThrow(transactionRequest, () -> new InvalidRequestException("validation.request.not.present"));
        validateIfPresentOrThrow(transactionRequest.getAmount(), () -> new InvalidRequestException("validation.request.amount.not.present"));
        validateIfPresentOrThrow(transactionRequest.getTimestamp(), () -> new InvalidRequestException("validation.request.timestamp.not.present"));
        validateAmountParsable(transactionRequest.getAmount());
        validateTimestamp(transactionRequest.getTimestamp());
    }

    private void validateIfPresentOrThrow(Object o, Supplier<InvalidRequestException> exceptionSupplier) {
        Optional.ofNullable(o)
                .orElseThrow(exceptionSupplier);
    }

    private void validateAmountParsable(String amount) {
        try {
            new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("validation.request.amount.not.parsable");
        }
    }

    private void validateTimestamp(String timestamp) {
        try {
            Instant instant = Instant.parse(timestamp);
            long durationInMilliseconds = Instant.now().toEpochMilli() - instant.toEpochMilli();
            if (isTransactionOlderThanSixtySec(durationInMilliseconds)) { // older 60 secs is not acceptable
                throw new TransactionTooOldException("validation.request.timestamp.too.old");
            } else if (isFutureTransaction(durationInMilliseconds)) { //  or future timestamp is not acceptable
                throw new FutureTransactionException("validation.request.timestamp.future");
            }
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException("validation.request.timestamp.not.parsable");
        }
    }

    private boolean isTransactionOlderThanSixtySec(long durationInMilliseconds) {
        return durationInMilliseconds > TIMESTAMP_ACCEPTANCE_THRESHOLD_IN_MILLISECONDS;
    }

    private boolean isFutureTransaction(long durationInMilliseconds) {
        return durationInMilliseconds < 0;
    }
}