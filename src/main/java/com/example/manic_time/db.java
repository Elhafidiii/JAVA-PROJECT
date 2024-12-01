package com.example.manic_time;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class db {

    protected void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public static Connection connect() {
        db database = new db();
        Connection connection = null;
        try {
            // Charger le driver JDBC pour MySQL
            database.loadDriver();

            // Connexion à la base de données MySQL
            connection = DriverManager.getConnection("jdbc:mysql://localhost/manic1", "root", "");

            // Vérifier si la connexion est valide
            if (connection != null && connection.isValid(2)) {
                System.out.println("Connection to MySQL database successful!");
            } else {
                System.out.println("Connection to MySQL database failed!");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        }
        return connection;
    }

    // Méthode pour fermer la connexion (à appeler après utilisation)
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing connection.");
                e.printStackTrace();
            }
        }
    }
}
