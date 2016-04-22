package com.miriamlaurel.jcarb.model.asset;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Instrument {
    private final Asset primary;
    private final Asset secondary;

    public Instrument(@NotNull Asset primary, @NotNull Asset secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public static Instrument fromCode(String code) {
        assert(code.contains("/"));
        String[] tokens = code.split("\\/");
        String primary = tokens[0];
        String secondary = tokens[1];
        return new Instrument(Asset.fromCode(primary), Asset.fromCode(secondary));
    }

    public @NotNull Asset getPrimary() {
        return primary;
    }

    public @NotNull Asset getSecondary() {
        return secondary;
    }

    @Override
    public String toString() {
        return primary + "/" + secondary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instrument that = (Instrument) o;
        return Objects.equals(primary, that.primary) &&
                Objects.equals(secondary, that.secondary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primary, secondary);
    }
}
