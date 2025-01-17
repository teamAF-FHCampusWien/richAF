package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.services.DBService;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Setter;

public class AddBottomSheetController {

    //Triggers when bottomsheet is hidden
    private EventHandler<ActionEvent> onCancel;
    private EventHandler<ActionEvent> onSubmit;
    private DBService _dbService;

    @FXML
    private VBox bottomSheet;

    @FXML
    private Button cancelBottomSheet;

    @FXML
    private Button confirmBottomSheet;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtLink;

    public AddBottomSheetController(DBService dbService) {
        this._dbService = dbService;

    }

    public void setOnCancel(EventHandler<ActionEvent> handler) {
        this.onCancel = handler;
        System.out.println("onCancel handler set");
    }

    public void setOnSubmit(EventHandler<ActionEvent> handler) {
        this.onSubmit = handler;
        System.out.println("onSubmit handler set");
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        System.out.println("Cancel button pressed");

        hideAddBottomSheet();
        if (onCancel != null) {
            System.out.println("Invoking onCancel handler");
            onCancel.handle(event);
        } else {
            System.out.println("onCancel handler is not set");
        }
    }

    @FXML
    private void handleSubmitAction(ActionEvent event) {
        System.out.println("onSubmit button pressed");
        if ((txtName.getText()!="")&&(txtLink.getText()!=""))
            _dbService.addSource(txtName.getText(),txtLink.getText());
        hideAddBottomSheet();
        if (onSubmit != null) {
            System.out.println("Invoking onSubmit handler");
            onCancel.handle(event);
        } else {
            System.out.println("onSubmit handler is not set");
        }
    }

    //End of EventHandler


    // Methods

    public void hideAddBottomSheet() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), bottomSheet);
        slideDown.setToY(750);
        slideDown.play();
    }





}
