    package vrs;

import java.awt.print.PrinterException;
    import java.sql.*;
    import javax.swing.JOptionPane;
    import java.time.LocalDate;
    import java.time.ZoneId;
    import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
    import javax.swing.table.DefaultTableModel;
import vrs.CustomerDashboard;
import vrs.SessionManager;
    
    public class Returncar extends javax.swing.JFrame {
    private Connection connection;
    private final int loggedInUserId;

    public Returncar() {
        this.loggedInUserId = SessionManager.getCustomerId(); // Get user ID from Session Manager
        System.out.println("Logged In User ID: " + loggedInUserId); // Debugging line to check user ID
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Return Car");
        connectDatabase();
        loadActiveBookings();

        // Adding ListSelectionListener to the table
        ReturnCarsTable.getSelectionModel().addListSelectionListener(e -> {
            // Check if a row is selected
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ReturnCarsTable.getSelectedRow();

                if (selectedRow >= 0) {
                    // Get the booking details from the selected row
                    int bookingId = (int) ReturnCarsTable.getValueAt(selectedRow, 0);
                    Date bookingReturnDate = (Date) ReturnCarsTable.getValueAt(selectedRow, 2); // Get the booking's expected return date

                    // Update the text field with the return date from the table
                    if (bookingReturnDate != null) {
                        bookingDateTextField.setText(bookingReturnDate.toString());  // Display the date in the TextField
                    } else {
                        bookingDateTextField.setText("");  // Clear text field if no date
                    }
                }
            }
        });
    }

    // Method to connect to the database
    private Connection connectDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/vehiclerentalsystem?useSSL=false&serverTimezone=UTC";
            String user = "Jenina";
            String password = "qwertyuiop";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage());
        }
        return connection;
    }
    // Method to load active bookings for the logged-in user
    private void loadActiveBookings() {
    // Modified query to join ReturnCarsTable with CarsTable to fetch model based on car_id
    // Since return_date was removed from booking, this query will no longer attempt to fetch it
    String query = "SELECT r.booking_id, c.model, r.return_date " +
                   "FROM booking r " +
                   "JOIN cars c ON r.car_id = c.car_id " +
                   "WHERE r.user_id = ? AND r.status = 'active'";

    try (PreparedStatement pst = connection.prepareStatement(query)) {
        // Set the logged-in user's ID in the query
        pst.setInt(1, loggedInUserId);

        // Execute the query
        ResultSet rs = pst.executeQuery();

        // Clear existing rows in the table
        DefaultTableModel model = (DefaultTableModel) ReturnCarsTable.getModel();
        model.setRowCount(0); // Reset the table rows before adding new data

        // Check if there are results in the database
        boolean found = false;

        // Populate the table with active booking data
        while (rs.next()) { 
            int bookingId = rs.getInt("booking_id");
            String carModel = rs.getString("model"); // Retrieve the model name
            Date startDate = rs.getDate("return_date");

            // Add data to the table row
            model.addRow(new Object[]{bookingId, carModel, startDate});
            found = true; // Data found
        }

        // If no data is found, show a message
        if (!found) {
            JOptionPane.showMessageDialog(this, "No active bookings found for the logged-in user.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load active bookings: " + e.getMessage());
    }
}
    private boolean returnVehicle(int carId, int bookingId, LocalDate userReturnDate, long lateFee, long damageFee) {
    String getBookingDetailsQuery = "SELECT return_date, car_id, user_id FROM booking WHERE booking_id = ? AND status = 'active'";
    String updateBookingStatusQuery = "UPDATE booking SET status = 'returned', return_date = ? WHERE booking_id = ?";
    String updateCarStatusQuery = "UPDATE cars SET status = 'Available' WHERE car_id = ?";
    String getCarPriceQuery = "SELECT price FROM cars WHERE car_id = ?"; 

    try {
        // Begin transaction
        connection.setAutoCommit(false);

        // Retrieve booking details from the database
        LocalDate bookingDate = null;
        LocalDate bookingReturnDate = null;
        int userId = -1;

        try (PreparedStatement ps = connection.prepareStatement(getBookingDetailsQuery)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                bookingDate = rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null;
                bookingReturnDate = rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null;
                carId = rs.getInt("car_id");
                userId = rs.getInt("user_id");
            } else {
                JOptionPane.showMessageDialog(this, "Booking not found or already returned.");
                return false;
            }
        }

        if (bookingDate == null || bookingReturnDate == null) {
            JOptionPane.showMessageDialog(this, "Booking date or return date is not available for this booking.");
            return false;
        }

        // Calculate late fee
        long daysLate = ChronoUnit.DAYS.between(bookingReturnDate, userReturnDate);
        if (daysLate > 0) {
            // Get car price from the database
            long carPrice = 0;
            try (PreparedStatement ps = connection.prepareStatement(getCarPriceQuery)) {
                ps.setInt(1, carId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    carPrice = rs.getLong("price");  
                }
            }

            // If the vehicle is returned late, apply late fee
            lateFee = carPrice * 2;  
            JOptionPane.showMessageDialog(this, "You are " + daysLate + " days late. Late fee: PHP " + lateFee);
        } else {
            JOptionPane.showMessageDialog(this, "Returned on time. No late fee.");
        }

        // Insert return data into the 'returns' table
        if (!insertReturnData(bookingId, java.sql.Date.valueOf(userReturnDate), userId, carId, lateFee, damageFee)) {
            JOptionPane.showMessageDialog(this, "Failed to insert return data.");
            connection.rollback(); // Rollback on failure
            return false;
        }

        // Update booking status to 'returned' and set the return date
        try (PreparedStatement ps = connection.prepareStatement(updateBookingStatusQuery)) {
            ps.setDate(1, java.sql.Date.valueOf(userReturnDate));
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }

        // Update car status to 'Available'
        try (PreparedStatement ps = connection.prepareStatement(updateCarStatusQuery)) {
            ps.setInt(1, carId);
            ps.executeUpdate();
        }

        // Commit transaction
        connection.commit();
        JOptionPane.showMessageDialog(this, "Vehicle returned successfully!");
        return true;

    } catch (SQLException e) {
        try {
            connection.rollback(); // Rollback on error
        } catch (SQLException rollbackException) {
            rollbackException.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Error processing return: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            connection.setAutoCommit(true); // Reset auto-commit to true
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return false;
}

private boolean insertReturnData(int bookingId, java.sql.Date userReturnDate, int car_id, int userId, long lateFee, long damageFee) {
    String insertQuery = "INSERT INTO returns (booking_id, return_date, car_id, user_id, late_fee, damage_fee) VALUES (?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
        ps.setInt(1, bookingId);
        ps.setDate(2, userReturnDate); 
        ps.setInt(3, car_id);  
        ps.setInt(4, userId);  
        ps.setLong(5, lateFee);  
        ps.setLong(6, damageFee); 

        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;  
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to insert return data: " + e.getMessage());
        return false;
    }
}

private void returnVehicleAndCheckDamage(int carId, int bookingId) throws SQLException {
    // Mark vehicle as under maintenance if damaged
    String updateQuery = "UPDATE cars SET status = 'Under Maintenance' WHERE car_id = ?";
    
    try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
        stmt.setInt(1, carId);  
        stmt.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error marking vehicle as under maintenance.");
        return;
    }

    // Log damage
    String damageLogQuery = "INSERT INTO damage_logs (car_id, booking_id, damage_reported) VALUES (?, ?, ?)";
    
    try (PreparedStatement logStmt = connection.prepareStatement(damageLogQuery)) {
        logStmt.setInt(1, carId);  
        logStmt.setInt(2, bookingId);  
        logStmt.setBoolean(3, true);  
        logStmt.executeUpdate();  
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error logging damage details.");
    }
}
private boolean updateBookingStatus(int bookingId) {
    try {
        // Database connection
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vehiclerentalsystem?useSSL=false&serverTimezone=UTC", "Jenina","qwertyuiop");
        String updateBookingStatusQuery = "UPDATE booking SET status = 'returned' WHERE booking_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(updateBookingStatusQuery)) {
            pstmt.setInt(1, bookingId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Successfully updated the status
                return true;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return false; // If update fails
}
private int getCarIdFromBooking(int bookingId) {
    int carId = -1;
    String query = "SELECT car_id FROM booking WHERE booking_id = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, bookingId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            carId = rs.getInt("car_id");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error retrieving car_id: " + ex.getMessage());
    }
    return carId;
}

// Method to calculate late fee
private long calculateLateFee(LocalDate bookingDate, LocalDate userReturnDate) {
    // Your late fee calculation logic here
    // Example: if user returns later than the booking date, charge a fee
    long daysLate = ChronoUnit.DAYS.between(bookingDate, userReturnDate);
    if (daysLate > 0) {
        return daysLate * 100;  // Assuming 100 is the daily late fee
    }
    return 0;
}




// Method to mark the vehicle as under maintenance
private void markVehicleAsUnderMaintenance(int carId, int bookingId) throws SQLException {
    if (connection == null || connection.isClosed()) {
        connection = connectDatabase(); // Reconnect if necessary
    }

    String updateCarQuery = "UPDATE cars SET status = 'Under Maintenance' WHERE car_id = ?";
    String updateBookingQuery = "UPDATE booking SET status = 'Under Maintenance' WHERE car_id = ? AND booking_id = ?";

    try {
        // Start a transaction
        connection.setAutoCommit(false);

        // Update the car status
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateCarQuery)) {
            preparedStatement.setInt(1, carId);
            int carRowsAffected = preparedStatement.executeUpdate();
            if (carRowsAffected == 0) {
                JOptionPane.showMessageDialog(this, "No such vehicle found to mark as under maintenance.");
                connection.rollback(); // Rollback if no car is found
                return;
            }
        }

        // Update the booking status
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookingQuery)) {
            preparedStatement.setInt(1, carId);
            preparedStatement.setInt(2, bookingId);
            int bookingRowsAffected = preparedStatement.executeUpdate();
            if (bookingRowsAffected == 0) {
                JOptionPane.showMessageDialog(this, "Booking not found to mark as under maintenance.");
                connection.rollback(); // Rollback if no booking is found
                return;
            }
        }

        // Commit the transaction if everything was successful
        connection.commit();
        JOptionPane.showMessageDialog(this, "Vehicle marked as under maintenance.");
    } catch (SQLException ex) {
        // Handle SQL exception and rollback transaction if necessary
        if (connection != null) {
            connection.rollback();
        }
        Logger.getLogger(Returncar.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "An error occurred while updating the maintenance status.");
    } finally {
        // Reset auto-commit to true after transaction
        if (connection != null) {
            connection.setAutoCommit(true);
        }
    }
}

// Method to fetch the vehicle's price from the database
private long getVehiclePrice(int carId) {
    long vehiclePrice = 0;
    String query = "SELECT Price FROM cars WHERE car_id = ?";  // Query to get the price from 'cars' table
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, carId);  // Set the car_id parameter to fetch the price
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            vehiclePrice = rs.getLong("Price");  // Fetch the vehicle price from the 'Price' column
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error retrieving vehicle price: " + ex.getMessage());
    }
    return vehiclePrice;
}



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ReturnjButton1 = new javax.swing.JButton();
        CanceljButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ReturnCarsTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        damageCheckbox = new javax.swing.JCheckBox();
        bookingDateTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        Print = new javax.swing.JButton();
        returnDateChooser = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 200, 105));

        jLabel1.setFont(new java.awt.Font("Mongolian Baiti", 1, 24)); // NOI18N
        jLabel1.setText("Return Car");

        ReturnjButton1.setText("Return");
        ReturnjButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReturnjButton1ActionPerformed(evt);
            }
        });

        CanceljButton2.setText("Cancel");
        CanceljButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanceljButton2ActionPerformed(evt);
            }
        });

        ReturnCarsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Booking ID", "Model", "Return Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(ReturnCarsTable);

        jLabel7.setFont(new java.awt.Font("Rockwell", 0, 10)); // NOI18N
        jLabel7.setText("Copyright - BSIT 2101(2024-2025). All Rights Reserved");

        jLabel2.setText("Retrun Date: ");

        damageCheckbox.setText("Is the vehicle damage?");
        damageCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                damageCheckboxActionPerformed(evt);
            }
        });

        jLabel3.setText("Booking Date: ");

        Print.setText("Print Receipt");
        Print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ReturnjButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CanceljButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 184, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(damageCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bookingDateTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Print)
                            .addComponent(returnDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(163, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 98, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ReturnjButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CanceljButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bookingDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(returnDateChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addComponent(damageCheckbox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Print)
                        .addGap(13, 13, 13))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 665, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ReturnjButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReturnjButton1ActionPerformed
 
    // Get the selected booking from the table
    int selectedRow = ReturnCarsTable.getSelectedRow();

    // Check if a booking is selected
    if (selectedRow >= 0) {
        // Get the booking details
        int bookingId = (int) ReturnCarsTable.getValueAt(selectedRow, 0);
        Date bookingReturnDate = (Date) ReturnCarsTable.getValueAt(selectedRow, 2); // Get the booking's expected return date

        // Check if bookingReturnDate is null and handle appropriately
        if (bookingReturnDate != null) {
            // Set the booking return date in the text field (assuming it's a JTextField)
            bookingDateTextField.setText(bookingReturnDate.toString());  // Display the date in the TextField
        } else {
            JOptionPane.showMessageDialog(this, "No return date available for this booking.");
            return;  // Exit if no return date is available
        }

        // Get the return date selected by the user (from JDateChooser or similar component)
        java.util.Date userReturnDate = returnDateChooser.getDate();  // Get the selected date from JDateChooser

        // Check if the user has selected a return date
        if (userReturnDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a return date.");
            return;  // Exit if no date is selected
        }

        // Convert java.util.Date to LocalDate
        LocalDate localUserReturnDate = userReturnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Convert the booking's return date (java.sql.Date) to LocalDate
        LocalDate localBookingReturnDate = bookingReturnDate.toLocalDate();

        // Calculate late fee if necessary
        long lateFee = calculateLateFee(localBookingReturnDate, localUserReturnDate);

        // Retrieve car_id from the database using bookingId
        int carId = getCarIdFromBooking(bookingId);

        // Get the vehicle price for the carId (you can display or calculate based on this)
        long vehiclePrice = getVehiclePrice(carId);

        // Check if the damage checkbox is selected and calculate damage fee only if it is
        long damageFee = 0;
        if (damageCheckbox.isSelected()) {
            damageFee = 3000; // Assuming damage fee is a fixed amount
            try {
                // Call markVehicleAsUnderMaintenance to handle the vehicle damage and maintenance marking
                markVehicleAsUnderMaintenance(carId, bookingId); // Mark vehicle as under maintenance
                // Optionally log damage details
                returnVehicleAndCheckDamage(carId, bookingId);
                JOptionPane.showMessageDialog(this, "Vehicle is under maintenance due to damage. It cannot be booked by other customers until repaired.");
            } catch (SQLException ex) {
                Logger.getLogger(Returncar.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "An error occurred while marking the vehicle as under maintenance.");
            }
        }

        // Display a notice with the fees before returning the vehicle
        String feesMessage = "Late Fee: Php " + lateFee + "\n" +
                             "Damage Fee: Php " + damageFee + "\n" +
                             "Total: Php " + (lateFee + damageFee);
        int confirmReturn = JOptionPane.showConfirmDialog(this, feesMessage + "\n\nDo you want to proceed with the return?", "Confirm Return", JOptionPane.YES_NO_OPTION);

        if (confirmReturn == JOptionPane.YES_OPTION) {
            // Now, insert the return data into the returns table
            boolean isReturnSuccessful = returnVehicle(carId, bookingId, localUserReturnDate, lateFee, damageFee);

            if (isReturnSuccessful) {
                // After returning, update the booking status to "returned"
                updateBookingStatus(bookingId);

                // After returning, remove the row from the table
                DefaultTableModel model = (DefaultTableModel) ReturnCarsTable.getModel();
                model.removeRow(selectedRow);

                JOptionPane.showMessageDialog(this, "Vehicle returned successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Return failed. Please check your return details.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Return cancelled.");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a booking to return.");
    }

        }//GEN-LAST:event_ReturnjButton1ActionPerformed

    private void CanceljButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanceljButton2ActionPerformed
        // TODO add your handling code here:
        CustomerDashboard customerDashboard = new CustomerDashboard();
        customerDashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_CanceljButton2ActionPerformed

    private void damageCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_damageCheckboxActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_damageCheckboxActionPerformed

    private void PrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrintActionPerformed
       // Retrieve the selected row from the table
    int selectedRow = ReturnCarsTable.getSelectedRow();
    if (selectedRow != -1) {
        // Get booking details from the table model
        DefaultTableModel model = (DefaultTableModel) ReturnCarsTable.getModel();
        
        // Assuming the columns are: Booking ID, Car Model, Booking Date
        int bookingId = (int) model.getValueAt(selectedRow, 0); // Booking ID in column 0
        String carModel = (String) model.getValueAt(selectedRow, 1); // Car Model in column 1
        Date bookingDate = (Date) model.getValueAt(selectedRow, 2); // Booking Date in column 2

        // Assume the return date is the current date
        LocalDate userReturnDate = LocalDate.now(); // Example: Set to current date
        
        // Calculate the late fee
        long lateFee = calculateLateFee(bookingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), userReturnDate);
        
        // Damage fee logic
        long damageFee = damageCheckbox.isSelected() ? 500 : 0; // Damage fee of PHP 500 if selected

        // Create a string representing the receipt
        String receipt = "------------------ Payment Receipt ------------------\n"
                + "Booking ID: " + bookingId + "\n"
                + "Car Model: " + carModel + "\n"
                + "Booking Date: " + bookingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n"  // Displaying booking date
                + "Return Date: " + userReturnDate + "\n"
                + "Late Fee: PHP " + lateFee + "\n"
                + "Damage Fee: PHP " + damageFee + "\n"
                + "----------------------------------------------------";

        // Create a JTextArea to display and print the receipt
        JTextArea textArea = new JTextArea();
        textArea.setText(receipt);
        textArea.setEditable(false);
        
        // Use the Java Print API to print the receipt
        boolean complete = false;
        try {
            complete = textArea.print();
        } catch (PrinterException ex) {
            Logger.getLogger(Returncar.class.getName()).log(Level.SEVERE, "Printing error", ex);
        }
        
        if (complete) {
            JOptionPane.showMessageDialog(this, "Receipt printed successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Printing was canceled.");
        }
    } else {
        // Display a message if no row is selected
        JOptionPane.showMessageDialog(this, "Please select a booking to print.");
    
    }//GEN-LAST:event_PrintActionPerformed
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Returncar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Returncar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Returncar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Returncar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Returncar returncar = new Returncar();
            returncar.setVisible(true);  // Then show the window`
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CanceljButton2;
    private javax.swing.JButton Print;
    private javax.swing.JTable ReturnCarsTable;
    private javax.swing.JButton ReturnjButton1;
    private javax.swing.JTextField bookingDateTextField;
    private javax.swing.JCheckBox damageCheckbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser returnDateChooser;
    // End of variables declaration//GEN-END:variables
}
