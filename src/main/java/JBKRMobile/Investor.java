package JBKRMobile;

import java.util.ArrayList;
import java.text.NumberFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

abstract class Investor {
    protected String username;
    protected String password;
    protected double balance;
    protected double totalFundsAdded;
    protected ArrayList<Transaction> transactions;
    protected ArrayList<OwnedStock> portfolio;
    protected final static String DB_PATH = "src/main/java/JBKRMobile/Database/";

    // Creates investor objects for when they sign up
    public Investor(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 0;
        this.totalFundsAdded = 0;
        this.portfolio = new ArrayList<OwnedStock>();
        this.transactions = new ArrayList<Transaction>();
    }

    // Creates investor objects for existing investors with previous data
    public Investor(String username, String password, double balance, double totalFundsAdded,
            ArrayList<OwnedStock> portfolio, ArrayList<Transaction> transactions) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.totalFundsAdded = totalFundsAdded;
        this.portfolio = portfolio;
        this.transactions = transactions;
    }

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

    // Adds balance to the investor's account
    public void deposit(double balance) {
        this.balance += balance;
        totalFundsAdded += balance;
    }

    // Subtracts balance from the user
    public boolean withdraw(double balance) {
        if (balance > this.balance) {
            return false;
        } else {
            this.balance -= balance;
            totalFundsAdded -= balance;
            return true;
        }
    }

    /**
     * Abstract method that takes in ticker symbol of a stock and the quantity. The
     * method will add that quantity of stock to the investor's portfolio, and
     * remove the money spent. Creates a transaction object in the process.
     */
    public abstract int buyStock(String ticker, int quantity);

    /**
     * Method that takes in ticker symbol of a stock and the quantity. The
     * method will remove that quantity of stock from the investor's portfolio, and
     * add the balance received. Creates a transaction object in the process.
     */
    public boolean sellStock(String ticker, int quantity) {
        for (int i = portfolio.size(); i > 0; i--) {
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

    // Sells every stock the user owns. Credits the balance to the account
    // accordingly.
    public void sellAll() {
        for (int i = portfolio.size(); i > 0; i--) {
            sellStock(portfolio.get(i).getTicker(), portfolio.get(i).getQuantity());
        }
    }

    /**
     * Abstract method that takes in an array of tickers. Also takes in a double
     * representing amount the user wants to spend. The method return the
     * combination of stocks, from the list, that will spend as much money as
     * possible without going over the limit.
     */
    public abstract String buyMax(ArrayList<String> tickers, double balance);

    /**
     * Returns a double which represents the net worth of the investor. Is
     * calculated by finding value of all stocks and adding balance onto that.
     */
    public double getNetWorth() {
        double netWorth = 0;
        for (int i = 0; i < portfolio.size(); i++) {
            API.setSymbol(portfolio.get(i).getTicker());
            netWorth += portfolio.get(i).getQuantity() * API.getPrice();
        }
        return netWorth + balance;
    }

    // Returns the lifetime profit of the user
    public double calculateProfit() {
        return balance - totalFundsAdded;
    }

    // Helper method for the buyMax method. Returns value of all stocks in a string
    // array.
    protected double calcValueOfArray(ArrayList<String> array) {
        double value = 0;
        for (int i = 0; i < array.size(); i++) {
            API.setSymbol(array.get(i));
            value += API.getPrice();
        }
        return value;
    }

    /**
     * Method that returns an ArrayList<String> of the portfolio's tickers
     */
    public ArrayList<String> getTickersOfPortfolio() {
        ArrayList<String> tickers = new ArrayList<String>();
        for (int i = 0; i < portfolio.size(); i++) {
            tickers.add(portfolio.get(i).getTicker());
        }
        return tickers;
    }

    // Gets the index of the specified ticker if it exists. Returns -1 otherwise
    public int getTickerIndex(String ticker) {
        for (int i = 0; i < portfolio.size(); i++) {
            if (portfolio.get(i).getTicker().equals(ticker)) {
                return i;
            }
        }
        return -1;
    }

    // Gets transaction history
    public String getTransactionHistory() {
        if (transactions.isEmpty()) {
            return "You have not made any transactions.";
        }
        String output = "";
        for (int i = 0; i < transactions.size(); i++) {
            output += transactions.get(i).toString() + "\n";
        }
        return output;
    }

    /**
     * Sorts portfolio by quantity owned. Most owned stocks will be at the front of
     * the array after the sort. Uses insertion sort.
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

    // Sorts portfolio by price, most expensive stocks will be at the front of the
    // array after the sort
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

    // Helper method for the buyMax method. Uses recursion.
    @SuppressWarnings("unchecked")
    protected ArrayList<String> permute(ArrayList<String> tickers, double balance, ArrayList<String> bought) {
        ArrayList<String> bestCombo = (ArrayList<String>) bought.clone();
        for (int i = 0; i < tickers.size(); i++) {
            ArrayList<String> newArray = (ArrayList<String>) bought.clone();
            newArray.add(tickers.get(i));
            if (calcValueOfArray(newArray) < balance) {
                API.setSymbol(tickers.get(i));
                ArrayList<String> potentialBestCombo = permute(tickers, balance, newArray);
                if (calcValueOfArray(potentialBestCombo) > calcValueOfArray(bestCombo)) {
                    bestCombo = potentialBestCombo;
                }
            } else if (calcValueOfArray(newArray) == balance) {
                return newArray;
            }
        }
        return bestCombo;
    }

    /**
     * Generates and returns a string describing everything about the investor
     */
    protected void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH + username + ".db"));
            bw.write(Login.encryptPassword(password) + "\n");
            if (this instanceof Adult) {
                bw.write("Adult\n");
            } else {
                bw.write("Child\n");
            }
            bw.write(balance + "\n");
            bw.write(totalFundsAdded + "\n");
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

    // Returns all information of the investor in a clean and organized manner
    public String toString() {
        return String.format("Net worth: %s\nProfit: %s\n",
                NumberFormat.getCurrencyInstance().format(getNetWorth()),
                NumberFormat.getCurrencyInstance().format(calculateProfit()));
    }
}
