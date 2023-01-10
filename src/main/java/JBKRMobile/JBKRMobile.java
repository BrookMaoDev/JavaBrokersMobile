package JBKRMobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class JBKRMobile {
    private API api = new API();
    private static final StockData STOCK_DATA = new StockData();
    private String dataSetting;
    private static final String DEFAULT_DATA_SETTING = "most-active";
    private boolean loggedIn;

    public JBKRMobile() {
        dataSetting = DEFAULT_DATA_SETTING;
    }

    /**
     * Data types: "most-active", "gainers", "losers", "crypto"
     */
    public void setDataSetting(String dataSetting) {
        this.dataSetting = dataSetting;
    }

    public void run() {
        try {
            // Setup terminal/console and screen layers
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            // Create panels for each ticker
            Panel mainPanel = new Panel();
            mainPanel.setFillColorOverride(ANSI.WHITE);
            Table<String> table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE");
            // table.setPreferredSize(new TerminalSize(40, 10));

            ArrayList<String> data = STOCK_DATA.getData(dataSetting);
            int c = 50;
            for (int i = 0; i < c; i++) {
                try {
                    api.setSymbol(data.get(i));
                    table.getTableModel().addRow(api.getSymbol(), api.getPrice(), api.getChange(),
                            api.getPercentChange() + "%");
                } catch (Exception e) {
                    c++;
                }
            }
            mainPanel.addComponent(table);

            // test menu
            MenuBar menubar = new MenuBar();
            Menu menu = new Menu("Login");
            // add login here
            MenuItem username = new MenuItem("Username: ");
            MenuItem password = new MenuItem("Password; ");
            menu.add(username);
            menu.add(password);
            menubar.add(menu);
            mainPanel.addComponent(menubar);

            // Create window
            BasicWindow window = new BasicWindow("JBKR Mobile");
            window.setCloseWindowWithEscape(true);
            window.setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.CENTERED));
            window.setComponent(mainPanel.withBorder(Borders.singleLine(dataSetting)));

            // Create gui and start gui
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                    new EmptySpace(ANSI.BLACK));
            gui.addWindowAndWait(window);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
