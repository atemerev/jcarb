package com.miriamlaurel.jcarb.client;

import com.mashape.unirest.http.Unirest;
import com.miriamlaurel.jcarb.common.StoppedException;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.*;
import com.miriamlaurel.jcarb.model.trading.Exec;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.function.Consumer;

public class GatecoinApi extends HttpJsonTradingApi {

    private static final String ENDPOINT = "https://www.gatecoin.com/api/Public/MarketDepth/";

    public GatecoinApi(Consumer<OrderBook> orderBookListener, int pollIntervalSeconds) {
        super(orderBookListener, pollIntervalSeconds);
    }

    @Override
    public String getName() {
        return "Gatecoin";
    }

    @Override
    public void stop() {
        super.stop();
        try {
            Unirest.shutdown();
            onTermination(new StoppedException(getName() + " is stopped"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected OrderBook parseOrderBook(JSONObject json, Instrument instrument) {
        OrderBook book = new OrderBook(instrument);
        JSONArray bidArray = json.getJSONArray("bids");
        JSONArray askArray = json.getJSONArray("asks");
        for (int i = 0; i < bidArray.length(); i++) {
            JSONObject orderJson = bidArray.getJSONObject(i);
            Order order = parseOrder(i, Side.BID, instrument, orderJson);
            book.addOrder(order);
        }
        for (int i = 0; i < askArray.length(); i++) {
            JSONObject orderJson = askArray.getJSONObject(i);
            Order order = parseOrder(i, Side.ASK, instrument, orderJson);
            book.addOrder(order);
        }
        return book;
    }

    @Override
    public void trade(Consumer<Exec> executionResponseListener) {
        throw new NoSuchMethodError("Not implemented");
    }

    @NotNull
    private Order parseOrder(int count, Side side, Instrument instrument, JSONObject orderJson) {
        BigDecimal price = orderJson.getBigDecimal("price");
        BigDecimal amount = orderJson.getBigDecimal("volume");
        String orderId = Integer.toString(count);
        OrderKey key = new OrderKey(orderId, Party.GATECOIN, instrument, side);
        return new Order(key, amount, price, Instant.now());
    }

    @Override
    protected URI instrumentToBookUri(Instrument instrument) {
        try {
            String ticker = instrument.getPrimary().toString() + instrument.getSecondary().toString();
            return new URI(ENDPOINT + ticker);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
