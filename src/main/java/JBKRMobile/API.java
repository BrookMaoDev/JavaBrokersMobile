package JBKRMobile;

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

    public double getPrice() {
        return Double.parseDouble(stock.getPrice().setScale(2, RoundingMode.HALF_EVEN).toString());
    }

    public double getPreviousClose() {
        return Double.parseDouble(stock.getPreviousClose().setScale(2, RoundingMode.HALF_EVEN).toString());
    }

    public double getOpen() {
        return Double.parseDouble(stock.getOpen().setScale(2, RoundingMode.HALF_EVEN).toString());
    }

    public int getVolume() {
        return (int) Math.round(stock.getVolume());
    }

    public double getChange() {
        // Set max decimal places to 8
        DecimalFormat df = new DecimalFormat("#.########");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        BigDecimal n = stock.getChange();
        Double d = n.doubleValue();
        return Double.parseDouble(df.format(d));
    }

    public double getPercentChange() {
        return Double.parseDouble(stock.getChangeInPercent().setScale(2, RoundingMode.HALF_EVEN).toString());
    }
}
