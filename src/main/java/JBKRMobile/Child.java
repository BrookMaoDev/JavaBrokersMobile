package JBKRMobile;

import java.util.ArrayList;
import java.text.NumberFormat;

public class Child extends Investor {
    // Spend limit for all child investors
    private final static double TRANSACTION_SPEND_LIMIT = 500;

    public Child(String username, String password) {
        super(username, password);
    }

    public Child(String username, String password, double balance, double totalFundsAdded,
            int numTransactions, ArrayList<Transaction> transactions, int stocksInPortfolio,
            ArrayList<OwnedStock> portfolio) {
        super(username, password, balance, totalFundsAdded, numTransactions, transactions,
                stocksInPortfolio, portfolio);
    }

    public static double getTransactionSpendLimit() {
        return TRANSACTION_SPEND_LIMIT;
    }

    // Attempts to buy stock
    public int buyStock(String ticker, int quantity) {
        API.setSymbol(ticker);
        double price = API.getPrice();
        Buy purchase = new Buy(java.time.LocalDate.now().toString(), ticker, quantity, price);
        double cost = purchase.costOfTransaction();

        // Check if the transaction exceeds the spending limit
        if (this instanceof Child && cost > TRANSACTION_SPEND_LIMIT) {
            return 3;
        }

        // Check if the user has enough balance
        if (cost > balance) {
            return 2;
        }
        balance -= cost;
        transactions.add(purchase);
        numTransactions++;

        int tickerIndex = getTickerIndex(ticker);
        // The user does not own this stock yet
        if (tickerIndex < 0) {
            // THIS IS CAUSING CRASH
            portfolio.add(new OwnedStock(ticker, quantity));
            stocksInPortfolio++;
        } else {
            // The user owns this stock
            portfolio.get(tickerIndex).addQuantity(quantity);
        }

        sortPortfolio();
        return 1;
    }

    public String buyMax(ArrayList<String> tickers, double balance) {
        if (balance > TRANSACTION_SPEND_LIMIT) {
            balance = TRANSACTION_SPEND_LIMIT;
        }

        ArrayList<String> bought = new ArrayList<String>();
        ArrayList<String> bestCombo = permute(tickers, balance, bought);
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

        for (int i = 0; i < output.size(); i += 2) {
            API.setSymbol(output.get(i));
            buyStock(output.get(i), Integer.parseInt(output.get(i + 1)));
        }

        for (int i = 0; i < output.size(); i += 2) {
            String ticker = output.get(i);
            int quantity = Integer.parseInt(output.get(i + 1));
            buyStock(ticker, quantity);
        }

        String out = "Stocks bought:\n";

        for (int i = 0; i < output.size(); i += 2) {
            out += "Stock: " + output.get(i) + "\n";
            out += "Quantity: " + output.get(i + 1) + "\n";
            API.setSymbol(output.get(i));
            out += "Price: " + NumberFormat.getCurrencyInstance().format(API.getPrice()) + "\n";
            out += "Price of this purchase: "
                    + NumberFormat.getCurrencyInstance().format(API.getPrice() * Integer.parseInt(output.get(i + 1)))
                    + "\n";
        }

        out += "Total Price of Purchase: " + NumberFormat.getCurrencyInstance().format(calcValueOfArray(bestCombo));
        return out;
    }
}
