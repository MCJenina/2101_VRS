package vrs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class SignUpClass {
    private String username;
    private String password;
    private String fullName;
    private String contactNumber;
    private String license;
    private String address;

    // Constructor
    public SignUpClass(String username, String password, String fullName, String contactNumber, String license, String address) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.license = license;
        this.address = address;
    }

    // Database registration logic
    public boolean registerCustomer() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vehiclerentalsystem", "Jenina", "qwertyuiop");

            // Check if username already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(checkQuery);
            pstmt.setString(1, username);
            var resultSet = pstmt.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.");
                return false;
            }

            // Insert new customer into the database
            String query = "INSERT INTO users (username, password, contact_number, address, Name, license_number, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, contactNumber);
            pstmt.setString(4, address);
            pstmt.setString(5, fullName);
            pstmt.setString(6, license);
            pstmt.setString(7, "customer"); // Default role

            int rowsAffected = pstmt.executeUpdate();

            // Check if insert was successful
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        } finally {
            // Close database resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
