package com.byinal.util;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class BigDecimalSummaryStatistics implements Consumer<BigDecimal> {

    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal minValue = BigDecimal.ZERO;
    private BigDecimal maxValue = BigDecimal.ZERO;
    private long count = 0;

    /**
     * Records another value into the summary information.
     *
     * @param amount the input value
     * @throws NullPointerException if amount is null
     */
    @Override
    public void accept(BigDecimal amount) {
        if (isCountZero()) {
            minValue = amount;
            maxValue = amount;
        } else {
            minValue = minValue.min(amount);
            maxValue = maxValue.max(amount);
        }
        count++;
        sum = sum.add(amount);
    }

    /**
     * Combines the state of another BigDecimalSummaryStatistics into this
     * one.
     *
     * @param other another BigDecimalSummaryStatistics
     * @throws NullPointerException if other is null
     */
    public void combine(BigDecimalSummaryStatistics other) {
        if (isCountZero()) {
            count = other.count;
            sum = other.sum;
            minValue = other.minValue;
            maxValue = other.maxValue;
        } else {
            count = count + other.count;
            sum = sum.add(other.sum);
            minValue = minValue.min(other.minValue);
            maxValue = maxValue.max(other.maxValue);
        }
    }

    public final long getCount() {
        return count;
    }

    public final BigDecimal getSum() {
        return sum;
    }

    public final BigDecimal getMin() {
        return minValue;
    }

    public final BigDecimal getMax() {
        return maxValue;
    }

    public final BigDecimal getAverage() {
        return isCountZero() ? BigDecimal.ZERO : getSum().divide(BigDecimal.valueOf(getCount()), 2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isCountZero() {
        return getCount() == 0L;
    }

    @Override
    public String toString() {
        return String.format(
                "%s{count=%d, sum=%f, min=%f, average=%f, max=%f}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax());
    }
}