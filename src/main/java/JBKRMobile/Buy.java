package JBKRMobile;

public class Buy extends Transaction {
    public Buy(String date, String ticker, int quantity, double price) {
        super(date, ticker, quantity, price);
    }

    public String fileString() {
        return "buy\n"+ date + "\n" + ticker + "\n" + quantity + "\n" + price;
    }

    public String toString() {
        return "buy\n" + super.toString();
    }
}
