package com.byinal.controller;

import com.byinal.convertor.TransactionRequestToDtoConverter;
import com.byinal.model.TransactionDto;
import com.byinal.model.request.TransactionRequest;
import com.byinal.service.TransactionService;
import com.byinal.validator.TransactionRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Transactions endpoint to accept transaction requests
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionRequestValidator transactionRequestValidator;
    private final TransactionRequestToDtoConverter transactionRequestToDtoConverter;
    private final TransactionService transactionService;

    public TransactionController(TransactionRequestValidator transactionRequestValidator,
                                 TransactionRequestToDtoConverter transactionRequestToDtoConverter,
                                 TransactionService transactionService) {
        this.transactionRequestValidator = transactionRequestValidator;
        this.transactionRequestToDtoConverter = transactionRequestToDtoConverter;
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTransaction(@RequestBody TransactionRequest transactionRequest) {
        ZonedDateTime startTime = ZonedDateTime.now();
        logger.info("Create transaction started with request: {}", transactionRequest); // request contains no sensitive data so logging shouldn't be a problem
        transactionRequestValidator.validate(transactionRequest);
        TransactionDto transactionDto = transactionRequestToDtoConverter.apply(transactionRequest);
        transactionService.add(transactionDto);
        logger.info("Create transaction is ended. Duration: {} in millis.", ChronoUnit.MILLIS.between(startTime, ZonedDateTime.now()));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction() {
        ZonedDateTime startTime = ZonedDateTime.now();
        logger.info("Delete transaction started");
        transactionService.deleteAll();
        logger.info("Delete transaction is ended. Duration: {} in millis.", ChronoUnit.MILLIS.between(startTime, ZonedDateTime.now()));
    }
}