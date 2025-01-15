package com.example.manic_time;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TacheControllerTest {

    private TacheController controller;
    private DatabaseConnection mockDatabaseConnection;
    private Connection mockConnection;
    private PreparedStatement mockStatement;

    @BeforeEach
    void setUp() throws SQLException {
        controller = new TacheController();

        // Mock DatabaseConnection et Connection
        mockDatabaseConnection = mock(DatabaseConnection.class);
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);

        when(DatabaseConnection.getConnection()).thenReturn(mockConnection);
    }

    @Test
    void testOnAddTaskClick_ValidInput() throws SQLException {
        // Préparation des données
        DatePicker mockDatePicker = mock(DatePicker.class);
        TextField mockTitleField = mock(TextField.class);
        TextField mockDescriptionField = mock(TextField.class);

        when(mockDatePicker.getValue()).thenReturn(LocalDate.now());
        when(mockTitleField.getText()).thenReturn("Test Task");
        when(mockDescriptionField.getText()).thenReturn("Test Description");

        controller.datePicker = mockDatePicker;
        controller.tasktitleField = mockTitleField;
        controller.taskdescriptionField = mockDescriptionField;

        // Mock de la requête SQL
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        // Exécution
        controller.onAddTaskClick();

        // Vérifications
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockTitleField, times(1)).clear();
        verify(mockDescriptionField, times(1)).clear();
        verify(mockDatePicker, times(1)).setValue(null);
    }

    @Test
    void testOnAddTaskClick_InvalidInput() {
        // Préparation des champs vides
        DatePicker mockDatePicker = mock(DatePicker.class);
        TextField mockTitleField = mock(TextField.class);
        TextField mockDescriptionField = mock(TextField.class);

        when(mockDatePicker.getValue()).thenReturn(null);
        when(mockTitleField.getText()).thenReturn("");
        when(mockDescriptionField.getText()).thenReturn("");

        controller.datePicker = mockDatePicker;
        controller.tasktitleField = mockTitleField;
        controller.taskdescriptionField = mockDescriptionField;

        // Exécution
        controller.onAddTaskClick();

        // Vérifications (aucune interaction avec la base de données)
        verify(mockTitleField, never()).clear();
        verify(mockDescriptionField, never()).clear();
    }

    @Test
    void testOnDeleteTaskClick_ValidTask() throws SQLException {
        // Préparation des données
        TableView<Tache> mockDayTable = mock(TableView.class);
        Tache mockTask = new Tache(1, "Test Task", "Description", LocalDate.now(), false);

        when(mockDayTable.getSelectionModel()).thenReturn(mock(TableView.TableViewSelectionModel.class));
        when(mockDayTable.getSelectionModel().getSelectedItem()).thenReturn(mockTask);

        controller.daytasktable = mockDayTable;

        // Mock de la requête SQL
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        // Exécution
        controller.onDeleteTaskClick();

        // Vérifications
        verify(mockStatement, times(1)).executeUpdate();
        assertFalse(controller.getTodayTasks().contains(mockTask));
    }

    @Test
    void testOnDeleteTaskClick_NoTaskSelected() throws SQLException {
        // Préparation sans tâche sélectionnée
        TableView<Tache> mockDayTable = mock(TableView.class);
        when(mockDayTable.getSelectionModel()).thenReturn(mock(TableView.TableViewSelectionModel.class));
        when(mockDayTable.getSelectionModel().getSelectedItem()).thenReturn(null);

        controller.daytasktable = mockDayTable;

        // Exécution
        controller.onDeleteTaskClick();

        // Vérifications (aucune interaction avec la base de données)
        verify(mockStatement, never()).executeUpdate();
    }

    @Test
    void testLoadTasks() throws SQLException {
        // Préparation des données simulées
        ObservableList<Tache> mockTasks = FXCollections.observableArrayList();
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false); // Une seule tâche
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("titre")).thenReturn("Sample Task");
        when(mockResultSet.getString("description")).thenReturn("Sample Description");
        when(mockResultSet.getDate("dateTermine")).thenReturn(java.sql.Date.valueOf(LocalDate.now()));
        when(mockResultSet.getBoolean("estTermine")).thenReturn(false);

        controller.loadTasks();

        // Vérifications
        assertEquals(1, controller.getTodayTasks().size());
        Tache loadedTask = controller.getTodayTasks().get(0);
        assertEquals("Sample Task", loadedTask.getTitre());
    }
}
