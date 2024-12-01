package com.example.manic_time;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class SignUpControllerTest {

    @InjectMocks
    private SignUpController signUpController;

    private TextField fullNameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label messageLabel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialiser JavaFX
        javafx.application.Platform.startup(() -> {});

        // Mock des champs texte et labels
        fullNameField = new TextField();
        emailField = new TextField();
        passwordField = new PasswordField();
        confirmPasswordField = new PasswordField();
        messageLabel = new Label();

        // Injecter les dépendances
        signUpController = new SignUpController();
        signUpController.fullNameField = fullNameField;
        signUpController.emailField = emailField;
        signUpController.passwordField = passwordField;
        signUpController.confirmPasswordField = confirmPasswordField;
        signUpController.messageLabel = messageLabel;
    }

    @Test
    void testHandleSignUp_AllFieldsEmpty() {
        // Configurer les champs comme vides
        fullNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");

        // Exécuter la méthode
        signUpController.handleSignUp();

        // Vérifier le message d'erreur
        assertEquals("Please fill in all fields.", messageLabel.getText());
        assertTrue(messageLabel.getStyle().contains("-fx-text-fill: red;"));
    }

    @Test
    void testHandleSignUp_PasswordsDoNotMatch() {
        // Configurer les champs avec des mots de passe non concordants
        fullNameField.setText("youssef");
        emailField.setText("0000@gmail.com");
        passwordField.setText("0000");
        confirmPasswordField.setText("0001");

        // Exécuter la méthode
        signUpController.handleSignUp();

        // Vérifier le message d'erreur
        assertEquals("Passwords do not match.", messageLabel.getText());
        assertTrue(messageLabel.getStyle().contains("-fx-text-fill: red;"));
    }

    @Test
    void testHandleSignUp_SuccessfulSignUp() {
        // Configurer les champs avec des données valides
        fullNameField.setText("youssef");
        emailField.setText("0000@gmail.com");
        passwordField.setText("0000");
        confirmPasswordField.setText("0000");

        // Mock de la méthode addUser
        signUpController = spy(signUpController);
        signUpController.fullNameField = fullNameField;
        signUpController.emailField = emailField;
        signUpController.passwordField = passwordField;
        signUpController.confirmPasswordField = confirmPasswordField;
        signUpController.messageLabel = messageLabel;

        // Simuler l'ajout réussi de l'utilisateur
        doReturn(true).when(signUpController).addUser(anyString(), anyString(), anyString());
        doNothing().when(signUpController).handleLoginLink();

        // Exécuter la méthode
        signUpController.handleSignUp();

        // Vérifier le message de succès
        assertEquals("Sign-Up successful!", messageLabel.getText());
        assertTrue(messageLabel.getStyle().contains("-fx-text-fill: green;"));

        // Vérifier si handleLoginLink est appelé pour retourner à la page de login
        verify(signUpController, times(1)).handleLoginLink();
    }

    @Test
    void testHandleSignUp_FailedSignUp() {
        // Configurer les champs avec des données valides
        fullNameField.setText("youssef");
        emailField.setText("0002@gmail.com");
        passwordField.setText("0002");
        confirmPasswordField.setText("0002");

        // Mock de la méthode addUser pour retourner false
        signUpController = spy(signUpController);
        doReturn(false).when(signUpController).addUser(anyString(), anyString(), anyString());

        // Exécuter la méthode
        signUpController.handleSignUp();

        // Vérifier le message d'erreur
        assertEquals("Sign-Up failed. Try again.", messageLabel.getText());
        assertTrue(messageLabel.getStyle().contains("-fx-text-fill: red;"));

        // Vérifier que handleLoginLink n'est pas appelé
        verify(signUpController, times(0)).handleLoginLink();
    }
}
