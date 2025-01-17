package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.model.Config;
import at.ac.fhcampuswien.richAF.services.OllamaService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;


import java.io.*;
import java.util.Properties;

public class DevMenuController {
    @FXML
    private StackPane devMenuRoot;

    @FXML
    private Button devMenuDiscard;

    @FXML
    private Button devMenuSave;

    @FXML
    private TextField ollPortTxt;

    @FXML
    private TextField ollTempTxt;

    @FXML
    private TextArea ollPromptTxt;




    private Config _config;
    private OllamaService _ollamaService;

    public DevMenuController(Config _config,OllamaService _ollamaService) {
        this._config = _config;
        this._ollamaService = _ollamaService;
    }



    public void hideHiddenDevMenu() {
        devMenuRoot.setVisible(false);
    }

    public void loadDataFromConfig() {
            String endpoint = _config.getProperty("ollama.endpoint");
            String temperature = _config.getProperty("ollama.temperature");
            String basePrompt = _config.getProperty("ollama.baseprompt");

            ollPortTxt.setText(endpoint);
            ollTempTxt.setText(temperature);
            ollPromptTxt.setText(basePrompt);
    }

    public void saveDataToConfig() {
        _config.saveDataToUserConfig("ollama.endpoint", ollPortTxt.getText());
        _config.saveDataToUserConfig("ollama.temperature", ollTempTxt.getText());
        _config.saveDataToUserConfig("ollama.baseprompt", ollPromptTxt.getText());

        _ollamaService.SetBaseUri();


    }
}
