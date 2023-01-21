package JBKRMobile;

import java.util.ArrayList;
import java.text.NumberFormat;

/*
 * Adult
 * Brook Mao, Wing Li, Owen Wang
 * Last modified: Jan 20, 2023
 * Represents an adult investor.
 */
public class Adult extends Investor {

    /**
     * @param username: the username the investor signed up with
     * @param password: the password the investor signed up with (unencrypted)
     * @return does not return anything.
     *         Creates an instance of Adult. This constructor is used when someone
     *         SIGNS UP.
     */
    public Adult(String username, String password) {
        super(username, password);
    }

    /**
     * @param username:        the username of the investor
     * @param password:        the password of the investor (unencrypted)
     * @param balance:         the money the investor has
     * @param totalFundsAdded: how much the investor has added to the account
     * @param portfolio:       array of OwnedStock that the investor has purchased
     *                         before
     * @param transactions:    array of transaction that the investor has made
     *                         before
     * @return does not return anything.
     *         Creates an instance of Adult. This constructor is used when someone
     *         LOGS IN.
     */
    public Adult(String username, String password, double balance, double totalFundsAdded,
            ArrayList<OwnedStock> portfolio, ArrayList<Transaction> transactions) {
        super(username, password, balance, totalFundsAdded, portfolio, transactions);
    }

    /**
     * @param ticker:   the ticker of the stock that the user wants to purchase
     * @param quantity: how much of the stock the user wants to purchase
     * @return int
     *         1 means the stock was bought successfully
     *         2 means the user has insufficient funds
     *         Buys the specified quantity of the stock with the specified ticker.
     *         Updates the portfolio and balance accordingly.
     */
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

    /**
     * @param tickers: an arrayList of ticker symbols that the user is okay with
     *                 spending money on.
     * @param balance: the amount of money the user is willing to spend. This method
     *                 cannot spend more than this amount.
     * @return String
     *         Returns a list of stocks bought in an organized fashion.
     *         This program will spend as much of the balance as possible on the
     *         specified list of tickers passed in.
     */
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
