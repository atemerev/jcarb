package com.miriamlaurel.jcarb.client;

import com.miriamlaurel.jcarb.model.Instrument;
import com.miriamlaurel.jcarb.model.OrderBook;

public interface SyncApi {
    OrderBook getOrderBook(Instrument instrument);
}
