package at.ac.fhcampuswien.richAF.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class FilterController {
    private EventHandler<ActionEvent> onDone;

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

}
