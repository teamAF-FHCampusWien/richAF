package at.ac.fhcampuswien.richAF.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class EditBottomSheetController {
    //Triggers when bottomsheet is hidden
    private EventHandler<ActionEvent> onCancel;

    public void setOnCancel(EventHandler<ActionEvent> handler) {
        this.onCancel = handler;
        System.out.println("onCancel handler set");
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        System.out.println("Cancel button pressed");
        hideEditBottomSheet();
        if (onCancel != null) {
            System.out.println("Invoking onCancel handler");
            onCancel.handle(event);
        } else {
            System.out.println("onCancel handler is not set");
        }
    }

    @FXML
    private VBox editBottomSheet;

    @FXML
    private Button cancelEditBottomSheet;

    // Methods
    public void hideEditBottomSheet() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), editBottomSheet);
        slideDown.setToY(950);
        slideDown.play();
    }

}
