package com.byinal.repository;

import com.byinal.util.BigDecimalSummaryStatistics;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRepositoryTest {

    private TransactionRepository transactionStore;

    @Before
    public void setUp() {
        transactionStore = TransactionRepository.getInstance();
    }

    @Test
    public void should_save_tx_concurrently_and_give_stats_successfully() throws InterruptedException {
        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        final CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Runnable producer = () -> IntStream.rangeClosed(1, 1903)
                .forEach(index -> {
                    transactionStore.save(Instant.now().minusMillis(index).toEpochMilli(), new BigDecimal(index));
                    latch.countDown();
                });

        IntStream.rangeClosed(0, numberOfThreads)
                .forEach(index -> executorService.submit(producer));

        latch.await();
        executorService.awaitTermination(3, TimeUnit.SECONDS);

        BigDecimalSummaryStatistics stats = transactionStore.getTxStatistics();

        // assertions
        assertThat(stats).isNotNull();
        assertThat(stats.getMax()).isEqualTo(new BigDecimal(1903));
        assertThat(stats.getMin()).isEqualTo(new BigDecimal(1));
        assertThat(stats.getSum()).isEqualTo(new BigDecimal(7246624));
        assertThat(stats.getCount()).isEqualTo(7612L);
        assertThat(stats.getAverage()).isEqualTo(new BigDecimal(952).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void should_delete_all_txs() {
        //given
        IntStream.rangeClosed(1, 2000)
                .forEach(index -> transactionStore.save(Instant.now().minusMillis(index).toEpochMilli(), new BigDecimal(index)));

        //when
        BigDecimalSummaryStatistics statsBeforeDeletion = transactionStore.getTxStatistics();

        transactionStore.deleteAll();

        BigDecimalSummaryStatistics statsAfterDeletion = transactionStore.getTxStatistics();

        //then
        assertThat(statsBeforeDeletion).isNotNull();
        assertThat(statsBeforeDeletion.getCount()).isGreaterThan(0);
        assertThat(statsBeforeDeletion.getMax()).isGreaterThan(BigDecimal.ZERO);
        assertThat(statsBeforeDeletion.getMin()).isGreaterThan(BigDecimal.ZERO);
        assertThat(statsBeforeDeletion.getSum()).isGreaterThan(BigDecimal.ZERO);
        assertThat(statsBeforeDeletion.getAverage()).isGreaterThan(BigDecimal.ZERO);

        assertThat(statsAfterDeletion).isNotNull();
        assertThat(statsAfterDeletion).extracting("count", "max", "min", "sum", "average")
                .containsExactly(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}