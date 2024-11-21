package vrs;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Car extends javax.swing.JFrame {
    private Connection connection;

    // Constructor
    public Car() {
        initComponents(); 
        connectDatabase(); 
        loadCarDetails();  
    }

    // Method to load car details and update the table
    private void loadCarDetails() {
    String query = "SELECT car_id, Model, Type, Price, Status FROM cars";
    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Car ID");
        model.addColumn("Car Model");
        model.addColumn("Type");
        model.addColumn("Price");
        model.addColumn("Status");

        while (rs.next()) {
            Object[] row = new Object[5];
            row[0] = rs.getString("car_id");
            row[1] = rs.getString("Model");
            row[2] = rs.getString("Type");
            row[3] = rs.getDouble("Price");
            row[4] = rs.getString("Status");
            model.addRow(row);
        }

        // Update the table with the new data
        CarsTable.setModel(model);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading car details: " + e.getMessage());
    }
} 
    // Method to connect to the database
  private void connectDatabase() {
    try {
        String url = "jdbc:mysql://localhost:3306/vehiclerentalsystem?useSSL=false&serverTimezone=UTC";
        String user = "Jenina";
        String password = "qwertyuiop";
        
        // Print to confirm the database connection
        System.out.println("Attempting to connect to the database...");

        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Database connection successful!");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage());
    }
}


    private void updateCarStatus(String carId, String newStatus) {
    String query = "UPDATE cars SET Status = ? WHERE car_id = ?";
    System.out.println("Attempting to update car status: Car ID = " + carId + ", Status = " + newStatus);

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, newStatus);
        pstmt.setString(2, carId);

        // Execute the update query
        int rowsAffected = pstmt.executeUpdate();
        System.out.println("Rows affected: " + rowsAffected);
        
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Car status updated to: " + newStatus);
            loadCarDetails();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update car status. Car ID not found.");
        }
    } catch (SQLException e) {
        // Print the full stack trace for debugging
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating car status: " + e.getMessage());
    }
}
    // This method will be triggered when a car is booked
    private void handleAutoBooking(String carId) {
        // Check if the car is available for booking
        String query = "SELECT Status FROM cars WHERE car_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, carId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String currentStatus = rs.getString("Status");

                // Only allow booking if the car is available
                if ("Available".equals(currentStatus)) {
                    // Automatically change the car's status to "Reserved"
                    updateCarStatus(carId, "Reserved");
                } else {
                    JOptionPane.showMessageDialog(this, "The car is already reserved or out of service.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Car ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while checking car status: " + e.getMessage());
        }
    }

    // ActionListener for the car selection or table row click
    private void CarsTableMouseClicked(java.awt.event.MouseEvent evt) {
        // Get the selected car's ID from the table
        int selectedRow = CarsTable.getSelectedRow();
        String carId = CarsTable.getValueAt(selectedRow, 0).toString(); // Assuming car_id is in the first column
        
        // Automatically handle the booking for the selected car
        handleAutoBooking(carId);
    }


    // Method to add a new car to the database
    private void addCarToDatabase(String carId, String Status, double Price, String Model, String Type) {
        String query = "INSERT INTO cars (car_id, Status, Price, Model, Type) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Set parameters in the SQL query
            pstmt.setString(1, carId);
            pstmt.setString(2, Status);
            pstmt.setDouble(3, Price);
            pstmt.setString(4, Model);
            pstmt.setString(5, Type);

            // Execute the insert query
            int rowsAffected = pstmt.executeUpdate();

            // Show success message if a row was inserted
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Car added successfully.");
                loadCarDetails();  // Refresh the table after adding the new car
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add car.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        CarsTable = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        CarIdTXT = new javax.swing.JTextField();
        ModelTXT = new javax.swing.JTextField();
        CarPriceTXT = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        StatusCombo = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        CarTypeTXT = new javax.swing.JTextField();
        ADDbutton = new javax.swing.JButton();
        DELButton = new javax.swing.JButton();
        UPButton = new javax.swing.JButton();
        REFButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        backButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 200, 105));

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 20)); // NOI18N
        jLabel3.setText("X");

        jLabel1.setFont(new java.awt.Font("Mongolian Baiti", 1, 18)); // NOI18N
        jLabel1.setText("MANAGE CAR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 849, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel1)))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        CarsTable.setBackground(new java.awt.Color(255, 255, 204));
        CarsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Car ID", "MODEL", "PRICE", "STATUS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        CarsTable.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(CarsTable);

        jLabel8.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        jLabel8.setText("VEHICLE LIST");

        jPanel2.setBackground(new java.awt.Color(244, 244, 244));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "AutoMoBilis", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rockwell", 0, 12))); // NOI18N
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        CarIdTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CarIdTXTActionPerformed(evt);
            }
        });

        ModelTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModelTXTActionPerformed(evt);
            }
        });

        jLabel2.setText("Type");

        jLabel4.setText("MODEL");

        jLabel5.setText("PRICE");

        jLabel6.setText("STATUS");

        StatusCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AVAILABLE", "NOT AVAILABLE", "UNDER MAINTENANCE" }));
        StatusCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StatusComboActionPerformed(evt);
            }
        });

        jLabel9.setText("ID");

        CarTypeTXT.setText("jTextField1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel9))
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(CarIdTXT, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(ModelTXT, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(CarPriceTXT)
                    .addComponent(StatusCombo, 0, 1, Short.MAX_VALUE)
                    .addComponent(CarTypeTXT))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CarIdTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(CarTypeTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(StatusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ModelTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CarPriceTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(61, 61, 61))))
        );

        ADDbutton.setText("ADD");
        ADDbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ADDbuttonActionPerformed(evt);
            }
        });

        DELButton.setText("DELETE");
        DELButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DELButtonActionPerformed(evt);
            }
        });

        UPButton.setText("UPDATE");
        UPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UPButtonActionPerformed(evt);
            }
        });

        REFButton.setText("REFRESH");
        REFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                REFButtonActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 200, 105));

        jLabel7.setFont(new java.awt.Font("Rockwell", 0, 10)); // NOI18N
        jLabel7.setText("Copyright - BSIT 2101(2024-2025). All Rights Reserved");

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(backButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(backButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(UPButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(REFButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ADDbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44)
                                .addComponent(DELButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(56, 56, 56)
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(296, 296, 296))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ADDbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DELButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(UPButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(REFButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CarIdTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CarIdTXTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CarIdTXTActionPerformed

    private void ModelTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModelTXTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ModelTXTActionPerformed

    private void StatusComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StatusComboActionPerformed
      // Get the selected status from the combo box
    String selectedStatus = (String) StatusCombo.getSelectedItem();
    if (selectedStatus == null || selectedStatus.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select a valid status.");
        return;
    }

    // Get the selected row from the CarsTable
    int selectedRow = CarsTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a car from the table.");
        return; // Stop execution if no row is selected
    }

    // Get the car ID from the selected row
    String carId = CarsTable.getValueAt(selectedRow, 0).toString();
    System.out.println("Selected Car ID: " + carId + " - New Status: " + selectedStatus);  // Debugging statement

    // Update the car status in the database
    updateCarStatus(carId, selectedStatus);

    }//GEN-LAST:event_StatusComboActionPerformed

    private void ADDbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ADDbuttonActionPerformed
        String carId = CarIdTXT.getText().trim();
        String carModel = ModelTXT.getText().trim();
        String carType = CarTypeTXT.getText().trim();
        String priceText = CarPriceTXT.getText().trim();
        String status = (String) StatusCombo.getSelectedItem();

        // Validate the inputs
        if (carId.isEmpty() || carModel.isEmpty() || carType.isEmpty() || priceText.isEmpty() || status == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all the fields.");
            return;
        }

        // Validate that the price input is a valid number
        double price;
        try {
            price = Double.parseDouble(priceText); // Convert price text to a double
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            return;
        }

        // SQL query to insert the new car into the database
        String query = "INSERT INTO cars (car_id, Model, Type, Price, Status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Set the query parameters using the values from the input fields
            pstmt.setString(1, carId);
            pstmt.setString(2, carModel);
            pstmt.setString(3, carType);
            pstmt.setDouble(4, price);
            pstmt.setString(5, status);

            // Execute the SQL query to insert the car
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Car added successfully!");
                loadCarDetails(); // Refresh the car list on the GUI after adding
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add the car. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Print the error for debugging purposes
            JOptionPane.showMessageDialog(this, "Error adding car: " + ex.getMessage());
        }

        // Clear the input fields after successful addition
        CarIdTXT.setText(""); // Clear the car ID text field
        ModelTXT.setText(""); // Clear the car model text field
        CarTypeTXT.setText(""); // Clear the car type text field
        CarPriceTXT.setText(""); // Clear the car price text field
        StatusCombo.setSelectedIndex(0); // Reset the status dropdown to the default value
    


    }//GEN-LAST:event_ADDbuttonActionPerformed

    private void DELButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DELButtonActionPerformed
        // Retrieve the Car ID from the text field
        // Retrieve the Car ID from the text field
        String carID = CarIdTXT.getText();

        // Ensure the Car ID is not empty
        if (carID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Car ID.");
            return;
        }

        // SQL query to delete the car with the specified Car ID
        String query = "DELETE FROM cars WHERE car_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Set the Car ID parameter in the SQL query
            pstmt.setString(1, carID);

            // Execute the deletion
            int rowsAffected = pstmt.executeUpdate();

            // Check if any rows were affected (i.e., if a car was deleted)
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Car deleted successfully.");
                loadCarDetails();  // Refresh the table after deleting
            } else {
                JOptionPane.showMessageDialog(this, "Car ID not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete car.");
        }

    }//GEN-LAST:event_DELButtonActionPerformed

    private void UPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UPButtonActionPerformed
        // TODO add your handling code here:
        String carID = CarIdTXT.getText();
        String model = ModelTXT.getText();
        String carType = CarTypeTXT.getText();
        String price = CarPriceTXT.getText();
        String status = (String) StatusCombo.getSelectedItem();

        String query = "UPDATE cars SET Model = ?, Type = ?, Price = ?, Status = ? WHERE CarID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, model);
            pstmt.setString(2, carType);
            pstmt.setString(3, price);
            pstmt.setString(4, status);
            pstmt.setString(5, carID);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Car updated successfully.");
                loadCarDetails();  // Refresh the table after updating
            } else {
                JOptionPane.showMessageDialog(this, "Car ID not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update car.");
        }

    }//GEN-LAST:event_UPButtonActionPerformed

    private void REFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_REFButtonActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_REFButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        AdminDashboard adminDashboard = new AdminDashboard();
        adminDashboard.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_backButtonActionPerformed

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
            java.util.logging.Logger.getLogger(Car.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Car.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Car.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Car.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Car().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ADDbutton;
    private javax.swing.JTextField CarIdTXT;
    private javax.swing.JTextField CarPriceTXT;
    private javax.swing.JTextField CarTypeTXT;
    private javax.swing.JTable CarsTable;
    private javax.swing.JButton DELButton;
    private javax.swing.JTextField ModelTXT;
    private javax.swing.JButton REFButton;
    private javax.swing.JComboBox<String> StatusCombo;
    private javax.swing.JButton UPButton;
    private javax.swing.JButton backButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
