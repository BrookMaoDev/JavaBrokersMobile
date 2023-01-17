package JBKRMobile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Login {
    private static final int KEY = 5;
    private static final String DB_PATH = "src/main/java/JBKRMobile/Database/";

    public static Investor login(String username, String password) {
        Investor user = null;
        try {
            // Searches for username & password combination
            BufferedReader br = new BufferedReader(new FileReader(DB_PATH + username + ".db"));
            String encryptedPassword = encryptPassword(password);
            int investorType;
            double money;
            double spentMoney;
            double addedMoney;
            int numTransactions;
            int stocksInPortfolio;
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            ArrayList<OwnedStock> portfolio = new ArrayList<OwnedStock>();

            if (br.readLine().equals(encryptedPassword)) {
                if (br.readLine().equalsIgnoreCase("adult")) {
                    investorType = 1; // Adult is 1
                } else {
                    investorType = 0; // Assume that it is either "adult" or "child"
                }
                money = Double.parseDouble(br.readLine());
                spentMoney = Double.parseDouble(br.readLine());
                addedMoney = Double.parseDouble(br.readLine());
                numTransactions = Integer.parseInt(br.readLine());

                // get transactions
                for (int i = 0; i < numTransactions; i++) {
                    if (br.readLine().equalsIgnoreCase("buy")) {
                        // date, ticker, quantity, price
                        transactions.add(new Buy(br.readLine(), br.readLine(), Integer.parseInt(br.readLine()),
                                Double.parseDouble(br.readLine())));
                    } else {
                        transactions.add(new Sell(br.readLine(), br.readLine(), Integer.parseInt(br.readLine()),
                                Double.parseDouble(br.readLine())));
                    }
                }

                // get owned stocks
                stocksInPortfolio = Integer.parseInt(br.readLine());
                for (int i = 0; i < stocksInPortfolio; i++) {
                    // ticker, quantity
                    portfolio.add(new OwnedStock(br.readLine(), Integer.parseInt(br.readLine())));
                }

                // Adult: 1. Child: anything else
                if (investorType == 1) {
                    user = new Adult(username, password, money, spentMoney, addedMoney, numTransactions,
                            transactions, stocksInPortfolio, portfolio);
                } else {
                    user = new Child(username, password, money, spentMoney, addedMoney, numTransactions,
                            transactions, stocksInPortfolio, portfolio);
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("Failed to read information");
        }
        return user;
    }

    public static Investor createUser(String username, String password, String accountType) {
        Investor user = null;
        try {
            // Write username and encrypted password to file
            BufferedReader checkUsername = new BufferedReader(new FileReader(DB_PATH + username + ".db"));
            checkUsername.close();
        } catch (FileNotFoundException e) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH + username + ".db", true));
                bw.write(username + "\n");
                bw.write(encryptPassword(password) + "\n");
                bw.write(accountType + "\n");
                bw.close();
                if (accountType.equals("adult")) {
                    user = new Adult(username, password);
                } else {
                    user = new Child(username, password);
                }
            } catch (IOException i) {
                return user;
            }
        } catch (IOException e) {
            return user;
        }
        return user;
    }

    private static String encryptPassword(String password) {
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] += KEY;
        }
        return String.valueOf(chars);
    }
}
