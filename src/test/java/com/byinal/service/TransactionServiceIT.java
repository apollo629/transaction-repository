package com.byinal.service;

import com.byinal.Application;
import com.byinal.model.TransactionDto;
import com.byinal.model.response.StatisticsResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class TransactionServiceIT {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private StatisticsService statisticsService;

    @Test
    @Ignore
    public void should_save_and_give_stats_concurrently() throws InterruptedException {

        int numberOfThreads = 3;
        final CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        TransactionDto transactionDto = new TransactionDto();

        IntStream.rangeClosed(1, numberOfThreads)
                .forEach(index -> executorService.submit(() -> {
                    for (int i = 1; i <= 1903; i++) {
                        transactionDto.setTimestamp(Instant.now().minusMillis(i).toEpochMilli());
                        transactionDto.setAmount(new BigDecimal(i));
                        transactionService.add(transactionDto);
                    }
                    latch.countDown();
                }));

//
//        IntStream.rangeClosed(0, numberOfThreads)
//                .forEach(index -> executorService.execute(producer));

        latch.await();

        executorService.awaitTermination(3, TimeUnit.SECONDS);

        StatisticsResponse statisticsResponse = statisticsService.retrieveStatsResponse();

        System.out.println(statisticsResponse.toString());

        // assertions
        assertThat(statisticsResponse).isNotNull();
        assertThat(statisticsResponse.getCount()).isEqualTo(5709L);
        assertThat(statisticsResponse.getMax()).isEqualTo("1903.00");
        assertThat(statisticsResponse.getMin()).isEqualTo("1.00");
        assertThat(statisticsResponse.getSum()).isEqualTo("5434968.00");
        assertThat(statisticsResponse.getAvg()).isEqualTo("952.00");
    }


}
