package JBKRMobile;

import java.util.ArrayList;
import java.text.NumberFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Investor
 * Brook Mao, Wing Li, Owen Wang
 * Last modified: Jan 20, 2023
 * Abstract class representing an investor.
 */
abstract class Investor {
    protected String username;
    protected String password;
    protected double balance;
    protected double totalFundsAdded;
    protected ArrayList<Transaction> transactions;
    protected ArrayList<OwnedStock> portfolio;
    protected final static String DB_PATH = "src/main/java/JBKRMobile/Database/";

    /**
     * @param username: the username the investor signed up with
     * @param password: the password the investor signed up with (unencrypted)
     * @return does not return anything.
     *         Called from child classes only. This constructor is used when someone
     *         SIGNS UP.
     */
    public Investor(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 0;
        this.totalFundsAdded = 0;
        this.portfolio = new ArrayList<OwnedStock>();
        this.transactions = new ArrayList<Transaction>();
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
     *         Called from child classes only. This constructor is used when someone
     *         LOGS IN.
     */
    public Investor(String username, String password, double balance, double totalFundsAdded,
            ArrayList<OwnedStock> portfolio, ArrayList<Transaction> transactions) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.totalFundsAdded = totalFundsAdded;
        this.portfolio = portfolio;
        this.transactions = transactions;
    }

    // Accessors and mutators

    public double getFunds() {
        return balance;
    }

    public double getAddedFunds() {
        return totalFundsAdded;
    }

    public ArrayList<OwnedStock> getPortfolio() {
        return portfolio;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @param ticker:   the ticker of the stock that the user wants to purchase
     * @param quantity: how much of the stock the user wants to purchase
     * @return boolean
     *         true means stock was successfully bought
     *         false means stock unsuccessfully bought
     *         Buys the specified quantity of the stock with the specified ticker.
     *         Updates the portfolio and balance accordingly.
     */
    public boolean buyStock(String ticker, int quantity) {
        API.setSymbol(ticker);
        double price = API.getPrice();
        Transaction transaction = new Transaction("Buy", java.time.LocalDate.now().toString(), ticker, quantity, price);
        double cost = transaction.costOfTransaction();

        if (cost > balance) {
            return false;
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
        return true;
    }

    /**
     * @param ticker:   the ticker of the stock the user wants to sell
     * @param quantity: the ticker of the stock the user wants to sell
     * @return boolean
     *         Returns true if the stocks were successfully sold
     *         Returns false if the user does not own the stock with the given
     *         ticker, or they do not own enough to sell the specified quantity
     *         Sells the specified quantity of the stock with the specified ticker.
     *         Updates the portfolio and balance accordingly.
     */
    public boolean sellStock(String ticker, int quantity) {
        for (int i = 0; i < portfolio.size(); i++) {
            if (portfolio.get(i).getTicker().equalsIgnoreCase(ticker)) {
                API.setSymbol(ticker);
                Transaction transaction = new Transaction("Sell", java.time.LocalDate.now().toString(), ticker,
                        quantity, API.getPrice());
                if (quantity < portfolio.get(i).getQuantity()) {
                    portfolio.get(i).subtractQuantity(quantity);
                } else if (quantity == portfolio.get(i).getQuantity()) {
                    portfolio.remove(i);
                } else {
                    return false;
                }
                balance += transaction.costOfTransaction();
                transactions.add(transaction);
                return true;
            }
        }
        return false;
    }

    /**
     * @return nothing
     *         Sells all stocks the user owns.
     */
    public void sellAll() {
        for (int i = portfolio.size() - 1; i >= 0; i--) {
            sellStock(portfolio.get(i).getTicker(), portfolio.get(i).getQuantity());
        }
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

    /**
     * @return double which represents the user's net worth
     *         Calculates the user's net worth. Adds up the value of all their
     *         OwnedStock and any leftover balance.
     */
    public double getNetWorth() {
        double netWorth = 0;
        for (int i = 0; i < portfolio.size(); i++) {
            API.setSymbol(portfolio.get(i).getTicker());
            netWorth += portfolio.get(i).getValue();
        }
        return netWorth + balance;
    }

    /**
     * @return double which represents the amount of profit the user had made since
     *         creating the account
     *         Returns the lifetime profit of the user.
     */
    public double calculateProfit() {
        return getNetWorth() - totalFundsAdded;
    }

    /**
     * @param array: represents an array of tickers
     * @return double representing the value of all the tickers added up
     *         Helper method that can calculate the value of an array of STRING
     *         tickers
     */
    protected double calcValueOfArray(ArrayList<String> array) {
        double value = 0;
        for (int i = 0; i < array.size(); i++) {
            API.setSymbol(array.get(i));
            value += API.getPrice();
        }
        return value;
    }

    /**
     * @return returns an arrayList of Strings which are the ticker symbols of all
     *         stocks in the portfolio.
     */
    public ArrayList<String> getTickersOfPortfolio() {
        ArrayList<String> tickers = new ArrayList<String>();
        for (int i = 0; i < portfolio.size(); i++) {
            tickers.add(portfolio.get(i).getTicker());
        }
        return tickers;
    }

    /**
     * @param ticker: the ticker we want to find
     * @return int - index of the OwnedStock with the ticker in the portfolio array
     */
    public int getTickerIndex(String ticker) {
        for (int i = 0; i < portfolio.size(); i++) {
            if (portfolio.get(i).getTicker().equals(ticker)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return String of last 5 transactions
     */
    public String getTransactionHistory() {
        if (transactions.isEmpty()) {
            return "You have not made any transactions.";
        }
        String output = "";
        if (transactions.size() > 5) {
            for (int i = 0; i < transactions.size(); i++) {
                output += transactions.get(i).toString() + "\n";
            }
        } else {
            for (int i = 0; i < 5; i++) {
                output += transactions.get(i).toString() + "\n";
            }
        }
        return output;
    }

    /**
     * @return true if the sort is successful
     *         Sorts the portfolio by quantity, with most owned at the front
     */
    public boolean sortPortfolioByQuantity() {
        for (int i = 1; i < portfolio.size(); i++) {
            OwnedStock moved = portfolio.get(i);
            int empty = i;
            for (int j = i - 1; j >= 0 && portfolio.get(j).compareQuantity(moved) < 0; j--) {
                portfolio.set(j + 1, portfolio.get(j));
                empty = j;
            }
            portfolio.set(empty, moved);
        }
        return true;
    }

    /**
     * @return true if the sort is successful
     *         Sorts the portfolio by price, with most expensive at the front
     */
    public boolean sortPortfolioByPrice() {
        for (int i = 1; i < portfolio.size(); i++) {
            OwnedStock moved = portfolio.get(i);
            int empty = i;
            for (int j = i - 1; j >= 0 && portfolio.get(j).comparePrice(moved) < 0; j--) {
                portfolio.set(j + 1, portfolio.get(j));
                empty = j;
            }
            portfolio.set(empty, moved);
        }
        return true;
    }

    /**
     * @param tickers: tickers the method is allowed to buy
     * @param balance: the amount the method is allowed to spend
     * @param bought:  a list of already bought tickers
     * @return an array of Strings which represent the combination of tickers that
     *         spends the most balance as possible
     *         Helper method for the buyMax method.
     */
    @SuppressWarnings("unchecked")
    protected ArrayList<String> permute(ArrayList<String> tickers, double balance, ArrayList<String> bought) {
        ArrayList<String> bestCombo = (ArrayList<String>) bought.clone();
        for (int i = 0; i < tickers.size(); i++) {
            API.setSymbol(tickers.get(i));
            double price = API.getPrice();
            if (price < balance) {
                ArrayList<String> newArray = (ArrayList<String>) bought.clone();
                newArray.add(tickers.get(i));
                ArrayList<String> potentialBestCombo = permute(tickers, balance - price, newArray);
                if (calcValueOfArray(potentialBestCombo) > calcValueOfArray(bestCombo)) {
                    bestCombo = potentialBestCombo;
                }
            } else if (price == balance) {
                ArrayList<String> newArray = (ArrayList<String>) bought.clone();
                newArray.add(tickers.get(i));
                return newArray;
            }
        }
        return bestCombo;
    }

    /**
     * Saves investor info to file
     */
    protected void save() {
        File dbPath = new File(DB_PATH);
        if (!dbPath.exists()) {
            dbPath.mkdirs();
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH + username + ".db"));
            bw.write(Login.encryptPassword(password) + "\n");
            if (this instanceof Adult) {
                bw.write("Adult\n");
            } else {
                bw.write("Child\n");
            }
            bw.write(String.format("%.2f\n", balance));
            bw.write(String.format("%.2f\n", totalFundsAdded));
            bw.write(portfolio.size() + "\n");
            for (int i = 0; i < portfolio.size(); i++) {
                bw.write(portfolio.get(i).fileString() + "\n");
            }
            bw.write(transactions.size() + "\n");
            for (int i = 0; i < transactions.size(); i++) {
                bw.write(transactions.get(i).fileString() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param date: the date the transactions should be on
     * @return returns an ArrayList of transactions that took place on a certain day
     */
    public ArrayList<Transaction> transactionsOnDay(String date) {
        try {
            ArrayList<Transaction> trans = new ArrayList<Transaction>();
            int firstIndex = firstIndex(date);
            int lastIndex = lastIndex(date);
            for (int i = firstIndex; i <= lastIndex; i++) {
                trans.add(transactions.get(i));
            }
            return trans;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param date: the date
     * @return returns the index of the last transaction on that given day
     */
    private int lastIndex(String date) {
        int top = transactions.size() - 1;
        int bottom = 0;
        int mid;
        while (top >= bottom) {
            mid = (top + bottom) / 2;
            if (compareDates(transactions.get(mid).getDate(), date) > 0) {
                top = mid - 1;
            } else if (compareDates(transactions.get(mid).getDate(), date) < 0) {
                bottom = mid + 1;
            } else {
                if (mid == (transactions.size() - 1) || transactions.get(mid + 1).dateEquals(date) == false) {
                    return mid;
                } else {
                    bottom = mid + 1;
                }
            }
        }
        return -1;
    }

    /**
     * @param date: the date
     * @return returns the index of the first transaction on that given day
     */
    private int firstIndex(String date) {
        int top = transactions.size() - 1;
        int bottom = 0;
        int mid;
        while (top >= bottom) {
            mid = (top + bottom) / 2;
            if (compareDates(transactions.get(mid).getDate(), date) > 0) {
                top = mid - 1;
            } else if (compareDates(transactions.get(mid).getDate(), date) < 0) {
                bottom = mid + 1;
            } else {
                if (mid == 0 || transactions.get(mid - 1).dateEquals(date) == false) {
                    return mid;
                } else {
                    top = mid - 1;
                }
            }
        }
        return -1;
    }

    /**
     * @param date1: a date
     * @param date2: another date
     * @return return 1 if date1 > date2
     *         return 0 if date1 == date2
     *         return -1 if date1 < date2
     */
    private int compareDates(String date1, String date2) {
        int intd1 = Integer.parseInt(date1.substring(0, 4) + date1.substring(5, 7) + date1.substring(8, 10));
        int intd2 = Integer.parseInt(date2.substring(0, 4) + date2.substring(5, 7) + date2.substring(8, 10));

        if (intd1 > intd2) {
            return 1;
        } else if (intd1 < intd2) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Returns all information about the investor in a clean and organized manner
     */
    public String toString() {
        return String.format("Net worth: %s\nProfit: %s\n",
                NumberFormat.getCurrencyInstance().format(getNetWorth()),
                NumberFormat.getCurrencyInstance().format(calculateProfit()));
    }
}
