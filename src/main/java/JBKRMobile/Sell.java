package JBKRMobile;

public class Sell extends Transaction {
    public Sell(String date, String ticker, int quantity, double price) {
        super(date, ticker, quantity, price);
    }
    
    public String fileString() {
        return "sell\n"+ date + "\n" + ticker + "\n" + quantity + "\n" + price;
    }

    public String toString() {
        return "sell\n" + super.toString();
    }
}
