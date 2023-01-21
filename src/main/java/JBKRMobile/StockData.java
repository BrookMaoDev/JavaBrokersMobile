/**
Class Name: Adult
Author: Owen Wang
Date: January 20, 2023
School: A Y Jackson Secondary School
Purpose: Helps with pulling data from YahooFinance.
 */

package JBKRMobile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;

public class StockData {
    /**
     * Data types: "most-active", "gainers", "losers", "crypto"
     */
    public static ArrayList<String> getData(String dataType) {
        try {
            // URL with data type to determine what data to scrape
            String url = "https://finance.yahoo.com/" + dataType + "?offset=0&count=50";
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
            e.printStackTrace();
        }
        return null;
    }
}
