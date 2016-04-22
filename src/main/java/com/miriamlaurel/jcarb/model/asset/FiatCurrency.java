package com.miriamlaurel.jcarb.model.asset;

import org.jetbrains.annotations.NotNull;

import java.util.Currency;
import java.util.Objects;

public class FiatCurrency implements Asset {

    private final Currency currency;

    public FiatCurrency(@NotNull Currency currency) {
        this.currency = currency;
    }

    public FiatCurrency(@NotNull String code) {
        this.currency = Currency.getInstance(code);
    }

    public @NotNull Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiatCurrency that = (FiatCurrency) o;
        return Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency);
    }
}
