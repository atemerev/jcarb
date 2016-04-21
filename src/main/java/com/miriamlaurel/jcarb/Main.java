package com.miriamlaurel.jcarb;

import com.miriamlaurel.jcarb.fetcher.CoinbaseApi;
import com.miriamlaurel.jcarb.model.Instrument;
import com.miriamlaurel.jcarb.model.OrderBook;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        CoinbaseApi api = new CoinbaseApi();
        OrderBook book = api.getOrderBook(Instrument.fromCode("BTC/USD"));
        System.out.println(book.toString());
    }
}
