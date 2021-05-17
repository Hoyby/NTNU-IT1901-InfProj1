package ajour.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * App launcher.
 */

public class AjourApp extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Parent parent = FXMLLoader.load(getClass().getResource("AjourApp.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Ajour");
        primaryStage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }

}