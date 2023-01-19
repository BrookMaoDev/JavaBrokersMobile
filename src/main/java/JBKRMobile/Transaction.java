package JBKRMobile;

public abstract class Transaction {
    //Fields
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
    public String getDate() {
        return date;
    }

    public String getTicker() {
        return ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
    
    // The profit gained from this transaction
    // Profit is calculated by quantity * price;
    public double costOfTransaction() {
        return quantity * price;
    }

    public String toString() {
        return "Date: "+ date + "\nTicker: " + ticker + "\nQuantity: " + quantity + "\nPrice: " + price;
    }

    // String that contains information about the transaction to write to a file
    public abstract String fileString();
}
