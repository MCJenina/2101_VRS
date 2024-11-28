    package vrs;

    import java.sql.*;
    import javax.swing.JOptionPane;
    import java.time.LocalDate;
    import java.time.ZoneId;
    import java.time.temporal.ChronoUnit;
    import javax.swing.table.DefaultTableModel;
    
    public class Returncar extends javax.swing.JFrame {
    private Connection connection;
    private final int loggedInUserId;

    public Returncar() {
       this.loggedInUserId = SessionManager.getCustomerId(); // Get user ID from Session Manager
        System.out.println("Logged In User ID: " + loggedInUserId); // Debugging line to check user ID
        initComponents();
        this.setTitle("Return Car");
        connectDatabase();
        loadActiveBookings();
        
      
        
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
    String query = "SELECT r.booking_id, c.model, r.booking_date " +
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
            Date startDate = rs.getDate("booking_date");

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
    private boolean returnVehicle(int car_id, int bookingId, LocalDate userReturnDate, long lateFee, long damageFee) {
    String getBookingDetailsQuery = "SELECT booking_date, car_id, user_id FROM booking WHERE booking_id = ? AND status = 'active'";

    String updateBookingStatusQuery = "UPDATE booking SET status = 'returned', booking_date = ?WHERE booking_id = ?";

    String updateCarStatusQuery = "UPDATE cars SET status = 'Available' WHERE car_id = ?";

    try {
        // Retrieve booking details from the database
        LocalDate bookingDate = null;
        LocalDate bookingReturnDate = null;
        int carId = -1;
        int userId = -1; // Initialize userId

        try (PreparedStatement ps = connection.prepareStatement(getBookingDetailsQuery)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                bookingDate = rs.getDate("booking_date") != null ? rs.getDate("booking_date").toLocalDate() : null;
                java.sql.Date returnDate = rs.getDate("booking_date");
                if (returnDate != null) {
                    bookingReturnDate = returnDate.toLocalDate();
                }
                carId = rs.getInt("car_id");
                userId = rs.getInt("user_id");  // Retrieve user_id from booking table
            } else {
                JOptionPane.showMessageDialog(this, "Booking not found or already returned.");
                return false;
            }
        }

        if (bookingDate == null || bookingReturnDate == null) {
            JOptionPane.showMessageDialog(this, "Booking date or return date is not available for this booking.");
            return false;
        }

        long daysLate = ChronoUnit.DAYS.between(bookingReturnDate, userReturnDate);
        if (daysLate > 0) {
            lateFee = daysLate * 1000;
            JOptionPane.showMessageDialog(this, "You are " + daysLate + " days late. Late fee: PHP " + lateFee);
        } else {
            JOptionPane.showMessageDialog(this, "Returned on time. No late fee.");
        }

        // Insert return data into the 'returns' table
        if (!insertReturnData( bookingId, java.sql.Date.valueOf(userReturnDate), userId,car_id, lateFee, damageFee)) {
            JOptionPane.showMessageDialog(this, "Failed to insert return data.");
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

        JOptionPane.showMessageDialog(this, "Vehicle returned successfully!");
        return true;

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error processing return: " + e.getMessage());
        e.printStackTrace();
    }

    return false;
}

private boolean insertReturnData( int bookingId,  java.sql.Date userReturnDate,int car_id,int userId, long lateFee, long damageFee) {
    String insertQuery = "INSERT INTO returns (  booking_id, return_date,car_id ,user_id, late_fee, damage_fee) VALUES (?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
        // Set each parameter in the prepared statement
        ps.setInt(3, car_id);  // car_id
        ps.setInt(4, userId);  // user_id (added to the query)
        ps.setInt(1, bookingId);  // booking_id
        ps.setDate(2, userReturnDate);  // return_date
        ps.setLong(5, lateFee);  // late_fee
        ps.setLong(6, damageFee);  // damage_fee

        // Execute the insert query
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;  // Return true if insertion was successful
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to insert return data: " + e.getMessage());
        return false;
    }
}


private long calculateLateFee(LocalDate bookingReturnDate, LocalDate userReturnDate) {

// Convert java.util.Date to LocalDate
            // Calculate the number of days late (if the returnDate is after the bookingReturnDate)
     long daysLate = ChronoUnit.DAYS.between(bookingReturnDate, userReturnDate);

     if (daysLate > 0) {
        return daysLate * 1000; // 1000 is the late fee per day, adjust this if needed
    }
    
    // Return 0 if the vehicle is returned on time or early
    return 0;
}private boolean updateBookingStatus(int bookingId) {
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
        returnDateChooser = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();

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
                "Booking ID", "Model", "Booking Date"
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

        jButton1.setText("Print Receipt");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(damageCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bookingDateTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(returnDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jButton1))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(returnDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(damageCheckbox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
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

        // Convert the booking's return date (java.sql.Date) to LocalDate, with a null check
        if (bookingReturnDate != null) {
            LocalDate localBookingReturnDate = bookingReturnDate.toLocalDate();

            // Calculate late fee if necessary
            long lateFee = calculateLateFee(localBookingReturnDate, localUserReturnDate);

            // Check if the damage checkbox is selected and calculate damage fee
            long damageFee = 0;
            if (damageCheckbox.isSelected()) {
                damageFee = 500; // Assuming damage fee is a fixed amount, you can adjust this
            }

            // Retrieve car_id from the database using bookingId
            int carId = getCarIdFromBooking(bookingId);

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
            JOptionPane.showMessageDialog(this, "Booking return date is missing.");
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         // Create a string representing the receipt
          // Retrieve selected row from the table
        int selectedRow = ReturnCarsTable.getSelectedRow();
        if (selectedRow != -1) {
        // Get booking details from the table model
        DefaultTableModel model = (DefaultTableModel) ReturnCarsTable.getModel();
        int bookingId = (int) model.getValueAt(selectedRow, 0); // Assuming bookingId is in column 0
        String carModel = (String) model.getValueAt(selectedRow, 1); // Assuming carModel is in column 1
        Date bookingDate = (Date) model.getValueAt(selectedRow, 2); // Assuming bookingDate is in column 2

        // Assume userReturnDate, lateFee, and damageFee are already calculated
        LocalDate userReturnDate = LocalDate.now(); // Example: Set to current date
        long lateFee = calculateLateFee(bookingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), userReturnDate);
        long damageFee = damageCheckbox.isSelected() ? 500 : 0; // Example damage fee logic

         
        String receipt = "------------------ Payment Receipt ------------------\n"
        
        + "Booking ID: " + bookingId + "\n"
        + "Car Model: " + carModel + "\n"
        + "Booking Date: " + bookingDate + "\n"
        + "Return Date: " + userReturnDate + "\n"
        + "Late Fee: PHP " + lateFee + "\n"
        + "Damage Fee: PHP " + damageFee + "\n"
        + "----------------------------------------------------";
        
        // Use the Java Print API
        try {
            boolean complete = new javax.swing.JTextArea(receipt).print();
            if (complete) {
                javax.swing.JOptionPane.showMessageDialog(this, "Receipt printed successfully.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Printing was canceled.");
            }
        } catch (java.awt.print.PrinterException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }//GEN-LAST:event_jButton1ActionPerformed
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
    private javax.swing.JTable ReturnCarsTable;
    private javax.swing.JButton ReturnjButton1;
    private javax.swing.JTextField bookingDateTextField;
    private javax.swing.JCheckBox damageCheckbox;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser returnDateChooser;
    // End of variables declaration//GEN-END:variables
}
