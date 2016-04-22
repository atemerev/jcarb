package com.miriamlaurel.jcarb;

import com.miriamlaurel.jcarb.client.CoinbaseApi;
import com.miriamlaurel.jcarb.client.KrakenApi;
import com.miriamlaurel.jcarb.model.analysis.OrderBookMontage;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.OrderBook;
import com.miriamlaurel.jcarb.model.order.Party;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Main {

    private static final Instrument BTCUSD = Instrument.fromCode("BTC/USD");
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final OrderBookMontage montage = new OrderBookMontage(BTCUSD);

    public static void main(String[] args) throws IOException {
        Consumer<OrderBook> listener = book -> {
            montage.accept(book);
            System.out.println(montage.getGlobalBook().getSpread());
        };

        KrakenApi krakenApi = new KrakenApi(listener, 2);
        CoinbaseApi coinbaseApi = new CoinbaseApi(listener, 2);
        krakenApi.subscribe(BTCUSD);
        coinbaseApi.subscribe(BTCUSD);
    }
}
