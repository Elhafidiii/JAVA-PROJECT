package com.example.manic_time;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginController {

    @FXML
    protected TextField emailField;

    @FXML
    protected PasswordField passwordField;

    @FXML
    protected Label messageLabel;

    @FXML
    protected Hyperlink signUpLink;

    @FXML
    void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("All fields are required!", "red");
            return;
        }

        boolean isAuthenticated = authenticateUser(email, password);

        if (isAuthenticated) {
            showMessage("Login successful!", "green");
            try {
                // Charger la vue principale
                FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                Parent mainView = loader.load();

                // Remplacer la vue actuelle dans la scène
                Scene currentScene = signUpLink.getScene();
                currentScene.setRoot(mainView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showMessage("Invalid email or password.", "red");
        }
    }

    boolean authenticateUser(String email, String password) {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND motDePass = ?";

        try (Connection connection = db.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Retourne true si l'utilisateur est trouvé

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Database error: " + e.getMessage(), "red");
        }
        return false; // Retourne false si aucun utilisateur n'est trouvé
    }

    private void showMessage(String message, String color) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: " + color + ";");
        }
    }

    @FXML
    private void handleSignUpLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp-view.fxml"));
            Parent signUpView = loader.load();

            Scene currentScene = signUpLink.getScene();
            currentScene.setRoot(signUpView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
