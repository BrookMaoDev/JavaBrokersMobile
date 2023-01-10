package JBKRMobile;

public abstract class Transaction {
    private final static double FEE = 1.0;
    private String date;
    private String ticker;
    private long quantity;
    private double price;

    public Transaction(String date, String ticker, long quantity, double price) {
        this.date = date;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
    }

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
        return date + "\n" + ticker + "\n" + quantity + "\n" + price;
    }

    abstract double costOfTransaction();
}
