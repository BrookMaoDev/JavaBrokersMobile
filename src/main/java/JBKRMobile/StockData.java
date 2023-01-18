package JBKRMobile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;

public class StockData {
    public StockData() {
    }

    /**
     * Data types: "most-active", "gainers", "losers", "crypto"
     */
    public ArrayList<String> getData(String dataType) {
        try {
            // URL with data type to determine what data to scrape
            String url = "https://finance.yahoo.com/" + dataType + "?count=100&offset=0";
            // Parse site to HTML
            Document doc = Jsoup.connect(url).get();
            // Retrieve elements from table
            Element table = doc.select("table tbody").first();
            ArrayList<String> data = new ArrayList<String>();
            for (Element row : table.select("tr")) {
                data.add(row.select("td").first().text());
            }
            return data;
        } catch (Exception e) {
        }
        return null;
    }
}
