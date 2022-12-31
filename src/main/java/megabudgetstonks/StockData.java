package megabudgetstonks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;

public class StockData {
    public StockData() {
    }

    /**
     * Data types: "trending-tickers", "gainers", "losers", "crypto"
     */
    public ArrayList<String> getData(String dataType) {
        // URL with data type to determine what data to scrape
        String url = "https://finance.yahoo.com/" + dataType;
        try {
            // Parse site to HTML
            Document doc = Jsoup.connect(url).get();
            // Retrieve elements from table
            Element table = doc.select("table tbody").first();
            ArrayList<String> data = new ArrayList<String>();
            for (Element row : table.select("tr")) {
                data.add(row.select("td").first().text());
            }
            return data;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }
}
