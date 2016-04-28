package com.miriamlaurel.jcarb.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.miriamlaurel.jcarb.common.Mortal;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.*;
import com.miriamlaurel.jcarb.model.order.op.*;
import com.miriamlaurel.jcarb.model.trading.Exec;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.miriamlaurel.jcarb.client.CoinbaseApi.ENDPOINT;

public class CoinbaseWsApi implements TradingApi, Mortal {

    private static final URI WS_ENDPOINT;
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    protected Runnable reconnectTask;

    static {
        try {
            WS_ENDPOINT = new URI("wss://ws-feed.exchange.coinbase.com");
        } catch (URISyntaxException e) {
            // Should not happen :)
            throw new RuntimeException(e);
        }
    }

    private WebSocketClient wsClient;
    private int initialSeq = -1;
    private final Consumer<OrderOp> opListener;
    private final Instrument instrument;

    public CoinbaseWsApi(Instrument instrument, Consumer<OrderOp> opListener) {
        this.instrument = instrument;
        this.opListener = opListener;
        this.wsClient = new WebSocketClient(WS_ENDPOINT, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                JSONObject subscribeRequest = mkSubscribeRequest(CoinbaseWsApi.this.instrument);
                send(subscribeRequest.toString());
            }

            @Override
            public void onMessage(String msg) {
                JSONObject json = new JSONObject(msg);
                String msgType = json.getString("type");
                int seqNum = json.getInt("sequence");
                String orderId = json.getString("order_id");
                Side side = "buy".equalsIgnoreCase(json.getString("side")) ? Side.BID : Side.ASK;
                OrderKey key = new OrderKey(orderId, Party.COINBASE, CoinbaseWsApi.this.instrument, side);
                if (initialSeq == -1 || seqNum > initialSeq) {
                    if ("open".equals(msgType)) {
                        BigDecimal amount = new BigDecimal(json.getString("remaining_size"));
                        BigDecimal price = new BigDecimal(json.getString("price"));
                        Order order = new Order(key, amount, price);
                        CoinbaseWsApi.this.opListener.accept(new AddOrder(seqNum, order));
                    } else if ("done".equals(msgType)) {
                        CoinbaseWsApi.this.opListener.accept(new DeleteOrder(seqNum, key));
                    } else if ("change".equals(msgType)) {
                        if (json.has("new_size")) {
                            BigDecimal amount = new BigDecimal(json.getString("new_size"));
                            BigDecimal price = new BigDecimal(json.getString("price"));
                            Order order = new Order(key, amount, price);
                            CoinbaseWsApi.this.opListener.accept(new ChangeOrder(seqNum, order));
                        }
                    }
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                onTermination(new IOException(getName() + " WS disconnected: " + reason));
            }

            @Override
            public void onError(Exception e) {
                onTermination(e);
            }
        };
        this.reconnectTask = () -> {
            try {
                OrderBook book = getOrderBook(instrument);
                this.initialSeq = book.getSeq();
                opListener.accept(new ReplaceParty(initialSeq, Party.COINBASE, book));
                wsClient.setSocket(SSLSocketFactory.getDefault().createSocket("ws-feed.exchange.coinbase.com", 443));
                boolean connected = wsClient.connectBlocking();
                if (!connected) {
                    onTermination(new IOException("Can't WS connect to: " + WS_ENDPOINT));
                }
            } catch (Throwable e) {
                onTermination(e);
            }
        };
        scheduler.schedule(reconnectTask, 2, TimeUnit.SECONDS);
    }


    @Override
    public String getName() {
        return "Coinbase";
    }

    @Override
    public void subscribe(Instrument... instruments) {

    }

    @Override
    public void trade(Consumer<Exec> executionResponseListener) {

    }

    private OrderBook getOrderBook(Instrument instrument) {
        try {
            String ticker = instrument.getPrimary() + "-" + instrument.getSecondary();
            String path = String.format("/products/%s/book?level=%d", ticker, 3);
            URI uri = new URI(ENDPOINT + path);
            HttpResponse<JsonNode> response = Unirest.get(uri.toString()).asJson();
            JSONObject json = response.getBody().getObject();
            return parseBook(json, instrument);
        } catch (UnirestException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static OrderBook parseBook(JSONObject json, Instrument instrument) {
        OrderBook book = new OrderBook(instrument);
        JSONArray bidArray = json.getJSONArray("bids");
        JSONArray askArray = json.getJSONArray("asks");
        int seqNum = json.getInt("sequence");
        for (int i = 0; i < bidArray.length(); i++) {
            JSONArray orderJson = bidArray.getJSONArray(i);
            Order order = parseOrder(Side.BID, instrument, orderJson);
            book.addOrder(order);
        }
        for (int i = 0; i < askArray.length(); i++) {
            JSONArray orderJson = askArray.getJSONArray(i);
            Order order = parseOrder(Side.ASK, instrument, orderJson);
            book.addOrder(order);
        }
        book.setSeq(seqNum);
        return book;
    }


    static Order parseOrder(Side side, Instrument instrument, JSONArray orderJson) {
        BigDecimal price = new BigDecimal(orderJson.getString(0));
        BigDecimal amount = new BigDecimal(orderJson.getString(1));
        String orderId = orderJson.getString(2);
        OrderKey key = new OrderKey(orderId, Party.COINBASE, instrument, side);
        return new Order(key, amount, price, Instant.now());
    }

    @Override
    public void stop() {
        onTermination(null);
    }

    @Override
    public void onTermination(Throwable e) {
        this.initialSeq = -1;
        try {
            wsClient.closeBlocking();
        } catch (InterruptedException e1) {
            // Ignore
        }
        if (e != null) {
            e.printStackTrace();
            scheduler.schedule(reconnectTask, 2, TimeUnit.SECONDS);
        } else {
            System.out.println(getName() + " WS service stopped on demand");
        }

    }

    private JSONObject mkSubscribeRequest(Instrument instrument) {
        String ticker = instrument.getPrimary() + "-" + instrument.getSecondary();
        JSONObject request = new JSONObject();
        request.put("type", "subscribe");
        request.put("product_id", ticker);
        return request;
    }
}
