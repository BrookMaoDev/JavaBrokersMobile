package JBKRMobile;

public class Buy extends Transaction {
    public Buy(String date, String ticker, int quantity, double price) {
        super(date, ticker, quantity, price);
    }

    // Cost of transaction
    public double costOfTransaction() {
        return super.getQuantity() * super.getPrice();
    }

    public String toString() {
        return "buy\n" + super.toString();
    }
}
