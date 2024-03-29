package com.miriamlaurel.jcarb.model.order;

import com.miriamlaurel.jcarb.common.JsonSerializable;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.op.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

public class OrderBook implements JsonSerializable, Consumer<OrderOp> {

    private int seq = 0;
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

    public synchronized void accept(OrderOp op) {
        if (op instanceof AddOrder) {
            AddOrder add = (AddOrder) op;
            addOrder(add.getOrder());
        } else if (op instanceof DeleteOrder) {
            DeleteOrder delete = (DeleteOrder) op;
            removeOrder(delete.getKey());
        } else if (op instanceof ChangeOrder) {
            ChangeOrder change = (ChangeOrder) op;
            removeOrder(change.getOrder().getKey());
            addOrder(change.getOrder());
        } else if (op instanceof ReplaceParty) {
            ReplaceParty replace = (ReplaceParty) op;
            replaceParty(replace.getParty(), replace.getPartyBook());
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
    public Order getOrderByPriceAggregated(@NotNull Side side, @NotNull BigDecimal price) {
        Set<Order> orders = getBest(side);
        BigDecimal totalAmount = BigDecimal.ZERO;
        StringJoiner sj = new StringJoiner(":");
        // todo aggregate parties correctly!
        Party party = null;
        for (Order order : orders) {
            totalAmount = totalAmount.add(order.getAmount());
            sj.add(order.getKey().getOrderId());
            party = order.getKey().getParty();
        }
        assert party != null;
        OrderKey key = new OrderKey(sj.toString(), party, instrument, side);
        return new Order(key, totalAmount, price);
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

    @NotNull
    public synchronized BigDecimal getBestPrice(@NotNull Side side, @NotNull Party party) {
        TreeMap<BigDecimal, Map<OrderKey, Order>> line = side == Side.BID ? bids : asks;
        if (line.isEmpty()) {
            throw new IllegalStateException("No prices found for side: " + side);
        }
        NavigableSet<BigDecimal> prices = line.navigableKeySet();
        for (BigDecimal price : prices) {
            Map<OrderKey, Order> orders = line.get(price);
            for (Order order : orders.values()) {
                if (party.equals(order.getKey().getParty())) {
                    return price;
                }
            }
        }
        throw new IllegalStateException(String.format("No prices found for side %s and party %s", side, party));
    }

    @NotNull
    public synchronized Order getBestAggregated(@NotNull Side side) {
        return getOrderByPriceAggregated(side, getBestPrice(side));
    }

    public synchronized void replaceParty(Party party, OrderBook theirBook) {
        for (OrderKey theirKey : theirBook.byKey.keySet()) {
            if (!party.equals(theirKey.getParty())) {
                throw new IllegalArgumentException(String.format("Parties don't match: required %s, got %s",
                        party, theirKey.getParty()));
            }
            removeByParty(party);
        }
        for (Order order : theirBook.byKey.values()) {
            addOrder(order);
        }

    }

    public synchronized void removeByParty(Party party) {
        Set<OrderKey> toRemove = new HashSet<>();
        for (OrderKey key : byKey.keySet()) {
            if (party.equals(key.getParty())) {
                toRemove.add(key);
            }
        }
        for (OrderKey key : toRemove) {
            removeOrder(key);
        }
    }

    public
    @NotNull
    Party getSingleParty() {
        if (byKey.isEmpty()) {
            throw new IllegalStateException("Can't get party for an empty order book");
        }
        Party result = null;
        for (OrderKey key : byKey.keySet()) {
            if (result == null) {
                result = key.getParty();
            } else {
                if (!result.equals(key.getParty())) {
                    throw new IllegalStateException("Multiple parties detected in this order book");
                }
            }
        }
        assert result != null;
        return result;
    }

    public synchronized boolean isEmpty() {
        return byKey.isEmpty();
    }

    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();
        for (BigDecimal price : bids.descendingKeySet()) {
            for (Order order : bids.get(price).values()) {
                builder.append(order);
                builder.append("\n");
            }
        }
        builder.append("↑↑↑ BIDS ↑↑↑\n");
        if (!bids.isEmpty() && !asks.isEmpty()) {
            builder.append(String.format("-- (%s) --\n", getSpread().stripTrailingZeros()));
        }
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

    @Override
    public synchronized JSONObject toJson() {
        JSONObject result = new JSONObject();
        JSONArray bidArray = new JSONArray();
        JSONArray askArray = new JSONArray();
        for (BigDecimal price : bids.navigableKeySet()) {
            for (Order order : bids.get(price).values()) {
                bidArray.put(orderToJson(order));
            }
        }
        for (BigDecimal price : asks.navigableKeySet()) {
            for (Order order : asks.get(price).values()) {
                askArray.put(orderToJson(order));
            }
        }
        result.put("type", "book");
        result.put("bids", bidArray);
        result.put("asks", askArray);
        return result;
    }

    public synchronized int getSeq() {
        return seq;
    }

    public synchronized void setSeq(int seq) {
        this.seq = seq;
    }

    private JSONArray orderToJson(Order order) {
        JSONArray result = new JSONArray();
        result.put(order.getKey().getParty());
        result.put(order.getPrice().toString());
        result.put(order.getAmount().toString());
        return result;
    }
}
