package com.miriamlaurel.jcarb.model.portfolio;

import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.Party;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.Objects;

public class Position {

    private final Party party;
    private final Instrument instrument;
    private final BigDecimal primary;
    private final BigDecimal secondary;
    private final Instant since;

    public Position(@NotNull Party party, @NotNull Instrument instrument, @NotNull BigDecimal primary, @NotNull BigDecimal secondary, @NotNull Instant since) {
        this.party = party;
        this.instrument = instrument;
        this.primary = primary;
        this.secondary = secondary;
        this.since = since;
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
    public BigDecimal getPrimary() {
        return primary;
    }

    @NotNull
    public BigDecimal getSecondary() {
        return secondary;
    }

    @NotNull
    public BigDecimal getPrice() {
        return secondary.divide(primary, MathContext.DECIMAL64).abs();
    }

    @NotNull
    public BigDecimal getProfitLoss(BigDecimal newPrice) {
        return newPrice.subtract(getPrice()).multiply(getPrimary());
    }

    @NotNull
    public Instant getSince() {
        return since;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(party, position.party) &&
                Objects.equals(instrument, position.instrument) &&
                Objects.equals(primary, position.primary) &&
                Objects.equals(secondary, position.secondary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(party, instrument, primary, secondary);
    }
}
