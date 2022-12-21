package megabudgetstonks;

import java.util.ArrayList;

/**
 * Last modified: ___ ___ __, 2022
 *
 */
public class Main {
    public static void cls() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {
        StockData sd = new StockData();
        ArrayList<String> s = sd.getData("trending-tickers");
        for (String x : s) {
            System.out.println(x);
        }
    }
}
