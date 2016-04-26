'use strict';

// Instrument

var Instrument = function(primary, secondary) {
    this.primary = primary;
    this.secondary = secondary;
};

Instrument.fromTicker = function(ticker) {
    var tokens = ticker.split("/");
    return new Instrument(tokens[0], tokens[1]);
};

Instrument.prototype.toString = Instrument.prototype.ticker;

// Order

var Order = function(venue, orderId, instrument, side, price, amount) {
    this.venue = venue;
    this.orderId = orderId;
    this.instrument = instrument;
    this.side = side;
    this.price = price;
    this.amount = amount;
};

// Order book

var OrderBook = function(ticker) {
    this.ticker = ticker;
    this.instrument = Instrument.fromTicker(ticker);
    this.bids = [];
    this.asks = [];
};

OrderBook.prototype.add = function(order) {
    var line = "BID" == order.side ? this.bids : this.asks;
    var insertPoint = findPrice(order.side, line, order.price);
    line.splice(Math.abs(insertPoint), 0, order);
};

OrderBook.prototype.delete = function(venue, side, orderId) {
    var line = "BID" == side ? this.bids : this.asks;
    var index = line.findIndex(function(o) {
        return side == o.side && venue == o.venue && orderId == o.orderId;
    });
    if (index >= 0) {
        line.splice(index, 1);
    }
};

// Utility functions

function findPrice(side, line, price) {
    var minIndex = 0;
    var maxIndex = line.length - 1;
    var currentIndex;
    var currentOrder;
    var resultIndex;

    while (minIndex <= maxIndex) {
        resultIndex = currentIndex = (minIndex + maxIndex) / 2 | 0;
        currentOrder = line[currentIndex];

        if (currentOrder.price < price) {
            if (side == "ASK") {
                minIndex = currentIndex + 1;
            } else {
                maxIndex = currentIndex - 1;
            }
        }
        else if (currentOrder.price > price) {
            if (side == "ASK") {
                maxIndex = currentIndex - 1;
            } else {
                minIndex = currentIndex + 1;
            }
        }
        else {
            return currentIndex;
        }
    }
    return ~maxIndex;
}