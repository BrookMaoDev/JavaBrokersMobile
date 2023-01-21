package JBKRMobile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Login
 * Owen Wang
 * Last modified: Jan 20, 2023
 * Manages account file IO.
 */
public class Login {
    private static final int KEY = 5; // key that encrypts the password

    /**
    @param username: username the user is attempting to sign in with
    @param password: password the user is attempting to sign in with
    @return an investor object with all the information about them loaded in
     */
    public static Investor login(String username, String password) {
        try {
            // Searches for username & password combination
            BufferedReader br = new BufferedReader(new FileReader(username + ".db"));
            ArrayList<OwnedStock> portfolio = new ArrayList<OwnedStock>();
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();

            if (br.readLine().equals(encryptPassword(password))) {
                String investorType = br.readLine();
                double balance = Double.parseDouble(br.readLine());
                double totalFundsAdded = Double.parseDouble(br.readLine());

                // get owned stocks
                int numStocks = Integer.parseInt(br.readLine());
                for (int i = 0; i < numStocks; i++) {
                    // ticker, quantity
                    portfolio.add(new OwnedStock(br.readLine(), Integer.parseInt(br.readLine())));
                }

                // get transactions
                int numTransactions = Integer.parseInt(br.readLine());
                for (int i = 0; i < numTransactions; i++) {
                    if (br.readLine().equalsIgnoreCase("Buy")) {
                        // date, ticker, quantity, price
                        transactions.add(new Transaction("Buy", br.readLine(), br.readLine(),
                                Integer.parseInt(br.readLine()),
                                Double.parseDouble(br.readLine())));
                    } else {
                        transactions.add(new Transaction("Sell", br.readLine(), br.readLine(),
                                Integer.parseInt(br.readLine()),
                                Double.parseDouble(br.readLine())));
                    }
                }
                br.close();

                // Adult: 1. Child: anything else
                if (investorType.equalsIgnoreCase("Adult")) {
                    return new Adult(username, password, balance, totalFundsAdded, portfolio, transactions);
                } else {
                    return new Child(username, password, balance, totalFundsAdded, portfolio, transactions);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
    @param username: the username the user wants to sign up with
    @param password: the password the user wants to sign up with
    @param accountType: the account type (either "adult" or "child") the user wants to sign up with
    @return an investor object with the given username, password, and accountType with everything else set to the defaults
     */
    public static Investor createUser(String username, String password, String accountType) {
        try {
            // Write username and encrypted password to file
            BufferedReader checkUsername = new BufferedReader(new FileReader(username + ".db"));
            checkUsername.close();
        } catch (IOException e) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(username + ".db", true));
                bw.write(encryptPassword(password) + "\n");
                bw.write(accountType + "\n");
                if (accountType.equalsIgnoreCase("Adult")) {
                    bw.write("0.00\n"); // balance
                    bw.write("0.00\n"); // funds added
                    bw.write("0\n");
                    bw.write("0\n");
                    bw.close();
                    return new Adult(username, password);
                } else {
                    bw.write(Child.getStartingBalance() + "\n");
                    bw.write(Child.getStartingBalance() + "\n");
                    bw.write("0\n");
                    bw.write("0\n");
                    bw.close();
                    return new Child(username, password);
                }
            } catch (IOException i) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
    @param username: the username being checked
    @return returns true if the username is already being used
            returns false if the username is not in use already
     */
    public static boolean checkUsername(String username) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(username + ".db"));
            br.close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    /**
    @param password: the actual password of the user
    @return the encrypted password of the user
     */
    public static String encryptPassword(String password) {
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] += KEY;
        }
        return String.valueOf(chars);
    }
}
