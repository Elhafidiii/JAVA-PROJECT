package com.example.manic_time;

import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Arc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FocusSessionControllerTest {

    private FocusSessionController controller;

    @BeforeEach
    void setUp() {
        // Initialiser le contrôleur
        controller = new FocusSessionController();

        // Simuler les éléments @FXML
        controller.timerLabel = mock(Label.class);
        controller.totalFocusTimeSpinner = new Spinner<>();
        controller.focusTimeSpinner = new Spinner<>();
        controller.breakTimeSpinner = new Spinner<>();
        controller.timerArc = mock(Arc.class);
        controller.phaseLabel = mock(Label.class);
    }

    @Test
    void testStartFocusSession_ValidInputs() {
        // Configurer les valeurs des Spinners
        controller.totalFocusTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 30));
        controller.focusTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300, 25));
        controller.breakTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 5));

        // Appeler la méthode
        controller.startFocusSession();

        // Vérifier les résultats
        assertNotNull(controller.timeline, "Le timeline doit être initialisé.");
        verify(controller.timerLabel, atLeastOnce()).setText(anyString());
    }

    @Test
    void testStartFocusSession_InvalidInputs() {
        // Configurer les valeurs des Spinners
        controller.totalFocusTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, -1));
        controller.focusTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300, 0));
        controller.breakTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 0));

        // Appeler la méthode
        controller.startFocusSession();

        // Vérifier que l'alerte s'affiche pour les entrées invalides
        // Utilisez Mockito pour vérifier l'affichage d'alerte si applicable
    }

    @Test
    void testPauseFocusSession() {
        controller.timeline = mock(Timeline.class);

        // Définir l'état initial
        controller.isPaused = false;

        // Appeler la méthode
        controller.pauseFocusSession();

        // Vérifier les résultats
        verify(controller.timeline, times(1)).pause();
        assertTrue(controller.isPaused, "La session doit être marquée comme en pause.");
    }

    @Test
    void testResetFocusSession() {
        controller.timeline = mock(Timeline.class);

        // Appeler la méthode
        controller.resetFocusSession();

        // Vérifier les résultats
        verify(controller.timeline, times(1)).stop();
        verify(controller.timerLabel).setText("00:00");
        assertFalse(controller.isPaused, "La session ne doit pas être en pause.");
        assertFalse(controller.isStarted, "La session ne doit pas être démarrée.");
    }

    @Test
    void testSendNotification() {
        // Vérifier la sortie standard pour une notification
        String title = "Test Title";
        String message = "Test Message";

        // Appeler la méthode
        controller.sendNotification(title, message);

        // Vérifier les résultats (la logique réelle peut inclure des mocks si nécessaire)
        // Assurez-vous qu'aucune exception n'est levée
    }

    @Test
    void testHandlePhaseChange() {
        // Simuler des valeurs
        controller.focusTimeField = mock(TextField.class);
        when(controller.focusTimeField.getText()).thenReturn("25");

        // Appeler la méthode
        controller.handlePhaseChange(25, 5);

        // Vérifier les changements de phase
        assertTrue(controller.isFocusPhase, "La phase doit être une phase de concentration.");
    }
}
