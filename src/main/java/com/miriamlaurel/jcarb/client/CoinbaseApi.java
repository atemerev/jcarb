package com.miriamlaurel.jcarb.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.miriamlaurel.jcarb.common.Lifecycle;
import com.miriamlaurel.jcarb.model.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

public class CoinbaseApi implements Lifecycle, SyncApi {

    private static final String ENDPOINT = "https://api.exchange.coinbase.com";
    private static final int BOOK_LEVEL = 2;

    public CoinbaseApi() {
    }

    @Override
    public void start() {
        // Don't need to do anything
    }

    @Override
    public void stop() {
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderBook getOrderBook(Instrument instrument) {
        try {
            OrderBook book = new OrderBook(instrument);
            String ticker = instrumentToTicker(instrument);
            String path = String.format("/products/%s/book?level=%d", ticker, BOOK_LEVEL);
            HttpResponse<JsonNode> response = Unirest.get(ENDPOINT + path).asJson();
            JsonNode json = response.getBody();
            JSONArray bidArray = json.getObject().getJSONArray("bids");
            JSONArray askArray = json.getObject().getJSONArray("asks");
            for (int i = 0; i < bidArray.length(); i++) {
                JSONArray orderJson = bidArray.getJSONArray(i);
                Order order = parseOrder(i, Side.BID, instrument, orderJson);
                book.addOrder(order);
            }
            for (int i = 0; i < askArray.length(); i++) {
                JSONArray orderJson = askArray.getJSONArray(i);
                Order order = parseOrder(i, Side.ASK, instrument, orderJson);
                book.addOrder(order);
            }
            return book;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Order parseOrder(int count, Side side, Instrument instrument, JSONArray orderJson) {
        BigDecimal price = new BigDecimal(orderJson.getString(0));
        BigDecimal amount = new BigDecimal(orderJson.getString(1));
        String orderId = Integer.toString(count);
        OrderKey key = new OrderKey(orderId, Party.COINBASE, instrument, side);
        return new Order(key, amount, price, Instant.now());
    }

    private String instrumentToTicker(Instrument instrument) {
        return instrument.getPrimary() + "-" + instrument.getSecondary();
    }
}
