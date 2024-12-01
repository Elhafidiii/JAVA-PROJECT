package com.example.manic_time;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/manic1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données : " + e.getMessage(), e);
        }
    }

    public void connect() {
        try (Connection connection = getConnection()) {
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    task_date DATE NOT NULL,
                    task_description VARCHAR(255) NOT NULL
                )
            """;
            connection.createStatement().execute(createTableQuery);
            System.out.println("Base de données et table prêtes !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion ou création de la table : " + e.getMessage());
        }
    }
}
