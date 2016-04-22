package com.miriamlaurel.jcarb.model.trading;

import java.math.BigDecimal;

public interface Exec {
    String getOrderId();
    BigDecimal getRemainingAmount();
}
