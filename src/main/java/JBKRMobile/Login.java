package JBKRMobile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Login {
    private static final int KEY = 5;
    private static final String DB_PATH = "src/main/java/JBKRMobile/database.db";

    public Login() {
    }

    public Investor login(String username, String password) {
        Investor user = null;
        try {
            // Searches for username & password combination
            BufferedReader reader = new BufferedReader(new FileReader(DB_PATH));
            boolean foundUser = false;
            String line, date, ticker = "";
            double money, spentMoney, addedMoney, price;
            int numTransactions, stocksInPortfolio, quantity;
            int investorType, transactionType; // int to support additional types
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            ArrayList<OwnedStock> portfolio = new ArrayList<OwnedStock>();
            String encryptedPassword = encryptPassword(password);
            while ((line = reader.readLine()) != null && !foundUser) {
                if (line.equals(username) && reader.readLine().equals(encryptedPassword)) {
                    line = reader.readLine();
                    if (line.equalsIgnoreCase("adult")) {
                        investorType = 1;
                    } else {
                        investorType = 0; // Assume that it is either "adult" or "child"
                    }
                    money = Double.parseDouble(reader.readLine());
                    spentMoney = Double.parseDouble(reader.readLine());
                    addedMoney = Double.parseDouble(reader.readLine());
                    numTransactions = Integer.parseInt(reader.readLine());
                    
                    //Add all transactions to the transactions list
                    for (int i = 0; i < numTransactions; i++) {
                        if (reader.readLine().equalsIgnoreCase("buy")) {
                            transactionType = 0; // buy is 0
                        } else {
                            transactionType = 1; // sell is 1
                        }
                        date = reader.readLine();
                        ticker = reader.readLine();
                        quantity = Integer.parseInt(reader.readLine());
                        price = Double.parseDouble(reader.readLine());

                        switch (transactionType) {
                            //Buy
                            case 0: transactions.add(new Buy(date, ticker, quantity, price));
                            break;
                            //Sell
                            default: transactions.add(new Sell(date, ticker, quantity, price));
                        }
                    }

                    stocksInPortfolio = Integer.parseInt(reader.readLine());

                    for (int i = 0; i < stocksInPortfolio; i++) {
                        ticker = reader.readLine();
                        quantity = Integer.parseInt(reader.readLine());

                        portfolio.add(new OwnedStock(ticker, quantity));
                    }
                    foundUser = true;

                    switch (investorType) {
                        //Child
                        case 0: user = new Child(username, password, money, spentMoney, addedMoney, numTransactions, transactions, stocksInPortfolio, portfolio);
                        break;
                        //Adult
                        default: user = new Adult(username, password, money, spentMoney, addedMoney, numTransactions, transactions, stocksInPortfolio, portfolio);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {

        }
        return user;
    }


    public boolean createUser(String username, String password) {
        try {
            // Determine if username is unique
            BufferedReader br = new BufferedReader(new FileReader(DB_PATH));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.equals(username)) {
                    br.close();
                    return false;
                }
                br.readLine();
            }
            br.close();

            // Write username and encrypted password to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH, true));
            bw.write(username + "\n");
            bw.write(encryptPassword(password) + "\n");
            bw.close();
            return true;
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }

    private String encryptPassword(String password) {
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] += KEY;
        }
        return String.valueOf(chars);
    }
}
