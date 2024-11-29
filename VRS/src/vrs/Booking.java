package vrs;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class Booking extends javax.swing.JFrame {
    private Connection connection;
    private final Map<String, Double> placeFees = new HashMap<>(); // Map to store place fees

    public Booking() {
        initComponents();  // Initialize components (auto-generated in the GUI designer)
        this.setLocationRelativeTo(null);
        connectDatabase();
        loadAvailableCars(); 
        initializePlaceFees(); // Populate the map with place fees

        // Attach the action listener to the JComboBox
        places.addActionListener(evt -> placesActionPerformed(evt));//whenever a user selects a place from the JComboBox, the placesActionPerformed method runs, updating the additional fees
        AdditionalFee.addActionListener(evt -> placesActionPerformed(evt));
        // Load available cars
        
        //in here when the customer select car in the table it will show in the JTextField in the right side labeled Selected cars
        // Add a ListSelectionListener to the table
        Bookingtable.getSelectionModel().addListSelectionListener(e -> {
        
        // Ensure the event is for the correct table and row is selected
        if (!e.getValueIsAdjusting() && Bookingtable.getSelectedRow() != -1) {
       
        // Get the selected row index
        int selectedRow = Bookingtable.getSelectedRow();

        // Retrieve the value of the "Model" column (column index 1)
        String carModel = Bookingtable.getValueAt(selectedRow, 1).toString();
        String priceText = Bookingtable.getValueAt(selectedRow, 4).toString();
        
        

        // Set the car model into the CarsGalingTable JTextField
        CarsGalingTable.setText(carModel);
        price.setText(priceText);
        
        }
    });
        
        
}
    // Method to populate place-to-fees mapping
    private void initializePlaceFees() {
        placeFees.put("Nasugbu Batangas", 150.0);
        placeFees.put("Calatagan Batangas", 500.0);
        placeFees.put("Calaca, Batangas", 300.0);
        placeFees.put("Lemery, Batangas", 350.0);
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
   
    // Load available cars for customers (you can replace this with admin-specific load method)
    private void loadAvailableCars() {
        String query = "SELECT car_id, Model, Type, Status, Price FROM cars WHERE Status = 'Available'";
        populateTable(query, false);  
        }
    
    // Populate table with query results
    private void populateTable(String query, boolean isBooking) {
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel();
            if (isBooking) {
                model.addColumn("Booking ID");
                model.addColumn("Customer Name");
                model.addColumn("Car Model");
                model.addColumn("Booking Date");
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
                    row[0] = rs.getInt("booking_id");
                    row[1] = rs.getString("customer_name");
                    row[2] = rs.getString("car_model");
                    row[3] = rs.getDate("booking_date");
                } else {
                    row[0] = rs.getInt("car_id");
                    row[1] = rs.getString("Model");
                    row[2] = rs.getString("Type");
                    row[3] = rs.getString("Status");
                    row[4] = rs.getDouble("Price");                 
                }
                model.addRow(row);
            }

            Bookingtable.setModel(model); // Assumes `Bookingtable` is a JTable

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        Bookingtable = new javax.swing.JTable();
        Bookpanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        Cancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        BOOK1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        BookingFillUp = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        AdditionalFee = new javax.swing.JTextField();
        places = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        print = new javax.swing.JButton();
        Destination = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cash = new javax.swing.JRadioButton();
        gcash = new javax.swing.JRadioButton();
        CarsGalingTable = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        pickUPDate = new com.toedter.calendar.JDateChooser();
        price = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(130, 70, 52));
        setResizable(false);

        Bookingtable.setBackground(new java.awt.Color(255, 255, 204));
        Bookingtable.setFont(Bookingtable.getFont().deriveFont((Bookingtable.getFont().getStyle() & ~java.awt.Font.ITALIC) & ~java.awt.Font.BOLD, 12));
        Bookingtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Model", "Types", "Status", "Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(Bookingtable);

        Bookpanel.setBackground(new java.awt.Color(255, 200, 105));

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 20)); // NOI18N
        jLabel3.setText("X");

        jLabel6.setFont(new java.awt.Font("Mongolian Baiti", 1, 18)); // NOI18N
        jLabel6.setText("MANAGE BOOKING");

        jLabel12.setFont(new java.awt.Font("Times New Roman", 2, 18)); // NOI18N
        jLabel12.setText("Available Cars");

        javax.swing.GroupLayout BookpanelLayout = new javax.swing.GroupLayout(Bookpanel);
        Bookpanel.setLayout(BookpanelLayout);
        BookpanelLayout.setHorizontalGroup(
            BookpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BookpanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1052, 1052, 1052)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(BookpanelLayout.createSequentialGroup()
                .addGap(411, 411, 411)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        BookpanelLayout.setVerticalGroup(
            BookpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BookpanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(BookpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 200, 105));

        Cancel.setText("CANCEL");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        BOOK1.setText("BOOK");
        BOOK1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BOOK1ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Rockwell", 0, 10)); // NOI18N
        jLabel7.setText("Copyright - BSIT 2101(2024-2025). All Rights Reserved");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BOOK1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BOOK1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7)))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        BookingFillUp.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        BookingFillUp.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel5.setText("Places");

        jLabel8.setText("Destination");

        jLabel9.setText("Additional Fees:");

        AdditionalFee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdditionalFeeActionPerformed(evt);
            }
        });

        places.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nasugbu Batangas", "Calatagan Batangas", "Calaca, Batangas", "Lemery, Batangas" }));
        places.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placesActionPerformed(evt);
            }
        });

        jLabel1.setText("Pick Up Date");

        print.setText("Print Receipt");
        print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });

        jLabel11.setText("Payment Method");

        buttonGroup1.add(cash);
        cash.setText("Cash");
        cash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashActionPerformed(evt);
            }
        });

        buttonGroup1.add(gcash);
        gcash.setText("Gcash");

        CarsGalingTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CarsGalingTableActionPerformed(evt);
            }
        });

        jLabel10.setText("Selected Car");

        javax.swing.GroupLayout BookingFillUpLayout = new javax.swing.GroupLayout(BookingFillUp);
        BookingFillUp.setLayout(BookingFillUpLayout);
        BookingFillUpLayout.setHorizontalGroup(
            BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BookingFillUpLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(BookingFillUpLayout.createSequentialGroup()
                        .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CarsGalingTable)
                            .addComponent(Destination)
                            .addComponent(AdditionalFee)
                            .addComponent(places, 0, 180, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(BookingFillUpLayout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11)
                                    .addGroup(BookingFillUpLayout.createSequentialGroup()
                                        .addComponent(cash)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(gcash))
                                    .addComponent(pickUPDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BookingFillUpLayout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(90, Short.MAX_VALUE))
            .addGroup(BookingFillUpLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(print)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        BookingFillUpLayout.setVerticalGroup(
            BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BookingFillUpLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CarsGalingTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BookingFillUpLayout.createSequentialGroup()
                        .addComponent(places, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(BookingFillUpLayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Destination, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(BookingFillUpLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cash)
                                    .addComponent(gcash))))
                        .addGap(4, 4, 4)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AdditionalFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79)
                        .addComponent(print))
                    .addComponent(pickUPDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 942, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BookingFillUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(Bookpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(Bookpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(BookingFillUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BOOK1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BOOK1ActionPerformed
     // Validate required input fields
    String customerName = CarsGalingTable.getText().trim();
    String licenseNumber = Destination.getText().trim();
    String address = AdditionalFee.getText().trim();

    // Get payment method from ButtonGroup
    String paymentMethod = "";
    if (cash.isSelected()) {
        paymentMethod = "Cash";
    } else if (gcash.isSelected()) {
        paymentMethod = "GCash";
    }

    // Ensure a payment method is selected
    if (paymentMethod.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select a payment method.");
        return;  // Exit if no payment method is selected
    }

    // Validate pickup date
     java.util.Date utilDate = pickUPDate.getDate();  // Get the selected date from JDateChooser
    if (utilDate == null) {
        JOptionPane.showMessageDialog(this, "Please select a pickup date.");
        return;  // Exit if no pickup date is selected
    }

    // Validate required fields
    if (customerName.isEmpty() || licenseNumber.isEmpty() || address.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all required fields: Name, License, and Address.");
        return;  // Exit if any field is empty
    }

    // Check if a car is selected
    int selectedRow = Bookingtable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a car to book.");
        return;  // Exit if no car is selected
    }

    // Get the car ID from the selected row
    int carId = (int) Bookingtable.getValueAt(selectedRow, 0);  // Assuming the first column is the car ID
    int customerId = SessionManager.getCustomerId();  // Retrieve the current logged-in customer ID

    // Confirm booking
    int confirmation = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to book this car?",
        "Confirm Booking",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (confirmation == JOptionPane.YES_OPTION) {
        bookCar(customerId, carId, paymentMethod, licenseNumber, address);
    } else {
        JOptionPane.showMessageDialog(this, "Booking cancelled.");
    }
}

private void bookCar(int customerId, int carId, String paymentMethod, String place, String additionalFeeText) {
    String checkUserQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
    String checkActiveBookingQuery = "SELECT COUNT(*) FROM booking WHERE user_id = ? AND status = 'active'";
    String insertQuery = "INSERT INTO booking (user_id, car_id, booking_date, status, payment_method, place, additional_fee, vehicle_price, total_price) VALUES (?, ?, NOW(), 'active', ?, ?, ?, ?, ?)";
    String updateCarQuery = "UPDATE cars SET status = 'Booked' WHERE car_id = ?";
    String getVehiclePriceQuery = "SELECT price FROM cars WHERE car_id = ?";
    double additionalFee = 0.0;

    // Parse the additional fee
    try {
        additionalFee = Double.parseDouble(additionalFeeText);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid additional fee amount.");
        return;
    }

    // Retrieve the vehicle price
    double vehiclePrice = 0.0;
    try (PreparedStatement getVehiclePriceStmt = connection.prepareStatement(getVehiclePriceQuery)) {
        getVehiclePriceStmt.setInt(1, carId);
        ResultSet rsVehiclePrice = getVehiclePriceStmt.executeQuery();
        if (rsVehiclePrice.next()) {
            vehiclePrice = rsVehiclePrice.getDouble("price");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error retrieving vehicle price: " + e.getMessage());
        e.printStackTrace();
        return;
    }

    double totalPrice = vehiclePrice + additionalFee;

    try {
        connection.setAutoCommit(false);

        // Check user existence
        try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery)) {
            checkUserStmt.setInt(1, customerId);
            ResultSet rsUser = checkUserStmt.executeQuery();
            if (rsUser.next() && rsUser.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Customer does not exist.");
                return;
            }
        }

        // Check active bookings
        try (PreparedStatement checkBookingStmt = connection.prepareStatement(checkActiveBookingQuery)) {
            checkBookingStmt.setInt(1, customerId);
            ResultSet rsBooking = checkBookingStmt.executeQuery();
            if (rsBooking.next() && rsBooking.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "You already have an active booking.");
                return;
            }
        }

        // Insert booking
        try (PreparedStatement insertBookingStmt = connection.prepareStatement(insertQuery)) {
            insertBookingStmt.setInt(1, customerId);
            insertBookingStmt.setInt(2, carId);
            insertBookingStmt.setString(3, paymentMethod);
            insertBookingStmt.setString(4, place);
            insertBookingStmt.setDouble(5, additionalFee);
            insertBookingStmt.setDouble(6, vehiclePrice);
            insertBookingStmt.setDouble(7, totalPrice);

            int rows = insertBookingStmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Booking successful!");
            }
        }

        // Update car status
        try (PreparedStatement updateCarStmt = connection.prepareStatement(updateCarQuery)) {
            updateCarStmt.setInt(1, carId);
            updateCarStmt.executeUpdate();
        }

        connection.commit();
        loadAvailableCars();

    } catch (SQLException e) {
        try {
            connection.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    }//GEN-LAST:event_BOOK1ActionPerformed

    private void AdditionalFeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdditionalFeeActionPerformed
        // TODO add your handling code here: Submitt Button
        
        // Extract destination from combo box
        
    }//GEN-LAST:event_AdditionalFeeActionPerformed

    private void placesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placesActionPerformed
        //places JComboBox 
        // Get the selected item from the combo box
        String selectedPlace = (String) places.getSelectedItem();

        // Set the selected place into the Destination text field
        Destination.setText(selectedPlace);
        
        
        Double fee = placeFees.getOrDefault(selectedPlace, 0.0);  // Get fee or default to 0.0
        AdditionalFee.setText(String.format("%.2f", fee));    
    }//GEN-LAST:event_placesActionPerformed

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
     // Initialize variables for customer name, total price, payment method, and car model
    String customerName = "";
    double totalPrice = 0.0;
    String paymentMethod = "";
    String carModel = "";  // To store the car model name

    // Assuming carModel is a string from the UI or another source
    carModel = CarsGalingTable.getText().trim(); // Get car model input as string

// SQL query to get customer name, total price, payment method, and car model
String query = "SELECT u.name, b.total_price, b.payment_method, c.model " +
               "FROM users u " +
               "JOIN booking b ON u.id = b.user_id " +
               "JOIN cars c ON b.car_id = c.car_id " + // Join with correct column names
               "WHERE c.model = ?";  // Use car model in the WHERE clause

try (PreparedStatement stmt = connection.prepareStatement(query)) {
    stmt.setString(1, carModel);  // Set the car model parameter
    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
        customerName = rs.getString("name");
        totalPrice = rs.getDouble("total_price");
        paymentMethod = rs.getString("payment_method");
        carModel = rs.getString("model");  // Retrieve car model from result set
    } else {
        JOptionPane.showMessageDialog(this, "No booking found for the selected car model.");
        return;
    }
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
    e.printStackTrace();
    return;
}

// Create the formatted receipt
String receipt = String.format(
    "--------------------AUTOMOBILIS---------------------\n\n" +
    "------------------ PAYMENT RECEIPT ------------------\n\n" +
    "Customer Name: %-30s\n" +
    "----------------------------------------------------\n" +
    "Car Model: %-30s\n" +  // Display car model here
    "Destination: %-30s\n" +
    "Pick-Up Date: %-20s\n" +
    "----------------------------------------------------\n" +
    "Car Price: PHP %-10s\n" +
    "Additional Fee: PHP %-10s\n" +
    "Total Price: PHP %-10.2f\n" +
    "Payment Method: %-20s\n" +
    "----------------------------------------------------\n" +
    "Thank you for booking with us! Have a safe journey.\n" +
    "----------------------------------------------------",
    customerName,         // Customer name
    carModel,             // Car model
    places.getSelectedItem(),  // Destination (ComboBox)
    new SimpleDateFormat("yyyy-MM-dd").format(pickUPDate.getDate()), // Pick-Up Date
    price.getText(),
    AdditionalFee.getText(), // Additional Fee
    totalPrice,           // Total Price
    paymentMethod         // Payment Method
);

// Create a JTextArea to display the receipt
JTextArea textArea = new JTextArea();
textArea.setText(receipt);
textArea.setEditable(false);  // Make it non-editable

// Use the Java Print API to print the content of JTextArea
try {
    boolean complete = textArea.print();
    if (complete) {
        JOptionPane.showMessageDialog(this, "Receipt printed successfully.");
    } else {
        JOptionPane.showMessageDialog(this, "Printing was canceled.");
    }
} catch (java.awt.print.PrinterException ex) {
    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
}
    }//GEN-LAST:event_printActionPerformed

    private void cashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cashActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        // TODO add your handling code here:
        CustomerDashboard cd = new CustomerDashboard();
        cd.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_CancelActionPerformed

    private void CarsGalingTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CarsGalingTableActionPerformed
        // TODO add your handling code here:
        // to display the selected car here
        
    }//GEN-LAST:event_CarsGalingTableActionPerformed

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
            java.util.logging.Logger.getLogger(Booking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Booking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Booking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Booking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new Booking().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AdditionalFee;
    private javax.swing.JButton BOOK1;
    private javax.swing.JPanel BookingFillUp;
    private javax.swing.JTable Bookingtable;
    private javax.swing.JPanel Bookpanel;
    private javax.swing.JButton Cancel;
    private javax.swing.JTextField CarsGalingTable;
    private javax.swing.JTextField Destination;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cash;
    private javax.swing.JRadioButton gcash;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser pickUPDate;
    private javax.swing.JComboBox<String> places;
    private javax.swing.JTextField price;
    private javax.swing.JButton print;
    // End of variables declaration//GEN-END:variables
}
