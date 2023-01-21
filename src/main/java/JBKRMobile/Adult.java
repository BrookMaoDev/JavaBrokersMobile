package JBKRMobile;

import java.util.ArrayList;
import java.text.NumberFormat;

public class Adult extends Investor {
    public Adult(String username, String password) {
        super(username, password);
    }

    public Adult(String username, String password, double balance, double totalFundsAdded,
            ArrayList<OwnedStock> portfolio, ArrayList<Transaction> transactions) {
        super(username, password, balance, totalFundsAdded, portfolio, transactions);
    }

    // Attempts to buy stock
    public int buyStock(String ticker, int quantity) {
        API.setSymbol(ticker);
        double price = API.getPrice();
        Transaction transaction = new Transaction("buy", java.time.LocalDate.now().toString(), ticker, quantity, price);
        double cost = transaction.costOfTransaction();

        if (cost > balance) {
            return 2;
        }
        balance -= cost;
        transactions.add(transaction);

        int tickerIndex = getTickerIndex(ticker);
        // The user does not own this stock yet
        if (tickerIndex < 0) {
            portfolio.add(new OwnedStock(ticker, quantity));
        } else {
            // The user owns this stock
            portfolio.get(tickerIndex).addQuantity(quantity);
        }

        sortPortfolioByQuantity();
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
            API.setSymbol(output.get(i));
            out += "Price: " + NumberFormat.getCurrencyInstance().format(API.getPrice())
                    + "\n";
            out += "Price of this purchase: "
                    + NumberFormat.getCurrencyInstance().format(API.getPrice() *
                            Integer.parseInt(output.get(i + 1)))
                    + "\n";
        }

        out += "Total Price of Purchase: " +
                NumberFormat.getCurrencyInstance().format(calcValueOfArray(bestCombo));
        return out;
    }
}
