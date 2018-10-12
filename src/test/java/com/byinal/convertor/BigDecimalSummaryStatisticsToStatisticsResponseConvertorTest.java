package com.byinal.convertor;

import com.byinal.model.response.StatisticsResponse;
import com.byinal.util.BigDecimalSummaryStatistics;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalSummaryStatisticsToStatisticsResponseConvertorTest {

    private final BigDecimalSummaryStatisticsToStatisticsResponseConverter convertor
            = new BigDecimalSummaryStatisticsToStatisticsResponseConverter();

    @Test
    public void should_convert_empty_statistics_successfully() {
        //given
        BigDecimalSummaryStatistics bigDecimalSummaryStatistics = new BigDecimalSummaryStatistics();

        //when
        StatisticsResponse statisticsResponse = convertor.apply(bigDecimalSummaryStatistics);

        //then
        assertThat(statisticsResponse)
                .isNotNull()
                .extracting("sum", "avg", "max", "min", "count")
                .containsExactly("0.00", "0.00", "0.00", "0.00", 0L);
    }

    @Test
    public void should_convert_successfully() {
        //given
        BigDecimalSummaryStatistics bigDecimalSummaryStatistics = new BigDecimalSummaryStatistics();

        IntStream.rangeClosed(1, 1903)
                .forEach(index -> bigDecimalSummaryStatistics.accept(new BigDecimal(index)));

        //when
        StatisticsResponse statisticsResponse = convertor.apply(bigDecimalSummaryStatistics);

        //then
        assertThat(statisticsResponse)
                .isNotNull()
                .extracting("sum", "avg", "max", "min", "count")
                .containsExactly("1811656.00", "952.00", "1903.00", "1.00", 1903L);
    }
}