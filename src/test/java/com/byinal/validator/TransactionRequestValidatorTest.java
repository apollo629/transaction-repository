package com.byinal.validator;

import com.byinal.exception.FutureTransactionException;
import com.byinal.exception.InvalidRequestException;
import com.byinal.exception.TransactionTooOldException;
import com.byinal.model.request.TransactionRequest;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRequestValidatorTest {

    private final TransactionRequestValidator transactionRequestValidator = new TransactionRequestValidator();

    @Test
    public void should_throw_exception_when_request_is_null() {
        //given
        TransactionRequest request = null;

        //when
        Throwable throwable = Assertions.catchThrowable(() -> transactionRequestValidator.validate(request));

        //then
        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("validation.request.not.present");
    }

    @Test
    public void should_throw_exception_when_amount_is_not_present() {
        //given
        TransactionRequest request = new TransactionRequest();

        //when
        Throwable throwable = Assertions.catchThrowable(() -> transactionRequestValidator.validate(request));

        //then
        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("validation.request.amount.not.present");
    }

    @Test
    public void should_throw_exception_when_timestamp_is_not_present() {
        //given
        TransactionRequest request = new TransactionRequest();
        request.setAmount("19.03");

        //when
        Throwable throwable = Assertions.catchThrowable(() -> transactionRequestValidator.validate(request));

        //then
        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("validation.request.timestamp.not.present");
    }

    @Test
    public void should_throw_exception_when_amount_is_not_parsable() {
        //given
        TransactionRequest request = new TransactionRequest();
        request.setAmount("One Hunderd");
        request.setTimestamp("2018-10-07T09:59:51.312Z");

        //when
        Throwable throwable = Assertions.catchThrowable(() -> transactionRequestValidator.validate(request));

        //then
        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("validation.request.amount.not.parsable");
    }

    @Test
    public void should_throw_exception_when_timestamp_is_not_parsable() {
        //given
        TransactionRequest request = new TransactionRequest();
        request.setAmount("19.03");
        request.setTimestamp("2018-10-07 09:59:51");

        //when
        Throwable throwable = Assertions.catchThrowable(() -> transactionRequestValidator.validate(request));

        //then
        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("validation.request.timestamp.not.parsable");
    }

    @Test
    public void should_throw_exception_when_timestamp_is_older_than_60_sec() {
        //given
        TransactionRequest request = new TransactionRequest();
        request.setAmount("19.03");
        String sixtySecAndOneMillisecAgo = Instant.now()
                                            .minusMillis(TimeUnit.SECONDS.toMillis(60))
                                            .minusMillis(TimeUnit.MILLISECONDS.toMillis(1))
                                            .toString();
        request.setTimestamp(sixtySecAndOneMillisecAgo);

        //when
        Throwable throwable = Assertions.catchThrowable(() -> transactionRequestValidator.validate(request));

        //then
        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(TransactionTooOldException.class)
                .hasMessage("validation.request.timestamp.too.old");
    }

    @Test
    public void should_throw_exception_when_timestamp_is_future() {
        //given
        TransactionRequest request = new TransactionRequest();
        request.setAmount("19.03");
        String oneMinLater = Instant.now()
                                    .plusMillis(TimeUnit.MINUTES.toMillis(1))
                                    .toString();
        request.setTimestamp(oneMinLater);

        //when
        Throwable throwable = Assertions.catchThrowable(() -> transactionRequestValidator.validate(request));

        //then
        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(FutureTransactionException.class)
                .hasMessage("validation.request.timestamp.future");
    }


    @Test
    public void should_not_throw_exception_when_request_is_valid() {
        //given
        TransactionRequest request = new TransactionRequest();
        request.setAmount("19.03");
        request.setTimestamp(Instant.now().toString());

        //when
        Throwable throwable = Assertions.catchThrowable(() -> transactionRequestValidator.validate(request));

        //then
        assertThat(throwable).isNull();
    }
}