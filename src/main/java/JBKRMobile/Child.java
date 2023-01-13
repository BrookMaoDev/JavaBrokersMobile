package JBKRMobile;

import java.util.ArrayList;

public class Child extends Investor {
  // Spend limit for all child investors
  private final static double TRANSACTION_SPEND_LIMIT = 500;

  public Child(String firstName, String lastName, String username, String password) {
    super(firstName, lastName, username, password);
  }

  // Attempts to buy stock
  public boolean buyStock(String ticker, int quantity) {
    api.setSymbol(ticker);
    double price = api.getPrice();

    // Check if the transaction exceeds the spending limit
    if (price * quantity > TRANSACTION_SPEND_LIMIT) {
      return false;
    }

    // Check if the user has enough money
    Buy purchase = new Buy(date, ticker, quantity, price);

    if (money < purchase.costOfTransaction()) {
      return false;
    }
    money -= purchase.costOfTransaction();

    transactions.add(purchase);

    int tickerIndex = getTickerIndex(ticker);

    // The user does not own this stock yet
    if (tickerIndex < 0) {
      OwnedStock boughtStock = new OwnedStock(ticker, quantity);
      portfolio.add(boughtStock);
    } else {
      // The user owns this stock
      portfolio.get(tickerIndex).addQuantity(quantity);
    }
    return true;
  }

  public boolean buyMax(ArrayList<String> tickers, double money) {
    return false;
  }
}