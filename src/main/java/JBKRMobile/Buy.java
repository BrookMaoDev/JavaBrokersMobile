package JBKRMobile;

public class Buy extends Transaction {
    public Buy(String date, String ticker, int quantity, double price) {
        super(date, ticker, quantity, price);
    }

    public String fileString() {
        return String.format("buy\n%s\n%s\n%d\n%.2f", date, ticker, quantity, price);
    }

    public String toString() {
        return "Buy\n" + super.toString();
    }
}
