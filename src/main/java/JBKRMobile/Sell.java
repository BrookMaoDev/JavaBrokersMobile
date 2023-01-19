package JBKRMobile;

public class Sell extends Transaction {
    public Sell(String date, String ticker, int quantity, double price) {
        super(date, ticker, quantity, price);
    }

    // The profit gained from this transaction
    // Profit is calculated by quantity * price;
    public double costOfTransaction() {
        return quantity * price;
    }
    
    public String fileString() {
        return "sell\n"+ date + "\n" + ticker + "\n" + quantity + "\n" + price;
    }

    public String toString() {
        return "sell\n" + super.toString();
    }
}
