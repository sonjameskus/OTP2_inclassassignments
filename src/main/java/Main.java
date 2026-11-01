
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void init() {
        System.out.println("INIT CALLED");
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/tempUI.fxml")
        );

        Parent root = fxmlLoader.load();

        stage.setTitle("Temperature Converter");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
