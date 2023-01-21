package JBKRMobile;

/*
 * OwnedStock
 * Wing Li, Brook Mao
 * Last modified: Jan 20, 2023
 * Represents an owned stock; Investor holds array of this object.
 */
public class OwnedStock {
    private String ticker;
    private int quantity;

    public OwnedStock(String ticker, int quantity) {
        this.ticker = ticker;
        this.quantity = quantity;
    }

    // Accessors
    public String getTicker() {
        return ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        quantity += amount;
    }

    public double getValue() {
        API.setSymbol(ticker);
        return API.getPrice() * quantity;
    }

    /**
     * Subtracts an int from quantity. If amount is greater than quantity, then
     * returns false before subtracting.
     * Otherwise, returns true
     * 
     * @param amount int
     */
    public boolean subtractQuantity(int amount) {
        if (quantity - amount < 0) {
            return false;
        }
        quantity -= amount;
        return true;
    }

    public int compareQuantity(OwnedStock other) {
        return quantity - other.getQuantity();
    }

    public double comparePrice(OwnedStock other) {
        API.setSymbol(ticker);
        double thisPrice = API.getPrice();
        API.setSymbol(other.getTicker());
        double otherPrice = API.getPrice();
        return thisPrice - otherPrice;
    }

    public String fileString() {
        return ticker + "\n" + quantity;
    }
}
