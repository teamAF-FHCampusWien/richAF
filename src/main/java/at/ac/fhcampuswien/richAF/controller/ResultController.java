package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.data.ArticleResult;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.Objects;

public class ResultController {

    @FXML
    private Text cardTitle;

    @FXML
    private Text cardSummary;

    @FXML
    private Label cardStock;

    @FXML
    private Hyperlink cardSource;

    @FXML
    private StackPane likelyToWin;

    @FXML
    private StackPane likelyToLose;

    @FXML
    private Rectangle stockLabel;

    @FXML
    private Rectangle relevanceLabel;

    @FXML
    private Rectangle trendLabel;

    @FXML
    private StackPane relevantLabel;

    @FXML
    private StackPane notRelevantLabel;

    public void setCardTitle(String title) {
        cardTitle.setText(title);
    }

    public void setCardSummary(String summary) {
        cardSummary.setText(summary);
    }

    public void setProps(ArticleResult res){
        cardTitle.setText(res.getTitle());
        cardSummary.setText(res.getSummary());
        cardSource.setText(res.getSource());
        cardStock.setText(res.getStock());

        stockLabel.widthProperty().bind(cardStock.widthProperty().add(20));

        if (Objects.equals(res.getRelevant(), "NO")) {
            relevantLabel.setVisible(false);
        } else {
            notRelevantLabel.setVisible(false);
        }


        if (Objects.equals(res.getTrend(), "DOWN")) {
            likelyToWin.setVisible(false);
        } else {
            likelyToLose.setVisible(false);
        }
    }

    //TODO: Add methods for displaying results
}
