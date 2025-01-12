package at.ac.fhcampuswien.richAF.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

public class BottomSheetController {

    @FXML
    private VBox bottomSheet;

    @FXML
    private Button cancelBottomSheet;

    @FXML
    private Button confirmBottomSheet;

    // Methods
    public void hideBottomSheet() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), bottomSheet);
        slideDown.setToY(750);
        slideDown.play();
    }

}
