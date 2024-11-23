
package controller;

import java.sql.*;
import javax.swing.*;

public class DBConnection {  // Renaming to DBConnection to match Java conventions for Singleton

    private static final String username = "Jenina"; // Database username
    private static final String password = "qwertyui"; // Database password
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehiclerentalsystem?serverTimezone=UTC";

    
    private static java.sql.Connection conn; // Instance variable for the database connection
    private static DBConnection dbc; // Singleton instance of the DBConnection class

    

    // Private constructor to prevent instantiation from other classes
     public DBConnection() {
        conn = null;
        try {
            // Manually load the JDBC driver for MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            conn = DriverManager.getConnection(DB_URL, username, password);
            JOptionPane.showMessageDialog(null, "Database has been loaded successfully");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    // Singleton pattern: ensures only one instance of DBConnection exists
    public static DBConnection getInstance() {
        if (dbc == null) {
            dbc = new DBConnection(); // Create a new instance if it doesn't exist
        }
        return dbc; // Return the singleton instance
    }

    // Method to get the current connection
    public java.sql.Connection getConnection() {
        return conn; // Return the established connection
    }

    // Method to close the connection (optional)
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close(); // Close the connection if it is open
                JOptionPane.showMessageDialog(null, "Database connection closed");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error closing connection: " + ex.getMessage());
            ex.printStackTrace(); // Log the exception for debugging
        }
    }
}


