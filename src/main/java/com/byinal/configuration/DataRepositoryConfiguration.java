package com.byinal.configuration;

import com.byinal.repository.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataRepositoryConfiguration {

    @Bean
    public TransactionRepository transactionRepository(){ return TransactionRepository.getInstance();}

}