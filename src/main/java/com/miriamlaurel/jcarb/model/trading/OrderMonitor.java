package com.miriamlaurel.jcarb.model.trading;

import com.miriamlaurel.jcarb.model.order.Order;

import java.util.function.Consumer;

public interface OrderMonitor {
    Order getOrder();
    void addExecListener(Consumer<Exec> listener);
}
