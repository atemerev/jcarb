package com.miriamlaurel.jcarb;

import com.miriamlaurel.jcarb.model.order.OrderBook;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;

public class Broadcaster extends WebSocketServer {

    public Broadcaster(InetSocketAddress address) {
        super(address);
        System.out.println("Websocket server started: " + address);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("Connection open: " + clientHandshake);
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

    public void broadcast(OrderBook book) {
        Collection<WebSocket> con = connections();
        synchronized (this) {
            for (WebSocket c : con) {
                c.send(book.toJson().toString());
            }
        }
    }
}
