package com.miriamlaurel.jcarb.model.order.op;

import com.miriamlaurel.jcarb.model.order.OrderBook;
import com.miriamlaurel.jcarb.model.order.Party;
import org.json.JSONObject;

public class ReplaceParty implements OrderOp {

    private final int seqNum;
    private final Party party;
    private final OrderBook partyBook;

    public ReplaceParty(int seqNum, Party party, OrderBook partyBook) {
        this.seqNum = seqNum;
        this.party = party;
        this.partyBook = partyBook;
    }

    @Override
    public int getSeqNum() {
        return seqNum;
    }

    public Party getParty() {
        return party;
    }

    public OrderBook getPartyBook() {
        return partyBook;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = partyBook.toJson();
        json.put("venue", party.toString());
        return json;
    }
}
