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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.sql.*;
import java.time.LocalDate;

public class HelloController {

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
}








