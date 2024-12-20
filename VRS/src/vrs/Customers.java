package vrs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Customers extends javax.swing.JFrame {

    public Customers() {
        initComponents();
        populateCustomerTable();
        this.setLocationRelativeTo(null); // Centers the window
        
        //So if the user want to update or delete, user just select in the table and it will display it the details 
        CustomersList.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int selectedRow = CustomersList.getSelectedRow(); // Get the selected row
            if (selectedRow != -1) {
                // Populate text fields with data from the selected row   
                CustomerTXT.setText(CustomersList.getValueAt(selectedRow, 1).toString()); 
                CustomersLIcenseTXT.setText(CustomersList.getValueAt(selectedRow, 2).toString());
                CustomersAddTXT.setText(CustomersList.getValueAt(selectedRow, 3).toString()); 
                CustomerIDTXT.setText(CustomersList.getValueAt(selectedRow, 0).toString());
                contactNumber.setText(CustomersList.getValueAt(selectedRow, 4).toString());
            }
        }
        });
        
    }

    // Establishes and returns a database connection
    private Connection connectDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/vehiclerentalsystem?useSSL=false&serverTimezone=UTC";
            String user = "Jenina";
            String password = "qwertyuiop";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            showError("Failed to connect to the database", e);
            return null;
        }
    }

    // Utility method for showing error messages
    private void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message + ": " + e.getMessage());
        e.printStackTrace();
    }

    // Populates the customer table with data from the database
   private void populateCustomerTable() {
    String query = """
        SELECT u.id AS customer_id, u.name AS customer_name, u.license_number, u.address, u.contact_number
        FROM users u
        LEFT JOIN booking b ON u.id = b.user_id AND b.status = 'active'
    """;

    try (Connection conn = connectDatabase();
         PreparedStatement pst = conn.prepareStatement(query);
         ResultSet rs = pst.executeQuery()) {

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Customer ID", "Name", "License Number", "Address", "Contact Number"}, 0
        );

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getString("license_number"),
                rs.getString("address"),
                rs.getString("contact_number")
            });
        }

        CustomersList.setModel(model);

    } catch (SQLException e) {
        showError("Error fetching customer data", e);
    }
   }


    // Checks if a customer has an active booking
    private boolean hasActiveBooking(int customerId) {
        String query = "SELECT COUNT(*) AS booking_count FROM booking WHERE user_id = ? AND status = 'active'";
        return executeCountQuery(query, customerId) > 0;
    }

    // Adds a booking for a customer
    private void addBooking(int customerId, int carId, String bookingDetails) {
        if (hasActiveBooking(customerId)) {
            JOptionPane.showMessageDialog(this, "Customer already has an active booking. Cannot create another.");
            return;
        }

        String insertQuery = "INSERT INTO booking (user_id, car_id, booking_details, status) VALUES (?, ?, ?, 'active')";
        try (Connection conn = connectDatabase();
             PreparedStatement pst = conn.prepareStatement(insertQuery)) {

            pst.setInt(1, customerId);
            pst.setInt(2, carId);
            pst.setString(3, bookingDetails);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Booking created successfully.");
            populateCustomerTable(); // Refresh customer list

        } catch (SQLException e) {
            showError("Error creating booking", e);
        }
    }

    private void deleteCustomer(int customerId) {
    // SQL query to delete the customer if they don't have an active booking
    String deleteQuery = """
        DELETE FROM users 
        WHERE id = ? AND NOT EXISTS (SELECT 1 FROM booking WHERE user_id = ? AND status = 'active')
    """;

    try (Connection conn = connectDatabase();
         PreparedStatement pst = conn.prepareStatement(deleteQuery)) {

        // Set the parameters for the delete query
        pst.setInt(1, customerId);
        pst.setInt(2, customerId);

        // Execute the delete query
        int rowsAffected = pst.executeUpdate();

        if (rowsAffected > 0) {
            // Successfully deleted customer
            JOptionPane.showMessageDialog(this, "Customer deleted successfully.");
            populateCustomerTable(); // Refresh customer list
        } else {
            // Customer cannot be deleted because they have an active booking
            JOptionPane.showMessageDialog(this, "Customer cannot be deleted because they have an active booking.");
        }

    } catch (SQLException e) {
        // Handle any SQL exceptions
        showError("Error deleting customer", e);
    }
    }

    // Executes a query that returns a single count
    private int executeCountQuery(String query, int parameter) {
        try (Connection conn = connectDatabase();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, parameter);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            showError("Error executing count query", e);
        }
        return 0; // Default to 0 on error
    }

    public void clearFields(){
        CustomerIDTXT.setText(" ");
        CustomerTXT.setText(" ");
        CustomersLIcenseTXT.setText(" ");
        CustomersAddTXT.setText(" ");
        contactNumber.setText(" ");
    }
        
        


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        CustomerTXT = new javax.swing.JTextField();
        CustomersLIcenseTXT = new javax.swing.JTextField();
        CustomersAddTXT = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        CustomerIDTXT = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        contactNumber = new javax.swing.JTextField();
        CLEAR = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        CustomersList = new javax.swing.JTable();
        SearchCustomer = new javax.swing.JButton();
        DeleteCustomer = new javax.swing.JButton();
        UpdateCustomer = new javax.swing.JButton();
        RefrshCustomer = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 200, 105));

        jLabel1.setFont(new java.awt.Font("Mongolian Baiti", 1, 18)); // NOI18N
        jLabel1.setText("MANAGE CUSTOMER");

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 20)); // NOI18N
        jLabel3.setText("X");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jLabel8.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        jLabel8.setText("CUSTOMERS LIST");

        jPanel2.setBackground(new java.awt.Color(244, 244, 244));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "AutoMoBilis"));

        CustomerTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomerTXTActionPerformed(evt);
            }
        });

        CustomersLIcenseTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomersLIcenseTXTActionPerformed(evt);
            }
        });

        CustomersAddTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomersAddTXTActionPerformed(evt);
            }
        });

        jLabel2.setText("NAME:");

        jLabel4.setText("LICENSE No.:");

        jLabel5.setText("ADDRESS:");

        jLabel6.setText("CUSTOMER ID:");

        CustomerIDTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomerIDTXTActionPerformed(evt);
            }
        });

        jLabel9.setText("CONTACT NUMBER:");

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
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(CLEAR)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6)
                                .addComponent(jLabel5)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4))
                            .addGap(41, 41, 41)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(CustomerIDTXT, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                                .addComponent(contactNumber, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(CustomerTXT, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(CustomersAddTXT, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(CustomersLIcenseTXT)))))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(CustomerIDTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(CustomerTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(CustomersLIcenseTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(CustomersAddTXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(contactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CLEAR)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        CustomersList.setBackground(new java.awt.Color(255, 255, 204));
        CustomersList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "NAME", "LICENSE NO.", "ADDRESS", "CUSTOMER ID", "CONTACT NUMBER"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Short.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
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
        CustomersList.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(CustomersList);
        CustomersList.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        SearchCustomer.setText("SEARCH");
        SearchCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchCustomerActionPerformed(evt);
            }
        });

        DeleteCustomer.setText("DELETE");
        DeleteCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteCustomerActionPerformed(evt);
            }
        });

        UpdateCustomer.setText("UPDATE");
        UpdateCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateCustomerActionPerformed(evt);
            }
        });

        RefrshCustomer.setText("REFRESH");
        RefrshCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefrshCustomerActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 200, 105));

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Rockwell", 0, 10)); // NOI18N
        jLabel7.setText("Copyright - BSIT 2101(2024-2025). All Rights Reserved");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(backButton)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addComponent(backButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(312, 312, 312))
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(UpdateCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(SearchCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(DeleteCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RefrshCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 678, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DeleteCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SearchCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(UpdateCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RefrshCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CustomerTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomerTXTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CustomerTXTActionPerformed

    private void CustomersLIcenseTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomersLIcenseTXTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CustomersLIcenseTXTActionPerformed

    private void CustomersAddTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomersAddTXTActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_CustomersAddTXTActionPerformed

    private void CustomerIDTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomerIDTXTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CustomerIDTXTActionPerformed

    private void SearchCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchCustomerActionPerformed
        // TODO add your handling code here:
         // Get the search term from the user
    String searchTerm = CustomerTXT.getText().trim();

    // Validate input
    if (searchTerm.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter a name or license number to search.");
        return;
    }

    // Ask for confirmation
    int confirmation = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to search for \"" + searchTerm + "\"?",
        "Confirm Search",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    // Proceed only if the user confirms
    if (confirmation == JOptionPane.YES_OPTION) {
        // SQL query to search customers based on name or license number
        String query = """
            SELECT u.id AS customer_id, u.name AS customer_name, u.license_number, u.address,
                   IFNULL(b.status, 'No Booking') AS booking_status
            FROM users u
            LEFT JOIN booking b ON u.id = b.user_id AND b.status = 'active'
            WHERE u.name LIKE ? OR u.license_number LIKE ?
        """;

        try (Connection conn = connectDatabase();
             PreparedStatement pst = conn.prepareStatement(query)) {

            // Set the search term with wildcards for partial matching
            pst.setString(1, "%" + searchTerm + "%");
            pst.setString(2, "%" + searchTerm + "%");

            try (ResultSet rs = pst.executeQuery()) {
                // Create a new table model for the search results
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Customer ID", "Name", "License Number", "Address", "Booking Status"}, 0
                );

                // Populate the table model with results
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("license_number"),
                        rs.getString("address"),
                        rs.getString("booking_status")
                    });
                }

                // Update the CustomersList table with the new model
                CustomersList.setModel(model);

                // Show a message if no results are found
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No matching customers found.");
                }

            }
        } catch (SQLException e) {
            showError("Error searching customer data", e);
        }
    } else {
        // If the user cancels, just return
        JOptionPane.showMessageDialog(this, "Search cancelled.");
    }
    }//GEN-LAST:event_SearchCustomerActionPerformed

    private void DeleteCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteCustomerActionPerformed
       // Get the selected row in the customer table
    int selectedRow = CustomersList.getSelectedRow();  
    if (selectedRow == -1) {
        // No customer selected, show a message
        JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
        return;
    }

    // Get the Customer ID from the selected row (first column in the table)
    int customerId = (int) CustomersList.getValueAt(selectedRow, 0);

    // Confirm the deletion
    int confirmation = JOptionPane.showConfirmDialog(
        this, 
        "Are you sure you want to delete the customer with ID: " + customerId + "?",
        "Delete Customer", 
        JOptionPane.YES_NO_OPTION
    );

    if (confirmation == JOptionPane.YES_OPTION) {
        // If confirmed, perform the deletion
        deleteCustomer(customerId);  // Call the deleteCustomer method here
        populateCustomerTable();   // Refresh the customer table after deletion
    } else {
        // If canceled, display a message
        JOptionPane.showMessageDialog(this, "Delete operation canceled.");
    }

    }//GEN-LAST:event_DeleteCustomerActionPerformed

    private void UpdateCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateCustomerActionPerformed
        // Get the customer details from the input fields
    String customerId = CustomerIDTXT.getText().trim();
    String customerName = CustomerTXT.getText().trim();
    String licenseNumber = CustomersLIcenseTXT.getText().trim();
    String address = CustomersAddTXT.getText().trim();
    String contact_number = contactNumber.getText().trim();
    
    // Validate inputs
    if (customerId.isEmpty() || customerName.isEmpty() || licenseNumber.isEmpty() || address.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all the fields.");
        return;
    }

    // Confirm update
    int confirmation = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to update the customer with ID \"" + customerId + "\"?",
        "Confirm Update",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (confirmation == JOptionPane.YES_OPTION) {
        // SQL query to update the customer details
        String query = """
            UPDATE users 
            SET name = ?, license_number = ?, address = ? 
            WHERE id = ?
        """;

        try (Connection conn = connectDatabase();
             PreparedStatement pst = conn.prepareStatement(query)) {

            // Set the parameters for the update query
            pst.setString(1, customerName);     // First placeholder (name)
            pst.setString(2, licenseNumber);   // Second placeholder (license_number)
            pst.setString(3, address);         // Third placeholder (address)
            pst.setString(4, customerId);      // Fourth placeholder (id)
            //pst.setString(5, contact_number);

            // Execute the update query
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully.");
                populateCustomerTable(); // Refresh the customer table after updating
            } else {
                JOptionPane.showMessageDialog(this, "Customer ID not found. No updates made.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage());
        }
    } else {
        // If the user cancels the update
        JOptionPane.showMessageDialog(this, "Update cancelled.");
    }
    }//GEN-LAST:event_UpdateCustomerActionPerformed

    private void RefrshCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefrshCustomerActionPerformed
        // TODO add your handling code here:
        // Show confirmation dialog
    int confirmation = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to refresh the customer data?",
        "Confirm Refresh",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (confirmation == JOptionPane.YES_OPTION) {
        // Call the method to populate or refresh the customer table
        populateCustomerTable();
        JOptionPane.showMessageDialog(this, "Customer data refreshed successfully.");
    } else {
        // If the user cancels the refresh action
        JOptionPane.showMessageDialog(this, "Refresh cancelled.");
    }
    }//GEN-LAST:event_RefrshCustomerActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        Admin_DashBoard adminDashboard = new Admin_DashBoard();
        adminDashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_backButtonActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        // TODO add your handling code here:
        this.dispose();
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
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Customers().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CLEAR;
    private javax.swing.JTextField CustomerIDTXT;
    private javax.swing.JTextField CustomerTXT;
    private javax.swing.JTextField CustomersAddTXT;
    private javax.swing.JTextField CustomersLIcenseTXT;
    private javax.swing.JTable CustomersList;
    private javax.swing.JButton DeleteCustomer;
    private javax.swing.JButton RefrshCustomer;
    private javax.swing.JButton SearchCustomer;
    private javax.swing.JButton UpdateCustomer;
    private javax.swing.JButton backButton;
    private javax.swing.JTextField contactNumber;
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
