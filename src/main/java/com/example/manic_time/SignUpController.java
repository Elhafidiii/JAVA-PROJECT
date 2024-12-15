package com.example.manic_time;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {

    @FXML
    protected TextField fullNameField;

    @FXML
    protected TextField emailField;

    @FXML
    protected PasswordField passwordField;

    @FXML
    protected PasswordField confirmPasswordField;

    @FXML
    protected Label messageLabel;

    @FXML
    private Hyperlink loginLink;

    @FXML
    protected void handleLoginLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent loginView = loader.load();

            Scene currentScene = loginLink.getScene();
            currentScene.setRoot(loginView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleSignUp() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all fields.", "red");
        } else if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.", "red");
        } else {
            if (addUser(fullName, email, password)) {
                showMessage("Sign-Up successful!", "green");
                // Retourner à la page de login après l'inscription
                handleLoginLink();
            } else {
                showMessage("Sign-Up failed. Try again.", "red");
            }
        }
    }

    protected boolean addUser(String fullName, String email, String password) {
        String query = "INSERT INTO utilisateur (nom, email, motDePass) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, password);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Database error: " + e.getMessage(), "red");
        }
        return false;
    }

    private void showMessage(String message, String color) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: " + color + ";");
        }
    }
}
