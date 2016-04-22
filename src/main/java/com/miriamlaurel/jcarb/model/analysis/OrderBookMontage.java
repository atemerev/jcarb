package com.miriamlaurel.jcarb.model.analysis;

import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.OrderBook;
import com.miriamlaurel.jcarb.model.order.Party;

import java.util.function.Consumer;

public class OrderBookMontage implements Consumer<OrderBook> {

    private final Instrument instrument;
    private final OrderBook globalBook;

    public OrderBookMontage(Instrument instrument) {
        this.instrument = instrument;
        this.globalBook = new OrderBook(instrument);
    }

    @Override
    public synchronized void accept(OrderBook partyBook) {
        Party party = partyBook.getSingleParty();
        globalBook.replaceParty(party, partyBook);
    }

    public synchronized OrderBook getGlobalBook() {
        return globalBook;
    }
}
