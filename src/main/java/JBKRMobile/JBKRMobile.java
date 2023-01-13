package JBKRMobile;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

public class JBKRMobile {
    private static final StockData STOCK_DATA = new StockData();
    private static final String DEFAULT_DATA_SETTING = "most-active";
    private API api;
    private String dataSetting;
    private String username;
    private String password;
    private Button home;
    private Button search;
    private Button login;
    private Button signup;
    private Button logout;
    private static final String DB_PATH = "src/main/java/JBKRMobile/database.db";
    private static int maxQuery = 50;

    public JBKRMobile() {
        api = new API();
        dataSetting = DEFAULT_DATA_SETTING;
        username = "";
        password = "";
    }

    /**
     * Data types: "most-active", "gainers", "losers", "crypto"
     */
    public void setDataSetting(String dataSetting) {
        this.dataSetting = dataSetting;
    }

    public Table<String> generateData() {
        Table<String> table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE");
        ArrayList<String> data = STOCK_DATA.getData(dataSetting);
        int c = maxQuery;
        for (int i = 0; i < c; i++) {
            try {
                api.setSymbol(data.get(i));
                table.getTableModel().addRow(api.getSymbol() + "", api.getPrice() + "",
                        api.getChange() + "",
                        api.getPercentChange() + "%");
            } catch (Exception e) {
                c++;
            }
        }
        return table;
    }

    /**
     * Writes all information about the investor in a file, then writes all other information about every other
     * investor in the file, then saves it.
     */
    public void saveInvestor() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DB_PATH));
        } catch (IOException e) {

        }
    }

    public void run() {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory().setTerminalEmulatorFontConfiguration(
                new SwingTerminalFontConfiguration(true, null, new Font("Consolas", Font.PLAIN, 14)));
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
            Window window = new BasicWindow("JBKR Mobile");
            Panel mainPanel = new Panel();
            mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

            // Create panel for tickers
            Panel tickerPanel = new Panel();
            tickerPanel.setFillColorOverride(ANSI.WHITE);

            tickerPanel.addComponent(generateData());
            mainPanel.addComponent(tickerPanel.withBorder(Borders.singleLine(dataSetting)));

            Panel sidePanel = new Panel();

            home = new Button("Home", new Runnable() {
                @Override
                public void run() {
                    tickerPanel.removeAllComponents();
                    tickerPanel.addComponent(generateData());
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
                                Table<String> table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE");
                                try {
                                    api.setSymbol(query.toUpperCase());
                                    table.getTableModel().addRow(api.getSymbol() + "", api.getPrice() + "",
                                            api.getChange() + "",
                                            api.getPercentChange() + "%");
                                } catch (Exception e) {
                                }
                                tickerPanel.removeAllComponents();
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
                    sidePanel.removeAllComponents();
                    sidePanel.addComponent(home);
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
                                            if (new Login().login(username, password)) {
                                                MessageDialog.showMessageDialog(textGUI, "Log in",
                                                        "Successfully logged in.");
                                                sidePanel.removeAllComponents();
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
                                            if (new Login().createUser(username, password)) {
                                                MessageDialog.showMessageDialog(textGUI, "Sign up", "Account created.");
                                                sidePanel.removeAllComponents();
                                                sidePanel.addComponent(logout);
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

            mainPanel.addComponent(sidePanel);
            window.setHints(Arrays.asList(Window.Hint.CENTERED));
            window.setComponent(mainPanel);

            // Create gui and start gui
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                    new EmptySpace(ANSI.BLACK));
            gui.addWindowAndWait(window);
        } catch (

        IOException e) {
            System.out.println(e);
        }
    }
}