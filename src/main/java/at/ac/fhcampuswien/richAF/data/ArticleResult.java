package at.ac.fhcampuswien.richAF.data;

import lombok.Setter;
import lombok.Getter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class ArticleResult {
    @Getter @Setter
    private String stock;
    @Getter @Setter
    private String relevant;
    @Getter @Setter
    private String summary;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String trend;
    @Getter @Setter
    private String source;

    // Construct Object from Strings
    public ArticleResult(String tickerSymbol, String relevant, String summary) {
        this.stock = tickerSymbol;
        this.relevant = relevant;
        this.summary = summary;
    }

    // Construct Object from json
    public ArticleResult(String jsonString) {

        JSONObject jsonResponseObject = new JSONObject(jsonString);


        try {

            this.stock = jsonResponseObject.getString("stock");
            this.relevant = jsonResponseObject.getString("relevant");
            this.summary = jsonResponseObject.getString("summary");
            this.title = jsonResponseObject.getString("title");
            this.trend = jsonResponseObject.getString("trend");
            this.source = jsonResponseObject.getString("source");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
