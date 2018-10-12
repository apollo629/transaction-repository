package com.byinal.service;

import com.byinal.convertor.BigDecimalSummaryStatisticsToStatisticsResponseConverter;
import com.byinal.model.response.StatisticsResponse;
import com.byinal.repository.TransactionRepository;
import com.byinal.util.BigDecimalSummaryStatistics;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final TransactionRepository transactionRepository;
    private final BigDecimalSummaryStatisticsToStatisticsResponseConverter responseConvertor;

    public StatisticsService(TransactionRepository transactionRepository,
                             BigDecimalSummaryStatisticsToStatisticsResponseConverter responseConvertor) {
        this.transactionRepository = transactionRepository;
        this.responseConvertor = responseConvertor;
    }


    public StatisticsResponse retrieveStatsResponse() {
        BigDecimalSummaryStatistics txStatistics = transactionRepository.getTxStatistics();
        return responseConvertor.apply(txStatistics);
    }
}