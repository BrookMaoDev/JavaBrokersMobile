package JBKRMobile;

import java.util.ArrayList;

abstract class Investor {
    protected static String date;
    protected String username;
    protected String password;
    protected double money;
    protected double spentMoney;
    protected double addedMoney;
    protected int numTransactions;
    protected int stocksInPortfolio;
    protected ArrayList<Transaction> transactions;
    protected ArrayList<OwnedStock> portfolio;

    // Creates investor objects for when they sign up
    public Investor(String username, String password) {
        this.username = username;
        this.password = password;
        this.money = 0;
        this.spentMoney = 0;
        this.addedMoney = 0;
        this.numTransactions = 0;
        this.stocksInPortfolio = 0;
        this.transactions = new ArrayList<Transaction>();
        this.portfolio = new ArrayList<OwnedStock>();
        date = java.time.LocalDate.now().toString();
    }

    // Creates investor objects for existing investors with previous data
    public Investor(String username, String password, double money, double spentMoney, double addedMoney, int numTransactions, ArrayList<Transaction> transactions, int stocksInPortfolio, ArrayList<OwnedStock> portfolio) {
        this.username = username;
        this.password = password;
        this.money = money;
        this.spentMoney = spentMoney;
        this.addedMoney = addedMoney;
        this.numTransactions = numTransactions;
        this.stocksInPortfolio = stocksInPortfolio;
        this.transactions = transactions;
        this.portfolio = portfolio;
        date = java.time.LocalDate.now().toString();
    }

    public double getMoney() {
        return money;
    }

    public double getSpentMoney() {
        return spentMoney;
    }

    public double getAddedMoney() {
        return addedMoney;
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
     * representing money the user wants to spend. The method return the
     * combination of stocks, from the list, that will spend as much money as
     * possible without going over the limit.
     */
    public abstract String buyMax(ArrayList<String> tickers, double money);

    // Helper method for the buyMax method. Uses recursion.
    protected ArrayList<String> permute(ArrayList<String> tickers, double money, ArrayList<String> bought) {
        ArrayList<String> bestCombo = bought;
        for (int i = 0; i < tickers.size(); i++) {
            ArrayList<String> newArray = bought;
            newArray.add(tickers.get(i));
            if (calcValueOfArray(newArray) < money) {
                API.setSymbol(tickers.get(i));
                ArrayList<String> potentialBestCombo = permute(tickers, money, newArray);
                if (calcValueOfArray(potentialBestCombo) > calcValueOfArray(bestCombo)) {
                    bestCombo = potentialBestCombo;
                }
            } else if (calcValueOfArray(newArray) == money) {
                return newArray;
            }
        }
        return bestCombo;
    }

    // Helper method for the buyMax method. Returns value of all stocks in a string array.
    protected double calcValueOfArray(ArrayList<String> array) {
        double value = 0;
        for (int i = 0; i < array.size(); i++) {
            API.setSymbol(array.get(i));
            value += API.getPrice();
        }
        return value;
    }

    /**
     * Method that takes in ticker symbol of a stock and the quantity. The
     * method will remove that quantity of stock from the investor's portfolio, and
     * add the money received. Creates a transaction object in the process.
     */
    public boolean sellStock(String ticker, int quantity) {
        for (int i = 0; i < stocksInPortfolio; i++) {
            if (portfolio.get(i).getTicker().equals(ticker)) {
                if (portfolio.get(i).getQuantity() > quantity) {
                    API.setSymbol(ticker);
                    Sell sell = new Sell(date, ticker, quantity, API.getPrice());
                    portfolio.get(i).subtractQuantity(quantity);
                    money = money + sell.costOfTransaction();
                    transactions.add(sell);
                    return true;
                } else if (portfolio.get(i).getQuantity() == quantity) {
                    API.setSymbol(ticker);
                    Sell sell = new Sell(date, ticker, quantity, API.getPrice());
                    portfolio.remove(i); // Removes ownership of stock from portfolio since they sold them all
                    money = money + sell.costOfTransaction();
                    transactions.add(sell);
                    return true;
                } else {
                    return false;
                }
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
     * calculated by finding value of all stocks and adding money onto that.
     */
    public double getNetWorth() {
        double netWorth = 0;

        for (int i = 0; i < stocksInPortfolio; i++) {
            String ticker = portfolio.get(i).getTicker();
            int quantity = portfolio.get(i).getQuantity();
            API.setSymbol(ticker);
            netWorth += quantity * API.getPrice();
        }

        netWorth += money;

        return netWorth;
    }

    // Sells every stock the user owns. Credits the money to the account accordingly.
    public boolean sellAll() {
        for (int i = 0; i < stocksInPortfolio; i++) {
            String ticker = portfolio.get(i).getTicker();
            int quantity = portfolio.get(i).getQuantity();
            sellStock(ticker, quantity);
        }
        return true;
    }

    // Adds money to the investor's account
    public boolean addMoney(double money) {
        this.money += money;
        addedMoney += money;
        return true;
    }

    // Returns the lifetime profit of the user
    public double calculateProfit() {
        return getNetWorth() - addedMoney;
    }

    // Subtracts money from the user
    public boolean withdrawMoney(double money) {
        if (money > this.money) {
            return false;
        } else {
            this.money -= money;
            addedMoney -= money;
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
        String output = "Money: " + money + "\n";
        output += "Money Spent: " + spentMoney + "\n";
        output += "Money Added: " + addedMoney + "\n";
        output += "Stocks In Portfolio: " + stocksInPortfolio + "\n";
        output += "Lifetime Profit: " + calculateProfit();
        return output;
    }

    /**
     * Generates and returns a string describing everything about the investor
     */
    public String fileString() {
        String output = "";
        output += password + "\n";
        if (this instanceof Adult) {
            output = "adult\n";
        } else {
            output = "child\n";
        }
        output += money + "\n";
        output += spentMoney + "\n";
        output += addedMoney + "\n";
        output += numTransactions + "\n";
        for (int i = 0; i < numTransactions; i++) {
            output += transactions.get(i).fileString() + "\n";

        }
        output += stocksInPortfolio + "\n";
        for (int i = 0; i < stocksInPortfolio; i++) {
            output += portfolio.get(i).fileString() + "\n";
        }
        return output;
    }
}