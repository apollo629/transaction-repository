package com.byinal.service;

import com.byinal.model.TransactionDto;
import com.byinal.repository.TransactionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void should_add_tx() {
        //given
        TransactionDto transactionDto = new TransactionDto();
        long timestamp = Instant.now().toEpochMilli();
        transactionDto.setTimestamp(timestamp);
        transactionDto.setAmount(BigDecimal.TEN);

        //when
        transactionService.add(transactionDto);

        //then
        verify(transactionRepository).save(timestamp, BigDecimal.TEN);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    public void should_delete_txs() {
        //when
        transactionService.deleteAll();

        //then
        verify(transactionRepository).deleteAll();
        verifyNoMoreInteractions(transactionRepository);
    }
}