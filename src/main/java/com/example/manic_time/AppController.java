package com.example.manic_time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.time.LocalDate;

public class AppController {

    @FXML
    private DatePicker datePicker_TM;

    @FXML
    private TableView<ApplicationUsage> applicationsTable;

    @FXML
    private TableColumn<ApplicationUsage, String> appColumn;

    @FXML
    private TableColumn<ApplicationUsage, String> timeColumn;

    @FXML
    private TableColumn<ApplicationUsage, Image> iconColumn;



    public void initialize() {
        appColumn.setCellValueFactory(new PropertyValueFactory<>("application"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));
        iconColumn.setCellFactory(tc -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(item);
                    imageView.setFitWidth(32);  // Largeur de l'icône
                    imageView.setFitHeight(32); // Hauteur de l'icône
                    setGraphic(imageView);
                }
            }
        });
    }

    // Gestion de l'affichage des applications
    @FXML
    private void handleShowApplications() {
        LocalDate selectedDate = datePicker_TM.getValue();
        if (selectedDate == null) {
            System.out.println("Veuillez sélectionner une date.");
            return;
        }

        ObservableList<ApplicationUsage> data = fetchDataFromDatabase(selectedDate);
        applicationsTable.setItems(data);
    }

    // Extraction des données depuis la base
    private ObservableList<ApplicationUsage> fetchDataFromDatabase(LocalDate date) {
        ObservableList<ApplicationUsage> data = FXCollections.observableArrayList();

        String query = "SELECT nom_application, duree_utilisation, icone FROM UtilisationApplication1 WHERE date_utilisation = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/manic1", "root", "");
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date)); // Convertir LocalDate en java.sql.Date
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String appName = rs.getString("nom_application");
                String usageTime = rs.getString("duree_utilisation");

                byte[] iconBytes = rs.getBytes("icone");
                Image icon = null;
                if (iconBytes != null) {
                    icon = new Image(new ByteArrayInputStream(iconBytes));
                }

                data.add(new ApplicationUsage(appName, usageTime, icon));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
