package com.example.manic_time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import java.sql.*;
import java.time.LocalDate;

public class DashController {

    @FXML
    private ListView<tachesController.Task> todayTasksListView;
    @FXML
    private DatePicker datePicker_TM;
    @FXML
    private TableView<ApplicationUsage> applicationsTable;
    @FXML
    private TableColumn<ApplicationUsage, String> appColumn;
    @FXML
    private TableColumn<ApplicationUsage, String> timeColumn;
    @FXML
    private Label totalTimeLabel;

    private ObservableList<tachesController.Task> todayTasks = FXCollections.observableArrayList();
    private tachesController tachesCtrl = new tachesController(); // Instance pour accéder aux tâches

    public void initialize() {
        // Initialisation des tâches
        todayTasksListView.setItems(todayTasks);
        todayTasksListView.setCellFactory(param -> new TaskCell());
        loadTodayTasks();

        // Initialisation des applications
        appColumn.setCellValueFactory(new PropertyValueFactory<>("application"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

    }

    private void loadTodayTasks() {
        LocalDate today = LocalDate.now();
        tachesCtrl.loadTasksFromDatabase(); // Charger toutes les tâches

        todayTasks.setAll(
                tachesCtrl.getTodayTasks().stream()
                        .filter(task -> task.getDate().isEqual(today))
                        .toList()
        );
    }

    @FXML
    private void handleShowApplications() {
        LocalDate selectedDate = datePicker_TM.getValue();
        if (selectedDate == null) {
            System.out.println("Veuillez sélectionner une date.");
            return;
        }

        ObservableList<ApplicationUsage> data = fetchDataFromDatabase(selectedDate);
        applicationsTable.setItems(data);

        long totalTimeInSeconds = calculateTotalTime(data);
        totalTimeLabel.setText("Temps total : " + formatDuration(totalTimeInSeconds));
    }

    private ObservableList<ApplicationUsage> fetchDataFromDatabase(LocalDate date) {
        ObservableList<ApplicationUsage> data = FXCollections.observableArrayList();

        String query = "SELECT nom_application, duree_utilisation FROM UtilisationApplication WHERE date_utilisation = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/manic", "root", "");
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String appName = rs.getString("nom_application");
                String usageTime = rs.getString("duree_utilisation");

                data.add(new ApplicationUsage(appName, usageTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    private long calculateTotalTime(ObservableList<ApplicationUsage> data) {
        long totalSeconds = 0;

        for (ApplicationUsage usage : data) {
            String time = usage.getTime(); // Expected format: "HH:mm:ss"
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            totalSeconds += hours * 3600 + minutes * 60 + seconds;
        }

        return totalSeconds;
    }

    private String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static class TaskCell extends ListCell<tachesController.Task> {
        @Override
        protected void updateItem(tachesController.Task task, boolean empty) {
            super.updateItem(task, empty);

            if (task != null) {
                setText(task.toString());
                if (task.isCompleted()) {
                    setStyle("-fx-background-color: blue; -fx-text-fill: white;");
                } else {
                    setStyle("-fx-background-color: white; -fx-text-fill: black;");
                }
            } else {
                setText(null);
                setStyle(null);
            }
        }
    }
}
