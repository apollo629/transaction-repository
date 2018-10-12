package com.byinal.service;

import com.byinal.model.TransactionDto;
import com.byinal.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void add(TransactionDto transactionDto) {
        transactionRepository.save(transactionDto.getTimestamp(), transactionDto.getAmount());
    }

    public void deleteAll() {
        transactionRepository.deleteAll();
    }
}