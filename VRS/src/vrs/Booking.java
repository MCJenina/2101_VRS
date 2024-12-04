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
        String priceText = Bookingtable.getValueAt(selectedRow, 2).toString();
        
        

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
    String query = "SELECT c.car_id AS id, c.Model AS car_model, " +
                   "IFNULL(b.return_date, NULL) AS return_date, c.status, c.Price AS price " +
                   "FROM cars c " +
                   "LEFT JOIN booking b ON b.car_id = c.car_id AND b.status = 'active' " +
                   "WHERE c.status IN ('Available', 'Booked', 'Under Maintenance')";

    populateTable(query, false);  
}

// Populate table with query results
private void populateTable(String query, boolean isBooking) {
    try (PreparedStatement pstmt = connection.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {

        DefaultTableModel model = new DefaultTableModel();

        // Adjust columns to match the desired sequence
        model.addColumn("Car ID");
        model.addColumn("Model");
        model.addColumn("Price");
        model.addColumn("Status");
        model.addColumn("Return Date");

        // Populate the table with data from the result set
        while (rs.next()) {
            Object[] row = new Object[5]; // Five columns: Car ID, Model, Price, Status, and Return Date
            row[0] = rs.getInt("id");        // Car ID
            row[1] = rs.getString("car_model"); // Car model name
            row[2] = rs.getDouble("price");     // Car price
            row[3] = rs.getString("status");    // Car status
            row[4] = rs.getDate("return_date"); // Return date (can be null)
            model.addRow(row);
        }

        // Set the table model
        Bookingtable.setModel(model);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
    }
}
    private double getVehiclePrice(int carId) {
    String getVehiclePriceQuery = "SELECT Price FROM cars WHERE car_id = ?";
    double vehiclePrice = 0.0;

    try (PreparedStatement stmt = connection.prepareStatement(getVehiclePriceQuery)) {
        stmt.setInt(1, carId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            vehiclePrice = rs.getDouble("Price");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return vehiclePrice;
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
        ReturnDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(130, 70, 52));
        setResizable(false);

        Bookingtable.setBackground(new java.awt.Color(255, 255, 204));
        Bookingtable.setFont(Bookingtable.getFont().deriveFont((Bookingtable.getFont().getStyle() & ~java.awt.Font.ITALIC) & ~java.awt.Font.BOLD, 12));
        Bookingtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Model", "Types", "Status", "Price", "Return Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
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

        jLabel9.setText("Destination Fee:");

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

        jLabel4.setText("Return Date:");

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
                                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(BookingFillUpLayout.createSequentialGroup()
                                        .addComponent(cash)
                                        .addGap(18, 18, 18)
                                        .addComponent(gcash))
                                    .addComponent(jLabel11)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BookingFillUpLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pickUPDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ReturnDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))))
                .addContainerGap(84, Short.MAX_VALUE))
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
                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BookingFillUpLayout.createSequentialGroup()
                        .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CarsGalingTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(places, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pickUPDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addComponent(jLabel8))
                    .addGroup(BookingFillUpLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Destination, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ReturnDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(BookingFillUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdditionalFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cash)
                    .addComponent(gcash))
                .addGap(79, 79, 79)
                .addComponent(print)
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
    String address = AdditionalFee.getText().trim(); // Assuming AdditionalFee is a text field

    // Get payment method
    String paymentMethod = "";
    if (cash.isSelected()) {
        paymentMethod = "Cash";
    } else if (gcash.isSelected()) {
        paymentMethod = "GCash";
    }

    if (paymentMethod.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select a payment method.");
        return;
    }

    // Validate pickup date
    java.util.Date utilPickupDate = pickUPDate.getDate();
    if (utilPickupDate == null) {
        JOptionPane.showMessageDialog(this, "Please select a pickup date.");
        return;
    }

    // Validate return date
    java.util.Date utilReturnDate = ReturnDate.getDate(); // Assuming you have a JDateChooser for return date
    if (utilReturnDate == null) {
        JOptionPane.showMessageDialog(this, "Please select a return date.");
        return;
    }
    String returnDate = new SimpleDateFormat("yyyy-MM-dd").format(utilReturnDate);

    // Check for required fields
    if (customerName.isEmpty() || licenseNumber.isEmpty() || address.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
        return;
    }

    // Check if a car is selected
    int selectedRow = Bookingtable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a car to book.");
        return;
    }

    // Get car ID
    int carId = (int) Bookingtable.getValueAt(selectedRow, 0); // First column as car ID
    int customerId = SessionManager.getCustomerId(); // Retrieve the customer ID

    // Fetch vehicle price from the database
    double vehiclePrice = getVehiclePrice(carId); 

    // Retrieve additional fee from the input field (AdditionalFee)
    double additionalFee = 0.0;  // Default value
    try {
        additionalFee = Double.parseDouble(address); // We use the address field temporarily for additional fee
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid additional fee. Please enter a valid number.");
        return;  // Exit if the additional fee is not valid
    }

    // Calculate total price
    double totalPrice = vehiclePrice + additionalFee;

    // Display the fees to the user for confirmation
    String feeDetails = "Vehicle Price: " + vehiclePrice + "\n" +
                        "Additional Fee: " + additionalFee + "\n" +
                        "Total Price: " + totalPrice;

    int confirmation = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to book this car?\n\n" + feeDetails,
        "Confirm Booking",
        JOptionPane.YES_NO_OPTION
    );

    if (confirmation == JOptionPane.YES_OPTION) {
        // Proceed to book the car
        bookCar(customerId, carId, paymentMethod, address, additionalFee,returnDate, totalPrice);
        JOptionPane.showMessageDialog(this, "Booking successful!");
    } else {
        JOptionPane.showMessageDialog(this, "Booking cancelled.");
    }
}

private void bookCar(int customerId, int carId, String paymentMethod, String address, double additionalFee, String returnDate, double totalPrice) {
    // Check if the car is available for booking (not Booked or Under Maintenance)
    String checkCarStatusQuery = "SELECT status FROM cars WHERE car_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(checkCarStatusQuery)) {
        stmt.setInt(1, carId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String carStatus = rs.getString("status");

            // If the car is booked or under maintenance, show a message and stop execution
            if ("Booked".equals(carStatus) || "Under Maintenance".equals(carStatus)) {
                JOptionPane.showMessageDialog(this, "This car is not available for booking (Booked or Under Maintenance).");
                return;  // Stop execution if the car is unavailable
            }

            // If the car is available, proceed to booking
            String insertQuery = "INSERT INTO booking (user_id, car_id, booking_date, return_date, status, payment_method, payment_status, place, additional_fee, total_price) VALUES (?, ?, NOW(), ?, 'active', ?, 'Unpaid', ?, ?, ?)";
            String updateCarQuery = "UPDATE cars SET status = 'Booked' WHERE car_id = ?";

            connection.setAutoCommit(false);

            try (PreparedStatement insertBookingStmt = connection.prepareStatement(insertQuery);
                 PreparedStatement updateCarStmt = connection.prepareStatement(updateCarQuery)) {

                // Insert booking into the database
                insertBookingStmt.setInt(1, customerId);
                insertBookingStmt.setInt(2, carId);
                insertBookingStmt.setString(3, returnDate);
                insertBookingStmt.setString(4, paymentMethod);
                insertBookingStmt.setString(5, address);
                insertBookingStmt.setDouble(6, additionalFee); // Store the additional fee entered
                insertBookingStmt.setDouble(7, totalPrice);  // Store the total price including any additional fee

                int rows = insertBookingStmt.executeUpdate();
                
                // If the booking is successful, update car status to 'Booked'
                if (rows > 0) {
                    // Update car status to 'Booked'
                    updateCarStmt.setInt(1, carId);
                    updateCarStmt.executeUpdate();

                    // Commit transaction
                    connection.commit();

                    // Update the JTable with the new status and return date
                    updateCarStatusInJTable(carId, "Booked", returnDate);

                    JOptionPane.showMessageDialog(this, "Booking successfully made!");
                } else {
                    // If no rows are inserted, display error
                    connection.rollback();
                    JOptionPane.showMessageDialog(this, "Error during booking.");
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error inserting booking: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true); // Reset to default autocommit mode
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error checking car status: " + e.getMessage());
    }
}

private void updateCarStatusInJTable(int carId, String status, String returnDate) {
    // Loop through the rows in the JTable and update the status and return date
    for (int i = 0; i < Bookingtable.getRowCount(); i++) {
        // Get car ID from the first column of the JTable
        int currentCarId = (int) Bookingtable.getValueAt(i, 0);

        // If the car ID matches, update the status and return date
        if (currentCarId == carId) {
            Bookingtable.setValueAt(status, i, 3); // Assuming the 3rd column is the 'status' column
            Bookingtable.setValueAt(returnDate, i, 4); // Assuming the 4th column is 'return_date'
            break;
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
     // Initialize variables
String customerName = "";
double totalPrice = 0.0;
String paymentMethod = "";
String carModel = "";  // To store the car model name
String formattedReturnDate = "N/A";  // Default value for return date
double carPrice = 0.0; // Initialize car price
double additionalFee = 0.0; // Initialize additional fee

// Assuming carModel is a string from the UI
carModel = CarsGalingTable.getText().trim(); // Get car model input as string

// Query to retrieve booking details
String query = "SELECT u.name, b.total_price, b.payment_method, c.model, b.return_date, c.price, b.additional_fee " +
               "FROM users u " +
               "JOIN booking b ON u.id = b.user_id " +
               "JOIN cars c ON b.car_id = c.car_id " + 
               "WHERE c.model = ?";

try (PreparedStatement stmt = connection.prepareStatement(query)) {
    stmt.setString(1, carModel);  // Set the car model parameter
    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
        customerName = rs.getString("name");
        totalPrice = rs.getDouble("total_price");
        paymentMethod = rs.getString("payment_method");
        carModel = rs.getString("model");
        carPrice = rs.getDouble("price"); // Get car price from database
        additionalFee = rs.getDouble("additional_fee"); // Get additional fee
        Date returnDate = rs.getDate("return_date");  // Retrieve return date

        // Debugging: Check if returnDate is null
        System.out.println("Return Date from DB: " + returnDate);

        if (returnDate != null) {  // Check if returnDate is not null
            formattedReturnDate = new SimpleDateFormat("yyyy-MM-dd").format(returnDate);
        } else {
            // If return date is null, set to default value "N/A"
            System.out.println("Return Date is null, setting to 'N/A'");
            formattedReturnDate = "N/A";
        }

        // Calculate total price (car price + additional fee)
        totalPrice = carPrice + additionalFee;
    } else {
        JOptionPane.showMessageDialog(this, "No booking found for the selected car model.");
        return;
    }
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
    e.printStackTrace();
    return;
}

// Format the receipt
String receipt = String.format(
    "--------------------AUTOMOBILIS---------------------\n\n" +
    "------------------ PAYMENT RECEIPT ------------------\n\n" +
    "Customer Name: %-30s\n" +
    "----------------------------------------------------\n" +
    "Vehicle Model: %-30s\n" +
    "Destination: %-30s\n" +
    "Pick-Up Date: %-20s\n" +
    "Return Date: %-20s\n" +  // Include return date here
    "----------------------------------------------------\n" +
    "Car Price: PHP %-10.2f\n" +
    "Additional Fee: PHP %-10.2f\n" +
    "Total Price: PHP %-10.2f\n" +  // Now showing total price with the sum of car price + additional fee
    "Payment Method: %-20s\n" +
    "----------------------------------------------------\n" +
    "Thank you for booking with us! Have a safe journey.\n" +
    "----------------------------------------------------",
    customerName,           // Customer name
    carModel,               // Car model
    places.getSelectedItem(), // Destination (ComboBox)
    new SimpleDateFormat("yyyy-MM-dd").format(pickUPDate.getDate()), // Pick-Up Date
    formattedReturnDate,    // Correct Return Date
    carPrice,               // Car Price
    additionalFee,          // Additional Fee
    totalPrice,             // Total Price (sum of car price + additional fee)
    paymentMethod           // Payment Method
);

// Debugging: Check the formatted receipt
System.out.println("Formatted Receipt:\n" + receipt);

// Display receipt in JTextArea
JTextArea textArea = new JTextArea();
textArea.setText(receipt);
textArea.setEditable(false);

// Print receipt
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
    private com.toedter.calendar.JDateChooser ReturnDate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cash;
    private javax.swing.JRadioButton gcash;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
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
