package com.example.manic_time;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/manic";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Méthode pour charger explicitement le driver JDBC
    private static void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    // Méthode pour établir une connexion à la base de données
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Charger le driver JDBC pour MySQL
            loadDriver();

            // Etablir la connexion
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Vérifier la validité de la connexion
            if (connection.isValid(2)) {
                System.out.println("Connexion réussie à la base de données MySQL !");
            } else {
                System.out.println("Connexion échouée à la base de données MySQL !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Pilote JDBC introuvable !");
            e.printStackTrace();
        }
        return connection;
    }

    // Méthode pour fermer la connexion
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture de la connexion.");
                e.printStackTrace();
            }
        }
    }

    // Méthode pour créer les tables si elles n'existent pas
    public void connect() {
        String createTableQueryUtilisateur = """
                    create table IF NOT EXISTS Utilisateur(id int auto_increment primary key , nom Varchar(100) Not NULL, email Varchar(100) Not Null,motDePass varchar(100) NOT NULL);""";
        String createTableQueryTache = """
                    create table IF NOT EXISTS tache(id int auto_increment primary key , titre Varchar(255) Not NULL,utilisateur_id INT, description longtext ,estTermine Boolean ,dateTermine DATE, FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id));""";
        String createTableQueryPromodo = """
                    create table IF NOT EXISTS Promodo(id int auto_increment primary key , type VARCHAR(50) Not NULL,duree TIME NOT NULL ,utilisateur_id int,FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id));""";
        String createTableQueryUtilisationApplication = """
                    create table IF NOT EXISTS UtilisationApplication (id INT AUTO_INCREMENT PRIMARY KEY, nom_application VARCHAR(255) NOT NULL, duree_utilisation TIME NOT NULL,date_utilisation DATE NOT NULL);""";

        try (Connection connection = getConnection()) {
            if (connection != null) {
                executeQuery(connection, createTableQueryUtilisateur);
                executeQuery(connection, createTableQueryTache);
                executeQuery(connection, createTableQueryPromodo);
                executeQuery(connection, createTableQueryUtilisationApplication);
                System.out.println("Base de données et tables prêtes !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion ou création des tables : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour exécuter une requête
    private void executeQuery(Connection connection, String query) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.execute();
        }
    }
}
