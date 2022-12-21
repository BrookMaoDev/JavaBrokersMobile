package megabudgetstonks;

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
        sd.test();
    }
}
