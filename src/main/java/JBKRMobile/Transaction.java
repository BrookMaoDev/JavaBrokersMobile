package JBKRMobile;

public abstract class Transaction {
    private final static double FEE = 1.00;
    private String date;
    private String ticker;
    private long quantity;
    private double price;

    public String getDate() {
        return date;
    }

    public String getTicker() {
        return ticker;
    }

    public long getQuantity() {
        return quantity;
    }

    public double price() {
        return price;
    }

    abstract double costOfTransaction();
}
