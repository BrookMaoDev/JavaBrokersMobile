package JBKRMobile;

public class Sell extends Transaction {
    public Sell(String date, String ticker, int quantity, double price) {
        super(date, ticker, quantity, price);
    }

    // The profit gained from this transaction
    // Profit is calculated by quantity * price - fee
    public double costOfTransaction() {
        return super.getQuantity() * super.getPrice() - Transaction.getFee();
    }

    public String toString() {
        return "sell\n" + super.toString();
    }
}
