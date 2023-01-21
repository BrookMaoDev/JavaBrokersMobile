package JBKRMobile;

import java.util.ArrayList;

/**
 * Child
 * Brook Mao, Wing Li, Owen Wang
 * Last modified: Jan 20, 2023
 * Represents a child investor.
 */
public class Child extends Investor {
    // Spend limit for all child investors
    private final static double STARTING_BALANCE = 10000;

    /**
     * @param username: the username the investor signed up with
     * @param password: the password the investor signed up with (unencrypted)
     * @return does not return anything.
     *         Creates an instance of Child. This constructor is used when someone
     *         SIGNS UP.
     */
    public Child(String username, String password) {
        super(username, password, STARTING_BALANCE, STARTING_BALANCE, new ArrayList<OwnedStock>(),
                new ArrayList<Transaction>());
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
     *         Creates an instance of Child. This constructor is used when someone
     *         LOGS IN.
     */
    public Child(String username, String password, double balance, double totalFundsAdded,
            ArrayList<OwnedStock> portfolio, ArrayList<Transaction> transactions) {
        super(username, password, balance, totalFundsAdded, portfolio, transactions);
    }

    public static double getStartingBalance() {
        return STARTING_BALANCE;
    }

    public void resetAccount() {
        portfolio = new ArrayList<OwnedStock>();
        transactions = new ArrayList<Transaction>();
        balance = STARTING_BALANCE;
    }
}
