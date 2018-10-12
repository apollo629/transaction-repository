package com.byinal.convertor;

import com.byinal.model.TransactionDto;
import com.byinal.model.request.TransactionRequest;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRequestToDtoConverterTest {

    private final TransactionRequestToDtoConverter transactionRequestToDtoConverter = new TransactionRequestToDtoConverter();

    @Test
    public void should_convert_successfully_with_scale_four_to_scale_two_and_rounding() {
        //given
        TransactionRequest request = new TransactionRequest();
        request.setAmount("19.0260");
        Instant now = Instant.now();
        request.setTimestamp(now.toString());

        //when
        TransactionDto transactionDto = transactionRequestToDtoConverter.apply(request);

        //then
        assertThat(transactionDto)
                .isNotNull()
                .extracting("amount", "timestamp")
                .containsExactly(new BigDecimal("19.03"), now.toEpochMilli());
    }

    @Test
    public void should_convert_successfully_with_scale_six_to_scale_two_without_rounding() {
        //given
        TransactionRequest request = new TransactionRequest();
        request.setAmount("19.031234");
        Instant now = Instant.now();
        request.setTimestamp(now.toString());

        //when
        TransactionDto transactionDto = transactionRequestToDtoConverter.apply(request);

        //then
        assertThat(transactionDto)
                .isNotNull()
                .extracting("amount", "timestamp")
                .containsExactly(new BigDecimal("19.03"), now.toEpochMilli());
    }
}