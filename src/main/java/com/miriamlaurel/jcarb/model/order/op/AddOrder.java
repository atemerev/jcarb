package com.miriamlaurel.jcarb.model.order.op;

import com.miriamlaurel.jcarb.model.order.Order;
import com.miriamlaurel.jcarb.model.order.Side;
import org.json.JSONObject;

public class AddOrder implements OrderOp {

    private final int seqNum;
    private final Order order;

    public AddOrder(int seqNum, Order order) {
        this.seqNum = seqNum;
        this.order = order;
    }

    @Override
    public int getSeqNum() {
        return seqNum;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", "add");
        json.put("venue", order.getKey().getParty().toString());
        json.put("orderId", order.getKey().getOrderId());
        json.put("instrument", order.getKey().getInstrument().toString());
        json.put("side", order.getKey().getSide() == Side.BID ? "BID" : "ASK");
        json.put("price", order.getPrice().toPlainString());
        json.put("amount", order.getAmount().toPlainString());
        return json;
    }
}
