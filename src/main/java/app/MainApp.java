package app;

import javafx.application.Application;
import javafx.stage.Stage;
import util.SceneManager;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        stage.setTitle("Library Management System");
        SceneManager.switchTo("/ui/Login.fxml");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
