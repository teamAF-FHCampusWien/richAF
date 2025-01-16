package at.ac.fhcampuswien.richAF.controller;

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

    public void hideHiddenDevMenu() {
        devMenuRoot.setVisible(false);
    }

    public void loadDataFromConfig() {
        Properties properties = new Properties();
        String configFileName = "config.properties";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (inputStream == null) {
                System.err.println("Unable to find " + configFileName);
                return;
            }

            properties.load(inputStream);

            String endpoint = properties.getProperty("ollama.endpoint", "defaultEndpoint");
            String temperature = properties.getProperty("ollama.temperature", "0.0");
            String basePrompt = properties.getProperty("ollama.baseprompt", "defaultPrompt");

            ollPortTxt.setText(endpoint);
            ollTempTxt.setText(temperature);
            ollPromptTxt.setText(basePrompt);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDataToConfig() {
        String configFilePath = "config.properties";
        File configFile = new File(configFilePath);
        Properties properties = new Properties();

        if (configFile.exists()) {
            try (InputStream in = new FileInputStream(configFile)) {
                properties.load(in);
            } catch (IOException e) {
                System.err.println("Failed to load existing config.properties");
                e.printStackTrace();
            }
        }

        properties.setProperty("ollama.endpoint", ollPortTxt.getText());
        properties.setProperty("ollama.temperature", ollTempTxt.getText());
        properties.setProperty("ollama.baseprompt", ollPromptTxt.getText());

        File tempFile = new File("config.tmp");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            properties.store(out, "Updated by DevMenuController");
        } catch (IOException e) {
            System.err.println("Error writing temporary file");
            e.printStackTrace();
            return;
        }

        if (tempFile.renameTo(configFile)) {
            System.out.println("Config updated successfully");
        } else {
            System.err.println("Could not rename temp file to config.properties");
        }
    }
}
