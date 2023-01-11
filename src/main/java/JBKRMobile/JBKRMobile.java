package JBKRMobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

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
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();

            Window window = new BasicWindow("JBKR Mobile");


            // Create panel for tickers
            Panel tickerPanel = new Panel();
            tickerPanel.setFillColorOverride(ANSI.WHITE);

            Table<String> table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE");
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
            tickerPanel.addComponent(table);

            window.setHints(Arrays.asList(Window.Hint.CENTERED));
            window.setComponent(tickerPanel.withBorder(Borders.singleLine(dataSetting)));

            // Create gui and start gui
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                    new EmptySpace(ANSI.BLACK));
            gui.addWindowAndWait(window);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
