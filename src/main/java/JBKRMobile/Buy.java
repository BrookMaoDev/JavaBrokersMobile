package JBKRMobile;

public class Buy extends Transaction {
    public Buy(String date, String ticker, long quantity, double price) {
        super(date, ticker, quantity, price);
    }

    // The money lost from this transaction
    // Money lost is calculated by (-1 * quantity * price) - fee
    public double costOfTransaction() {
        return super.getQuantity() * super.getPrice() * -1 - Transaction.getFee();
    }

    public String toString() {
        return "buy\n" + super.toString();
    }
}
