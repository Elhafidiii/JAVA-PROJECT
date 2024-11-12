package com.example.manic_time;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection connect() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/manic", "root", "");
            System.out.println("Connection to MySQL database successful!");
            return connection;
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
        return null;
    }
}