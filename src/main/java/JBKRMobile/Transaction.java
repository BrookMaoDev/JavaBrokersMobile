package JBKRMobile;

import java.text.NumberFormat;

public abstract class Transaction {
    protected String date;
    protected String ticker;
    protected int quantity;
    protected double price;

    public Transaction(String date, String ticker, int quantity, double price) {
        this.date = date;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
    }

    // The profit gained from this transaction
    // Profit is calculated by quantity * price;
    public double costOfTransaction() {
        return quantity * price;
    }

    // String that contains information about the transaction to write to a file
    public abstract String fileString();

    public String toString() {
        return String.format("Date: %s\nTicker: %s\nQuantity: %d\nPrice: %s\n", date, ticker,
                quantity, NumberFormat.getCurrencyInstance().format(price));
    }
}
