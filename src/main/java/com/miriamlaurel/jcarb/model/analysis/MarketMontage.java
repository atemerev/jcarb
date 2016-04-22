package com.miriamlaurel.jcarb.model.analysis;

import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.OrderBook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MarketMontage implements Consumer<OrderBook> {
    private final Map<Instrument, OrderBookMontage> montages = new HashMap<>();

    @Override
    public synchronized void accept(@NotNull OrderBook partyBook) {
        Instrument instrument = partyBook.getInstrument();
        OrderBookMontage montage = montages.get(instrument);
        if (montage == null) {
            montage = new OrderBookMontage(instrument);
            montages.put(instrument, montage);
        }
        montage.accept(partyBook);
    }

    @Nullable
    public OrderBookMontage getMontage(@NotNull Instrument instrument) {
        return montages.get(instrument);
    }
}
