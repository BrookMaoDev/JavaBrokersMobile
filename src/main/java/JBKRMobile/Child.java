package JBKRMobile;

import java.util.ArrayList;

public class Child extends Investor {
    // Spend limit for all child investors
    private final static double TRANSACTION_SPEND_LIMIT = 500;

    public Child(String username, String password) {
        super(username, password);
    }
    
    public Child(String username, String password, double money, double spentMoney, double addedMoney, int numTransactions, ArrayList<Transaction> transactions, int stocksInPortfolio, ArrayList<OwnedStock> portfolio) {
        super(username, password, money, spentMoney, addedMoney, numTransactions, transactions, stocksInPortfolio, portfolio);
    }

    public String buyMax(ArrayList<String> tickers, double money) {
        if (money > TRANSACTION_SPEND_LIMIT) {
            return "You cannot spend more than the transaction spend limit of " + TRANSACTION_SPEND_LIMIT;
        }
        
        ArrayList<String> bought = new ArrayList<String>();
        double moneySpent = 0;
        ArrayList<String> bestCombo = permute(tickers, money, bought, moneySpent);
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

        String out = "";
        
        for (int i = 0; i < output.size(); i+=2) {
            out += "Stock: " + output.get(i) + "\n";
            out += "Quantity: " + output.get(i+1) + "\n";
            api.setSymbol(output.get(i));
            out += "Price of Individual Stock: " + api.getPrice() + "\n";
            out += "Price of Purchase: " + (api.getPrice() * Integer.parseInt(output.get(i+1))) + "\n";
            out += "\n";
        }

        out += "Total Price of Purchase: " + calcValueOfArray(bestCombo);
        return out;
    }

    // Attempts to buy stock
    public boolean buyStock(String ticker, int quantity) {
        api.setSymbol(ticker);
        double price = api.getPrice();

        // Check if the transaction exceeds the spending limit
        if (price * quantity > TRANSACTION_SPEND_LIMIT) {
            return false;
        }

        // Check if the user has enough money
        Buy purchase = new Buy(date, ticker, quantity, price);

        if (money < purchase.costOfTransaction()) {
            return false;
        }
        money -= purchase.costOfTransaction();

        transactions.add(purchase);

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