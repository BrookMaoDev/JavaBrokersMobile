package JBKRMobile;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

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
    private static final StockData STOCK_DATA = new StockData();
    private static final String DEFAULT_DATA_SETTING = "most-active";
    WindowBasedTextGUI textGUI;
    private boolean loggedIn;
    private Table<String> table;
    private String dataSetting;
    private String username;
    private String password;
    private String accountType;
    private Button home;
    private Button search;
    private Button login;
    private Button signup;
    private Button logout;
    private Button portfolio;
    private Investor user;
    private Button deposit;
    private static int maxQuery = 50;

    public JBKRMobile() {
        dataSetting = DEFAULT_DATA_SETTING;
        loggedIn = false;
    }

    /**
     * Data types: "most-active", "gainers", "losers", "crypto"
     */
    public void setDataSetting(String dataSetting) {
        this.dataSetting = dataSetting;
    }

    // scrape data from site
    public Table<String> generateData() {
        Table<String> table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE");
        ArrayList<String> data = STOCK_DATA.getData(dataSetting);
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

    private void buyStockWindow(Table<String> table) {
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
                        } else {
                            MessageDialog.showMessageDialog(textGUI, "Buy stock",
                                    "Insufficient funds.");
                        }
                    } catch (NumberFormatException e) {
                        MessageDialog.showMessageDialog(textGUI, "Sell stock", "Invalid entry.");
                    }
            }
        }
    }

    private void sellStockWindow(Table<String> table) {
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
                        } else {
                            MessageDialog.showMessageDialog(textGUI, "Sell stock",
                                    "Entered value exceeds owned volume.");
                        }
                    } catch (NumberFormatException e) {
                        MessageDialog.showMessageDialog(textGUI, "Sell stock", "Invalid entry.");
                    }
            }
        }
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
            Panel mainPanel = new Panel();
            mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
            textGUI = new MultiWindowTextGUI(screen);

            // Create panel for tickers
            Panel tickerPanel = new Panel();
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
            mainPanel.addComponent(tickerPanel.withBorder(Borders.singleLine(dataSetting)));

            Panel sidePanel = new Panel();

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
                                                sidePanel.removeAllComponents();
                                                sidePanel.addComponent(home);
                                                sidePanel.addComponent(search);
                                                sidePanel.addComponent(portfolio);
                                                sidePanel.addComponent(deposit);
                                                sidePanel.addComponent(logout);
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
                                                        sidePanel.removeAllComponents();
                                                        sidePanel.addComponent(home);
                                                        sidePanel.addComponent(search);
                                                        sidePanel.addComponent(portfolio);
                                                        sidePanel.addComponent(deposit);
                                                        sidePanel.addComponent(logout);
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
                    tickerPanel.removeAllComponents();
                    table.setSelectAction(new Runnable() {
                        @Override
                        public void run() {
                            new ActionListDialogBuilder()
                                    .setTitle("Portfolio").setDescription("Select action:")
                                    .addAction("Buy", new Runnable() {
                                        @Override
                                        public void run() {
                                            buyStockWindow(table);
                                        }
                                    })
                                    .addAction("Sell", new Runnable() {
                                        @Override
                                        public void run() {
                                            sellStockWindow(table);
                                        }
                                    }).build().showDialog(textGUI);
                        }
                    });
                    tickerPanel.addComponent(table);
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
                                    user.addMoney(Double.parseDouble(depositAmount));
                                    user.save();
                                    MessageDialog.showMessageDialog(textGUI, "Deposit",
                                            String.format("$%.2f added to wallet.", Double.parseDouble(depositAmount)));
                                } catch (NumberFormatException e) {
                                    MessageDialog.showMessageDialog(textGUI, "Deposit", "Invalid entry");
                                }
                        }
                    }
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