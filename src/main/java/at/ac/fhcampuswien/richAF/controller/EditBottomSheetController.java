package at.ac.fhcampuswien.richAF.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class EditBottomSheetController {

    @FXML
    private VBox editBottomSheet;

    @FXML
    private Button cancelEditBottomSheet;

    // Methods
    public void hideEditBottomSheet() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), editBottomSheet);
        slideDown.setToY(750);
        slideDown.play();
    }

}
