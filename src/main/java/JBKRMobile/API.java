package JBKRMobile;

import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.YahooFinance;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * API
 * Owen Wang
 * Last modified: Jan 20, 2023
 * Pulls data from YahooFinance using YahooFinance API
 */
class API {
    private static StockQuote stock; // The stock we are pulling information for

    /**
    @param symbol: the new value of the stock field
    Mutator method that changes the stock field
     */
    public static void setSymbol(String symbol) {
        try {
            stock = YahooFinance.get(symbol.toUpperCase()).getQuote();
        } catch (Exception e) {
        }
    }

    /**
    @return the stock field
    Accessor method that returns the stock field
     */
    public static String getSymbol() {
        return stock.getSymbol();
    }

    /**
    @return the live price of the stock with the same ticker as the stock field
     */
    public static double getPrice() {
        return Double.parseDouble(stock.getPrice().setScale(2, RoundingMode.HALF_EVEN).toString());
    }

    /**
    @return the previous close of the stock with the same ticker as the stock field
     */

    public static double getPreviousClose() {
        return Double.parseDouble(stock.getPreviousClose().setScale(2, RoundingMode.HALF_EVEN).toString());
    }

    /**
    @return the open price of the stock with the same ticker as the stock field
     */
    public static double getOpen() {
        return Double.parseDouble(stock.getOpen().setScale(2, RoundingMode.HALF_EVEN).toString());
    }

    /**
    @return the live trading volume of the stock with the same ticker as the stock field
     */
    public static int getVolume() {
        return (int) Math.round(stock.getVolume());
    }

    /**
    @return the change in stock price between now and the open
     */
    public static double getChange() {
        // Set max decimal places to 8
        DecimalFormat df = new DecimalFormat("#.########");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        BigDecimal n = stock.getChange();
        Double d = n.doubleValue();
        return Double.parseDouble(df.format(d));
    }

    /**
    @return the percent change in stock price between now and the open
     */
    public static double getPercentChange() {
        return Double.parseDouble(stock.getChangeInPercent().setScale(2, RoundingMode.HALF_EVEN).toString());
    }
}
