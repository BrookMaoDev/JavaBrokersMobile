package JBKRMobile;

public class OwnedStock {
    private String ticker;
    private long quantity;

    public OwnedStock(String ticker, long quantity) {
        this.ticker = ticker;
        this.quantity = quantity;
    }

    public String getTicker() {
        return ticker;
    }

    public long getQuantity() {
        return quantity;
    }
}
