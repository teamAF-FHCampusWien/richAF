package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.data.ArticleResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultController {

    @FXML
    private Label cardTitle;

    @FXML
    private Label cardSummary;

    public void setCardTitle(String title) {
        cardTitle.setText(title);
    }

    public void setCardSummary(String summary) {
        cardSummary.setText(summary);
    }

    //TODO: Add methods for displaying results
}
