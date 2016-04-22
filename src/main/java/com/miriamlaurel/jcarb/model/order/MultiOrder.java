package com.miriamlaurel.jcarb.model.order;

import com.miriamlaurel.jcarb.model.order.Order;

import java.util.List;

public class MultiOrder {

    private final List<Order> orders;

    public MultiOrder(List<Order> orders) {
        this.orders = orders;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
