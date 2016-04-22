package com.miriamlaurel.jcarb.model.trading;

import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.Party;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

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
    public Instant getSince() {
        return since;
    }
}
