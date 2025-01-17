package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.data.ArticleResult;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterController {
    private EventHandler<ActionEvent> onDone;
    private EventHandler<ActionEvent> onChanged;
    private List<ArticleResult> articles;
    private Map filtermap;
    @FXML
    private ChoiceBox<String> choiceTicker;

    @FXML
    private ChoiceBox<String> choiceMatch;

    @FXML
    private ChoiceBox<String> choiceWL;

    @FXML
    private ChoiceBox<String> choiceFeed;

    public void setOnDone(EventHandler<ActionEvent> handler) {
        this.onDone = handler;
        System.out.println("onCancel handler set");
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        System.out.println("Cancel button pressed");
        hideFilterMenu();
        if (onDone != null) {
            System.out.println("Invoking onCancel handler");
            onDone.handle(event);
        } else {
            System.out.println("onCancel handler is not set");
        }
    }

    @FXML
    private VBox filterMenu;

    public void hideFilterMenu() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), filterMenu);
        slideOut.setToX(300);
        slideOut.play();
    }

    public Map getFilter() {
        return filtermap;
    }

    public FilterController(List<ArticleResult> articles){
        this.articles = articles;

    }

    @FXML
    public void initialize() {
        choiceTicker.getItems().add("ALLE");
        choiceTicker.getItems().add("NONE");
        if (articles != null) {
            for (ArticleResult article : articles) {
                if (!choiceTicker.getItems().contains(article.getStock())) {
                    choiceTicker.getItems().add(article.getStock());
                }


            }

        }
        choiceTicker.setValue("ALLE");
    }

    public void setOnSelectionChanged(EventHandler<ActionEvent> handler) {
        filtermap.clear();
        filtermap.put("Ticker", choiceTicker.getValue());
        this.onChanged = handler;
        System.out.println("selection changed");
    }

}
