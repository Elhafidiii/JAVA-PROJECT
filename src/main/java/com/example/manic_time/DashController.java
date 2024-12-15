package com.example.manic_time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DashController {

    @FXML
    private TableView<Tache> daytasktable; // TableView pour les tâches du jour
    @FXML
    private TableColumn<Tache, String> titleColumn; // Colonne pour le titre de la tâche
    @FXML
    private TableColumn<Tache, String> descriptionColumn; // Colonne pour la description
    @FXML
    private TableColumn<Tache, String> statusColumn; // Colonne pour le statut de la tâche

    @FXML
    private TableView<ApplicationUsage> applicationsTable;

    @FXML
    private TableColumn<ApplicationUsage, String> appColumn;

    @FXML
    private TableColumn<ApplicationUsage, String> timeColumn;

    @FXML
    private Label totalTimeLabel;

    @FXML
    private BarChart<String, Number> weeklyUsageChart;

    @FXML
    private CategoryAxis dayAxis;

    @FXML
    private NumberAxis timeAxis;

    public void initialize() {
        // Charger les données des tâches du jour
        LocalDate today = LocalDate.now();
        ObservableList<Tache> taskData = fetchTasksFromDatabase(today);
        if (taskData != null) {
            daytasktable.setItems(taskData);

            // Configurer les colonnes de la TableView des tâches
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        }

        // Charger les données des applications du jour
        ObservableList<ApplicationUsage> appData = fetchDataFromDatabase(today);
        if (appData != null) {
            applicationsTable.setItems(appData);

            // Configurer les colonnes de la TableView des applications
            appColumn.setCellValueFactory(new PropertyValueFactory<>("application"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

            // Mettre à jour le total du temps
            long totalTimeInSeconds = calculateTotalTime(appData);
            totalTimeLabel.setText("Temps total : " + formatDuration(totalTimeInSeconds));
        }

        // Charger les données hebdomadaires
        loadWeeklyUsageChart();
    }

    private ObservableList<Tache> fetchTasksFromDatabase(LocalDate date) {
        ObservableList<Tache> data = FXCollections.observableArrayList();

        // Assurez-vous que le champ "dateTermine" est bien converti en date pure (sans l'heure)
        String query = "SELECT id, titre, description, dateTermine, estTermine FROM Tache WHERE DATE(dateTermine) = DATE(?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date)); // Passer la date actuelle
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String titre = rs.getString("titre");
                String description = rs.getString("description");
                LocalDate dateTermine = rs.getDate("dateTermine").toLocalDate();
                boolean estTermine = rs.getBoolean("estTermine");

                // Créer un objet Tache et l'ajouter à la liste
                data.add(new Tache(id, titre, description, dateTermine, estTermine));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }


    private ObservableList<ApplicationUsage> fetchDataFromDatabase(LocalDate date) {
        ObservableList<ApplicationUsage> data = FXCollections.observableArrayList();

        String query = "SELECT nom_application, duree_utilisation FROM UtilisationApplication WHERE date_utilisation = ?";
        try (Connection connection = DatabaseConnection.getConnection();
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

    private void loadWeeklyUsageChart() {
        weeklyUsageChart.getData().clear(); // Réinitialiser le graphique

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

        Map<String, Long> dailyUsage = fetchWeeklyData(startOfWeek);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Weekly Usage (hours)");

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startOfWeek.plusDays(i);
            String dayName = currentDate.getDayOfWeek().toString();
            long usageInSeconds = dailyUsage.getOrDefault(dayName, 0L);
            double usageInHours = usageInSeconds / 3600.0;

            series.getData().add(new XYChart.Data<>(dayName, usageInHours));
        }

        weeklyUsageChart.getData().add(series);
    }

    private Map<String, Long> fetchWeeklyData(LocalDate startOfWeek) {
        Map<String, Long> usageMap = new HashMap<>();

        String query = "SELECT date_utilisation, SUM(TIME_TO_SEC(duree_utilisation)) AS total_usage " +
                "FROM UtilisationApplication " +
                "WHERE date_utilisation BETWEEN ? AND ? " +
                "GROUP BY date_utilisation";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(startOfWeek));
            stmt.setDate(2, Date.valueOf(startOfWeek.plusDays(6)));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("date_utilisation").toLocalDate();
                long totalUsage = rs.getLong("total_usage");
                usageMap.put(date.getDayOfWeek().toString(), totalUsage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usageMap;
    }


}
