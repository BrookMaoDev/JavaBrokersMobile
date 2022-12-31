package megabudgetstonks;

import java.io.IOException;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.YahooFinance;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

class API {
    private StockQuote stock;

    public API() {
    }

    public void setSymbol(String symbol) {
        try {
            stock = YahooFinance.get(symbol.toUpperCase()).getQuote();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getSymbol() {
        return stock.getSymbol();
    }

    public String getPrice() {
        return stock.getPrice().setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    public String getPreviousClose() {
        return stock.getPreviousClose().setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    public String getOpen() {
        return stock.getOpen().setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    public String getVolume() {
        return stock.getVolume().toString();
    }

    public String getChange() {
        // Set max decimal places to 8
        DecimalFormat df = new DecimalFormat("#.########");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        BigDecimal n = stock.getChange();
        Double d = n.doubleValue();
        return df.format(d);
    }

    public String getPercentChange() {
        return stock.getChangeInPercent().setScale(2, RoundingMode.HALF_EVEN).toString();
    }
}
