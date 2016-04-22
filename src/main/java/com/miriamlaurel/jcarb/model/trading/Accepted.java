package com.miriamlaurel.jcarb.model.trading;

import java.math.BigDecimal;

public class Accepted implements Exec {
    private final String orderId;
    private final BigDecimal remainingAmount;

    public Accepted(String orderId, BigDecimal remainingAmount) {
        this.orderId = orderId;
        this.remainingAmount = remainingAmount;
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
}
