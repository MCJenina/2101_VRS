package vrs;

import com.toedter.calendar.JDateChooser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;


public class FillUpForm extends javax.swing.JFrame {
private Connection connection;


    //Creates new form FillUpForm    
    public FillUpForm() {
    initComponents();
    this.setTitle("Fill Up Form");
    pickupDate = new JDateChooser();
    ReturnDate = new JDateChooser();
    connectToDatabase(); // Ensure connection is established first
    loadAvailableCars(); 
     this.add(pickupDate);
     this.add(ReturnDate);
    
}

        
    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/vehiclerentalsystem?useSSL=false&serverTimezone=UTC";
            String user = "Jenina"; // Update with your username
            String password = "qwertyuiop"; // Update with your password
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage());
        }
    }

    private void loadAvailableCars() {
        String query = "SELECT car_id, Model, Type, Status, Price FROM cars WHERE Status = 'Available'";
        populateTable(query, false);
    }

     private void populateTable(String query, boolean isBooking) {
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel();

            if (isBooking) {
                model.addColumn("Reservation ID");
                model.addColumn("Pickup Date");
                model.addColumn("Return Date");
                model.addColumn("Destination");
                model.addColumn("Car Model");
                model.addColumn("Car Status");
            } else {
                model.addColumn("Car ID");
                model.addColumn("Model");
                model.addColumn("Type");
                model.addColumn("Status");
                model.addColumn("Price");
            }

            while (rs.next()) {
                Object[] row = new Object[model.getColumnCount()];
                if (isBooking) {
                    row[0] = rs.getInt("id");
                    row[1] = rs.getDate("pickup_date");
                    row[2] = rs.getDate("return_date");
                    row[3] = rs.getString("destination");
                    row[4] = rs.getString("Model");
                    row[5] = rs.getString("Status");
                } else {
                    row[0] = rs.getInt("car_id");
                    row[1] = rs.getString("Model");
                    row[2] = rs.getString("Type");
                    row[3] = rs.getString("Status");
                    row[4] = rs.getDouble("Price");
                }
                model.addRow(row);
            }

            Bookingtable.setModel(model); // Ensure this matches the JTable variable in FillUpForm

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }
     private void saveToDatabase(String pickupDate, String returnDate, String destination, double additionalFees) {
    Connection connection = null;
    PreparedStatement ps = null;
    
    try {
        // Connect to the database
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vehiclerentalsystem", "Jenina", "qwertyuiop");

        // SQL query to insert the data into the table
        String query = "INSERT INTO reservation (pickup_date, return_date, destination, payment_method, additional_fees) VALUES (?, ?, ?, ?, ?)";
        ps = connection.prepareStatement(query);
        ps.setString(1, pickupDate);
        ps.setString(2, returnDate);
        ps.setString(3, destination);  // Set destination
        ps.setString(4, cash.isSelected() ? "Cash" : "GCash");  // Set payment method
        ps.setDouble(5, additionalFees);  // Set additional fees

        // Execute the query to insert data
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Reservation saved successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Error saving reservation.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
    } finally {
        try {
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        places = new javax.swing.JComboBox<>();
        Destination = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cash = new javax.swing.JRadioButton();
        gcash = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        AdditionalFee = new javax.swing.JTextField();
        Submit = new javax.swing.JButton();
        Cancel = new javax.swing.JButton();
        Back = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        print = new javax.swing.JButton();
        pickupDate = new com.toedter.calendar.JDateChooser();
        ReturnDate = new com.toedter.calendar.JDateChooser();
        ModelCar = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Bookingtable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Pick Up Date");

        jLabel2.setText("Return Date");

        jLabel3.setText("Places");

        jLabel4.setText("Destination");

        places.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nasugbu Batangas", "Calatagan Batangas", "Calaca, Batangas", "Lemery, Batangas" }));
        places.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placesActionPerformed(evt);
            }
        });

        jLabel5.setText("Payment Method");

        buttonGroup1.add(cash);
        cash.setText("Cash");
        cash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashActionPerformed(evt);
            }
        });

        buttonGroup1.add(gcash);
        gcash.setText("Gcash");

        jLabel6.setText("Additional Fees:");

        AdditionalFee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdditionalFeeActionPerformed(evt);
            }
        });

        Submit.setText("Submit");
        Submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitActionPerformed(evt);
            }
        });

        Cancel.setText("Cancel");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        Back.setText("Back");
        Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackActionPerformed(evt);
            }
        });

        jLabel8.setText("List of Cars:");

        print.setText("Print Receipt");
        print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });

        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(Back))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(print))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(40, 40, 40)
                            .addComponent(AdditionalFee, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(280, 280, 280)
                            .addComponent(Cancel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Submit))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(40, 40, 40)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(ModelCar)
                                    .addGap(122, 122, 122)
                                    .addComponent(jLabel2)
                                    .addGap(25, 25, 25))
                                .addComponent(jLabel8)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(290, 290, 290)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(pickupDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 26, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(places, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(Destination, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ReturnDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(cash)
                            .addComponent(gcash)))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(pickupDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(10, 10, 10)
                        .addComponent(ReturnDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Destination, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cash))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gcash)
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Cancel)
                            .addComponent(Submit)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ModelCar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(200, 200, 200)
                                .addComponent(Back))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(140, 140, 140)
                                .addComponent(jLabel6))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(places, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(AdditionalFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel3))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(200, 200, 200)
                                .addComponent(print)))))
                .addGap(30, 30, 30))
        );

        Bookingtable.setBackground(new java.awt.Color(255, 255, 204));
        Bookingtable.setFont(Bookingtable.getFont().deriveFont((Bookingtable.getFont().getStyle() & ~java.awt.Font.ITALIC) & ~java.awt.Font.BOLD, 12));
        Bookingtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Model", "Type", "Status", "Price", "Image"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(Bookingtable);

        jButton1.setText("Select");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 40, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void placesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placesActionPerformed
        // TODO add your handling code here:
        //get selected value from combobox
        String selectedValue = places.getSelectedItem().toString();
        
        //now set this selected value into textfield
        Destination.setText(selectedValue);
    }//GEN-LAST:event_placesActionPerformed

    private void AdditionalFeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdditionalFeeActionPerformed
        // TODO add your handling code here: Submitt Button
        
    }//GEN-LAST:event_AdditionalFeeActionPerformed

    private void SubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitActionPerformed
       // Extract destination from combo box
    String destination = places.getSelectedItem().toString();
    
    // Example of additional fee calculation
    Map<String, Double> destinationFees = new HashMap<>();
    destinationFees.put("Nasugbu Batangas", 0.0);
    destinationFees.put("Calatagan Batangas", 500.0);
    destinationFees.put("Calaca, Batangas", 350.0);
    destinationFees.put("Lemery, Batangas", 450.0);
    
    double additionalFee = destinationFees.getOrDefault(destination, 0.0);
    double baseCost = 0.0;  // Base cost for the reservation
    double additionalFees = baseCost + additionalFee;

    
    // Get selected dates from JDateChooser components
    // Get the pickup and return dates
    Date pickDate = pickupDate.getDate();
    Date returnDate = ReturnDate.getDate();

    // Check if either of the dates is null (meaning no date was selected)
    if (pickDate == null || returnDate == null) {
        JOptionPane.showMessageDialog(this, "Both pick-up and return dates are required.");
        return; // Exit the method if dates are not selected
    }
    
    // You can proceed with further logic if both dates are selected
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String pickupDateStr = sdf.format(pickDate);  // Format the pickup date
    String returnDateStr = sdf.format(returnDate); // Format the return date
    
    // Additional validation for date range (Optional)
    if (pickDate.after(returnDate)) {
        JOptionPane.showMessageDialog(this, "Return date cannot be earlier than the pickup date.");
        return; // Exit if the return date is before the pickup date
    }
    
    // Now proceed with the rest of your form submission logic
    saveToDatabase(pickupDateStr, returnDateStr, destination, additionalFees);

    }//GEN-LAST:event_SubmitActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        // TODO add your handling code here:Cancel Button
        // Reset all form fields
        pickupDate.setDate(null);
        ReturnDate.setDate(null);
        places.setSelectedIndex(0); // Reset to default item
        cash.setSelected(false); // Unselect radio buttons
        gcash.setSelected(false);
        Destination.setText(""); // Clear the text field
        AdditionalFee.setText(""); // Optionally, clear another text field if applicable
    
    // Optionally, you can display a message to confirm the reset
    javax.swing.JOptionPane.showMessageDialog(this, "Form has been cleared.");
    }//GEN-LAST:event_CancelActionPerformed

    private void BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackActionPerformed
        // TODO add your handling code here:
        Vehicles toVehicles = new Vehicles();
        toVehicles.show();
        
        dispose();
                
    }//GEN-LAST:event_BackActionPerformed
private void readRecords() {
        try {
            String query = "SELECT * FROM cars"; // Adjust query based on your database schema
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) Bookingtable.getModel();
            model.setRowCount(0); // Clear existing rows in the table

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("car_id"),
                    rs.getString("Type"),
                    
                };
                model.addRow(row);               
            }
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Error loading records: " + e.getMessage());
        }
    }
    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
        // TODO add your handling code here:
        // Create a string representing the receipt
    String receipt = "------------------ Payment Receipt ------------------\n"
                   + "Car Id/Name: " + ModelCar.getText()+ "\n"
                   + "Destination: " + places.getSelectedItem() + "\n"
                   + "Pick-Up Date: " + pickupDate.getDate() + "\n"
                   + "Return Date: " + ReturnDate.getDate() + "\n"
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
    }//GEN-LAST:event_printActionPerformed

    private void cashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cashActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         // Get the selected row index
                int selectedRow = Bookingtable.getSelectedRow();

                if (selectedRow != -1) { // Ensure a row is selected
                    // Fetch the car model from the selected row (index 1 is the "Car Model" column)
                    String carModel = (String) Bookingtable.getValueAt(selectedRow, 1); // 1 is the "Car Model" column index

                    // Set the fetched car model in the ModelCar JTextField
                    ModelCar.setText(carModel);
                } else {
                    // If no row is selected, show a message
                    JOptionPane.showMessageDialog(null, "Please select a row from the table.");
                }
    } private void FillUpForm(int customerId, int carId) {
    // Queries
    String checkQuery = "SELECT COUNT(*) FROM booking WHERE user_id = ? AND status = 'active'";
    String insertQuery = "INSERT INTO booking (user_id, car_id, booking_date, status) VALUES (?, ?, NOW(), 'active')";
    String updateCarQuery = "UPDATE cars SET status = 'Booked' WHERE car_id = ?";

    try {
        // Begin transaction
        connection.setAutoCommit(false);

        // Step 1: Check for active booking
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, customerId);  // Set customerId
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "You already have an active booking.");
                return;  // Exit if the user has an active booking
            }
        }

        // Step 2: Insert new booking
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, customerId);  // The current user
            insertStmt.setInt(2, carId);       // The selected car
            insertStmt.executeUpdate();
        }

        // Step 3: Update car status
        try (PreparedStatement updateCarStmt = connection.prepareStatement(updateCarQuery)) {
            updateCarStmt.setInt(1, carId);  // Update car status
            updateCarStmt.executeUpdate();
        }

    } catch (SQLException e) {
        // Rollback in case of errors
        try {
            connection.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Error booking car: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // Restore auto-commit mode
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
            
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    
     // @param args the command line arguments
     
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
            java.util.logging.Logger.getLogger(FillUpForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FillUpForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FillUpForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FillUpForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        // Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FillUpForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AdditionalFee;
    private javax.swing.JButton Back;
    private javax.swing.JTable Bookingtable;
    private javax.swing.JButton Cancel;
    private javax.swing.JTextField Destination;
    private javax.swing.JTextField ModelCar;
    private com.toedter.calendar.JDateChooser ReturnDate;
    private javax.swing.JButton Submit;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cash;
    private javax.swing.JRadioButton gcash;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser pickupDate;
    private javax.swing.JComboBox<String> places;
    private javax.swing.JButton print;
    // End of variables declaration//GEN-END:variables
}
