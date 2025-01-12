package at.ac.fhcampuswien.richAF.data;

import lombok.Setter;
import lombok.Getter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArticleResult {
    @Getter @Setter
    private String tickerSymbol;
    @Getter @Setter
    private Boolean relevant;
    @Getter @Setter
    private String summary;

    // Construct Object from Strings
    public ArticleResult(String tickerSymbol, Boolean relevant, String summary) {
        this.tickerSymbol = tickerSymbol;
        this.relevant = relevant;
        this.summary = summary;
    }

    // Construct Object from json
    public ArticleResult(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            ArticleResult parsedArticle = mapper.readValue(jsonString, ArticleResult.class);
            this.tickerSymbol = parsedArticle.getTickerSymbol();
            this.relevant = parsedArticle.getRelevant();
            this.summary = parsedArticle.getSummary();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
