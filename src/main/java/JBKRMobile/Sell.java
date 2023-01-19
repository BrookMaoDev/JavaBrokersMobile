package JBKRMobile;

public class Sell extends Transaction {
    public Sell(String date, String ticker, int quantity, double price) {
        super(date, ticker, quantity, price);
    }

    public String fileString() {
        return String.format("sell\n%s\n%s\n%d\n%.2f", date, ticker, quantity, price);
    }

    public String toString() {
        return "Sell\n" + super.toString();
    }
}
