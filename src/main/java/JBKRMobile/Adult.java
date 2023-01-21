package JBKRMobile;

import java.util.ArrayList;

/**
 * Adult
 * Brook Mao, Wing Li, Owen Wang
 * Last modified: Jan 20, 2023
 * Represents an adult investor.
 */
public class Adult extends Investor {

    /**
     * @param username: the username the investor signed up with
     * @param password: the password the investor signed up with (unencrypted)
     * @return does not return anything.
     *         Creates an instance of Adult. This constructor is used when someone
     *         SIGNS UP.
     */
    public Adult(String username, String password) {
        super(username, password);
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
     *         Creates an instance of Adult. This constructor is used when someone
     *         LOGS IN.
     */
    public Adult(String username, String password, double balance, double totalFundsAdded,
            ArrayList<OwnedStock> portfolio, ArrayList<Transaction> transactions) {
        super(username, password, balance, totalFundsAdded, portfolio, transactions);
    }

    /**
     * @param balance: the amount of money the user wants to add to their account.
     * @return returns true if the deposit was successful.
     *         This method allows users to add money to their account.
     */
    public boolean deposit(double balance) {
        this.balance += balance;
        totalFundsAdded += balance;
        return true;
    }

    /**
     * @param balance: the amount of money the user wants to withdraw from their
     *                 account.
     * @return returns true if the withdraw was successful.
     *         returns false if the user tries to withdraw more than they have.
     *         This method allow users to take money out of their account.
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
}
