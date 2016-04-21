package com.miriamlaurel.jcarb.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Party {

    public static final Party COINBASE = new Party("Coinbase");
    public static final Party KRAKEN = new Party("Kraken");
    public static final Party BITFINEX = new Party("Bitfinex");
    public static final Party GATECOIN = new Party("Gatecoin");

    private final String code;

    public Party(@NotNull String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return Objects.equals(code, party.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
