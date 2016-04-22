package com.miriamlaurel.jcarb.model.order;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class Order implements Comparable<Order> {

    private final OrderKey key;
    private final BigDecimal amount;
    private final BigDecimal price;
    private final Instant timestamp;

    public Order(@NotNull OrderKey key, @NotNull BigDecimal amount, @NotNull BigDecimal price, @Nullable Instant timestamp) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(String.format("Order amount is %f (should be positive or zero)", amount));
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(String.format("Order price is %f (should be positive)", price));
        }
        this.key = key;
        this.amount = amount;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Order(OrderKey key, BigDecimal amount, BigDecimal price) {
        this(key, amount, price, null);
    }

    @NotNull
    public OrderKey getKey() {
        return key;
    }

    @NotNull
    public BigDecimal getAmount() {
        return amount;
    }

    @NotNull
    public BigDecimal getPrice() {
        return price;
    }

    @NotNull
    public Optional<Instant> getTimestamp() {
        return Optional.ofNullable(timestamp);
    }

    @Override
    public String toString() {
        return String.format("%s - %s @ %s", key, amount, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(key, order.key) &&
                Objects.equals(amount, order.amount) &&
                Objects.equals(price, order.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, amount, price);
    }

    @Override
    public int compareTo(@NotNull Order o) {
        return key.getSide() == Side.BID ? o.price.compareTo(price) : price.compareTo(o.price);
    }
}
