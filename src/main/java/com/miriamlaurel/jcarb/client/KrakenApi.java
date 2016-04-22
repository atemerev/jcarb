package com.miriamlaurel.jcarb.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.miriamlaurel.jcarb.common.StoppedException;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.*;
import com.miriamlaurel.jcarb.model.trading.Exec;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KrakenApi extends PollingTradingApi {

    private static final int TASK_THREAD_POOL_SIZE = 16;

    private static final String ENDPOINT = "https://api.kraken.com/0/public/Depth";
    private static final Map<String, String> assetMap = new HashMap<>();

    static {
        assetMap.put("BTC", "XXBT");
        assetMap.put("ETH", "XETH");
        assetMap.put("USD", "ZUSD");
        assetMap.put("EUR", "ZEUR");
        assetMap.put("LTC", "XLTC");
        assetMap.put("NMC", "XNMC");
    }

    public KrakenApi(Consumer<OrderBook> orderBookListener, int pollIntervalSeconds) {
        super(orderBookListener, pollIntervalSeconds);
    }

    @Override
    public String getName() {
        return "Kraken";
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
    public void trade(Consumer<Exec> executionResponseListener) {
        throw new NoSuchMethodError("Not implemented");
    }

    @Override
    protected OrderBook getOrderBook(Instrument instrument) {
        try {
            OrderBook book = new OrderBook(instrument);
            String ticker = instrumentToTicker(instrument);
            String path = String.format("?pair=%s", ticker);
            HttpResponse<JsonNode> response = Unirest.get(ENDPOINT + path).asJson();
            JSONObject json = response.getBody().getObject().getJSONObject("result").getJSONObject(ticker);
            JSONArray bidArray = json.getJSONArray("bids");
            JSONArray askArray = json.getJSONArray("asks");
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
        Instant timestamp = Instant.ofEpochMilli(orderJson.getLong(2) * 1000);
        String orderId = Integer.toString(count);
        OrderKey key = new OrderKey(orderId, Party.KRAKEN, instrument, side);
        return new Order(key, amount, price, timestamp);
    }

    private String instrumentToTicker(Instrument instrument) {
        String primary = assetMap.get(instrument.getPrimary().toString());
        String secondary = assetMap.get(instrument.getSecondary().toString());
        if (primary == null || secondary == null) {
            throw new IllegalArgumentException(String.format("Can't map instrument %s to Kraken pairs", instrument));
        }
        return primary + secondary;
    }
}
