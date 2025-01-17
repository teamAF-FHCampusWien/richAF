package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.model.dao.tblSource;
import at.ac.fhcampuswien.richAF.services.DBService;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

public class EditBottomSheetController {
    //Triggers when bottomsheet is hidden
    private EventHandler<ActionEvent> onCancel;

    private DBService _dbservice;

    @FXML
    private VBox editBottomSheet;

    @FXML
    private Button cancelEditBottomSheet;

    @FXML
    private Label defaultLabel;

    @FXML
    private Text defaultText;

    @FXML
    private GridPane gridSources;

    private String labelStyle;
    private String linkStyle;


    public EditBottomSheetController(DBService dbservice) {
        this._dbservice = dbservice;
    }




    public void setOnCancel(EventHandler<ActionEvent> handler) {
        this.onCancel = handler;
        System.out.println("Handler engaged");
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        hideEditBottomSheet();
        if (onCancel != null) {
            onCancel.handle(event);
        } else {
            System.out.println(" ");
        }
    }



    // Methods
    public void hideEditBottomSheet() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), editBottomSheet);
        slideDown.setToY(950);
        slideDown.play();
    }


    @FXML
    public void initialize() {
        // Annahme: Die Standardzeile ist die erste Zeile (Index 0)

        if (defaultLabel != null) {
            labelStyle = defaultLabel.getStyle();
        }
        if (defaultText != null) {
            linkStyle = defaultText.getStyle();
        }
        gridSources.getChildren().clear();
        if (_dbservice == null) {return;}
        ArrayList<tblSource> tblSources = _dbservice.getSources();
        for(int i=0;(i<10)&&(i<tblSources.size());i++) {
            addRow(tblSources.get(i),i);

        }

    }

    public void addRow(tblSource tbls, int i) {


        Label lblname = new Label(tbls.getName());
        Text txtlink = new Text(tbls.getStrUrl());

        Button btndel = new Button();
        ImageView deleteIcon = new ImageView(
                new Image(getClass().getResourceAsStream("/icons/delete.png"))
        );

        deleteIcon.setFitWidth(20);
        deleteIcon.setFitHeight(20);

        btndel.setGraphic(deleteIcon);
        btndel.getStyleClass().add("icon-button"); // apply your custom CSS class
        btndel.setOnAction(event -> removeSource(tbls.getId()));

        if (labelStyle != null) {
            lblname.setStyle(labelStyle);
        }
        if (linkStyle != null) {
            txtlink.setStyle(linkStyle);
        }
        lblname.setFont(new Font("Arial", 15));
        txtlink.setFont(new Font("Arial", 15));
        btndel.setOnAction(event -> removeSource(tbls.getId()));

        gridSources.add(lblname, 0, i);
        gridSources.add(txtlink, 1, i);
        gridSources.add(btndel, 2, i);

    }

    private void removeSource(int id) {
        // Remove all nodes in the specified row
        _dbservice.deleteSource(id);
        initialize();
    }

}
