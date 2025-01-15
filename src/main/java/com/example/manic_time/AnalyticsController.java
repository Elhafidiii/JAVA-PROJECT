package com.example.manic_time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsController {

    @FXML
    BarChart<String, Number> usageChart;

    @FXML
    private CategoryAxis dayAxis;

    @FXML
    private NumberAxis timeAxis;

    @FXML
    ChoiceBox<String> weekSelector;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    ScatterChart<String, Number> scatterChart;

    @FXML
    LineChart<String, Number> lineChart;

    @FXML
    private Button barChartButton;

    @FXML
    private Button lineChartButton;

    @FXML
    private Button scatterChartButton;

    private LocalDate today = LocalDate.now();

    private Map<String, Map<String, Long>> cache = new HashMap<>();

    Map<String, String> dayTranslations = Map.of(
            "MONDAY", "Lundi",
            "TUESDAY", "Mardi",
            "WEDNESDAY", "Mercredi",
            "THURSDAY", "Jeudi",
            "FRIDAY", "Vendredi",
            "SATURDAY", "Samedi",
            "SUNDAY", "Dimanche"
    );

    @FXML
    private void initialize() {
        populateWeekSelector();
        populateChart(getStartOfWeek(today));
    }

    LocalDate getStartOfWeek(LocalDate date) {
        return date.with(java.time.DayOfWeek.MONDAY); // Fixer le début de la semaine au lundi
    }

    private LocalDate getEndOfWeek(LocalDate startOfWeek) {
        return startOfWeek.plusDays(6); // Fin de la semaine
    }

    void populateWeekSelector() {
        ObservableList<String> weeks = FXCollections.observableArrayList();

        for (int i = 0; i < 4; i++) {
            LocalDate startOfWeek = getStartOfWeek(today.minusWeeks(i));
            LocalDate endOfWeek = getEndOfWeek(startOfWeek);

            String weekLabel = String.format("Semaine du %s au %s",
                    startOfWeek.toString(), endOfWeek.toString());
            weeks.add(weekLabel);
        }

        weekSelector.setItems(weeks);
        weekSelector.getSelectionModel().selectFirst(); // Sélectionner la semaine actuelle par défaut
    }

    @FXML
    void handleWeekSelection() {
        int selectedIndex = weekSelector.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) return;

        LocalDate startOfSelectedWeek = getStartOfWeek(today.minusWeeks(selectedIndex));
        populateChart(startOfSelectedWeek);
    }

    void populateChart(LocalDate startOfWeek) {
        // Réinitialiser les graphiques
        usageChart.getData().clear();
        scatterChart.getData().clear();
        lineChart.getData().clear();
        loadingIndicator.setVisible(true);

        Map<String, Long> dailyUsage = fetchWeeklyData(startOfWeek);

        // Cachez l'indicateur de chargement une fois les données chargées
        loadingIndicator.setVisible(false);

        // Série pour le graphique à barres
        XYChart.Series<String, Number> seriesBar = new XYChart.Series<>();
        seriesBar.setName("Temps d'utilisation (en heures)");

        // Série pour le ScatterChart (points)
        XYChart.Series<String, Number> seriesScatter = new XYChart.Series<>();
        seriesScatter.setName("Points");

        // Série pour le LineChart (courbe)
        XYChart.Series<String, Number> seriesLine = new XYChart.Series<>();
        seriesLine.setName("Courbe");

        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plus(i, ChronoUnit.DAYS);
            String dayName = dayTranslations.getOrDefault(date.getDayOfWeek().toString(), date.getDayOfWeek().toString());
            long usageInSeconds = dailyUsage.getOrDefault(dayName, 0L);
            double usageInHours = usageInSeconds / 3600.0;

            // Ajouter des données au graphique à barres
            seriesBar.getData().add(new XYChart.Data<>(dayName, usageInHours));

            // Ajouter des points au ScatterChart
            seriesScatter.getData().add(new XYChart.Data<>(dayName, usageInHours));

            // Ajouter des points à la ligne pour le LineChart
            seriesLine.getData().add(new XYChart.Data<>(dayName, usageInHours));
        }

        // Ajouter une nouvelle série pour la moyenne
        XYChart.Series<String, Number> averageSeries = new XYChart.Series<>();
        averageSeries.setName("Moyenne hebdomadaire");

        // Calculer la moyenne
        double totalUsage = 0;
        int daysWithData = 0;
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plus(i, ChronoUnit.DAYS);
            String dayName = dayTranslations.getOrDefault(date.getDayOfWeek().toString(), date.getDayOfWeek().toString());
            long usageInSeconds = dailyUsage.getOrDefault(dayName, 0L);
            if (usageInSeconds > 0) {
                totalUsage += usageInSeconds / 3600.0;
                daysWithData++;
            }
        }
        
        double averageUsage = daysWithData > 0 ? totalUsage / daysWithData : 0;

        // Ajouter la ligne moyenne à tous les graphiques
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plus(i, ChronoUnit.DAYS);
            String dayName = dayTranslations.getOrDefault(date.getDayOfWeek().toString(), date.getDayOfWeek().toString());
            averageSeries.getData().add(new XYChart.Data<>(dayName, averageUsage));
        }

        // Ajouter les séries aux graphiques
        usageChart.getData().add(seriesBar);
        scatterChart.getData().add(seriesScatter);
        lineChart.getData().add(seriesLine);

        // Ajouter la série moyenne aux graphiques
        usageChart.getData().add(averageSeries);
        scatterChart.getData().add(averageSeries);
        lineChart.getData().add(averageSeries);

        // Styliser la ligne moyenne
        averageSeries.getNode().setStyle("-fx-stroke-dash-array: 5 5;"); // Ligne pointillée
        for (XYChart.Data<String, Number> data : averageSeries.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-background-color: red;");
            }
        }

        // Ajouter un événement de clic sur chaque barre du graphique
        for (XYChart.Data<String, Number> data : seriesBar.getData()) {
            data.getNode().setOnMouseClicked(event -> {
                String day = data.getXValue();
                long usageInSeconds = dailyUsage.getOrDefault(day, 0L);
                long hours = usageInSeconds / 3600;
                long minutes = (usageInSeconds % 3600) / 60;

                // Afficher les détails d'utilisation dans un message
                String message = String.format("Le temps d'utilisation pour %s est de : %d heures et %d minutes.", day, hours, minutes);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Détails de l'utilisation");
                alert.setHeaderText("Temps d'utilisation de " + day);
                alert.setContentText(message);
                alert.showAndWait();
            });
        }
    }

    Map<String, Long> fetchWeeklyData(LocalDate startOfWeek) {
        String weekKey = startOfWeek.toString();
        if (cache.containsKey(weekKey)) {
            return cache.get(weekKey);
        }

        Map<String, Long> usageMap = new HashMap<>();
        String query = "SELECT date_utilisation, SUM(TIME_TO_SEC(duree_utilisation)) AS total_usage " +
                "FROM UtilisationApplication " +
                "WHERE date_utilisation BETWEEN ? AND ? " +
                "GROUP BY date_utilisation";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(startOfWeek));
            stmt.setDate(2, Date.valueOf(getEndOfWeek(startOfWeek)));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate date = rs.getDate("date_utilisation").toLocalDate();
                long totalUsage = rs.getLong("total_usage");

                String dayName = dayTranslations.getOrDefault(date.getDayOfWeek().toString(), date.getDayOfWeek().toString());
                usageMap.put(dayName, totalUsage);
            }

            cache.put(weekKey, usageMap);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur lors de la récupération des données.");
        }

        return usageMap;
    }

    void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur s'est produite");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleBarChartButton() {
        usageChart.setVisible(true);
        lineChart.setVisible(false);
        scatterChart.setVisible(false);
        
        // Récupérer la semaine sélectionnée
        int selectedIndex = weekSelector.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            LocalDate startOfSelectedWeek = getStartOfWeek(today.minusWeeks(selectedIndex));
            populateChart(startOfSelectedWeek);
        }
    }

    @FXML
    void handleLineChartButton() {
        usageChart.setVisible(false);
        lineChart.setVisible(true);
        scatterChart.setVisible(false);
        
        // Récupérer la semaine sélectionnée
        int selectedIndex = weekSelector.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            LocalDate startOfSelectedWeek = getStartOfWeek(today.minusWeeks(selectedIndex));
            populateChart(startOfSelectedWeek);
        }
    }

    @FXML
    void handleScatterChartButton() {
        usageChart.setVisible(false);
        lineChart.setVisible(false);
        scatterChart.setVisible(true);
        
        // Récupérer la semaine sélectionnée
        int selectedIndex = weekSelector.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            LocalDate startOfSelectedWeek = getStartOfWeek(today.minusWeeks(selectedIndex));
            populateChart(startOfSelectedWeek);
        }
    }
}
