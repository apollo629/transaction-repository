package com.byinal.service;

import com.byinal.convertor.BigDecimalSummaryStatisticsToStatisticsResponseConverter;
import com.byinal.model.response.StatisticsResponse;
import com.byinal.repository.TransactionRepository;
import com.byinal.util.BigDecimalSummaryStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {

    @Mock
    private TransactionRepository txRepository;

    @Mock
    private BigDecimalSummaryStatisticsToStatisticsResponseConverter converter;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    public void should_retrieve_stats_successfully() {
        //given
        BigDecimalSummaryStatistics stats = new BigDecimalSummaryStatistics();
        StatisticsResponse statsResponse = new StatisticsResponse();

        when(txRepository.getTxStatistics()).thenReturn(stats);
        when(converter.apply(stats)).thenReturn(statsResponse);

        //when
        StatisticsResponse statisticsResponse = statisticsService.retrieveStatsResponse();

        //then
        assertThat(statisticsResponse).isNotNull();
        InOrder inOrder = Mockito.inOrder(txRepository, converter);
        inOrder.verify(txRepository).getTxStatistics();
        inOrder.verify(converter).apply(stats);
        inOrder.verifyNoMoreInteractions();
    }
}