package com.example.manic_time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppControllerTest {

    private AppController controller;

    @BeforeEach
    void setUp() {
        controller = new AppController();
    }

    @Test
    public void testCalculateTotalTime() {
        // Préparer les données de test
        Map<String, Integer> inputData = new HashMap<>();
        inputData.put("Task1", 120);  // Durée en minutes
        inputData.put("Task2", 180);  // Durée en minutes

        // Convertir en ObservableList d'ApplicationUsage
        ObservableList<ApplicationUsage> tasks = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : inputData.entrySet()) {
            //tasks.add(new ApplicationUsage(entry.getKey(), entry.getValue()));  // L'ajout ici est correct
        }

        // Instancier le contrôleur
        AppController appController = new AppController();

        // Appeler la méthode avec les données corrigées
        //int totalTime = appController.calculateTotalTime(tasks);

        // Vérifier les résultats attendus
        //assertEquals(300, totalTime, "Le temps total calculé est incorrect !");
    }






    @Test
    void testFormatDuration() {
        long totalSeconds = 12345; // Correspond à 03:25:45
        String formattedTime = controller.formatDuration(totalSeconds);
        assertEquals("03:25:45", formattedTime);
    }

    @Test
    void testFetchDataFromDatabase() throws SQLException {
        // Mock DatabaseConnection
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Simulate result set data
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("nom_application")).thenReturn("App1", "App2");
        when(mockResultSet.getString("duree_utilisation")).thenReturn("01:30:00", "02:15:00");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        try (var mockedStatic = Mockito.mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            ObservableList<ApplicationUsage> result = controller.fetchDataFromDatabase(LocalDate.of(2025, 1, 12));
            assertEquals(2, result.size());
            assertEquals("App1", result.get(0).getApplication());
            assertEquals("01:30:00", result.get(0).getTime());
        }
    }

    @Test
    void testSearchFilter() {
        ObservableList<ApplicationUsage> testData = FXCollections.observableArrayList(
                new ApplicationUsage("App1", "01:30:00"),
                new ApplicationUsage("Browser", "02:15:00")
        );

        controller.masterData = testData;

        // Simulate search field change
        FilteredList<ApplicationUsage> filteredData = new FilteredList<>(testData, p -> true);
        String searchQuery = "browser";
        filteredData.setPredicate(usage -> usage.getApplication().toLowerCase().contains(searchQuery.toLowerCase()));

        assertEquals(1, filteredData.size());
        assertEquals("Browser", filteredData.get(0).getApplication());
    }
}
