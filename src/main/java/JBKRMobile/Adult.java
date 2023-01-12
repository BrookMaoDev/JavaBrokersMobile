package JBKRMobile;

public class Adult extends Investor {
  public Adult(String firstName, String lastName, String username, String password) {
      super(firstName, lastName, username, password);
  }

  public boolean buyMax(ArrayList<String> tickers, double money) {
      ArrayList<String> bought = new ArrayList<String>;
      double moneySpent = 0;
      ArrayList<String> bestCombo = permute(tickers, money, bought, moneySpent);
      ArrayList<String> output = new ArrayList<String>;
      for (int i = 0; i < bestCombo.size(); i++) {
          int index = output.indexOf(bestCombo.get(i));
          if (index == -1) {
              output.add(bestCombo.get(i));
              output.add("1");
          } else {
              String prevQuantity = output.get(index + 1);
              String newQuantity = (Integer.parseInt(prevQuantity) + 1) + "";
              output.set(index + 1, newQuantity);
          }
      }
  }

  // Attempts to buy stock
  public boolean buyStock(String ticker, long quantity) {
    api.setSymbol(ticker);
    double price = Double.parseDouble(api.getPrice);

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
}