/**
Class Name: Transaction
Author: Wing Li, Brook Mao
Date: January 20, 2023
School: A Y Jackson Secondary School
Purpose: Represents a transaction.
 */

package JBKRMobile;

import java.text.NumberFormat;

/*
 * Transaction
 * Wing Li, Brook Mao
 * Last modified: Jan 20, 2023
 * Represents a user transaction.
 */
public class Transaction {
    protected String transactionType;
    protected String date;
    protected String ticker;
    protected int quantity;
    protected double price;

    public Transaction(String transactionType, String date, String ticker, int quantity, double price) {
        this.transactionType = transactionType;
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
    public String fileString() {
        return String.format("%s\n%s\n%s\n%d\n%.2f", transactionType, date, ticker, quantity, price);
    }

    public String toString() {
        return String.format("%s\nDate: %s\nTicker: %s\nQuantity: %d\nPrice: %s\n", transactionType, date, ticker,
                quantity, NumberFormat.getCurrencyInstance().format(price));
    }
}
