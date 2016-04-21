package com.miriamlaurel.jcarb;

import com.miriamlaurel.jcarb.client.CoinbaseApi;
import com.miriamlaurel.jcarb.client.KrakenApi;
import com.miriamlaurel.jcarb.model.Instrument;
import com.miriamlaurel.jcarb.model.OrderBook;
import com.miriamlaurel.jcarb.model.Party;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Main {

    private static final Instrument BTCUSD = Instrument.fromCode("BTC/USD");
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final KrakenApi krakenApi = new KrakenApi();
    private static final CoinbaseApi coinbaseApi = new CoinbaseApi();
    private static final OrderBook montage = new OrderBook(BTCUSD);

    public static void main(String[] args) throws IOException {

        Supplier<OrderBook> krakenTask = () -> {
            OrderBook book = krakenApi.getOrderBook(BTCUSD);
            System.out.println("Got Kraken");
            return book;
        };
        Supplier<OrderBook> coinbaseTask = () -> {
            OrderBook book = coinbaseApi.getOrderBook(BTCUSD);
            System.out.println("Got Coinbase");
            return book;
        };
        CompletableFuture<Void> krakenFuture = CompletableFuture.supplyAsync(krakenTask, executor).thenAccept((book) -> montage.replaceParty(Party.KRAKEN, book));
        CompletableFuture<Void> coinbaseFuture = CompletableFuture.supplyAsync(coinbaseTask, executor).thenAccept((book) -> montage.replaceParty(Party.COINBASE, book));
        CompletableFuture.allOf(krakenFuture, coinbaseFuture).thenRun(() -> {
            System.out.println(montage);
            krakenApi.stop();
            coinbaseApi.stop();
        });
    }
}
