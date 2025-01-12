package at.ac.fhcampuswien.richAF;

import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
      launch();
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        stage.setResizable(false);
        stage.setTitle("Ready to get richAF?");
        stage.setScene(scene);
        stage.show();
    }
}