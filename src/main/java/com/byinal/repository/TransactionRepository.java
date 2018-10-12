package com.byinal.repository;

import com.byinal.util.BigDecimalSummaryStatistics;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * Spring bean used as data-store.
 * This store holds data in whole lifecycle of the application.
 */
public class TransactionRepository {

    private static final long STATS_REFRESH_SCOPE_IN_MILLISECONDS = TimeUnit.SECONDS.toMillis(60);

    private static TransactionRepository INSTANCE;
    private Instant createdAt;
    private ConcurrentNavigableMap<Long, BigDecimalSummaryStatistics> concurrentNavigableMap;

    private TransactionRepository() { }

    public static TransactionRepository getInstance() {
        return Optional.ofNullable(INSTANCE)
                .orElseGet(() -> {
                    INSTANCE = new TransactionRepository();
                    INSTANCE.createdAt = Instant.now();
                    INSTANCE.concurrentNavigableMap = new ConcurrentSkipListMap<>(Comparator.naturalOrder());
                    return INSTANCE;
                });
    }

    /**
     * Finds in which bucket given amount should go.
     * Writes in thread-safe mode with putVal call.
     */
    public void save(long timestamp, BigDecimal amount) {
        long bucket = timestamp - createdAt.toEpochMilli();
        putVal(bucket, amount);
    }

    /**
     * Combine stats then return combined version of stats.
     */
    public BigDecimalSummaryStatistics getTxStatistics() {
        BigDecimalSummaryStatistics summaryStatistics = new BigDecimalSummaryStatistics();
        retrieveTxsOfValidRange().forEach(summaryStatistics::combine);
        return summaryStatistics;
    }

    private synchronized void putVal(long bucket, BigDecimal amount) {
        BigDecimalSummaryStatistics bucketStats = concurrentNavigableMap.getOrDefault(bucket, new BigDecimalSummaryStatistics());
        bucketStats.accept(amount);
        concurrentNavigableMap.put(bucket, bucketStats);
    }

    /**
     * Method will return colelction of last 60 secs stats. Each object holds stats for specific range
     * ConcurrentNavigableMap leads us to achieve O(1) complexity.
     */
    private Collection<BigDecimalSummaryStatistics> retrieveTxsOfValidRange() {
        long duration = Instant.now().toEpochMilli() - createdAt.toEpochMilli();
        return concurrentNavigableMap.tailMap(duration - STATS_REFRESH_SCOPE_IN_MILLISECONDS).values();
    }

    public void deleteAll() {
        concurrentNavigableMap.clear();
    }
}