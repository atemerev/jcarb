package com.miriamlaurel.jcarb;

import com.miriamlaurel.jcarb.client.CoinbaseApi;
import com.miriamlaurel.jcarb.client.GatecoinApi;
import com.miriamlaurel.jcarb.client.KrakenApi;
import com.miriamlaurel.jcarb.model.analysis.OrderBookMontage;
import com.miriamlaurel.jcarb.model.asset.Instrument;
import com.miriamlaurel.jcarb.model.order.Order;
import com.miriamlaurel.jcarb.model.order.OrderBook;
import com.miriamlaurel.jcarb.model.order.Side;
import com.miriamlaurel.jcarb.model.portfolio.Position;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class Main {

    private static final Instrument BTCUSD = Instrument.fromCode("BTC/USD");
    private static final OrderBookMontage montage = new OrderBookMontage(BTCUSD);
    private static Position pLong = null;
    private static Position pShort = null;

    public static void main(String[] args) throws IOException {

        Broadcaster caster = new Broadcaster(new InetSocketAddress(9999));
        caster.start();

        Consumer<OrderBook> listener = book -> {
            montage.accept(book);
            OrderBook globalBook = montage.getGlobalBook();
            caster.broadcast(globalBook);
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
        KrakenApi krakenApi = new KrakenApi(listener, 2);
        CoinbaseApi coinbaseApi = new CoinbaseApi(listener, 2);
        GatecoinApi gatecoinApi = new GatecoinApi(listener, 2);
        krakenApi.subscribe(BTCUSD);
        coinbaseApi.subscribe(BTCUSD);
        gatecoinApi.subscribe(BTCUSD);
    }
}
