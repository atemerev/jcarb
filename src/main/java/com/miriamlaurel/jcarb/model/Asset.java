package com.miriamlaurel.jcarb.model;

public interface Asset {

    String BTC = "BTC";
    String ETH = "ETH";
    String LTC = "LTC";

    static Asset fromCode(String code) {
        if (BTC.equals(code) || ETH.equals(code) || BTC.equals(code)) {
            return new CryptoCurrency(code);
        } else {
            return new FiatCurrency(code);
        }
    }
}
