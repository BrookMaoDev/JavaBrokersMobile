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
        try {
            // Searches for username & password combination
            BufferedReader br = new BufferedReader(new FileReader(DB_PATH + username + ".db"));
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            ArrayList<OwnedStock> portfolio = new ArrayList<OwnedStock>();

            if (br.readLine().trim().equals(encryptPassword(password))) {
                String investorType = br.readLine().trim();
                double balance = Double.parseDouble(br.readLine().trim());
                double totalFundsSpent = Double.parseDouble(br.readLine().trim());
                double totalFundsAdded = Double.parseDouble(br.readLine().trim());

                // get transactions
                int numTransactions = Integer.parseInt(br.readLine().trim());
                for (int i = 0; i < numTransactions; i++) {
                    if (br.readLine().trim().equalsIgnoreCase("buy")) {
                        // date, ticker, quantity, price
                        transactions.add(new Buy(br.readLine().trim(), br.readLine().trim(),
                                Integer.parseInt(br.readLine().trim()),
                                Double.parseDouble(br.readLine().trim())));
                    } else {
                        transactions.add(new Sell(br.readLine().trim(), br.readLine().trim(),
                                Integer.parseInt(br.readLine().trim()),
                                Double.parseDouble(br.readLine().trim())));
                    }
                }

                // get owned stocks
                int stocksInPortfolio = Integer.parseInt(br.readLine().trim());
                for (int i = 0; i < stocksInPortfolio; i++) {
                    // ticker, quantity
                    portfolio.add(new OwnedStock(br.readLine().trim(), Integer.parseInt(br.readLine().trim())));
                }

                br.close();

                // Adult: 1. Child: anything else
                if (investorType.equalsIgnoreCase("adult")) {
                    return new Adult(username, password, balance, totalFundsSpent, totalFundsAdded, numTransactions,
                            transactions, stocksInPortfolio, portfolio);
                } else {
                    return new Child(username, password, balance, totalFundsSpent, totalFundsAdded, numTransactions,
                            transactions, stocksInPortfolio, portfolio);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    public static Investor createUser(String username, String password, String accountType) {
        try {
            // Write username and encrypted password to file
            BufferedReader checkUsername = new BufferedReader(new FileReader(DB_PATH + username + ".db"));
            checkUsername.close();
        } catch (FileNotFoundException e) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH + username + ".db", true));
                bw.write(encryptPassword(password) + "\n");
                bw.write(accountType + "\n");
                bw.write("0.0\n"); // balance
                bw.write("0.0\n"); // amount added
                bw.write("0.0\n"); // amount spent
                bw.write("0\n"); // num transactions
                bw.write("0\n"); // num stocks owned
                bw.close();
                if (accountType.equalsIgnoreCase("adult")) {
                    return new Adult(username, password);
                } else {
                    return new Child(username, password);
                }
            } catch (IOException i) {
            }
        } catch (IOException e) {
        }
        return null;
    }

    // returns false if username taken, true otherwise
    public static boolean checkUsername(String username) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(DB_PATH + username + ".db"));
            br.close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public static String encryptPassword(String password) {
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] += KEY;
        }
        return String.valueOf(chars);
    }
}
