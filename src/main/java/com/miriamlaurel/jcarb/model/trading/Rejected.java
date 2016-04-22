package com.miriamlaurel.jcarb.model.trading;

import java.math.BigDecimal;

public class Rejected implements Exec {
    private final String orderId;
    private final BigDecimal remainingAmount;

    public Rejected(String orderId, BigDecimal remainingAmount) {
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
