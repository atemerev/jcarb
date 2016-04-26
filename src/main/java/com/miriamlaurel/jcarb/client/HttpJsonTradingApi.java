package com.miriamlaurel.jcarb.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.miriamlaurel.jcarb.common.StoppedException;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.OrderBook;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

public abstract class HttpJsonTradingApi extends PollingTradingApi {

    public HttpJsonTradingApi(Consumer<OrderBook> orderBookListener, int pollIntervalSeconds) {
        super(orderBookListener, pollIntervalSeconds);
    }

    @Override
    public synchronized void stop() {
        try {
            Unirest.shutdown();
            onTermination(new StoppedException(getName() + " is stopped"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected OrderBook getOrderBook(Instrument instrument) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(instrumentToBookUri(instrument).toString()).asJson();
            JSONObject json = response.getBody().getObject();
            return parseOrderBook(json, instrument);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

    }

    protected abstract URI instrumentToBookUri(Instrument instrument);
    protected abstract OrderBook parseOrderBook(JSONObject json, Instrument instrument);
}
