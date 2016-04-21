package com.miriamlaurel.jcarb.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;

public class OrderBook {

    private final Instrument instrument;
    private final TreeMap<BigDecimal, Map<OrderKey, Order>> bids = new TreeMap<>(Comparator.reverseOrder());
    private final TreeMap<BigDecimal, Map<OrderKey, Order>> asks = new TreeMap<>();
    private final Map<OrderKey, Order> byKey = new HashMap<>();

    public OrderBook(@NotNull Instrument instrument) {
        this.instrument = instrument;
    }

    @NotNull
    public Instrument getInstrument() {
        return instrument;
    }

    public synchronized void addOrder(@NotNull Order order) {
        if (!instrument.equals(order.getKey().getInstrument())) {
            throw new IllegalArgumentException(
                    String.format("Order instrument %s should match order book instrument %s",
                            instrument, order.getKey().getInstrument()));
        }
        OrderKey key = order.getKey();
        Order existing = byKey.get(key);
        TreeMap<BigDecimal, Map<OrderKey, Order>> line = key.getSide() == Side.BID ? bids : asks;
        if (existing != null) {
            removeOrder(key);
        }
        byKey.put(key, order);
        BigDecimal price = order.getPrice();
        Map<OrderKey, Order> byPrice = line.getOrDefault(price, new HashMap<>());
        byPrice.put(key, order);
        line.put(price, byPrice);
    }

    @Nullable
    public synchronized Order removeOrder(@NotNull OrderKey key) {
        Order existing = byKey.get(key);
        TreeMap<BigDecimal, Map<OrderKey, Order>> line = key.getSide() == Side.BID ? bids : asks;
        if (existing != null) {
            byKey.remove(key);
            BigDecimal price = existing.getPrice();
            Map<OrderKey, Order> byPrice = line.get(price);
            byPrice.remove(key);
            if (byPrice.isEmpty()) {
                line.remove(price);
            }
            return existing;
        } else {
            return null;
        }
    }

    @Nullable
    public synchronized Order getOrderByKey(@NotNull OrderKey key) {
        return byKey.get(key);
    }

    @NotNull
    public synchronized Set<Order> getOrdersByPrice(@NotNull Side side, @NotNull BigDecimal price) {
        SortedMap<BigDecimal, Map<OrderKey, Order>> line = side == Side.BID ? bids : asks;
        Map<OrderKey, Order> orderMap = line.get(price);
        if (orderMap == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(orderMap.values());
        }
    }

    @NotNull
    public synchronized BigDecimal getBestPrice(@NotNull Side side) {
        SortedMap<BigDecimal, Map<OrderKey, Order>> line = side == Side.BID ? bids : asks;
        if (line.isEmpty()) {
            throw new IllegalStateException(String.format("Order book is empty for %s side", side));
        }
        return line.firstKey();
    }

    @NotNull
    public synchronized Set<Order> getBest(@NotNull Side side) {
        return getOrdersByPrice(side, getBestPrice(side));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (BigDecimal price : bids.descendingKeySet()) {
            for (Order order : bids.get(price).values()) {
                builder.append(order);
                builder.append("\n");
            }
        }
        builder.append("↑↑↑ BIDS ↑↑↑\n");
        builder.append("-------------\n");
        builder.append("↓↓↓ ASKS ↓↓↓\n");
        for (BigDecimal price : asks.navigableKeySet()) {
            for (Order order : asks.get(price).values()) {
                builder.append(order);
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public BigDecimal getSpread() {
        if (bids.isEmpty() || asks.isEmpty()) {
            throw new IllegalStateException("Order book is not full; can't determine spread");
        }
        BigDecimal bestBid = getBestPrice(Side.BID);
        BigDecimal bestAsk = getBestPrice(Side.ASK);
        return bestAsk.subtract(bestBid);
    }
}
