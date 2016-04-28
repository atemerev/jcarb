package com.miriamlaurel.jcarb.model.order.op;

import com.miriamlaurel.jcarb.model.order.OrderKey;
import com.miriamlaurel.jcarb.model.order.Side;
import org.json.JSONObject;

public class DeleteOrder implements OrderOp {

    private final int seqNum;
    private final OrderKey key;

    public DeleteOrder(int seqNum, OrderKey key) {
        this.seqNum = seqNum;
        this.key = key;
    }

    @Override
    public int getSeqNum() {
        return seqNum;
    }

    public OrderKey getKey() {
        return key;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", "delete");
        json.put("venue", key.getParty().toString());
        json.put("side", key.getSide() == Side.BID ? "BID" : "ASK");
        json.put("orderId", key.getOrderId());
        return json;
    }
}
