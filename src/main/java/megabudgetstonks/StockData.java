package megabudgetstonks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class StockData {
    public StockData() {
    }

    public ArrayList<String> getData(String dataType) {
        // Data types: "trending-tickers", "gainers", "losers", "crypto"
        ArrayList<String> data = new ArrayList<String>();
        // URL with data type to determine what data to scrape
        String url = "https://finance.yahoo.com/" + dataType;
        try {
            // Parse site to HTML
            Document doc = Jsoup.connect(url).get();
            // Retrieve elements from table
            Elements tickers = doc.getElementsByClass("simpTblRow");
            // Retrieve all ticker names
            for (Element ticker : tickers) {
                data.add((String) ticker.text().substring(0, ticker.text().indexOf(' ')));
            }
            return data;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    // More specific methods

    // public ArrayList<String> getCryptoTickers() {
    //     ArrayList<String> crypto = new ArrayList<String>();
    //     String url = "https://finance.yahoo.com/crypto";
    //     try {
    //         Document doc = Jsoup.connect(url).get();
    //         Elements tickers = doc.getElementsByClass("simpTblRow");
    //         for (Element ticker : tickers) {
    //             crypto.add((String) ticker.text().substring(0, ticker.text().indexOf(' ')));
    //         }
    //         return crypto;
    //     } catch (IOException e) {
    //         System.out.println(e);
    //     }
    //     return null;
    // }

    // public ArrayList<String> getGainers() {
    //     ArrayList<String> gainers = new ArrayList<String>();
    //     String url = "https://finance.yahoo.com/gainers";
    //     try {
    //         Document doc = Jsoup.connect(url).get();
    //         Elements tickers = doc.getElementsByClass("simpTblRow");
    //         for (Element ticker : tickers) {
    //             gainers.add((String) ticker.text().substring(0, ticker.text().indexOf(' ')));
    //         }
    //         return gainers;
    //     } catch (IOException e) {
    //         System.out.println(e);
    //     }
    //     return null;
    // }

    // public ArrayList<String> getLosers() {
    //     ArrayList<String> losers = new ArrayList<String>();
    //     String url = "https://finance.yahoo.com/losers";
    //     try {
    //         Document doc = Jsoup.connect(url).get();
    //         Elements tickers = doc.getElementsByClass("simpTblRow");
    //         for (Element ticker : tickers) {
    //             losers.add((String) ticker.text().substring(0, ticker.text().indexOf(' ')));
    //         }
    //         return losers;
    //     } catch (IOException e) {
    //         System.out.println(e);
    //     }
    //     return null;
    // }
}
