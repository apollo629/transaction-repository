package com.byinal.convertor;

import com.byinal.model.response.StatisticsResponse;
import com.byinal.util.BigDecimalSummaryStatistics;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Function;


@Component
public class BigDecimalSummaryStatisticsToStatisticsResponseConverter implements Function<BigDecimalSummaryStatistics, StatisticsResponse> {

    private static final int SCALE = 2;
    private static final int ROUND_HALF_UP = BigDecimal.ROUND_HALF_UP;

    @Override
    public StatisticsResponse apply(BigDecimalSummaryStatistics bigDecimalSummaryStatistics) {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setCount(bigDecimalSummaryStatistics.getCount());
        statisticsResponse.setSum(bigDecimalSummaryStatistics.getSum().setScale(SCALE, ROUND_HALF_UP).toString());
        statisticsResponse.setMax(bigDecimalSummaryStatistics.getMax().setScale(SCALE, ROUND_HALF_UP).toString());
        statisticsResponse.setMin(bigDecimalSummaryStatistics.getMin().setScale(SCALE, ROUND_HALF_UP).toString());
        statisticsResponse.setAvg(bigDecimalSummaryStatistics.getAverage().setScale(SCALE, ROUND_HALF_UP).toString());
        return statisticsResponse;
    }
}