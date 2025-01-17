package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.data.ArticleResult;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ResultController {

    @FXML
    private Label cardTitle;

    @FXML
    private Text cardSummary;

    @FXML
    private Label cardStock;

    @FXML
    private Label cardRelevant;

    @FXML
    private Label cardTrend;

    @FXML
    private Hyperlink cardSource;

    public void setCardTitle(String title) {
        cardTitle.setText(title);
    }

    public void setCardSummary(String summary) {
        cardSummary.setText(summary);
    }

    public void setProps(ArticleResult res){
        cardTitle.setText(res.getTitle());
        cardSummary.setText(res.getSummary());
        cardRelevant.setText(res.getRelevant());
        cardTrend.setText(res.getTrend());
        cardSource.setText(res.getSource());
        cardStock.setText(res.getStock());


    }

    //TODO: Add methods for displaying results
}
