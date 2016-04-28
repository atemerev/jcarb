package com.miriamlaurel.jcarb;

import com.miriamlaurel.jcarb.client.CoinbaseWsApi;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.Order;
import com.miriamlaurel.jcarb.model.order.OrderBook;
import com.miriamlaurel.jcarb.model.order.Side;
import com.miriamlaurel.jcarb.model.order.op.AddOrder;
import com.miriamlaurel.jcarb.model.order.op.OrderOp;
import com.miriamlaurel.jcarb.model.portfolio.Position;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.function.Consumer;

public class Main {

    private static final Instrument BTCUSD = Instrument.fromCode("BTC/USD");


    public static void main(String[] args) throws IOException {

        Broadcaster broadcaster = new Broadcaster(new InetSocketAddress(9999));
        broadcaster.start();

/*
        Consumer<OrderBook> listener = book -> {
            broadcaster.broadcast(globalBook);
            if (pLong == null && globalBook.getSpread().compareTo(new BigDecimal(-0.5)) < 0) {
                Order bestBid = globalBook.getBestAggregated(Side.BID);
                Order bestAsk = globalBook.getBestAggregated(Side.ASK);
                BigDecimal size = bestBid.getAmount().min(bestAsk.getAmount());
                pLong = new Position(bestAsk.getKey().getParty(), BTCUSD, size, bestAsk.getPrice().multiply(size).negate(), Instant.now());
                pShort = new Position(bestBid.getKey().getParty(), BTCUSD, size.negate(), bestBid.getPrice().multiply(size), Instant.now());
                System.out.println("!!!! Starting arbitrage !!!!");
            } else if (pLong != null) {
                BigDecimal longPl = pLong.getProfitLoss(globalBook.getBestPrice(Side.BID, pLong.getParty()));
                BigDecimal shortPl = pShort.getProfitLoss(globalBook.getBestPrice(Side.ASK, pShort.getParty()));
                System.out.println("Profit/loss: " + longPl.add(shortPl));
            }
        };
*/

        CoinbaseWsApi coinbaseApi = new CoinbaseWsApi(BTCUSD, broadcaster);
    }
}
