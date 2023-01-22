package JBKRMobile;

/**
 * Transaction
 * Wing Li, Brook Mao
 * Last modified: Jan 20, 2023
 * Represents a user transaction.
 */
public class Transaction {
    protected String transactionType; // the type of transaction, either "Buy" or "Sell"
    protected String date; // the date of the transaction in form: YYYY-MM-DD
    protected String ticker; // the ticker of the stock being bought/sold
    protected double price; // the price of the stock at the time of buy/sell
    protected int quantity; // the amount being bought/sold

    /**
     * @param transactionType: the type of the transaction (currently either "buy"
     *                         or "sell")
     * @param date:            the date the transaction took place in the form
     *                         "YYYY-MM-DD"
     * @param ticker:          the ticker of the stock exchanged in the transaction
     * @param price:           the unit price of the stock exchanged in the
     *                         transaction
     * @param quantity:        the quantity of the stock exchanged in the
     *                         transaction
     * @return does not return anything.
     *         Creates a new Transaction object
     */
    public Transaction(String transactionType, String date, String ticker, double price, int quantity) {
        this.transactionType = transactionType;
        this.date = date;
        this.ticker = ticker;
        this.price = price;
        this.quantity = quantity;
    }

    // Accessors
    public String getTransactionType() {
        return transactionType;
    }

    public String getDate() {
        return date;
    }

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Determines whether or not this transaction occured in the provided date
     *
     * @param date: The date to be compared in the form "YYYY-MM-DD"
     * @return boolean
     *         true if the provided date matches the date this transaction was made
     *         false otherwise
     */
    public boolean dateEquals(String date) {
        return this.date.equals(date);
    }

    /**
     * The cost of the transaction
     * Cost is calculated by quantity * price
     * 
     * @return double
     */
    public double costOfTransaction() {
        return quantity * price;
    }

    /**
     * Returns the information about this object in a format
     * to be written to a file
     * 
     * @return String
     */
    public String fileString() {
        return String.format("%s\n%s\n%s\n%.2f\n%d", transactionType, date, ticker, price, quantity);
    }
}
