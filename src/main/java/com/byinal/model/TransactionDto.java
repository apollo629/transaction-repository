package com.byinal.model;

import java.math.BigDecimal;

public class TransactionDto {

    private BigDecimal amount;
    private long timestamp;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format(
                "%s{amount=%f, timestamp=%s}",
                this.getClass().getSimpleName(),
                getAmount(),
                getTimestamp());
    }
}