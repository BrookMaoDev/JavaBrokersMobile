package JBKRMobile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Child extends Investor {
    // Spend limit for all child investors
    private final static double TRANSACTION_SPEND_LIMIT = 500;

    public Child(String username, String password) {
        super(username, password);
    }

    public Child(String username, String password, double balance, double totalAmountSpent, double totalAmountAdded,
            int numTransactions, ArrayList<Transaction> transactions, int stocksInPortfolio,
            ArrayList<OwnedStock> portfolio) {
        super(username, password, balance, totalAmountSpent, totalAmountAdded, numTransactions, transactions,
                stocksInPortfolio, portfolio);
    }
    
    public static double getTransactionSpendLimit() {
        return TRANSACTION_SPEND_LIMIT;
    }

    public String buyMax(ArrayList<String> tickers, double balance) {
        if (balance > TRANSACTION_SPEND_LIMIT) {
            return "You cannot spend more than the transaction spend limit of " + TRANSACTION_SPEND_LIMIT;
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

        String out = "";

        for (int i = 0; i < output.size(); i += 2) {
            String ticker = output.get(i);
            int quantity = Integer.parseInt(output.get(i + 1));
            buyStock(ticker, quantity);
        }

        out += "Total Price of Purchase: " + calcValueOfArray(bestCombo);
        return out;
    }

    // Attempts to buy stock
    public boolean buyStock(String ticker, int quantity) {
        API.setSymbol(ticker);
        double price = API.getPrice();
        
        Buy purchase = new Buy(date, ticker, quantity, price);
        double cost = purchase.costOfTransaction();

        // Check if the transaction exceeds the spending limit
        if (cost > TRANSACTION_SPEND_LIMIT) {
            return false;
        }

        // Check if the user has enough balance
        if (balance < cost) {
            return false;
        }
        balance -= cost;
        totalAmountSpent += cost;

        transactions.add(purchase);
        numTransactions++;

        int tickerIndex = getTickerIndex(ticker);

        // The user does not own this stock yet
        if (tickerIndex < 0) {
            OwnedStock boughtStock = new OwnedStock(ticker, quantity);
            portfolio.add(boughtStock);
            stocksInPortfolio++;
        } else {
            // The user owns this stock
            portfolio.get(tickerIndex).addQuantity(quantity);
        }

        sortPortfolio();
        return true;
    }

    public void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH + username + ".db"));
            bw.write(Login.encryptPassword(password) + "\n");
            bw.write("child\n");
            bw.write(balance + "\n");
            bw.write(totalAmountSpent + "\n");
            bw.write(totalAmountAdded + "\n");
            bw.write(numTransactions + "\n");
            for (int i = 0; i < numTransactions; i++) {
                bw.write(transactions.get(i).fileString() + "\n");
            }
            bw.write(stocksInPortfolio + "\n");
            for (int i = 0; i < stocksInPortfolio; i++) {
                bw.write(portfolio.get(i).fileString() + "\n");
            }
            bw.close();
        } catch (IOException e) {
        }
    }
}
