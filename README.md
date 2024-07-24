# JBKR Mobile

JBKR Mobile (Java Brokers Mobile) is a stock trading simulator inspired by Hollywood movies. You know those scenes where stock data in white text is displayed on several black monitors for stock brokers to ignore? 📉 We've added a twist! Now, stock brokers can ignore colorful monitors instead of boring black-and-white ones. 🌈

![JBKR Mobile](https://github.com/wang-owen/JBKR-Mobile/assets/69203168/9bc91515-cfc4-459c-a814-54f108e8fa7a)

### Check out our software planning UML diagram on [Lucidchart](https://lucid.app/documents/view/7044a729-50ac-48f7-a6cd-f85f6570feef).

## Technologies Used

-   **Java** - The core programming language for the app.
-   [**Yahoo Finance API**](https://github.com/sstrickx/yahoofinance-api) - For pulling real-time stock data from the NYSE and NASDAQ.
-   [**Lanterna**](https://github.com/mabe02/lanterna) - For the Text User Interface (TUI) in the desktop version.
-   [**LucidChart**](https://lucid.app) - For UML diagramming.

## Features

### 📈 Stock Data

-   Stock data is pulled in real-time through the Yahoo Finance API from the NYSE and NASDAQ.
-   Stocks can be searched by their ticker name, with the most relevant results being displayed.

### 😃 Users

-   Users can create profiles with a username and password (encrypted) to save their purchases and balance. User sessions can be managed by logging in and out without resetting upon app restart.
-   A searching algorithm verifies credentials with the database when a user logs in.
-   Users can create either adult or child profiles, with child profiles being restricted to a certain balance and stock purchases.
-   Users can view their portfolio, showing owned stocks, net worth, and net profit gained since the creation of their account.
-   Stocks can be modified directly within the portfolio (e.g., purchase more, sell stocks).
-   Stocks in the portfolio can be sorted alphabetically, by quantity, or by price using insertion sort.

### 💸 Transactions

-   Users may deposit and withdraw balance to and from their account, which can be used to purchase stocks.
-   Stocks listed on the NYSE and NASDAQ are available for purchase, with the user specifying the amount they wish to buy.
-   Users can activate a "buy max" function on their owned stocks, which recursively purchases quantities of their owned stocks from most expensive to least until they are no longer able to do so.
