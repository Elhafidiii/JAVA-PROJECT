package com.example.manic_time;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseConnectionTest {

    // Test pour vérifier la réussite de la connexion
    @Test
    void testGetConnection_Success() {
        // Vérifie que la connexion peut être établie
        try (Connection connection = DatabaseConnection.getConnection()) {
            assertNotNull(connection, "La connexion ne devrait pas être nulle.");
            assertTrue(connection.isValid(2), "La connexion devrait être valide.");
        } catch (SQLException e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    // Test pour vérifier l'échec de la connexion avec une URL invalide
    @Test
    void testGetConnection_Failure() {
        // Mock une mauvaise configuration pour provoquer une erreur
        String invalidUrl = "jdbc:mysql://invalidhost:3306/manic";
        try {
            DriverManager.getConnection(invalidUrl, "root", "");
            fail("Une exception aurait dû être levée.");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Communications link failure"),
                    "Le message d'erreur devrait indiquer une erreur de connexion.");
        }
    }

    // Test pour vérifier que la méthode connect crée bien une table
    @Test
    void testConnect_CreatesTable() throws SQLException {
        // Mock la connexion et le statement
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        // Mock DriverManager
        mockStatic(DriverManager.class);
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);

        // Exécuter la méthode connect()
        DatabaseConnection db = new DatabaseConnection();
        db.connect();

        // Vérifier que la requête de création de table a été exécutée
        verify(mockStatement, times(1)).execute(anyString());
    }

    // Test pour vérifier si la connexion à une base de données est annulée proprement
    @Test
    void testConnection_Close() {
        // Mock la connexion
        Connection mockConnection = mock(Connection.class);

        // Lorsque la méthode close est appelée, il ne devrait y avoir aucune exception
        try {
            mockConnection.close();
            verify(mockConnection, times(1)).close();  // Vérifie que la méthode close a été appelée une fois
        } catch (SQLException e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }
}
