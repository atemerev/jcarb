package com.miriamlaurel.jcarb.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CryptoCurrency implements Asset {
    private String code;

    public CryptoCurrency(@NotNull String code) {
        this.code = code;
    }

    public @NotNull String getCode() {
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
        CryptoCurrency that = (CryptoCurrency) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
