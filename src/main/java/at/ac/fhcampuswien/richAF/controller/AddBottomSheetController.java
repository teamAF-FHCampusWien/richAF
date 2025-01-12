package at.ac.fhcampuswien.richAF.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AddBottomSheetController {

    @FXML
    private VBox bottomSheet;

    @FXML
    private Button cancelBottomSheet;

    @FXML
    private Button confirmBottomSheet;

    // Methods
    public void hideAddBottomSheet() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), bottomSheet);
        slideDown.setToY(750);
        slideDown.play();
    }

}
