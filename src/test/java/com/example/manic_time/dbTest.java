package com.example.manic_time;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class dbTest {

    @Test
    void testConnect_SuccessfulConnection() throws SQLException, ClassNotFoundException {
        // Mock DriverManager et Connection
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isValid(2)).thenReturn(true);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection("jdbc:mysql://localhost/manic1", "root", ""))
                    .thenReturn(mockConnection);

            // Exécuter la méthode connect
            Connection connection = db.connect();

            // Vérifier les interactions et le résultat
            assertNotNull(connection);
            assertTrue(connection.isValid(2));
        }
    }

    @Test
    void testConnect_ClassNotFoundException() throws ClassNotFoundException {
        db database = spy(new db());
        doThrow(ClassNotFoundException.class).when(database).loadDriver();

        Connection connection = database.connect();

        // Vérifier que la connexion a échoué
        assertNull(connection);
    }


    @Test
    void testConnect_SQLException() throws SQLException {
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection("jdbc:mysql://localhost/manic1", "root", ""))
                    .thenThrow(SQLException.class);

            // Exécuter la méthode connect
            Connection connection = db.connect();

            // Vérifier que la connexion a échoué
            assertNull(connection);
        }
    }

    @Test
    void testCloseConnection_Success() throws SQLException {
        // Mock la connexion
        Connection mockConnection = mock(Connection.class);

        // Exécuter la méthode closeConnection
        db.closeConnection(mockConnection);

        // Vérifier que close a été appelé
        verify(mockConnection, times(1)).close();
    }

    @Test
    void testCloseConnection_NullConnection() {
        // Exécuter la méthode closeConnection avec une connexion null
        db.closeConnection(null);

        // Aucun comportement attendu, mais le test ne doit pas lever d'exception
        assertDoesNotThrow(() -> db.closeConnection(null));
    }

    @Test
    void testCloseConnection_SQLException() throws SQLException {
        // Mock la connexion et simuler une exception lors de la fermeture
        Connection mockConnection = mock(Connection.class);
        doThrow(SQLException.class).when(mockConnection).close();

        // Exécuter la méthode closeConnection
        db.closeConnection(mockConnection);

        // Vérifier que close a été appelé malgré l'exception
        verify(mockConnection, times(1)).close();
    }
}
