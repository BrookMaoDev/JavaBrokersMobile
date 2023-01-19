package JBKRMobile;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import java.text.NumberFormat;

import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;

public class JBKRMobile {
    private static final String DEFAULT_DATA_SETTING = "most-active";
    WindowBasedTextGUI textGUI;
    Panel mainPanel;
    Panel tickerPanel;
    Panel sidePanel;
    private boolean loggedIn;
    private Table<String> table;
    private String username;
    private String password;
    private String accountType;
    private Label balance;
    private Button home;
    private Button search;
    private Button login;
    private Button signup;
    private Button logout;
    private Button portfolio;
    private Investor user;
    private Button deposit;
    private Button withdraw;
    private Button buyMax;
    private Button sellAll;
    private Button additionalInfo;
    private Button transactionHistory;
    private static int maxQuery = 50;

    public JBKRMobile() {
        loggedIn = false;
    }

    // scrape data from site
    public Table<String> generateData() {
        Table<String> table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE");
        ArrayList<String> data = StockData.getData(DEFAULT_DATA_SETTING);
        int c = maxQuery;
        for (int i = 0; i < c; i++) {
            try {
                API.setSymbol(data.get(i));
                table.getTableModel().addRow(API.getSymbol() + "", API.getPrice() + "",
                        API.getChange() + "",
                        API.getPercentChange() + "%");
            } catch (Exception e) {
                c++;
            }
        }
        return table;
    }

    private void updateSidebar() {
        sidePanel.removeAllComponents();
        balance = new Label(
                NumberFormat.getCurrencyInstance().format(user.getFunds()));
        sidePanel.addComponent(balance);
        sidePanel.addComponent(home);
        sidePanel.addComponent(search);
        sidePanel.addComponent(portfolio);
        sidePanel.addComponent(buyMax);
        sidePanel.addComponent(sellAll);
        sidePanel.addComponent(additionalInfo);
        sidePanel.addComponent(transactionHistory);
        sidePanel.addComponent(deposit);
        sidePanel.addComponent(withdraw);
        sidePanel.addComponent(logout);
    }

    private boolean buyStockWindow(Table<String> table) {
        String ticker = table.getTableModel()
                .getRow(table.getSelectedRow()).get(0);
        String quantity = new TextInputDialogBuilder()
                .setTitle(ticker)
                .setDescription(
                        "Enter desired quantity of "
                                + ticker + ":")
                .build().showDialog(textGUI);
        if (quantity != null) {
            switch (quantity) {
                case "":
                    MessageDialog.showMessageDialog(textGUI, "Buy stock", "Invalid input.");
                    break;

                default:
                    try {
                        if (user.buyStock(ticker, Integer.parseInt(quantity))) {
                            user.save();
                            updateSidebar();
                            return true;
                        } else {
                            MessageDialog.showMessageDialog(textGUI, "Buy stock",
                                    "Insufficient funds.");
                        }
                    } catch (NumberFormatException e) {
                        MessageDialog.showMessageDialog(textGUI, "Buy stock", "Invalid entry.");
                    } catch (Exception e) {
                        MessageDialog.showMessageDialog(textGUI, "Buy stock", e + "");
                    }
            }
        }
        return false;
    }

    private boolean sellStockWindow(Table<String> table) {
        String ticker = table.getTableModel()
                .getRow(table.getSelectedRow()).get(0);
        String quantity = new TextInputDialogBuilder()
                .setTitle(ticker)
                .setDescription(
                        "Enter quantity of "
                                + ticker + " to sell:")
                .build().showDialog(textGUI);
        if (quantity != null) {
            switch (quantity) {
                case "":
                    MessageDialog.showMessageDialog(textGUI, "Sell stock", "Invalid input.");
                    break;

                default:
                    try {
                        if (user.sellStock(ticker, Integer.parseInt(quantity))) {
                            user.save();
                            updateSidebar();
                            return true;
                        } else {
                            MessageDialog.showMessageDialog(textGUI, "Sell stock",
                                    "Entered value exceeds owned volume.");
                        }
                    } catch (Exception e) {
                        MessageDialog.showMessageDialog(textGUI, "Sell stock", "Invalid entry.");
                    }
            }
        }
        return false;
    }

    private void portfolioTable() {
        table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE", "QUANTITY");
        ArrayList<OwnedStock> data = user.getPortfolio();
        try {
            for (int i = 0; i < data.size(); i++) {
                API.setSymbol(data.get(i).getTicker());
                table.getTableModel().addRow(API.getSymbol() + "", API.getPrice() + "",
                        API.getChange() + "", API.getPercentChange() + "%", data.get(i).getQuantity() + "");
            }
        } catch (Exception e) {
        }
        table.setSelectAction(new Runnable() {
            @Override
            public void run() {
                new ActionListDialogBuilder()
                        .setTitle("Portfolio").setDescription("Select action:")
                        .addAction("Buy", new Runnable() {
                            @Override
                            public void run() {
                                buyStockWindow(table);
                                portfolioTable();
                            }
                        })
                        .addAction("Sell", new Runnable() {
                            @Override
                            public void run() {
                                sellStockWindow(table);
                                portfolioTable();
                            }
                        }).build().showDialog(textGUI);
            }
        });
        tickerPanel.removeAllComponents();
        tickerPanel.addComponent(table);
    }

    public void run() {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory().setTerminalEmulatorFontConfiguration(
                new SwingTerminalFontConfiguration(true, null, AWTTerminalFontConfiguration
                        .filterMonospaced(new Font("Consolas", Font.PLAIN, 14), new Font("Monaco", Font.PLAIN, 14))));
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
            Window window = new BasicWindow("JBKR Mobile");
            mainPanel = new Panel();
            mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
            textGUI = new MultiWindowTextGUI(screen);

            // Create panel for tickers
            tickerPanel = new Panel();
            tickerPanel.setFillColorOverride(ANSI.WHITE);

            table = generateData();
            table.setSelectAction(new Runnable() {
                @Override
                public void run() {
                    if (loggedIn) {
                        buyStockWindow(table);
                    }
                }
            });
            tickerPanel.addComponent(table);
            mainPanel.addComponent(tickerPanel.withBorder(Borders.singleLine(DEFAULT_DATA_SETTING)));

            sidePanel = new Panel();

            home = new Button("Home", new Runnable() {
                @Override
                public void run() {
                    tickerPanel.removeAllComponents();
                    table = generateData();
                    table.setSelectAction(new Runnable() {
                        @Override
                        public void run() {
                            if (loggedIn) {
                                buyStockWindow(table);
                            }
                        }
                    });
                    tickerPanel.addComponent(table);
                }
            });
            sidePanel.addComponent(home);

            search = new Button("Search", new Runnable() {
                @Override
                public void run() {
                    String query = new TextInputDialogBuilder().setTitle("Search").setDescription("Enter ticker:")
                            .build().showDialog(textGUI);
                    if (query != null) {
                        switch (query) {
                            case "":
                                MessageDialog.showMessageDialog(textGUI, "Search", "Empty query.");
                                break;

                            default:
                                table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE");
                                try {
                                    API.setSymbol(query.toUpperCase());
                                    table.getTableModel().addRow(API.getSymbol() + "", API.getPrice() + "",
                                            API.getChange() + "",
                                            API.getPercentChange() + "%");
                                } catch (Exception e) {
                                }
                                tickerPanel.removeAllComponents();
                                table.setSelectAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (loggedIn) {
                                            buyStockWindow(table);
                                        }
                                    }
                                });
                                tickerPanel.addComponent(table);
                        }
                    }
                }
            });
            sidePanel.addComponent(search);

            // Log out button
            logout = new Button("Log out", new Runnable() {
                @Override
                public void run() {
                    loggedIn = false;
                    tickerPanel.removeAllComponents();
                    table = generateData();
                    tickerPanel.addComponent(table);
                    sidePanel.removeAllComponents();
                    sidePanel.addComponent(home);
                    sidePanel.addComponent(search);
                    sidePanel.addComponent(login);
                    sidePanel.addComponent(signup);
                }
            });

            // Log In button
            login = new Button("Log in", new Runnable() {
                @Override
                public void run() {
                    username = new TextInputDialogBuilder().setTitle("Log in").setDescription("Enter username:")
                            .build().showDialog(textGUI);

                    if (username != null) {
                        switch (username) {
                            case "":
                                MessageDialog.showMessageDialog(textGUI, "Log in", "Invalid username.");
                                break;

                            default:
                                password = new TextInputDialogBuilder().setTitle("Log in")
                                        .setDescription("Enter password:")
                                        .setPasswordInput(true).build().showDialog(textGUI);
                                if (password != null) {
                                    switch (password) {
                                        case "":
                                            MessageDialog.showMessageDialog(textGUI, "Log in", "Invalid password.");
                                            break;

                                        default:
                                            user = Login.login(username, password);
                                            if (user != null) {
                                                loggedIn = true;
                                                updateSidebar();
                                            } else {
                                                MessageDialog.showMessageDialog(textGUI, "Log in",
                                                        "User does not exist.");
                                            }
                                    }
                                }
                        }
                    }
                }
            });
            sidePanel.addComponent(login);

            signup = new Button("Sign up", new Runnable() {
                @Override
                public void run() {
                    username = new TextInputDialogBuilder().setTitle("Sign up").setDescription("Enter username:")
                            .build().showDialog(textGUI);

                    if (username != null) {
                        switch (username) {
                            case "":
                                MessageDialog.showMessageDialog(textGUI, "Sign up", "Invalid username.");
                                break;

                            default:
                                password = new TextInputDialogBuilder().setTitle("Sign up")
                                        .setDescription("Enter password:")
                                        .setPasswordInput(true).build().showDialog(textGUI);

                                if (password != null) {
                                    switch (password) {
                                        case "":
                                            MessageDialog.showMessageDialog(textGUI, "Sign up", "Invalid password.");
                                            break;

                                        default:
                                            if (Login.checkUsername(username)) {
                                                accountType = null;
                                                new ActionListDialogBuilder()
                                                        .setTitle("Sign up").setDescription("Select account type:")
                                                        .addAction("Adult", new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                accountType = "adult";
                                                            }
                                                        })
                                                        .addAction("Child", new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                accountType = "child";
                                                            }
                                                        }).build().showDialog(textGUI);
                                                if (accountType != null) {
                                                    user = Login.createUser(username, password, accountType);
                                                    if (user != null) {
                                                        loggedIn = true;
                                                        MessageDialog.showMessageDialog(textGUI, "Sign up",
                                                                "Account created.");
                                                        updateSidebar();
                                                    }
                                                }
                                            } else {
                                                MessageDialog.showMessageDialog(textGUI, "Sign up",
                                                        "Username unavailable.");
                                            }
                                    }
                                }
                        }
                    }
                }
            });
            sidePanel.addComponent(signup);

            portfolio = new Button("Portfolio", new Runnable() {
                @Override
                public void run() {
                    // Retrieve saved tickers
                    updateSidebar();
                    portfolioTable();
                }
            });

            deposit = new Button("Deposit", new Runnable() {
                @Override
                public void run() {
                    String depositAmount = new TextInputDialogBuilder().setTitle("Deposit")
                            .setDescription("Enter deposit amount:")
                            .build().showDialog(textGUI);
                    if (depositAmount != null) {
                        switch (depositAmount) {
                            case "":
                                MessageDialog.showMessageDialog(textGUI, "Deposit", "Invalid entry.");
                                break;

                            default:
                                try {
                                    user.deposit(Double.parseDouble(depositAmount));
                                    user.save();
                                    updateSidebar();
                                    MessageDialog.showMessageDialog(textGUI, "Deposit",
                                            String.format("%s added to balance.",
                                                    NumberFormat.getCurrencyInstance()
                                                            .format(Double.parseDouble(depositAmount))));
                                } catch (NumberFormatException e) {
                                    MessageDialog.showMessageDialog(textGUI, "Deposit", "Invalid entry");
                                }
                        }
                    }
                }
            });

            withdraw = new Button("Withdraw", new Runnable() {
                @Override
                public void run() {
                    String withdrawAmount = new TextInputDialogBuilder().setTitle("Withdraw")
                            .setDescription("Enter withdraw amount:")
                            .build().showDialog(textGUI);
                    if (withdrawAmount != null) {
                        switch (withdrawAmount) {
                            case "":
                                MessageDialog.showMessageDialog(textGUI, "Withdraw", "Invalid entry.");
                                break;

                            default:
                                try {
                                    if (user.withdraw(Double.parseDouble(withdrawAmount))) {
                                        user.save();
                                        updateSidebar();
                                        MessageDialog.showMessageDialog(textGUI, "Withdraw",
                                                String.format("%s withdrew from balance.",
                                                        NumberFormat.getCurrencyInstance()
                                                                .format(Double.parseDouble(withdrawAmount))));
                                    } else {
                                        MessageDialog.showMessageDialog(textGUI, "Withdraw",
                                                "Withdraw amount exceeds balance.");
                                    }
                                } catch (NumberFormatException e) {
                                    MessageDialog.showMessageDialog(textGUI, "Withdraw", "Invalid entry");
                                }
                        }
                    }
                }
            });

            buyMax = new Button("Buy max", new Runnable() {
                @Override
                public void run() {
                    new ActionListDialogBuilder()
                            .setTitle("Buy max")
                            .setDescription(
                                    "This will spend as much money as possible on the stocks in your portfolio.\n\nProceed?")
                            .addAction("Yes", new Runnable() {
                                @Override
                                public void run() {
                                    MessageDialog.showMessageDialog(textGUI, "Buy max",
                                            user.buyMax(user.getTickersOfPortfolio(), user.getFunds()));
                                    portfolioTable();
                                }
                            })
                            .build().showDialog(textGUI);
                }
            });

            additionalInfo = new Button("Additional info", new Runnable() {
                @Override
                public void run() {
                    MessageDialog.showMessageDialog(textGUI, "Additional info",
                            String.format(
                                    "Total funds spent: %s\nTotal funds added: %s\nNet worth: %s\nTotal profit: %s",
                                    NumberFormat.getCurrencyInstance().format(user.getSpentFunds()),
                                    NumberFormat.getCurrencyInstance().format(user.getAddedFunds()),
                                    NumberFormat.getCurrencyInstance().format(user.getNetWorth()),
                                    NumberFormat.getCurrencyInstance().format(user.calculateProfit())));
                }
            });

            sellAll = new Button("Sell all", new Runnable() {
                @Override
                public void run() {
                    new ActionListDialogBuilder()
                            .setTitle("Sell all")
                            .setDescription(
                                    "This will sell all your stocks.\n\nProceed?")
                            .addAction("Yes", new Runnable() {
                                @Override
                                public void run() {
                                    user.sellAll();
                                    portfolioTable();
                                }
                            })
                            .build().showDialog(textGUI);
                }
            });

            transactionHistory = new Button("Transaction history", new Runnable() {
                @Override
                public void run() {
                    MessageDialog.showMessageDialog(textGUI, "Transaction history", user.getTransactionHistory());
                }
            });

            mainPanel.addComponent(sidePanel);
            window.setHints(Arrays.asList(Window.Hint.CENTERED));
            window.setComponent(mainPanel);

            // Create gui and start gui
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                    new EmptySpace(ANSI.BLACK));
            gui.addWindowAndWait(window);
        } catch (IOException e) {
            System.exit(0);
        }
    }
}