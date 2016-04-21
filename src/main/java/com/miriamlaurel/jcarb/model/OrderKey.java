package com.miriamlaurel.jcarb.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OrderKey {
    private final String orderId;
    private final Party party;
    private final Instrument instrument;
    private final Side side;

    public OrderKey(@NotNull String orderId, @NotNull Party party, @NotNull Instrument instrument, @NotNull Side side) {
        this.orderId = orderId;
        this.party = party;
        this.instrument = instrument;
        this.side = side;
    }

    @NotNull
    public String getOrderId() {
        return orderId;
    }

    @NotNull
    public Party getParty() {
        return party;
    }

    @NotNull
    public Instrument getInstrument() {
        return instrument;
    }

    @NotNull
    public Side getSide() {
        return side;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s (%s)", party, instrument, side, orderId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderKey orderKey = (OrderKey) o;
        return Objects.equals(orderId, orderKey.orderId) &&
                Objects.equals(party, orderKey.party) &&
                Objects.equals(instrument, orderKey.instrument) &&
                side == orderKey.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, party, instrument, side);
    }
}
