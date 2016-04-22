package com.miriamlaurel.jcarb.client;

import com.miriamlaurel.jcarb.common.Mortal;
import com.miriamlaurel.jcarb.common.StoppedException;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.OrderBook;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class PollingTradingApi implements TradingApi, Mortal {

    private static final int TASK_THREAD_POOL_SIZE = 4;

    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(TASK_THREAD_POOL_SIZE);
    protected final Set<Instrument> subscriptions = new HashSet<>();
    protected Runnable fetchMarketDataTask = null;
    protected final Consumer<OrderBook> orderBookListener;

    public PollingTradingApi(Consumer<OrderBook> orderBookListener, int pollIntervalSeconds) {
        this.orderBookListener = orderBookListener;
        this.fetchMarketDataTask = () -> {
            Stream<CompletableFuture<?>> futures = subscriptions.stream()
                    .map(instrument -> getOrderBookAsync(instrument).whenComplete((book, exception) -> {
                        if (book != null) {
                            orderBookListener.accept(book);
                        }
                        if (exception != null) {
                            onTermination(exception);
                        }
                    }));
            CompletableFuture[] futureArray = futures.toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(futureArray).thenRun(
                    () -> scheduler.schedule(fetchMarketDataTask, pollIntervalSeconds, TimeUnit.SECONDS));
        };
        scheduler.schedule(fetchMarketDataTask, pollIntervalSeconds, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void stop() {
        scheduler.shutdown();
        // Don't forget to call onTermination when override!
    }

    protected abstract OrderBook getOrderBook(Instrument instrument);

    @Override
    public synchronized void subscribe(Instrument... instruments) {
        Collections.addAll(subscriptions, instruments);
    }

    protected CompletableFuture<OrderBook> getOrderBookAsync(Instrument instrument) {
        return CompletableFuture.supplyAsync(() -> getOrderBook(instrument));
    }
}
