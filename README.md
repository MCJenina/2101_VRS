package com.mycompany.vrsystem;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpClass {
    private String username;
    private String password;
    private String Name;
    private String address;
    private String contactNumber;
    private String licenseNumber;

    // Constructor to initialize values
    public SignUpClass(String username, String password, String Name, String address, String contactNumber, String licenseNumber) {
        this.username = username;
        this.password = password;
        this.Name = Name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.licenseNumber = licenseNumber;
    }

    public boolean registerCustomer() {
        String query = "INSERT INTO users  ( username, password, Name, address, contact_number, license_number) VALUES ( ?, ?, ?, ?, ?, ?)";

        try (Connection conn = VrSystem.conn(); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, Name);
            pst.setString(4, address);
            pst.setString(5, contactNumber);
            pst.setString(6, licenseNumber);

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Registration successful!");
            return true;
            } else {
                JOptionPane.showMessageDialog(null, "Registration failed. Please try again.");
             return false;
            }
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    } 
}




