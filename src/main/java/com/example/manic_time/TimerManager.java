package com.example.manic_time;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerManager {

    static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    static int remainingTime = 300; // Timer duration in seconds (e.g., 5 minutes)

    public static void startAppTimer(String appName, int timeInSeconds) {
        executorService.scheduleAtFixedRate(() -> {
            if (AppChecker.isAppRunning(appName)) {
                runTimer();
            }
        }, 0, 5, TimeUnit.SECONDS); // Check every 5 seconds
    }

    static void runTimer() {
        executorService.scheduleAtFixedRate(() -> {
            if (remainingTime > 0) {
                System.out.println("Remaining time: " + remainingTime + " seconds");
                remainingTime--;
            } else {
                Platform.runLater(() -> showAlert("Time's up!", "The timer for your app has reached 00:00."));
                executorService.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS); // Timer ticks every second
    }

    static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
