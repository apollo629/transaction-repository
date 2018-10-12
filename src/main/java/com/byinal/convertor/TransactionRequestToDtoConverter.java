package com.byinal.convertor;

import com.byinal.model.TransactionDto;
import com.byinal.model.request.TransactionRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;

/**
 * Convertor for requests to data transfer object.
 * Amount is converted to BigDecimal from String
 * Timestamp is converted to Long from String
 */
@Component
public class TransactionRequestToDtoConverter implements Function<TransactionRequest, TransactionDto> {

    @Override
    public TransactionDto apply(TransactionRequest transactionRequest) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal(transactionRequest.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
        transactionDto.setTimestamp(Instant.parse(transactionRequest.getTimestamp()).toEpochMilli());
        return transactionDto;
    }
}