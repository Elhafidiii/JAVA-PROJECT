package com.example.manic_time;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import com.example.manic_time.AppTimeLimit;
import javafx.beans.property.SimpleStringProperty;

public class AppLimiteControler {

    @FXML
    private TableView<AppTimeLimit> timeLimitsTable;

    @FXML
    private ComboBox<String> appComboBox;

    @FXML
    private TextField hoursField;

    @FXML
    private TextField minutesField;

    @FXML
    private TableColumn<AppTimeLimit, String> appNameColumn;

    @FXML
    private TableColumn<AppTimeLimit, String> timeLimitColumn;

    @FXML
    private TableColumn<AppTimeLimit, String> remainingTimeColumn;

    private Map<String, Timeline> appTimers = new HashMap<>();

    @FXML
    public void initialize() {
        // Remplir le ComboBox avec toutes les applications installées
        List<String> installedApps = AppFetcher.getInstalledApplications();
        appComboBox.getItems().addAll(installedApps);

        // Configuration correcte des colonnes
        appNameColumn.setCellValueFactory(new PropertyValueFactory<>("appName"));
        timeLimitColumn.setCellValueFactory(new PropertyValueFactory<>("timeLimit"));
        remainingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("remainingTime"));

        // Validation des champs de temps
        hoursField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                hoursField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (!newVal.isEmpty() && Integer.parseInt(newVal) > 23) {
                hoursField.setText("23");
            }
        });

        minutesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                minutesField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (!newVal.isEmpty() && Integer.parseInt(newVal) > 59) {
                minutesField.setText("59");
            }
        });
    }

    @FXML
    private void handleAddLimit() {
        String selectedApp = appComboBox.getValue();
        if (selectedApp == null || hoursField.getText().isEmpty() && minutesField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner une application et entrer une durée.");
            return;
        }

        try {
            int hours = hoursField.getText().isEmpty() ? 0 : Integer.parseInt(hoursField.getText());
            int minutes = minutesField.getText().isEmpty() ? 0 : Integer.parseInt(minutesField.getText());
            int totalSeconds = (hours * 3600) + (minutes * 60);
            
            if (totalSeconds == 0) {
                showAlert("Erreur", "La durée doit être supérieure à 0.");
                return;
            }

            startTimer(selectedApp, totalSeconds);
            
            // Vider les champs
            appComboBox.setValue(null);
            hoursField.clear();
            minutesField.clear();
            
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des nombres valides.");
        }
    }

    private void startTimer(String app, int seconds) {
        AtomicInteger totalSeconds = new AtomicInteger(seconds);
        Timeline timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);

        AppTimeLimit appTimeLimit = new AppTimeLimit(
            app,
            formatTime(seconds),
            formatTime(seconds)
        );
        
        Platform.runLater(() -> {
            timeLimitsTable.getItems().add(appTimeLimit);
        });

        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            if (AppFetcher.isAppRunning(app)) {
                int remaining = totalSeconds.decrementAndGet();
                
                if (remaining <= 0) {
                    timer.stop();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Temps écoulé");
                        alert.setHeaderText(null);
                        alert.setContentText("La durée de travail dans " + app + " est terminée. Que souhaitez-vous faire ?");

                        ButtonType quitterBtn = new ButtonType("Quitter");
                        ButtonType ajouterTempsBtn = new ButtonType("Ajouter 5 minutes");
                        ButtonType resterBtn = new ButtonType("Rester");

                        alert.getButtonTypes().setAll(quitterBtn, ajouterTempsBtn, resterBtn);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent()) {
                            if (result.get() == quitterBtn) {
                                AppFetcher.closeApp(app);
                                timeLimitsTable.getItems().remove(appTimeLimit);
                            } else if (result.get() == ajouterTempsBtn) {
                                totalSeconds.set(5 * 60);
                                timer.play();
                            } else if (result.get() == resterBtn) {
                                timeLimitsTable.getItems().remove(appTimeLimit);
                            }
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        appTimeLimit.setRemainingTime(formatTime(remaining));
                        timeLimitsTable.refresh();
                    });
                }
            }
        }));

        appTimers.put(app, timer);
        timer.play();
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    @FXML
    private void handleSaveChanges() {
        // Implémenter la sauvegarde si nécessaire
    }

    @FXML
    private void handleCancelTimer() {
        AppTimeLimit selected = timeLimitsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Timeline timer = appTimers.get(selected.getAppName());
            if (timer != null) {
                timer.stop();
                appTimers.remove(selected.getAppName());
            }
            timeLimitsTable.getItems().remove(selected);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
