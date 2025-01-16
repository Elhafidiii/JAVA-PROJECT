package com.example.manic_time;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.shape.Arc;

import java.awt.*;

public class FocusSessionController {


    @FXML
    private void goToDashboard() {
        // Navigate to dashboard
    }

    @FXML
    private void goToTMSystem() {
        // Navigate to TM System
    }

    @FXML
    private void goToSmartTiming() {
        // Navigate to Smart Timing
    }

    @FXML
    private void goToAnalytics() {
        // Navigate to Analytics
    }

    @FXML
    private void goToSetLimits() {
        // Navigate to Set Limits
    }
    @FXML
    Label timerLabel;
    @FXML
    private Spinner<Integer> focusSessionSpinner;

    private Timeline focusTimer;
//    private int focusTimeRemaining;
//    private int breakTimeRemaining;

    @FXML
    TextField focusTimeField;
    @FXML
    private TextField totalFocusTimeField;

    @FXML
    private TextField breakTimeField;
    @FXML
    private BorderPane mainPane; // L'élément BorderPane de votre vue principale

    private int totalFocusTime; // Total focus time in seconds
    private int remainingSessionTime;// Remaining time in the full session
    private int focusTimeRemaining;
    private int breakTimeRemaining;
    boolean isFocusPhase;
    Timeline timeline;
    int phaseTime;
    boolean isPaused = false;
    boolean isStarted = false;
    private int breakTime = 300;
    @FXML
    Spinner<Integer> totalFocusTimeSpinner;

    @FXML
    Spinner<Integer> focusTimeSpinner;

    @FXML
    Spinner<Integer> breakTimeSpinner;

    @FXML
    Arc timerArc;
    @FXML
    Label phaseLabel;



    @FXML
    public void initialize() {
        // Initialize Spinners with value factories
        totalFocusTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 5));
        focusTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300, 0));
        breakTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 0));

    }


    @FXML
    void startFocusSession() {
        try {
            if (!isStarted) {
                int totalSessionMinutes = Integer.parseInt(totalFocusTimeSpinner.getValue().toString());
                int focusMinutes = Integer.parseInt(focusTimeSpinner.getValue().toString());
                int breakMinutes = Integer.parseInt(breakTimeSpinner.getValue().toString());

                if (totalSessionMinutes <= 0 || focusMinutes <= 0 || breakMinutes <= 0) {
                    showAlert("Invalid Input", "Please enter valid positive numbers for all time fields.");
                    return;
                }

                remainingSessionTime = totalSessionMinutes * 60;
                isFocusPhase = true;
                phaseTime = focusMinutes * 60;
                sendNotification("Focus Session Started", "Your focus session has begun! Stay focused.");
                startTimer(focusMinutes * 60, breakMinutes * 60);
                isStarted = true;
            } else if (isPaused) {
                timeline.play();
                isPaused = false;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for time.");
        }
    }

    void handlePhaseChange(int focusTime, int breakTime) {
        if (isFocusPhase) {
            // Focus phase ended, starting break
            sendNotification("Break Started", "Time for a short break! Relax and recharge.");
            isFocusPhase = false;
            phaseTime = this.breakTime; // Use the correct break time
        } else {
            // Break phase ended, starting focus
            sendNotification("Focus Phase Started", "Back to work! Stay focused.");
            isFocusPhase = true;
            phaseTime = Integer.parseInt(focusTimeField.getText()) * 60; // Recalculate focus time
        }
    }

    private void showFallbackNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        showFallbackNotification("Test Notification", "This is a test notification!");
    }

    private void startTimer(int focusTime, int breakTime) {
        if (timeline != null) {
            timeline.stop();
        }

        if (!isPaused) {
            phaseTime = isFocusPhase ? focusTime : breakTime;
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (remainingSessionTime <= 0) {
                timerLabel.setText("Session Complete!");
                timeline.stop();
                sendNotification("Session Complete", "Your focus session is complete!");
                return;
            }

            phaseTime--;
            remainingSessionTime--;

            int minutes = phaseTime / 60;
            int seconds = phaseTime % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

            if (phaseTime <= 0) {
                if (isFocusPhase) {
                    // Focus phase ended, starting break
                    sendNotification("Break Started", "Time for a short break! Relax and recharge.");
                } else {
                    // Break phase ended, starting focus
                    sendNotification("Focus Phase Started", "Back to work! Stay focused.");
                }
                isFocusPhase = !isFocusPhase;
                startTimer(focusTime, breakTime);
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void startFocusPhase(int breakTime) {
        // Start focus phase timer
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            if (focusTimeRemaining > 0) {
                focusTimeRemaining--;
                remainingSessionTime--; // Decrement remaining session time
                updateTimerDisplay(focusTimeRemaining);
            } else {
                // Transition to break phase
                startBreakSession(breakTime);
            }
        });

        focusTimer = new Timeline(keyFrame);
        focusTimer.setCycleCount(Timeline.INDEFINITE);
        focusTimer.play();
    }

    private void startBreakSession(int breakTime) {
        breakTimeRemaining = breakTime * 60; // Convert minutes to seconds
        sendNotification("Break Started", "Time for a short break! Relax and recharge.");

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            if (breakTimeRemaining > 0) {
                breakTimeRemaining--;
                remainingSessionTime--; // Decrement remaining session time
                updateTimerDisplay(breakTimeRemaining);
            } else {
                // Break session is over, resume focus session
                focusTimer.stop(); // Stop the current timer
                resumeRemainingFocusTime();
            }
        });

        focusTimer = new Timeline(keyFrame);
        focusTimer.setCycleCount(Timeline.INDEFINITE);
        focusTimer.play();
    }


    private void resumeRemainingFocusTime() {
        // Ensure the remaining focus time continues
        focusTimeRemaining = remainingSessionTime;

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            if (focusTimeRemaining > 0) {
                focusTimeRemaining--;
                remainingSessionTime--; // Decrement remaining session time
                updateTimerDisplay(focusTimeRemaining);
            } else {
                // Full session is over
                focusTimer.stop(); // Stop the timer
                showAlert("Session Complete", "The entire focus session has ended.");
            }
        });

        focusTimer = new Timeline(keyFrame);
        focusTimer.setCycleCount(Timeline.INDEFINITE);
        focusTimer.play();
    }


    void updateTimerDisplay(int remainingTime) {
        // Mise à jour du texte du timer
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        
        // Mise à jour de l'arc de progression
        double progress = 1 - (remainingTime / (double) phaseTime);
        timerArc.setLength(progress * -360); // L'arc se remplit dans le sens anti-horaire
        
        // Mise à jour du label de phase
        phaseLabel.setText(isFocusPhase ? "FOCUS" : "PAUSE");
        
        // Mise à jour de la couleur de l'arc selon la phase
        String arcColor = isFocusPhase ? "#2ECC71" : "#F1C40F";
        timerArc.setStyle("-fx-fill: transparent; -fx-stroke: " + arcColor + "; -fx-stroke-width: 8;");
    }



    @FXML
    void pauseFocusSession() {
        if (timeline != null && !isPaused) {
            timeline.pause();
            isPaused = true;
        }
    }
    @FXML
    void resetFocusSession() {
        if (timeline != null) {
            timeline.stop();
        }
        timerLabel.setText("00:00");
        isPaused = false;
        isStarted = false;
    }

    void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void sendNotification(String title, String message) {
        System.out.println("Notification triggered: " + title + " - " + message);
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage("/icons/WhatsApp_Image_2024-11-23_at_18.21.51_f6de1cc7-removebg-preview.png"); // Ensure the correct path

                TrayIcon trayIcon = new TrayIcon(image, "Focus Timer");
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip("Focus Timer");

                tray.add(trayIcon);
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
                tray.remove(trayIcon); // Clean up after notification

            } catch (AWTException | NullPointerException e) {
                System.err.println("Error showing notification: " + e.getMessage());
                showFallbackNotification(title, message);
            }
        } else {
            System.err.println("System Tray not supported!");
            showFallbackNotification(title, message);
        }
    }





}