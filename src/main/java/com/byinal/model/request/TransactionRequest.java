package com.byinal.model.request;

public class TransactionRequest {

    private String amount;
    private String timestamp;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format(
                "%s{amount=%s, timestamp=%s}",
                this.getClass().getSimpleName(),
                getAmount(),
                getTimestamp());
    }
}