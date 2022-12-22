package megabudgetstonks;

import java.io.IOException;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.YahooFinance;
import java.math.BigDecimal;

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

    public BigDecimal getPrice() {
        return stock.getPrice();
    }

    public BigDecimal getPreviousClose() {
        return stock.getPreviousClose();
    }

    public BigDecimal getOpen() {
        return stock.getOpen();
    }

    public BigDecimal getBid() {
        return stock.getBid();
    }

    public BigDecimal getAsk() {
        return stock.getAsk();
    }

    public Long getVolume() {
        return stock.getVolume();
    }

    public BigDecimal getChange() {
        return stock.getChange();
    }

    public BigDecimal getPercentChange() {
        return stock.getChangeInPercent();
    }
}
