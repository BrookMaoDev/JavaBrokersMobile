package JBKRMobile;

import java.util.ArrayList;

public class Adult extends Investor {
    public Adult(String username, String password) {
        super(username, password);
    }

    public Adult(String username, String password, double balance, double totalFundsSpent, double totalFundsAdded,
            int numTransactions, ArrayList<Transaction> transactions, int stocksInPortfolio,
            ArrayList<OwnedStock> portfolio) {
        super(username, password, balance, totalFundsSpent, totalFundsAdded, numTransactions, transactions,
                stocksInPortfolio, portfolio);
    }

    // Attempts to buy stock
    public int buyStock(String ticker, int quantity) {
        API.setSymbol(ticker);
        double price = API.getPrice();
        Buy purchase = new Buy(java.time.LocalDate.now().toString(), ticker, quantity, price);
        double cost = purchase.costOfTransaction();

        if (cost > balance) {
            return 2;
        }
        balance -= cost;
        totalFundsSpent += cost;
        transactions.add(purchase);
        numTransactions++;

        int tickerIndex = getTickerIndex(ticker);
        // The user does not own this stock yet
        if (tickerIndex < 0) {
            OwnedStock boughtStock = new OwnedStock(ticker, quantity);
            portfolio.add(boughtStock);
            stocksInPortfolio++;
            sortPortfolio();
        } else {
            // The user owns this stock
            portfolio.get(tickerIndex).addQuantity(quantity);
        }

        sortPortfolio();
        save();
        return 1;
    }

    public String buyMax(ArrayList<String> tickers, double balance) {
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

        String out = "Stocks bought:\n";

        for (int i = 0; i < output.size(); i += 2) {
            out += "Stock: " + output.get(i) + "\n";
            out += "Quantity: " + output.get(i + 1) + "\n";
            API.setSymbol(output.get(i).getTicker());
            out += "Price: " + API.getPrice() + "\n";
            out += "Price of this purchase: " + API.getPrice() * Integer.parseInt(output.get(i+1)) + "\n";
        }

        out += "Total Price of Purchase: " + calcValueOfArray(bestCombo);
        save();
        return out;
    }
}
