package JBKRMobile;

public class Buy extends Transaction {
    public Buy(String date, String ticker, long quantity, double price) {
        super(date, ticker, quantity, price);
    }

    // The profit gained from this transaction
    // Profit is calculated by quantity * price - fee
    public double costOfTransaction() {
        return super.getQuantity() * super.getPrice() * -1 - Transaction.getFee();
    }
}
