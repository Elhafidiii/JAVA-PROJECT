package com.example.manic_time;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 820, 540);
        scene.getStylesheets().add(this.getClass().getResource("/com/example/manic_time/HelloApplication.css").toExternalForm());

        stage.setTitle("MANICTIME");
        stage.setScene(scene);

        // Active le plein écran
        stage.setFullScreen(true);

        stage.show();
    }

    public static void main(String[] args) {
        DatabaseConnection n = new DatabaseConnection();
        n.connect();
        launch();
    }
}
