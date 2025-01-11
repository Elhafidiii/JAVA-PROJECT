package com.example.manic_time;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.sql.*;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class HelloController implements Initializable {

    public static int currentUserId = 1; // À remplacer par la vraie valeur de l'utilisateur connecté

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField taskField;

    @FXML
    private ListView<Task> tasksListView;  // ListView instead of TableView

    private ObservableList<Task> tasksList = FXCollections.observableArrayList();



    @FXML
    private BorderPane mainPane; // L'élément BorderPane de votre vue principale

    @FXML
    private TableView<TaskData> daytasktable;
    @FXML
    private TableColumn<TaskData, String> titleColumn;
    @FXML
    private TableColumn<TaskData, String> descriptionColumn;
    @FXML
    private TableColumn<TaskData, String> statusColumn;

    @FXML
    private TableView<AppUsageData> applicationsTable;
    @FXML
    private TableColumn<AppUsageData, String> appColumn;
    @FXML
    private TableColumn<AppUsageData, String> timeColumn;

    @FXML
    private Label totalTimeLabel;

    @FXML
    private BarChart<String, Number> weeklyUsageChart;
    @FXML
    private CategoryAxis weeklyChartXAxis;
    @FXML
    private NumberAxis weeklyChartYAxis;

    private DatabaseConnection databaseConnection;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuration des colonnes pour la table des tâches
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Configuration des colonnes pour la table des applications
        appColumn.setCellValueFactory(new PropertyValueFactory<>("application"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Charger les données
        loadTodaysTasks();
        loadApplicationUsage();
        loadWeeklyChart();
    }

    private void loadTodaysTasks() {
        LocalDate today = LocalDate.now();
        String query = "SELECT titre, description, estTermine FROM tache WHERE DATE(dateTermine) = ? AND utilisateur_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(today));
            pstmt.setInt(2, currentUserId);
            
            ResultSet rs = pstmt.executeQuery();
            ObservableList<TaskData> tasks = FXCollections.observableArrayList();
            
            while (rs.next()) {
                tasks.add(new TaskData(
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getBoolean("estTermine") ? "Terminé" : "En cours"
                ));
            }
            
            daytasktable.setItems(tasks);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des tâches");
        }
    }

    private void loadApplicationUsage() {
        LocalDate today = LocalDate.now();
        String query = "SELECT nom_application, duree_utilisation FROM utilisationapplication WHERE DATE(date_utilisation) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(today));
            ResultSet rs = pstmt.executeQuery();
            ObservableList<AppUsageData> appUsage = FXCollections.observableArrayList();
            long totalSeconds = 0;
            
            while (rs.next()) {
                String duration = rs.getString("duree_utilisation");
                appUsage.add(new AppUsageData(
                    rs.getString("nom_application"),
                    duration
                ));
                totalSeconds += convertToSeconds(duration);
            }
            
            applicationsTable.setItems(appUsage);
            totalTimeLabel.setText("Temps total : " + formatDuration(totalSeconds));
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des données d'utilisation");
        }
    }

    private void loadWeeklyChart() {
        weeklyUsageChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Utilisation hebdomadaire");

        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        String query = "SELECT DATE(date_utilisation) as date, SUM(TIME_TO_SEC(duree_utilisation)) as total_seconds " +
                      "FROM utilisationapplication " +
                      "WHERE date_utilisation BETWEEN ? AND ? " +
                      "GROUP BY DATE(date_utilisation)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(startOfWeek));
            pstmt.setDate(2, java.sql.Date.valueOf(startOfWeek.plusDays(6)));
            
            ResultSet rs = pstmt.executeQuery();
            Map<LocalDate, Double> usageData = new HashMap<>();
            
            while (rs.next()) {
                LocalDate date = rs.getDate("date").toLocalDate();
                double hours = rs.getDouble("total_seconds") / 3600.0;
                usageData.put(date, hours);
            }
            
            // Remplir le graphique pour chaque jour de la semaine
            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = startOfWeek.plusDays(i);
                String dayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
                double hours = usageData.getOrDefault(currentDate, 0.0);
                series.getData().add(new XYChart.Data<>(dayName, hours));
            }
            
            weeklyUsageChart.getData().add(series);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du graphique hebdomadaire");
        }
    }

    // Classes de données pour les TableView
    public static class TaskData {
        private final SimpleStringProperty titre;
        private final SimpleStringProperty description;
        private final SimpleStringProperty status;

        public TaskData(String titre, String description, String status) {
            this.titre = new SimpleStringProperty(titre);
            this.description = new SimpleStringProperty(description);
            this.status = new SimpleStringProperty(status);
        }

        // Getters
        public String getTitre() { return titre.get(); }
        public String getDescription() { return description.get(); }
        public String getStatus() { return status.get(); }
    }

    public static class AppUsageData {
        private final SimpleStringProperty application;
        private final SimpleStringProperty time;

        public AppUsageData(String application, String time) {
            this.application = new SimpleStringProperty(application);
            this.time = new SimpleStringProperty(time);
        }

        // Getters
        public String getApplication() { return application.get(); }
        public String getTime() { return time.get(); }
    }

    @FXML
    protected void onTMSystemClick() {
        try {
            // Charger la vue TM System
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard-view.fxml"));
            Parent tmSystemView = loader.load();

            // Remplacer le contenu central
            mainPane.setCenter(tmSystemView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue TM System.");
        }
    }


    @FXML
    protected void onDashboardClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dash.fxml"));
            Parent dashboardView = loader.load();
            mainPane.setCenter(dashboardView); // Charger la vue "Dashboard"
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSmartClick() {
        try {
            // Charger la vue TM System
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tm-system-view.fxml"));
            Parent Smart = loader.load();

            // Remplacer le contenu central
            mainPane.setCenter(Smart);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la Smart timing.");
        }
    }

    @FXML
    protected void onPromodoClick() {
        try {
            // Charger la vue TM System
            FXMLLoader loader = new FXMLLoader(getClass().getResource("focussession-view.fxml"));
            Parent Smart = loader.load();

            // Remplacer le contenu central
            mainPane.setCenter(Smart);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la Smart timing.");
        }
    }

    @FXML
    protected void onAnalyticsClick() {
        try {
            // Charger la vue TM System
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
            Parent Analytics = loader.load();

            // Remplacer le contenu central
            mainPane.setCenter(Analytics);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de Analytics.");
        }
    }

    @FXML
    protected void onAppLimitClick() {
        try {
            // Charger la vue TM System
            FXMLLoader loader = new FXMLLoader(getClass().getResource("app-limite.fxml"));
            Parent Analytics = loader.load();

            // Remplacer le contenu central
            mainPane.setCenter(Analytics);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de scenes.");
        }
    }

    // Méthode pour fermer l'application
    @FXML
    private void onCloseClick() {
        // Ferme l'application
        Platform.exit();
    }

    @FXML
    private Button btnDashboard;


    // Méthode pour déconnecter l'utilisateur et afficher la page de connexion

    @FXML
    private void onLogOutClick() {
        try {
            // Charger la scène de connexion depuis le bon chemin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manic_time/Login-view.fxml"));
            Scene loginScene = new Scene(loader.load(), 1000, 700); // Taille de la scène (1000x700)

            // Obtenir le stage actuel
            Stage currentStage = (Stage) btnDashboard.getScene().getWindow();

            // Changer de scène et définir le titre
            currentStage.setScene(loginScene);
            currentStage.setTitle("MANICTIME");

        } catch (IOException e) {
            e.printStackTrace();
            // Afficher une alerte en cas d'erreur
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Erreur de chargement de la page de connexion");
            alert.showAndWait();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private long convertToSeconds(String timeStr) {
        String[] parts = timeStr.split(":");
        return Long.parseLong(parts[0]) * 3600 + 
               Long.parseLong(parts[1]) * 60 + 
               Long.parseLong(parts[2]);
    }

    private String formatDuration(long seconds) {
        return String.format("%02d:%02d:%02d", 
            seconds / 3600, 
            (seconds % 3600) / 60, 
            seconds % 60);
    }

}








