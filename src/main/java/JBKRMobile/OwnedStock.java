package JBKRMobile;

/**
 * OwnedStock
 * Wing Li, Brook Mao
 * Last modified: Jan 20, 2023
 * Represents an owned stock; Investor holds array of this object.
 */
public class OwnedStock {
    private String ticker; // the ticker of the stock they own
    private int quantity; // how much of that stock they own

    /**
     * Creates a new OwnedStock object
     * 
     * @param username: the ticker of the stock
     * @param password: the quantity of the stock
     */
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

    /**
     * Adds an int to quantity
     *
     * @param int
     */
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
     * Otherwise, returns true.
     * 
     * @param int
     * @return boolean
     */
    public boolean subtractQuantity(int amount) {
        if (quantity - amount < 0) {
            return false;
        }
        quantity -= amount;
        return true;
    }

    /**
     * Compares the quantity of the implicit OwnedStock to another OwnedStock
     * 
     * @param other: OwnedStock to be compared
     * @return int
     *         The difference between the quantities
     *         Negative if the implicit quantity is lower than the explicit quantity
     *         Zero if they're equal
     *         Positive if the implicit quantity is greater than the explicit
     *         quantity
     */
    public int compareQuantity(OwnedStock other) {
        return quantity - other.getQuantity();
    }

    /**
     * Compares the unit price of the implicit OwnedStock to another OwnedStock
     * 
     * @param other: OwnedStock to be compared
     * @return int
     *         The difference between the prices
     *         Negative if the implicit price is lower than the explicit price
     *         Zero if they're equal
     *         Positive if the implicit price is greater than the explicit price
     */
    public double comparePrice(OwnedStock other) {
        API.setSymbol(ticker);
        double thisPrice = API.getPrice();
        API.setSymbol(other.getTicker());
        double otherPrice = API.getPrice();
        return thisPrice - otherPrice;
    }

    /**
     * Returns the information about this object in a format
     * to be written to a file
     * 
     * @return String
     */
    public String fileString() {
        return ticker + "\n" + quantity;
    }
}
