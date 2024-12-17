package com.example.manic_time;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AppLimiteControler {

    @FXML
    private TableColumn<ApplicationTime, String> appNameColumn;

    @FXML
    private TableColumn<ApplicationTime, String> timeLimitColumn;

    @FXML
    private TableColumn<ApplicationTime, String> remainingTimeColumn;

    @FXML
    private ComboBox<String> appComboBox;

    @FXML
    private TextField hoursField;

    @FXML
    private TextField minutesField;

    @FXML
    private TableView<ApplicationTime> timeLimitsTable;

    // Store timers for each application
    private final Map<String, TimerData> appTimers = new HashMap<>();

    @FXML
    public void initialize() {
        // Set up the table columns
        appNameColumn.setCellValueFactory(cellData -> cellData.getValue().appNameProperty());
        timeLimitColumn.setCellValueFactory(cellData -> cellData.getValue().timeLimitProperty());
        remainingTimeColumn.setCellValueFactory(cellData -> cellData.getValue().remainingTimeProperty());

        try {
            // Fetch installed applications
            List<String> installedApps = AppFetcher.getInstalledApplications();

            if (installedApps == null || installedApps.isEmpty()) {
                System.err.println("No installed applications found.");
            } else {
                System.out.println("Installed Applications: " + installedApps);
                appComboBox.getItems().addAll(installedApps);
            }
        } catch (Exception e) {
            System.err.println("Error fetching installed applications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddLimit() {
        String app = appComboBox.getValue();
        String hoursText = hoursField.getText();
        String minutesText = minutesField.getText();

        if (app == null || hoursText.isEmpty() || minutesText.isEmpty()) {
            showAlert("Error", "Please select an application and enter a valid time limit.");
            return;
        }

        try {
            int hours = Integer.parseInt(hoursText);
            int minutes = Integer.parseInt(minutesText);

            if (hours < 0 || minutes < 0 || minutes >= 60) {
                showAlert("Error", "Invalid time format. Hours must be >= 0 and minutes must be between 0 and 59.");
                hoursField.clear();
                minutesField.clear();
                return;
            }

            String timeLimit = String.format("%02d:%02d", hours, minutes);
            String remainingTime = timeLimit;

            // Prevent duplicate timers for the same app
            if (appTimers.containsKey(app)) {
                showAlert("Error", "A timer already exists for this application.");
                return;
            }

            // Add new entry to the ObservableList
            ApplicationTime newEntry = new ApplicationTime(app, timeLimit, remainingTime);
            timeLimitsTable.getItems().add(newEntry);

            // Set up a timer for this application
            startTimerForApp(app, hours, minutes);

            showAlert("Success", "Time limit added for " + app + ": " + timeLimit);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values for hours and minutes.");
            hoursField.clear();
            minutesField.clear();
        }
    }

    private void startTimerForApp(String app, int hours, int minutes) {
        AtomicInteger totalSeconds = new AtomicInteger((hours * 3600) + (minutes * 60));

        // Declare the timer variable outside of the timeline
        Timeline timer = new Timeline();
        Timeline appMonitorTimeline = new Timeline(); // Declare the app monitoring Timeline

        // Initialize the Timer Timeline with a KeyFrame
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            int remaining = totalSeconds.decrementAndGet();
            System.out.println("Remaining time for " + app + ": " + remaining);  // Debugging line

            if (remaining <= 0) {
                // Stop the timer when it reaches zero
                appTimers.remove(app);
                showAlert("Time's Up!", "Time limit for " + app + " has expired!");
                updateRemainingTime(app, 0);  // Update UI to show 00:00
                timer.stop();  // Directly stop the timer
                stopAppMonitoring(appMonitorTimeline);  // Stop the app status monitoring
            } else {
                updateRemainingTime(app, remaining);
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE); // Set to indefinite to keep the timer running

        // Monitor application status
        SimpleBooleanProperty isAppOpen = monitorApplicationStatus(app, appMonitorTimeline);
        isAppOpen.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("App " + app + " is open. Starting timer.");  // Debugging line
                timer.play();
            } else {
                System.out.println("App " + app + " is closed. Pausing timer.");  // Debugging line
                timer.pause();  // Pausing the timer directly
            }
        });

        // Start the timer if the app is already open
        if (isAppOpen.get()) {
            timer.play();
        }

        // Store the timer and associated data
        appTimers.put(app, new TimerData(timer, totalSeconds, isAppOpen, appMonitorTimeline));
    }

    private void stopAppMonitoring(Timeline appMonitorTimeline) {
        if (appMonitorTimeline != null) {
            appMonitorTimeline.stop();  // Stop the app monitoring Timeline
            System.out.println("Stopped app status monitoring.");
        }
    }

    private SimpleBooleanProperty monitorApplicationStatus(String app, Timeline appMonitorTimeline) {
        SimpleBooleanProperty isAppOpen = new SimpleBooleanProperty(false);

        // Use a Timeline to periodically check if the app is running
        appMonitorTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(5), event -> {
            boolean isRunning = AppFetcher.isAppRunning(app); // Check if the app is running
            isAppOpen.set(isRunning);
            System.out.println("App " + app + " is running: " + isRunning);  // Debugging line
        }));

        appMonitorTimeline.setCycleCount(Timeline.INDEFINITE);
        appMonitorTimeline.play(); // Start the monitoring Timeline

        return isAppOpen;
    }

    private void updateRemainingTime(String app, int remainingSeconds) {
        String remainingTime = String.format("%02d:%02d", remainingSeconds / 3600, (remainingSeconds % 3600) / 60);
        for (ApplicationTime entry : timeLimitsTable.getItems()) {
            if (entry.getAppName().equals(app)) {
                entry.setRemainingTime(remainingTime); // Ensure this updates the UI
                break;
            }
        }
    }


    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            System.out.println("Showing alert: " + message);  // Debugging line
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void handleSaveChanges() {
        // Save changes to time limits (can include saving to a file or database)
        showAlert("Save Changes", "All changes have been saved successfully.");
    }

    @FXML
    private void handleCancelTimer() {
        ApplicationTime selectedApp = timeLimitsTable.getSelectionModel().getSelectedItem();

        if (selectedApp == null) {
            showAlert("Error", "Please select an application to cancel the timer.");
            return;
        }

        String appName = selectedApp.getAppName();

        if (appTimers.containsKey(appName)) {
            TimerData timerData = appTimers.remove(appName);
            timerData.getTimer().stop();
            timeLimitsTable.getItems().remove(selectedApp);
            showAlert("Cancel Timer", "Timer for " + appName + " has been canceled.");
        } else {
            showAlert("Error", "No active timer found for the selected application.");
        }
    }

    private static class TimerData {
        private final Timeline timer;
        private final AtomicInteger remainingTime;
        private final SimpleBooleanProperty isAppOpen;

        public TimerData(Timeline timer, AtomicInteger remainingTime, SimpleBooleanProperty isAppOpen, Timeline appMonitorTimeline) {
            this.timer = timer;
            this.remainingTime = remainingTime;
            this.isAppOpen = isAppOpen;
        }

        public Timeline getTimer() {
            return timer;
        }

        public AtomicInteger getRemainingTime() {
            return remainingTime;
        }

        public SimpleBooleanProperty getIsAppOpen() {
            return isAppOpen;
        }
    }
}
