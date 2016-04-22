package com.miriamlaurel.jcarb.client;

import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.trading.Exec;

import java.util.function.Consumer;

public interface TradingApi {
    String getName();

    void subscribe(Instrument... instruments);

    void trade(Consumer<Exec> executionResponseListener);
}
