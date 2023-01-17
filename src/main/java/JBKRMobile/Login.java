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
            BufferedReader br = new BufferedReader(new FileReader(username + ".db"));
            String encryptedPassword = encryptPassword(password);
            String line;
            int investorType;
            double money;
            double spentMoney;
            double addedMoney;
            int numTransactions;
            int stocksInPortfolio;
            ArrayList<Transaction> transactions = new ArrayList<Transaction>();
            ArrayList<OwnedStock> portfolio = new ArrayList<OwnedStock>();

            while ((line = br.readLine()) != null) {
                if (line.equals(encryptedPassword)) {
                    line = br.readLine();
                    if (line.equalsIgnoreCase("adult")) {
                        investorType = 1;
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
                        portfolio.add(new OwnedStock(br.readLine(), Integer.parseInt(br.readLine())));
                    }

                    if (investorType == 1) {
                        user = new Adult(username, password, money, spentMoney, addedMoney, numTransactions,
                                transactions, stocksInPortfolio, portfolio);
                    } else {
                        user = new Child(username, password, money, spentMoney, addedMoney, numTransactions,
                                transactions, stocksInPortfolio, portfolio);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            return user;
        }
        return null;
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
