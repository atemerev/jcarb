package com.miriamlaurel.jcarb;

import com.miriamlaurel.jcarb.common.JsonSerializable;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.OrderBook;
import com.miriamlaurel.jcarb.model.order.Side;
import com.miriamlaurel.jcarb.model.order.op.OrderOp;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.function.Consumer;

public class Broadcaster extends WebSocketServer implements Consumer<OrderOp> {

    private static final Instrument BTCUSD = Instrument.fromCode("BTC/USD");

    private OrderBook book = new OrderBook(BTCUSD);

    public Broadcaster(InetSocketAddress address) {
        super(address);
        System.out.println("Websocket server started: " + address);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("Connection open: " + clientHandshake);
        webSocket.send(book.toJson().toString());
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void accept(OrderOp orderOp) {
        book.accept(orderOp);
        System.out.println("BTC/USD " + book.getBestAggregated(Side.BID).getPrice() + "/" + book.getBestAggregated(Side.ASK).getPrice());
        broadcast(orderOp);
    }

    void broadcast(JsonSerializable object) {
        Collection<WebSocket> con = connections();
        synchronized (this) {
            for (WebSocket c : con) {
                c.send(object.toJson().toString());
            }
        }
    }
}
