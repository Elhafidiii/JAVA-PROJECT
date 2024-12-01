package com.example.manic_time;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class loginControllerTest {

    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;

    @InjectMocks
    private loginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialiser JavaFX
        javafx.application.Platform.startup(() -> {});

        // Initialiser les champs avec des objets réels
        emailField = new TextField();
        passwordField = new PasswordField();
        messageLabel = new Label();

        // Injecter les dépendances dans loginController
        loginController = new loginController();
        loginController.emailField = emailField;
        loginController.passwordField = passwordField;
        loginController.messageLabel = messageLabel;
    }

    @Test
    void testHandleLogin_ValidCredentials() {
        // Mock de la méthode authenticateUser
        loginController = spy(loginController);
        doReturn(true).when(loginController).authenticateUser(eq("0000@gmail.com"), eq("0000"));

        // Configurer les champs
        emailField.setText("0000@gmail.com");
        passwordField.setText("0000");

        // Exécuter la méthode
        loginController.handleLogin();

        // Vérifier que le message de succès est affiché
        assertEquals("Login successful!", messageLabel.getText());
        assertTrue(messageLabel.getStyle().contains("-fx-text-fill: green;"));
    }

    @Test
    void testHandleLogin_InvalidCredentials() {
        // Mock de la méthode authenticateUser
        loginController = spy(loginController);
        doReturn(false).when(loginController).authenticateUser(eq("0001@gmail.com"), eq("0001"));

        // Configurer les champs
        emailField.setText("0001@gmail.com");
        passwordField.setText("0001");

        // Exécuter la méthode
        loginController.handleLogin();

        // Vérifier que le message d'erreur est affiché
        assertEquals("Invalid email or password.", messageLabel.getText());
        assertTrue(messageLabel.getStyle().contains("-fx-text-fill: red;"));
    }

    @Test
    void testHandleLogin_EmptyFields() {
        // Configurer les champs
        emailField.setText("");
        passwordField.setText("");

        // Exécuter la méthode
        loginController.handleLogin();

        // Vérifier que le message d'erreur est affiché
        assertEquals("All fields are required!", messageLabel.getText());
        assertTrue(messageLabel.getStyle().contains("-fx-text-fill: red;"));
    }

}