package at.ac.fhcampuswien.richAF;

import javafx.application.Application;
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
        Scene scene = new Scene(fxmlLoader.load(), 800, 400);
        stage.setScene(scene);
        stage.show();
    }
}