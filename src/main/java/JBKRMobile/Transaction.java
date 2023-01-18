package JBKRMobile;

public abstract class Transaction {
    //Fields
    protected final static double FEE = 1.0;
    protected String date;
    protected String ticker;
    protected int quantity;
    protected double price;

    // Constructor
    public Transaction(String date, String ticker, int quantity, double price) {
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

    // String that contains information about the transaction to write to a file
    public String fileString() {
        String output = "";
        if (this instanceof Buy) {
            output = "buy\n";
        } else {
            output = "sell\n";
        }
        return output + date + "\n" + ticker + "\n" + quantity + "\n" + price;
    }

    abstract double costOfTransaction();
}
