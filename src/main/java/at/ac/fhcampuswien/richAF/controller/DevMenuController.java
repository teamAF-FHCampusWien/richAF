package at.ac.fhcampuswien.richAF.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class DevMenuController {
    @FXML
    private StackPane devMenuRoot;

    @FXML
    private Button devMenuDiscard;

    public void hideHiddenDevMenu() {
        devMenuRoot.setVisible(false);
    }
}
