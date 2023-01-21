/**
Class Name: Investor
Author: Brook Mao, Wing Li
Date: January 20, 2023
School: A Y Jackson Secondary School
Purpose: Represents an investor.
 */

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

    /**
    @param username: the username the investor signed up with
    @param password: the password the investor signed up with (unencrypted)
    @return does not return anything.
    Called from child classes only. This constructor is used when someone SIGNS UP.
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
    @param username: the username of the investor
    @param password: the password of the investor (unencrypted)
    @param balance: the money the investor has
    @param totalFundsAdded: how much the investor has added to the account
    @param portfolio: array of OwnedStock that the investor has purchased before
    @param transactions: array of transaction that the investor has made before
    @return does not return anything.
    Called from child classes only. This constructor is used when someone LOGS IN.
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
    @param balance: the amount of money the user wants to add to their account.
    @return returns true if the deposit was successful.
    This method allows users to add money to their account.
     */
    public boolean deposit(double balance) {
        this.balance += balance;
        totalFundsAdded += balance;
        return true;
    }

    /**
    @param balance: the amount of money the user wants to withdraw from their account.
    @return returns true if the withdraw was successful.
            returns false if the user tries to withdraw more than they have.
    This method allow users to take money out of their account.
     */
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
    @param ticker: the ticker of the stock the user wants to purchase
    @param quantity: the amount of the stock the user wants to purchase
    @return int
            1 means the stock was bought successfully
            2 means the user has insufficient funds
            3 (only applies if the investor is a child) means the child is exceeding their transaction spend limit
    Buys the specified quantity of the stock with the specified ticker.
    Updates the portfolio and balance accordingly.
     */
    public abstract int buyStock(String ticker, int quantity);

    /**
    @param ticker: the ticker of the stock the user wants to sell
    @param quantity: the ticker of the stock the user wants to sell
    @return boolean
            Returns true if the stocks were successfully sold
            Returns false if the user does not own the stock with the given ticker, or they do not own enough to sell the specified quantity
    Sells the specified quantity of the stock with the specified ticker.
    Updates the portfolio and balance accordingly.
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

    /**
    @return nothing
    Sells all stocks the user owns.
     */
    public void sellAll() {
        for (int i = portfolio.size(); i > 0; i--) {
            sellStock(portfolio.get(i).getTicker(), portfolio.get(i).getQuantity());
        }
    }

    /**
    @param tickers: an arrayList of ticker symbols that the user is okay with spending money on.
    @param balance: the amount of money the user is willing to spend. This method cannot spend more than this amount.
    @return String
            Returns a list of stocks bought in an organized fashion.
    This program will spend as much of the balance as possible on the specified list of tickers passed in.
     */
    public abstract String buyMax(ArrayList<String> tickers, double balance);

    /**
    @return double which represents the user's net worth
    Calculates the user's net worth. Adds up the value of all their OwnedStock and any leftover balance.
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
    @return double which represents the amount of profit the user had made since creating the account
    Returns the lifetime profit of the user.
     */
    public double calculateProfit() {
        return getNetWorth() - totalFundsAdded;
    }

    /**
    @param array: represents an array of tickers
    @return double representing the value of all the tickers added up
    Helper method that can calculate the value of an array of STRING tickers
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
    @return returns an arrayList of Strings which are the ticker symbols of all stocks in the portfolio.
     */
    public ArrayList<String> getTickersOfPortfolio() {
        ArrayList<String> tickers = new ArrayList<String>();
        for (int i = 0; i < portfolio.size(); i++) {
            tickers.add(portfolio.get(i).getTicker());
        }
        return tickers;
    }

    /**
    @param ticker: the ticker we want to find
    @return int - index of the OwnedStock with the ticker in the portfolio array
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
    @return String of all transactions in a clean and organized manner
     */
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
    @return true if the sort is successful
    Sorts the portfolio by quantity, with most owned at the front
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
    @return true if the sort is successful
    Sorts the portfolio by price, with most expensive at the front
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
    @param tickers: tickers the method is allowed to buy
    @param balance: the amount the method is allowed to spend
    @param bought: a list of already bought tickers
    @return an array of Strings which represent the combination of tickers that spends the most balance as possible
    Helper method for the buyMax method.
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
    Saves investor info to file
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

    /**
    Returns all information about the investor in a clean and organized manner
     */
    public String toString() {
        return String.format("Net worth: %s\nProfit: %s\n",
                NumberFormat.getCurrencyInstance().format(getNetWorth()),
                NumberFormat.getCurrencyInstance().format(calculateProfit()));
    }
}