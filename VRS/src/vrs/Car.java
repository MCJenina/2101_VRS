package vrs;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Car extends javax.swing.JFrame {
    private Connection connection;

    // Constructor
    public Car() {
        initComponents(); 
        this.setLocationRelativeTo(null);
        connectDatabase(); 
        loadCarDetails();  
        
        //So if the user want to update or delete, user just select in the table and it will display it the details 
        CarsTable.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int selectedRow = CarsTable.getSelectedRow(); // Get the selected row
            if (selectedRow != -1) {
                // Populate text fields with data from the selected row
                CarIdTXT.setText(CarsTable.getValueAt(selectedRow, 0).toString()); // Car ID
                plateNumbertxt.setText(CarsTable.getValueAt(selectedRow, 1).toString()); // Plate Number
                ModelTXT.setText(CarsTable.getValueAt(selectedRow, 2).toString()); // Model
                capacity.setSelectedItem(CarsTable.getValueAt(selectedRow, 3).toString()); // Capacity (ComboBox)
                CarTypeTXT.setSelectedItem(CarsTable.getValueAt(selectedRow, 4).toString()); // Type (ComboBox)
                CarPriceTXT.setText(CarsTable.getValueAt(selectedRow, 5).toString()); // Price
                //StatusCombo.setSelectedItem(CarsTable.getValueAt(selectedRow, 6).toString()); // Status (ComboBox)
            }
        }
        });
        
        
    }

    // Method to load car details and update the table
    private void loadCarDetails() {
    String query = "SELECT car_id,plateNumber, Model,Capacity, Type, Price, Status FROM cars";
    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Car ID");
        model.addColumn("Plate Number");
        model.addColumn("Car Model");
        model.addColumn("Capacity");
        model.addColumn("Type");
        model.addColumn("Price");
        model.addColumn("Status");

        while (rs.next()) {
            Object[] row = new Object[7];
            row[0] = rs.getString("car_id");
            row[1] = rs.getString("plateNumber");
            row[2] = rs.getString("Model");
            row[3] = rs.getString("Capacity");
            row[4] = rs.getString("Type");
            row[5] = rs.getDouble("Price");
            row[6] = rs.getString("Status");
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
    private void addCarToDatabase(String carId, String plateNumber, String Status, double Price, String Model,String Capacity, String Type) {
        String query = "INSERT INTO cars (car_id, Status, Price, Model, Type) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Set parameters in the SQL query
            pstmt.setString(1, carId);
            pstmt.setString(2, plateNumber);
            pstmt.setString(3, Status);
            pstmt.setDouble(4, Price);
            pstmt.setString(5, Model);
            pstmt.setString(6, Capacity);
            pstmt.setString(7, Type);

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
    public void clearFields(){
      CarIdTXT.setText(" ");
      plateNumbertxt.setText(" "); 
      ModelTXT.setText(" ");
      capacity.setSelectedIndex(0);
      CarTypeTXT.setSelectedIndex(0);
      CarPriceTXT.setText("");
      
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
        jLabel10 = new javax.swing.JLabel();
        plateNumbertxt = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        capacity = new javax.swing.JComboBox<>();
        CarTypeTXT = new javax.swing.JComboBox<>();
        CLEAR = new javax.swing.JButton();
        ADDbutton = new javax.swing.JButton();
        DELButton = new javax.swing.JButton();
        UPButton = new javax.swing.JButton();
        REFButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        backButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 200, 105));

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel3.setText("X");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Mongolian Baiti", 1, 18)); // NOI18N
        jLabel1.setText("MANAGE CAR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
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
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Car ID", "PLATE NUMBER", "MODEL", "CAPACITY", "TYPE", "PRICE", "STATUS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        CarsTable.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(CarsTable);
        CarsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jLabel8.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        jLabel8.setText("VEHICLE LIST");

        jPanel2.setBackground(new java.awt.Color(244, 244, 244));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "AutoMoBilis", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Rockwell", 0, 12))); // NOI18N

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

        jLabel2.setText("Type:");

        jLabel4.setText("Model:");

        jLabel5.setText("Price:");

        jLabel6.setText("Status:");

        StatusCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Under Maintenance" }));
        StatusCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StatusComboActionPerformed(evt);
            }
        });

        jLabel9.setText("ID:");

        jLabel10.setText("Plate Number:");

        jLabel11.setText("Capacity:");

        capacity.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2 Seaters", "5 Seaters", "8 Seaters" }));

        CarTypeTXT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2 Wheels", "4 Wheels" }));

        CLEAR.setText("CLEAR");
        CLEAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CLEARActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(capacity, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CarPriceTXT)
                            .addComponent(StatusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CarIdTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(plateNumbertxt, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(CLEAR))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ModelTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CarTypeTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 20, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(CarIdTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(plateNumbertxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(ModelTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CarTypeTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(capacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CarPriceTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(StatusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(CLEAR))
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
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UPButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ADDbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(REFButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(DELButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(ADDbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DELButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(UPButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(REFButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
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
        String plateNumber = plateNumbertxt.getText();
        String carModel = ModelTXT.getText().trim();
        String Capacity = capacity.getSelectedItem().toString();
        String carType = CarTypeTXT.getSelectedItem().toString();
        String priceText = CarPriceTXT.getText().trim();
        String status = (String) StatusCombo.getSelectedItem();

        // Validate the inputs
        if (carId.isEmpty() || carModel.isEmpty() || carType.isEmpty() || priceText.isEmpty() || status == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all the fields.");
            return;
        }
    
        // Check if the plate number already exists for another car
        String checkQuery = "SELECT COUNT(*) FROM cars WHERE plateNumber = ? AND car_id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
         pstmt.setString(1, plateNumber);
            pstmt.setString(2, carId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Plate number already exists. Please enter a unique plate number.");
                   return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking plate number: " + ex.getMessage());
            return;
        }

        

        // Validate that the price input is a valid number
        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            return;
        }

        // Show confirmation dialog before proceeding
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to add this car?",
            "Confirm Add",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // SQL query to insert the new car into the database
            String query = "INSERT INTO cars (car_id, plateNumber, Model, Capacity , Type, Price, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                // Set the query parameters
                pstmt.setString(1, carId);
                pstmt.setString(2, plateNumber);
                pstmt.setString(3, carModel);
                pstmt.setString(4, Capacity);
                pstmt.setString(5, carType);
                pstmt.setDouble(6, price);
                pstmt.setString(7, status);

                // Execute the query
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Car added successfully!");
                    loadCarDetails(); // Refresh the list after adding
                   
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add the car. Please try again.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace(); // Print for debugging
                JOptionPane.showMessageDialog(this, "Error adding car: " + ex.getMessage());
            }

            // Clear the input fields
            CarIdTXT.setText("");
            ModelTXT.setText("");
           
            CarPriceTXT.setText("");
            StatusCombo.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "Add operation canceled.");
        }
    }//GEN-LAST:event_ADDbuttonActionPerformed

    private void DELButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DELButtonActionPerformed
    // Retrieve the selected row
    int selectedRow = CarsTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a car from the table to delete.");
        return;
    }

    // Retrieve the Car ID from the table
    String carID = CarsTable.getValueAt(selectedRow, 0).toString();

    // Show confirmation dialog
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete the car with ID: " + carID + "?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (confirm == JOptionPane.YES_OPTION) {
        String query = "DELETE FROM cars WHERE car_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, carID);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Car deleted successfully.");
                loadCarDetails(); // Refresh the table
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed. Car ID not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting car: " + ex.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Delete operation canceled.");
    }

    }//GEN-LAST:event_DELButtonActionPerformed

    private void UPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UPButtonActionPerformed
    // Add a MouseListener to the table
     
        

// Collect input from text fields and combo box

    
        String carId = CarIdTXT.getText().trim();
        String plateNumber = plateNumbertxt.getText();
        String carModel = ModelTXT.getText().trim();
        String Capacity = capacity.getSelectedItem().toString();
        String carType = CarTypeTXT.getSelectedItem().toString();
        String priceText = CarPriceTXT.getText().trim();
        String status = (String) StatusCombo.getSelectedItem();

    // Validate inputs
    if (carId.isEmpty() ||plateNumber.isEmpty() || carModel.isEmpty() || Capacity.isEmpty() || carType.isEmpty() || priceText.isEmpty() || status.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please choose the row in the table"); //(this, "Please fill out all fields."); this is before
        return;
    }
    
        // Check if the plate number already exists for another car
        String checkQuery = "SELECT COUNT(*) FROM cars WHERE plateNumber = ? AND car_id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
         pstmt.setString(1, plateNumber);
            pstmt.setString(2, carId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Plate number already exists. Please enter a unique plate number.");
                   return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking plate number: " + ex.getMessage());
            return;
        }

    // Show confirmation dialog
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to update the details for Car ID: " + carId + "?",
        "Confirm Update",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );
    if (confirm == JOptionPane.YES_OPTION) {
        // SQL query to update car details
        String query = "UPDATE cars SET plateNumber = ?, Model = ?, Type = ?,capacity = ?, Price = ?, Status = ? WHERE car_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, plateNumber);
            pstmt.setString(2, carModel);
            pstmt.setString(3, carType);
            pstmt.setString(4, Capacity);
            pstmt.setDouble(5, Double.parseDouble(priceText)); // Ensure price is numeric
            pstmt.setString(6, status);
            pstmt.setString(7, carId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Car details updated successfully.");
                loadCarDetails(); // Refresh table
                
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Car ID not found.");
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating car details: " + ex.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Update operation canceled.");
    }
    
    /*SQL query to update car details
    String query = "UPDATE cars SET Model = ?, Type = ?, Price = ?, Status = ? WHERE car_id = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        // Set parameters for the query
        pstmt.setString(1, model);
        pstmt.setString(2, carType);
        pstmt.setString(3, price);
        pstmt.setString(4, status);
        pstmt.setString(5, carID);

        // Execute the update query
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Car details updated successfully.");
            loadCarDetails();  // Refresh the car details in the table
        } else {
            JOptionPane.showMessageDialog(this, "Car ID not found. Update failed.");
        }
    } catch (SQLException ex) {
        ex.printStackTrace(); // Debugging purposes
        JOptionPane.showMessageDialog(this, "Error updating car details: " + ex.getMessage());
    }*/
    }//GEN-LAST:event_UPButtonActionPerformed

    private void REFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_REFButtonActionPerformed
        // TODO add your handling code here:
         // Show a confirmation dialog
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to refresh the car details?",
        "Confirm Refresh",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    // If the user clicks "Yes"
    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // Call the method to reload car details
            loadCarDetails();
            JOptionPane.showMessageDialog(this, "Car details refreshed successfully.");
        } catch (Exception ex) {
            ex.printStackTrace(); // For debugging
            JOptionPane.showMessageDialog(this, "Failed to refresh car details: " + ex.getMessage());
        }
    } else {
        // If the user clicks "No", do nothing
        JOptionPane.showMessageDialog(this, "Refresh canceled.");
    }

    }//GEN-LAST:event_REFButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        Admin_DashBoard adminDashboard = new Admin_DashBoard();
        adminDashboard.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_backButtonActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jLabel3MouseClicked

    private void CLEARActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CLEARActionPerformed
        // TODO add your handling code here:
        clearFields();
    }//GEN-LAST:event_CLEARActionPerformed

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
    private javax.swing.JButton CLEAR;
    private javax.swing.JTextField CarIdTXT;
    private javax.swing.JTextField CarPriceTXT;
    private javax.swing.JComboBox<String> CarTypeTXT;
    private javax.swing.JTable CarsTable;
    private javax.swing.JButton DELButton;
    private javax.swing.JTextField ModelTXT;
    private javax.swing.JButton REFButton;
    private javax.swing.JComboBox<String> StatusCombo;
    private javax.swing.JButton UPButton;
    private javax.swing.JButton backButton;
    private javax.swing.JComboBox<String> capacity;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JTextField plateNumbertxt;
    // End of variables declaration//GEN-END:variables
}
