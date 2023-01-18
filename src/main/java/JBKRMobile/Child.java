package JBKRMobile;

import java.util.ArrayList;

public class Child extends Investor {
    // Spend limit for all child investors
    private final static double TRANSACTION_SPEND_LIMIT = 500;

    public Child(String username, String password) {
        super(username, password);
    }

    public Child(String username, String password, double wallet, double totalAmountSpent, double totalAmountAdded, int numTransactions, ArrayList<Transaction> transactions, int stocksInPortfolio, ArrayList<OwnedStock> portfolio) {
        super(username, password, wallet, totalAmountSpent, totalAmountAdded, numTransactions, transactions, stocksInPortfolio, portfolio);
    }

    public String buyMax(ArrayList<String> tickers, double wallet) {
        if (wallet > TRANSACTION_SPEND_LIMIT) {
            return "You cannot spend more than the transaction spend limit of " + TRANSACTION_SPEND_LIMIT;
        }
        
        ArrayList<String> bought = new ArrayList<String>();
        ArrayList<String> bestCombo = permute(tickers, wallet, bought);
        ArrayList<String> output = new ArrayList<String>();
        for (int i = 0; i < bestCombo.size(); i++) {
            int index = output.indexOf(bestCombo.get(i));
            if (index == -1) {
                output.add(bestCombo.get(i));
                output.add("1");
            } else {
                String prevQuantity = output.get(index + 1);
                String newQuantity = (Integer.parseInt(prevQuantity) + 1) + "";
                output.set(index + 1, newQuantity);
            }
        }

        for (int i = 0; i < output.size(); i+=2) {
            API.setSymbol(output.get(i));
            Buy buy = new Buy(date, output.get(i), output.get(i+1), API.getPrice());
        }

        String out = "";
        
        for (int i = 0; i < output.size(); i+=2) {
            out += "Stock: " + output.get(i) + "\n";
            out += "Quantity: " + output.get(i+1) + "\n";
            API.setSymbol(output.get(i));
            out += "Price of Individual Stock: " + API.getPrice() + "\n";
            out += "Price of Purchase: " + (API.getPrice() * Integer.parseInt(output.get(i+1))) + "\n";
            out += "\n";
        }

        out += "Total Price of Purchase: " + calcValueOfArray(bestCombo);
        return out;
    

        String out = "";
        
        for (int i = 0; i < output.size(); i+=2) {
            out += "Stock: " + output.get(i) + "\n";
            out += "Quantity: " + output.get(i+1) + "\n";
            API.setSymbol(output.get(i));
            out += "Price of Individual Stock: " + API.getPrice() + "\n";
            out += "Price of Purchase: " + (API.getPrice() * Integer.parseInt(output.get(i+1))) + "\n";
            out += "\n";
        }

        out += "Total Price of Purchase: " + calcValueOfArray(bestCombo);
        return out;
    }

    // Attempts to buy stock
    public boolean buyStock(String ticker, int quantity) {
        API.setSymbol(ticker);
        double price = API.getPrice();

        // Check if the transaction exceeds the spending limit
        if (price * quantity > TRANSACTION_SPEND_LIMIT) {
            return false;
        }

        // Check if the user has enough wallet
        Buy purchase = new Buy(date, ticker, quantity, price);

        if (wallet < purchase.costOfTransaction()) {
            return false;
        }
        wallet -= purchase.costOfTransaction();

        transactions.add(purchase);
        numTransactions++;
        stocksInPortfolio++;

        int tickerIndex = getTickerIndex(ticker);

        // The user does not own this stock yet
        if (tickerIndex < 0) {
            OwnedStock boughtStock = new OwnedStock(ticker, quantity);
            portfolio.add(boughtStock);
        } else {
            // The user owns this stock
            portfolio.get(tickerIndex).addQuantity(quantity);
        }
        return true;
    }
}
