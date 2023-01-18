package JBKRMobile;

import java.util.ArrayList;

abstract class Investor {
    protected static String date;
    protected String username;
    protected String password;
    protected double balance;
    protected double totalAmountSpent;
    protected double totalAmountAdded;
    protected int numTransactions;
    protected int stocksInPortfolio;
    protected ArrayList<Transaction> transactions;
    protected ArrayList<OwnedStock> portfolio;
    protected final static String DB_PATH = "src/main/java/JBKRMobile/Database/";

    // Creates investor objects for when they sign up
    public Investor(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 0;
        this.totalAmountSpent = 0;
        this.totalAmountAdded = 0;
        this.numTransactions = 0;
        this.stocksInPortfolio = 0;
        this.transactions = new ArrayList<Transaction>();
        this.portfolio = new ArrayList<OwnedStock>();
        date = java.time.LocalDate.now().toString();
    }

    // Creates investor objects for existing investors with previous data
    public Investor(String username, String password, double balance, double totalAmountSpent, double totalAmountAdded,
            int numTransactions, ArrayList<Transaction> transactions, int stocksInPortfolio,
            ArrayList<OwnedStock> portfolio) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.totalAmountSpent = totalAmountSpent;
        this.totalAmountAdded = totalAmountAdded;
        this.numTransactions = numTransactions;
        this.stocksInPortfolio = stocksInPortfolio;
        this.transactions = transactions;
        this.portfolio = portfolio;
        date = java.time.LocalDate.now().toString();
    }

    public double getFunds() {
        return balance;
    }

    public double getSpentFunds() {
        return totalAmountSpent;
    }

    public double getAddedFunds() {
        return totalAmountAdded;
    }

    public int getNumTransactions() {
        return numTransactions;
    }

    public int getStocksInPortfolio() {
        return stocksInPortfolio;
    }

    public ArrayList<OwnedStock> getPortfolio() {
        return portfolio;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Abstract method that takes in ticker symbol of a stock and the quantity. The
     * method will add that quantity of stock to the investor's portfolio, and
     * remove the money spent. Creates a transaction object in the process.
     */
    public abstract boolean buyStock(String ticker, int quantity);

    /**
     * Abstract method that takes in an array of tickers. Also takes in a double
     * representing amount the user wants to spend. The method return the
     * combination of stocks, from the list, that will spend as much money as
     * possible without going over the limit.
     */
    public abstract String buyMax(ArrayList<String> tickers, double balance);

    // Helper method for the buyMax method. Uses recursion.
    protected ArrayList<String> permute(ArrayList<String> tickers, double balance, ArrayList<String> bought) {
        ArrayList<String> bestCombo = bought;
        for (int i = 0; i < tickers.size(); i++) {
            ArrayList<String> newArray = bought;
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
        for (int i = 0; i < stocksInPortfolio; i++) {
            tickers.add(portfolio.get(i).getTicker());
        }
        return tickers;
    }

    /**
     * Method that takes in ticker symbol of a stock and the quantity. The
     * method will remove that quantity of stock from the investor's portfolio, and
     * add the balance received. Creates a transaction object in the process.
     */
    public boolean sellStock(String ticker, int quantity) {
        for (int i = 0; i < stocksInPortfolio; i++) {
            if (portfolio.get(i).getTicker().equals(ticker)) {
                if (portfolio.get(i).getQuantity() > quantity) {
                    API.setSymbol(ticker);
                    Sell sell = new Sell(date, ticker, quantity, API.getPrice());
                    portfolio.get(i).subtractQuantity(quantity);
                    balance = balance + sell.costOfTransaction();
                    transactions.add(sell);
                    numTransactions++;
                    return true;
                } else if (portfolio.get(i).getQuantity() == quantity) {
                    API.setSymbol(ticker);
                    Sell sell = new Sell(date, ticker, quantity, API.getPrice());
                    portfolio.remove(i); // Removes ownership of stock from portfolio since they sold them all
                    stocksInPortfolio--;
                    balance = balance + sell.costOfTransaction();
                    transactions.add(sell);
                    numTransactions++;
                    return true;
                }
                return false;
            }
        }

        return false;
    }

    // Gets the index of the specified ticker if it exists. Returns -1 otherwise
    public int getTickerIndex(String ticker) {
        for (int i = 0; i < stocksInPortfolio; i++) {
            if (portfolio.get(i).getTicker().equals(ticker)) {
                return i;
            }
        }
        return -1;
    }

    // Prints transaction history
    public void printTransactionHistory() {
        if (numTransactions == 0) {
            System.out.println("You have not made any transactions.");
            return;
        }
        for (int i = 0; i < numTransactions; i++) {
            System.out.println(transactions.get(i));
            System.out.println();
        }
    }

    /**
     * Returns a double which represents the net worth of the investor. Is
     * calculated by finding value of all stocks and adding balance onto that.
     */
    public double getNetWorth() {
        double netWorth = 0;

        for (int i = 0; i < stocksInPortfolio; i++) {
            String ticker = portfolio.get(i).getTicker();
            int quantity = portfolio.get(i).getQuantity();
            API.setSymbol(ticker);
            netWorth += quantity * API.getPrice();
        }

        netWorth += balance;

        return netWorth;
    }

    // Sells every stock the user owns. Credits the balance to the account
    // accordingly.
    public boolean sellAll() {
        for (int i = 0; i < stocksInPortfolio; i++) {
            String ticker = portfolio.get(i).getTicker();
            int quantity = portfolio.get(i).getQuantity();
            sellStock(ticker, quantity);
        }
        stocksInPortfolio = 0;
        return true;
    }

    // Adds balance to the investor's account
    public boolean deposit(double balance) {
        this.balance += balance;
        totalAmountAdded += balance;
        return true;
    }

    // Returns the lifetime profit of the user
    public double calculateProfit() {
        return getNetWorth() - totalAmountAdded;
    }

    // Subtracts balance from the user
    public boolean withdraw(double balance) {
        if (balance > this.balance) {
            return false;
        } else {
            this.balance -= balance;
            totalAmountAdded -= balance;
            return true;
        }
    }

    /**
     * Sorts portfolio by quantity owned. Most owned stocks will be at the front of
     * the array after the sort. Uses insertion sort.
     */
    public boolean sortPortfolio() {
        for (int i = 1; i < portfolio.size(); i++) {
            int kickedIndex = i;
            for (int j = i - 1; i >= 0; i--) {
                if (portfolio.get(i).compareQuantity(portfolio.get(j)) > 0) {
                    kickedIndex = j;
                } else {
                    break;
                }
            }
            OwnedStock temp = portfolio.get(i);
            portfolio.set(i, portfolio.get(kickedIndex));
            portfolio.set(kickedIndex, temp);
        }
        return true;
    }

    // Returns all information of the investor in a clean and organized manner
    public String toString() {
        String output = "balance: " + balance + "\n";
        output += "balance Spent: " + totalAmountSpent + "\n";
        output += "balance Added: " + totalAmountAdded + "\n";
        output += "Stocks In Portfolio: " + stocksInPortfolio + "\n";
        output += "Lifetime Profit: " + calculateProfit();
        return output;
    }

    /**
     * Generates and returns a string describing everything about the investor
     */
    public abstract void save();
}
