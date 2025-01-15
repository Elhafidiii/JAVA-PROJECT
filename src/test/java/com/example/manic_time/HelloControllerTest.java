package com.example.manic_time;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

class HelloControllerTest {

    private HelloController controller;
    private DatabaseConnection mockDatabaseConnection;

    @BeforeEach
    void setUp() {
        controller = new HelloController();
        mockDatabaseConnection = mock(DatabaseConnection.class);
    }

    @Test
    void testLoadTodaysTasks() throws SQLException {
        // Mock database interaction
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(DatabaseConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Mock result set
        when(mockResultSet.next()).thenReturn(true, true, false); // 2 tasks in the result set
        when(mockResultSet.getString("titre")).thenReturn("Task 1", "Task 2");
        when(mockResultSet.getString("description")).thenReturn("Description 1", "Description 2");
        when(mockResultSet.getBoolean("estTermine")).thenReturn(true, false);

        controller.loadTodaysTasks();

        TableView<HelloController.TaskData> taskTable = controller.daytasktable;
        assertNotNull(taskTable);
        ObservableList<HelloController.TaskData> tasks = taskTable.getItems();
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitre());
        assertEquals("Description 2", tasks.get(1).getDescription());
    }

    @Test
    void testConvertToSeconds() {
        long seconds = controller.convertToSeconds("02:15:30");
        assertEquals(8130, seconds); // 2 hours, 15 minutes, 30 seconds
    }

    @Test
    void testFormatDuration() {
        String formatted = controller.formatDuration(8130);
        assertEquals("02:15:30", formatted);
    }

    @Test
    void testLoadApplicationUsage() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(DatabaseConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false); // 1 app usage record
        when(mockResultSet.getString("nom_application")).thenReturn("App 1");
        when(mockResultSet.getString("duree_utilisation")).thenReturn("01:30:00");

        controller.loadApplicationUsage();

        TableView<HelloController.AppUsageData> appTable = controller.applicationsTable;
        assertNotNull(appTable);
        ObservableList<HelloController.AppUsageData> usage = appTable.getItems();
        assertEquals(1, usage.size());
        assertEquals("App 1", usage.get(0).getApplication());
        assertEquals("01:30:00", usage.get(0).getTime());
    }

    @Test
    void testShowError() {
        // For this test, you might need a testing framework for JavaFX like TestFX.
        // Ensure that the showError method displays an alert.
    }
}
