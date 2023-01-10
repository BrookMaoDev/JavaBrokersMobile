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

            final Window window = new BasicWindow("JBKR Mobile");

            Panel tickerPanel = new Panel();

            // GridLayout gridLayout = (GridLayout) tickerPanel.getLayoutManager();
            // gridLayout.setHorizontalSpacing(3);

            // Create panels for tickers
            tickerPanel.setFillColorOverride(ANSI.WHITE);
            Table<String> table = new Table<String>("TICKER", "PRICE", "CHANGE", "% CHANGE");
            // table.setLayoutData(GridLayout.createLayoutData(
            //         GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than
            //                                         // the component's preferred size
            //         GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than
            //                                         // the component's preferred size
            //         true, // Give the component extra horizontal space if available
            //         false, // Give the component extra vertical space if available
            //         2, // Horizontal span
            //         1));

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

            // tickerPanel.addComponent(new Label("Username: "));
            // tickerPanel.addComponent(
            //         new TextBox()
            //                 .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING,
            //                         GridLayout.Alignment.CENTER)));
            // tickerPanel.addComponent(tickerPanel);

            // Create window
            window.setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.FULL_SCREEN));
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
