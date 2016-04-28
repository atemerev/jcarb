var venues = ["Kraken", "Coinbase", "Gatecoin"];

function getScale(book, cutoff) {
    var bestBid = parseFloat(book["bids"][0][1]);
    var bestAsk = parseFloat(book["asks"][0][1]);
    var midPrice = (bestBid + bestAsk) / 2;
    return d3.scale.linear().domain([midPrice - cutoff, midPrice + cutoff]).range([-320, 320]);
}

var Dashboard = React.createClass({
    displayName: 'CommentBox',

    getInitialState: function () {
/*
        return {"book": {
            "asks": [["Kraken", "448.92500", "2.064"], ["Kraken", "448.92600", "100.000"], ["Kraken", "448.93000", "100.100"], ["Kraken", "449.00000", "1.000"], ["Kraken", "449.76000", "7.766"], ["Kraken", "449.77100", "100.000"], ["Kraken", "449.77400", "0.063"], ["Kraken", "449.77900", "0.052"], ["Kraken", "449.78000", "100.000"], ["Kraken", "450.00000", "28.724"], ["Kraken", "450.10800", "28.835"], ["Kraken", "450.11200", "2.370"], ["Kraken", "450.17600", "34.276"], ["Kraken", "450.70000", "11.086"], ["Kraken", "450.71100", "5.000"], ["Kraken", "451.00000", "2.500"], ["Coinbase", "451.08", "4.0176238"], ["Coinbase", "451.09", "0.650693"], ["Coinbase", "451.1", "0.30341776"], ["Kraken", "451.10700", "30.947"], ["Coinbase", "451.11", "0.18928669"], ["Kraken", "451.11000", "46.589"], ["Coinbase", "451.12", "0.10557"], ["Coinbase", "451.13", "0.025"], ["Coinbase", "451.14", "0.5673"], ["Coinbase", "451.15", "0.27689309"], ["Coinbase", "451.16", "0.11428582"], ["Coinbase", "451.17", "0.05"], ["Coinbase", "451.18", "0.025"], ["Coinbase", "451.19", "0.12856597"], ["Coinbase", "451.2", "0.28132543"], ["Coinbase", "451.21", "0.14289397"], ["Coinbase", "451.22", "0.43333279"], ["Coinbase", "451.23", "0.06427928"], ["Coinbase", "451.24", "0.202174"], ["Coinbase", "451.25", "0.67756787"], ["Coinbase", "451.26", "1.15"], ["Coinbase", "451.27", "1.125"], ["Coinbase", "451.28", "0.995"], ["Coinbase", "451.29", "7.63246"], ["Coinbase", "451.3", "1.18476142"], ["Coinbase", "451.31", "0.235"], ["Coinbase", "451.32", "0.90427142"], ["Coinbase", "451.33", "0.13417535"], ["Coinbase", "451.34", "0.10934301"], ["Coinbase", "451.35", "0.1143049"], ["Coinbase", "451.36", "0.0569"], ["Coinbase", "451.37", "0.11927497"], ["Coinbase", "451.38", "0.13933707"], ["Coinbase", "451.39", "0.28174"], ["Coinbase", "451.4", "0.33853"], ["Coinbase", "451.41", "0.11001354"], ["Coinbase", "451.42", "0.01"], ["Coinbase", "451.43", "0.01000199"], ["Coinbase", "451.44", "0.01000022"], ["Coinbase", "451.45", "0.34"], ["Coinbase", "451.46", "0.01000376"], ["Coinbase", "451.47", "2.5"], ["Kraken", "451.47200", "8.200"], ["Coinbase", "451.49", "0.21000466"], ["Coinbase", "451.5", "0.57480732"], ["Coinbase", "451.53", "0.5"], ["Coinbase", "451.55", "0.01"], ["Coinbase", "451.56", "0.95"], ["Coinbase", "451.57", "0.01000377"], ["Coinbase", "451.59", "1.56"], ["Coinbase", "451.6", "0.03346518"], ["Coinbase", "451.62", "1.48"], ["Coinbase", "451.63", "4.949"], ["Kraken", "452.00000", "1.500"], ["Kraken", "452.10000", "0.500"], ["Kraken", "452.14300", "33.413"], ["Kraken", "452.53300", "5.000"], ["Kraken", "453.30800", "31.346"], ["Kraken", "454.28100", "0.025"], ["Kraken", "454.32500", "35.062"], ["Kraken", "454.85800", "16.780"], ["Kraken", "455.00000", "1.102"], ["Kraken", "455.10000", "0.284"], ["Kraken", "455.35800", "32.266"], ["Kraken", "456.35300", "30.440"], ["Kraken", "456.78000", "3.068"], ["Kraken", "457.35500", "30.874"], ["Kraken", "457.88700", "4.051"], ["Kraken", "458.46800", "34.480"], ["Kraken", "459.44400", "33.400"], ["Kraken", "459.91000", "10.000"], ["Kraken", "460.00000", "1.619"], ["Kraken", "461.00000", "1.000"], ["Kraken", "464.00000", "0.244"], ["Kraken", "464.49650", "0.025"], ["Kraken", "465.00000", "1.500"], ["Kraken", "465.15085", "0.025"], ["Kraken", "467.00000", "0.250"], ["Kraken", "468.19300", "0.031"], ["Kraken", "469.91000", "10.000"], ["Kraken", "470.00000", "1.000"], ["Kraken", "470.86100", "0.013"], ["Kraken", "471.47700", "0.025"], ["Kraken", "472.65200", "0.031"], ["Kraken", "472.91450", "0.025"], ["Kraken", "474.91297", "0.100"], ["Kraken", "475.00000", "0.100"], ["Kraken", "475.47500", "0.999"], ["Kraken", "475.68000", "0.171"], ["Kraken", "477.11100", "0.031"], ["Kraken", "478.52500", "0.120"], ["Kraken", "479.77995", "0.010"], ["Kraken", "479.91000", "10.000"], ["Kraken", "480.00000", "0.100"], ["Kraken", "481.57000", "0.031"], ["Kraken", "483.78200", "0.058"], ["Kraken", "484.84745", "0.010"], ["Kraken", "486.02900", "0.031"], ["Kraken", "489.29789", "0.100"], ["Kraken", "489.51406", "0.010"], ["Kraken", "489.91000", "10.000"], ["Kraken", "494.51406", "0.050"], ["Kraken", "495.00000", "3.945"], ["Kraken", "495.40390", "0.022"], ["Kraken", "497.97975", "0.100"], ["Kraken", "499.00000", "1.400"], ["Kraken", "499.91000", "10.000"], ["Kraken", "500.00000", "13.210"], ["Kraken", "507.91297", "0.089"], ["Kraken", "509.91000", "10.000"], ["Kraken", "510.00000", "10.000"], ["Kraken", "511.00000", "0.784"], ["Kraken", "519.91000", "10.000"], ["Kraken", "520.00000", "10.000"], ["Kraken", "529.91000", "10.000"], ["Kraken", "530.00000", "10.000"], ["Kraken", "539.91000", "10.000"], ["Kraken", "540.00000", "10.000"], ["Kraken", "548.87600", "17.000"], ["Kraken", "549.91000", "10.000"], ["Kraken", "550.00000", "7.000"], ["Kraken", "550.20000", "0.200"], ["Kraken", "555.00000", "1.000"], ["Kraken", "559.91000", "10.000"], ["Kraken", "569.91000", "10.000"], ["Kraken", "579.91000", "10.000"], ["Kraken", "589.91000", "10.000"], ["Kraken", "593.00000", "1.000"], ["Kraken", "594.94400", "0.464"], ["Kraken", "598.00000", "9.990"], ["Kraken", "599.91000", "10.000"], ["Kraken", "620.00000", "1.000"], ["Kraken", "635.00000", "15.800"], ["Kraken", "647.76900", "25.000"]],
            "bids": [["Coinbase", "451", "3.26925002"], ["Coinbase", "450.99", "0.015"], ["Coinbase", "450.98", "0.165"], ["Coinbase", "450.97", "6.66425"], ["Coinbase", "450.96", "0.815"], ["Coinbase", "450.95", "0.015"], ["Coinbase", "450.94", "0.035"], ["Coinbase", "450.93", "0.015"], ["Coinbase", "450.92", "0.135"], ["Coinbase", "450.91", "0.025"], ["Coinbase", "450.9", "0.08703108"], ["Coinbase", "450.89", "0.025"], ["Coinbase", "450.88", "0.015"], ["Coinbase", "450.87", "0.025"], ["Coinbase", "450.86", "0.027"], ["Coinbase", "450.85", "0.005"], ["Coinbase", "450.84", "0.015"], ["Coinbase", "450.83", "0.015"], ["Coinbase", "450.82", "0.015"], ["Coinbase", "450.81", "0.025"], ["Coinbase", "450.8", "0.04703841"], ["Coinbase", "450.79", "0.015"], ["Coinbase", "450.78", "0.015"], ["Coinbase", "450.77", "0.015"], ["Coinbase", "450.76", "0.015"], ["Coinbase", "450.75", "0.0265"], ["Coinbase", "450.74", "0.015"], ["Coinbase", "450.73", "0.015"], ["Coinbase", "450.72", "0.025"], ["Coinbase", "450.71", "0.025"], ["Coinbase", "450.7", "0.0350202"], ["Coinbase", "450.69", "0.015"], ["Coinbase", "450.68", "2.335"], ["Coinbase", "450.67", "2.975"], ["Coinbase", "450.66", "3.075"], ["Coinbase", "450.65", "3.005"], ["Coinbase", "450.64", "3.005"], ["Coinbase", "450.63", "3.015"], ["Coinbase", "450.62", "2.957"], ["Coinbase", "450.61", "2.985"], ["Coinbase", "450.6", "7.13501798"], ["Coinbase", "450.59", "0.015"], ["Coinbase", "450.58", "0.015"], ["Coinbase", "450.57", "0.015"], ["Coinbase", "450.56", "0.025"], ["Coinbase", "450.55", "0.05547844"], ["Coinbase", "450.54", "0.033"], ["Coinbase", "450.53", "0.011"], ["Coinbase", "450.52", "0.01"], ["Coinbase", "450.51", "1.41"], ["Kraken", "447.55800", "13.380"], ["Kraken", "447.54700", "0.100"], ["Kraken", "447.50000", "4.987"], ["Kraken", "447.00000", "8.000"], ["Kraken", "446.34000", "76.386"], ["Kraken", "446.23600", "82.902"], ["Kraken", "445.78000", "55.280"], ["Kraken", "445.61000", "7.743"], ["Kraken", "445.19100", "126.710"], ["Kraken", "444.25000", "11.085"], ["Kraken", "444.20000", "12.243"], ["Kraken", "444.18400", "35.511"], ["Kraken", "444.00000", "85.413"], ["Kraken", "443.66100", "5.000"], ["Kraken", "443.21600", "2.496"], ["Kraken", "442.55000", "1.000"], ["Kraken", "442.23400", "34.578"], ["Kraken", "441.23700", "33.906"], ["Kraken", "441.00000", "0.050"], ["Kraken", "440.95400", "0.102"], ["Kraken", "440.40000", "1.000"], ["Kraken", "440.30000", "126.723"], ["Kraken", "440.26300", "27.624"], ["Kraken", "440.19000", "0.035"], ["Kraken", "440.10000", "0.500"], ["Kraken", "440.00000", "1.020"], ["Kraken", "439.75000", "0.500"], ["Kraken", "439.26500", "32.207"], ["Kraken", "439.00000", "1.000"], ["Kraken", "438.40000", "1.000"], ["Kraken", "437.70700", "0.058"], ["Kraken", "436.00000", "0.985"], ["Kraken", "435.00000", "4.770"], ["Kraken", "432.95100", "0.121"], ["Kraken", "432.50000", "1.000"], ["Kraken", "432.41000", "3.000"], ["Kraken", "431.44500", "0.027"], ["Kraken", "431.00000", "2.500"], ["Kraken", "430.37700", "0.332"], ["Kraken", "430.00000", "2.020"], ["Kraken", "428.00000", "0.196"], ["Kraken", "427.55900", "0.364"], ["Kraken", "427.04000", "0.500"], ["Kraken", "427.02100", "0.060"], ["Kraken", "427.00000", "2.650"], ["Kraken", "426.25100", "0.027"], ["Kraken", "426.01700", "0.013"], ["Kraken", "425.01000", "1.000"], ["Kraken", "425.00000", "10.036"], ["Kraken", "424.95000", "1.000"], ["Kraken", "424.50000", "7.626"], ["Kraken", "423.92000", "1.000"], ["Kraken", "423.00000", "3.060"], ["Kraken", "422.88700", "1.000"], ["Kraken", "422.00000", "1.000"], ["Kraken", "421.70000", "0.522"], ["Kraken", "421.10000", "0.048"], ["Kraken", "421.00000", "1.260"], ["Kraken", "420.57100", "0.049"], ["Kraken", "420.10000", "0.250"], ["Kraken", "420.00000", "8.102"], ["Kraken", "419.95000", "1.143"], ["Kraken", "419.11000", "1.000"], ["Kraken", "418.15000", "1.000"], ["Kraken", "418.00000", "2.813"], ["Kraken", "417.28800", "0.060"], ["Kraken", "416.00000", "0.057"], ["Kraken", "415.00000", "80.221"], ["Kraken", "414.04000", "0.250"], ["Kraken", "414.00000", "0.016"], ["Kraken", "413.24000", "0.049"], ["Kraken", "412.00000", "0.027"], ["Kraken", "411.49900", "0.100"], ["Kraken", "411.00000", "1.742"], ["Kraken", "410.50000", "0.410"], ["Kraken", "410.10000", "0.103"], ["Kraken", "410.00000", "2.896"], ["Kraken", "409.00000", "0.030"], ["Kraken", "408.18000", "41.745"], ["Kraken", "407.99000", "0.025"], ["Kraken", "407.11900", "0.025"], ["Kraken", "407.00000", "0.500"], ["Kraken", "406.00000", "0.076"], ["Kraken", "405.76500", "5.500"], ["Kraken", "405.25900", "0.653"], ["Kraken", "405.00000", "8.557"], ["Kraken", "403.00000", "0.500"], ["Kraken", "402.92000", "0.395"], ["Kraken", "400.49900", "1.655"], ["Kraken", "400.36000", "5.200"], ["Kraken", "400.25000", "0.061"], ["Kraken", "400.00000", "11.915"], ["Kraken", "397.00000", "0.500"], ["Kraken", "396.16000", "4.825"], ["Kraken", "396.00000", "0.500"], ["Kraken", "395.00000", "0.393"], ["Kraken", "390.00000", "6.102"], ["Kraken", "389.90000", "0.150"], ["Kraken", "388.00000", "0.110"], ["Kraken", "387.99000", "0.015"]],
            "type": "book"
        }};
*/
        return {"book": new OrderBook("BTC/USD")};
    },

    componentDidMount: function() {
        var ws = new WebSocket("ws://localhost:9999");
        var r = this;
        ws.onmessage = function(event) {
            var msg = JSON.parse(event.data);
            var msgType = msg["type"];
            var book = r.state.book;
            var order;
            if ("add" == msgType) {
                order = new Order(msg.venue, msg.orderId, msg.instrument, msg.side, msg.price, msg.amount);
                book.add(order);
            }
            else if ("change" == msgType) {
                book.delete(msg.venue, msg.side, msg.orderId);
                order = new Order(msg.venue, msg.orderId, msg.instrument, msg.side, msg.price, msg.amount);
                book.add(order);
            } else if ("delete" == msgType) {
                book.delete(msg.venue, msg.side, msg.orderId);
            } else if ("book" == msgType) {
                var bids = book.bids.slice(0);
                var asks = book.asks.slice(0);
                var o;
                for (var i = 0; i < bids.length; i++) {
                    o = bids[i];
                    if (msg.venue == o.venue) {
                        book.delete(o.venue, "BID", o.orderId);
                    }
                }
                for (i = 0; i < asks.length; i++) {
                    o = asks[i];
                    if (msg.venue == o.venue) {
                        book.delete(o.venue, "ASK", o.orderId);
                    }
                }
                for (i = 0; i < msg.bids.length; i++) {
                    o = msg.bids[i];
                    order = new Order(msg.venue, o.orderId, book.instrument, "BID", o.price, o.amount);
                    book.add(order);
                }
                for (i = 0; i < msg.asks.length; i++) {
                    o = msg.asks[i];
                    order = new Order(msg.venue, o.orderId, book.instrument, "ASK", o.price, o.amount);
                    book.add(order);
                }
            }
            r.setState({"book": book});
        };
    },

    render: function () {
        var book = this.state.book;
        var marks = [];
        var venueTitles = [];
        for (i = 0; i < venues.length; i++) {
            venueTitles.push(<text x="0" y={i * 50}>{venues[i]}</text>);
        }
        var bestBid = 0;
        var bestAsk = 0;
        var bestBidX = 0;
        var bestAskX = 0;

        if (book.asks.length > 0 && book.bids.length > 0) {
            var scale = getScale(book, 5);
            for (var i = 0; i < book.bids.length + book.asks.length - 1; i++) {
                var order = i < book.bids.length ? book.bids[i] : book.asks[i - book.bids.length];
                const venue = order[0];
                var index = venues.findIndex(function(x, a, b) {return venue === x;});
                var price = parseFloat(order[1]);
                var amount = parseFloat(order[2]);
                var x = scale(price);
                if (!amount.isNaN && x > -320 && x < 320) {
                    var side = i < book.bids.length ? "bids" : "asks";
                    var yOffset = index * 50;
                    var size = Math.max(1, Math.min(15, amount * 5));
                    marks.push(<line x1={x} y1={yOffset-size} x2={x} y2={yOffset+size} className={side}/>);
                }
            }
            bestBid = parseFloat(book["bids"][0][1]);
            bestAsk = parseFloat(book["asks"][0][1]);
            bestBidX = scale(bestBid) + 1;
            bestAskX = scale(bestAsk) - 1;
        }
        var spreadX = bestBidX - bestAskX;
        return (

            <svg id="main" viewBox="0 0 1000 700">
                <g transform="translate(50,50)">
                    <rect width="900" height="600" fill="none" className="border"/>
                </g>
                <g id="grid" transform="translate(500,60)">
                    <g id="horizontal-grid" className="table-line" transform="translate(0,20)">
                        <line x1="-420" y1="0" x2="420" y2="0"/>
                        <line x1="-420" y1="50" x2="420" y2="50"/>
                        <line x1="-420" y1="100" x2="420" y2="100"/>
                        <line x1="-420" y1="150" x2="420" y2="150"/>
                    </g>
                    <g id="vertical-grid" className="table-line">
                        <line x1="-330" y1="0" x2="-330" y2="580"/>
                        <line x1="330" y1="0" x2="330" y2="580"/>
                    </g>
                    <g id="venues" className="legend-venue" transform="translate(-339,49)">
                        {venueTitles}
                    </g>
                    <g id="arb-zone">
                        <rect x={bestAskX} y="0" width={spreadX} height="580" className="arb-fill"/>
                        <line x1={bestAskX} y1="0" x2={bestAskX} y2="580" className="arb-ok"/>
                        <line x1={bestBidX} y1="0" x2={bestBidX} y2="580" className="arb-ok"/>
                    </g>
                    <g id="order-book" transform="translate(0, 45)" className="orders">
                        {marks}
                    </g>
                </g>
                <g className="legend" transform="translate(50,40)">
                    <text>BTC/USD Arbitrage Spectra</text>
                </g>
                <g className="legend legend-time" transform="translate(950,40)">
                    <text>Sat Apr 23 17:46:32</text>
                </g>
                <g className="legend legend-arb-size" transform="translate(500,73)">
                    <text>+{(bestBid - bestAsk).toFixed(3)}</text>
                </g>
                <script src="js/lib/react.js"></script>
                <script src="js/lib/react-dom.js"></script>
                <script src="js/lib/browser.min.js"></script>
                <script src="js/lib/d3.js"></script>
            </svg>

        );
    }
});
var db = React.createElement(Dashboard);
ReactDOM.render(db, document.body);

