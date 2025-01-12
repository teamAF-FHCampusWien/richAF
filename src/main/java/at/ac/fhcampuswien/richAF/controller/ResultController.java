package at.ac.fhcampuswien.richAF.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ResultController {

    @FXML
    private Label cardTitle;

    @FXML
    private Text cardSummary;

    public void setCardTitle(String title) {
        cardTitle.setText(title);
    }

    public void setCardSummary(String summary) {
        cardSummary.setText(summary);
    }

    //TODO: Add methods for displaying results
}
