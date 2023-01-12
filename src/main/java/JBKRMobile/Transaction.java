package JBKRMobile;

public abstract class Transaction {
    //Fields
    protected final static double FEE = 1.0;
    protected String date;
    protected String ticker;
    protected long quantity;
    protected double price;

    // Constructor
    public Transaction(String date, String ticker, long quantity, double price) {
        this.date = date;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
    }

    // Accessors 
    public static double getFee() {
        return FEE;
    }

    public String getDate() {
        return date;
    }

    public String getTicker() {
        return ticker;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        return "Date: "+ date + "\nTicker: " + ticker + "\nQuantity: " + quantity + "\nPrice: " + price;
    }

    abstract double costOfTransaction();
}
