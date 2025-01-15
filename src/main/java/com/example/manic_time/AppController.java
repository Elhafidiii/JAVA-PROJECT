package com.example.manic_time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class AppController {

    @FXML
    private DatePicker datePicker_TM;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<ApplicationUsage> applicationsTable;

    @FXML
    private TableColumn<ApplicationUsage, String> appColumn;

    @FXML
    private TableColumn<ApplicationUsage, String> timeColumn;

    @FXML
    private Label totalTimeLabel;

    ObservableList<ApplicationUsage> masterData = FXCollections.observableArrayList();

    public void initialize() {
        // Associer les colonnes aux propriétés de la classe ApplicationUsage
        appColumn.setCellValueFactory(new PropertyValueFactory<>("application"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Ajouter une recherche dynamique
        FilteredList<ApplicationUsage> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(usage -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return usage.getApplication().toLowerCase().contains(newValue.toLowerCase());
            });
        });

        // Appliquer le tri sur les données filtrées
        SortedList<ApplicationUsage> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(applicationsTable.comparatorProperty());
        applicationsTable.setItems(sortedData);
    }

    @FXML
    private void handleShowApplications() {
        LocalDate selectedDate = datePicker_TM.getValue();
        if (selectedDate == null) {
            System.out.println("Veuillez sélectionner une date.");
            return;
        }

        masterData.setAll(fetchDataFromDatabase(selectedDate));

        // Calculer et afficher le temps total
        long totalTimeInSeconds = calculateTotalTime(masterData);
        totalTimeLabel.setText("Temps total : " + formatDuration(totalTimeInSeconds));
    }

    ObservableList<ApplicationUsage> fetchDataFromDatabase(LocalDate date) {
        ObservableList<ApplicationUsage> data = FXCollections.observableArrayList();

        String query = "SELECT nom_application, duree_utilisation FROM UtilisationApplication WHERE date_utilisation = ?";
        try (Connection connection = DatabaseConnection.getConnection(); // Classe de gestion des connexions DB
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

    long calculateTotalTime(ObservableList<ApplicationUsage> data) {
        long totalSeconds = 0;

        for (ApplicationUsage usage : data) {
            String time = usage.getTime(); // Format attendu : "HH:mm:ss"
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            totalSeconds += hours * 3600 + minutes * 60 + seconds;
        }

        return totalSeconds;
    }

    String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
