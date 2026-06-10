package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private static final String URL      = "jdbc:mysql://localhost:3306/GrandVistaResort";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found! Check your MySQL JAR.");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return null;
    }
}
