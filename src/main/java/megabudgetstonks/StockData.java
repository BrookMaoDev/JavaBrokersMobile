package megabudgetstonks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class StockData {
    public StockData() {
    }

    public String[] getTrendingTickers() throws IOException {
        String[] trendingTickers = new String[20];
        int numTickers = 0;
        final String url = "https://stockanalysis.com/trending/";
        try {
            final Document document = Jsoup.connect(url).get();
            String tickerIdx = "td:nth-of-type(2)";
            for (Element row : document.select("table.symbol-table.svelte-1ga61q3 tr")) {
                if (row.select(tickerIdx).text().equals("")) {
                    continue;
                } else {
                    trendingTickers[numTickers] = row.select(tickerIdx).text();
                    numTickers++;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return trendingTickers;
    }

    public void test() {
        String url = "https://finance.yahoo.com/trending-tickers/";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements content = doc.getElementsByClass(
                    "Va(m).Ta(start).Pstart(6px).Pend(15px).Start(0).Pend(10px).simpTblRow:h_Bgc($hoverBgColor).Pos(st).Bgc($lv3BgColor).Z(1).Bgc($lv2BgColor).Ta(start)!.Fz(s)");
            for (Element e : content) {
                System.out.println(e.text());
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
