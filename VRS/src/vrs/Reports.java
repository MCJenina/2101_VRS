/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vrs;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class Reports extends javax.swing.JFrame {

    private Connection connection;

    public Reports() {
        initComponents();
        connectDatabase();  // Establish the database connection when the report is created
        populateMonthlySalesReport();
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

    // Method to populate monthly sales report
    public void populateMonthlySalesReport() {
        String query = """
            SELECT c.car_id, c.model, COUNT(b.booking_id) AS total_rentals, 
                   SUM(c.price + IFNULL(r.late_fee, 0) + IFNULL(r.damage_fee, 0)) AS total_revenue, 
                   DATE_FORMAT(b.booking_date, '%Y-%m') AS month 
            FROM cars c 
            JOIN booking b ON c.car_id = b.car_id 
            LEFT JOIN returns r ON b.booking_id = r.booking_id 
            WHERE b.status = 'returned' 
            GROUP BY c.car_id, c.model, month 
            ORDER BY total_revenue DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Car ID", "Model", "Total Rentals", "Total Revenue", "Month"}, 0
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("car_id"),
                    rs.getString("model"),
                    rs.getInt("total_rentals"),
                    rs.getDouble("total_revenue"),
                    rs.getString("month")
                });
            }

            salestable.setModel(model);  // `salestable` is your JTable

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching sales report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchSalesReport(String carId, String model, String month) {
        StringBuilder query = new StringBuilder("""
            SELECT c.car_id, c.model, COUNT(b.booking_id) AS total_rentals, 
                   SUM(c.price + IFNULL(r.late_fee, 0) + IFNULL(r.damage_fee, 0)) AS total_revenue, 
                   DATE_FORMAT(b.booking_date, '%Y-%m') AS month 
            FROM cars c 
            JOIN booking b ON c.car_id = b.car_id 
            LEFT JOIN returns r ON b.booking_id = r.booking_id 
            WHERE b.status = 'returned'
        """);

        if (!carId.isEmpty()) {
            query.append(" AND c.car_id = ? ");
        }
        if (!model.isEmpty()) {
            query.append(" AND c.model LIKE ? ");
        }
        if (!month.isEmpty()) {
            query.append(" AND DATE_FORMAT(b.booking_date, '%m') = ? ");
        }

        query.append(" GROUP BY c.car_id, c.model, month ORDER BY month DESC");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            int paramIndex = 1;

            if (!carId.isEmpty()) {
                stmt.setString(paramIndex++, carId);
            }
            if (!model.isEmpty()) {
                stmt.setString(paramIndex++, "%" + model + "%");
            }
            if (!month.isEmpty()) {
                stmt.setString(paramIndex++, month);
            }

            ResultSet rs = stmt.executeQuery();
            DefaultTableModel modelTable = (DefaultTableModel) salestable.getModel();
            modelTable.setRowCount(0);  // Clear existing rows

            while (rs.next()) {
                Object[] rowData = {
                    rs.getInt("car_id"),
                    rs.getString("model"),
                    rs.getInt("total_rentals"),
                    rs.getDouble("total_revenue"),
                    rs.getString("month")
                };
                modelTable.addRow(rowData);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching report: " + e.getMessage());
            e.printStackTrace();
        }
    }





    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        salestable = new javax.swing.JTable();
        Bookpanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        searchCarIdField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        searchModelField = new javax.swing.JTextField();
        monthChooser = new com.toedter.calendar.JMonthChooser();
        jLabel5 = new javax.swing.JLabel();
        yearChooser = new com.toedter.calendar.JYearChooser();
        search = new javax.swing.JButton();
        back = new javax.swing.JButton();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        salestable.setBackground(new java.awt.Color(255, 255, 204));
        salestable.setFont(salestable.getFont().deriveFont((salestable.getFont().getStyle() & ~java.awt.Font.ITALIC) & ~java.awt.Font.BOLD, 12));
        salestable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Car Id", "Model", "Total Renatls", "Total Revenue", "Month"
            }
        ));
        jScrollPane1.setViewportView(salestable);

        Bookpanel.setBackground(new java.awt.Color(255, 200, 105));

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 20)); // NOI18N
        jLabel3.setText("X");

        jLabel6.setFont(new java.awt.Font("Mongolian Baiti", 1, 18)); // NOI18N
        jLabel6.setText("SALES REPORT");

        javax.swing.GroupLayout BookpanelLayout = new javax.swing.GroupLayout(Bookpanel);
        Bookpanel.setLayout(BookpanelLayout);
        BookpanelLayout.setHorizontalGroup(
            BookpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BookpanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addGap(616, 616, 616)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        BookpanelLayout.setVerticalGroup(
            BookpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BookpanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(BookpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 200, 105));

        jLabel7.setFont(new java.awt.Font("Rockwell", 0, 10)); // NOI18N
        jLabel7.setText("Copyright - BSIT 2101(2024-2025). All Rights Reserved");

        jLabel1.setText("Vehicle Id:");

        searchCarIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchCarIdFieldActionPerformed(evt);
            }
        });

        jLabel4.setText("Model:");

        searchModelField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchModelFieldActionPerformed(evt);
            }
        });

        jLabel5.setText("Month:");

        search.setText("Search");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        back.setText("Back");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(monthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(yearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(searchModelField, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(searchCarIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(78, 78, 78)
                                        .addComponent(search)))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(back)
                .addGap(59, 59, 59))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(searchCarIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(search))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(searchModelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(monthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(yearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(back)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Bookpanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(Bookpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchCarIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchCarIdFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchCarIdFieldActionPerformed

    private void searchModelFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchModelFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchModelFieldActionPerformed

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        // TODO add your handling code here: // Kunin ang input mula sa mga fields
      String carId = searchCarIdField.getText().trim();  // JTextField for Car ID
    String model = searchModelField.getText().trim();  // JTextField for Model
    String selectedMonth = "";

    if (monthChooser.getMonth() >= 0) {
        int month = monthChooser.getMonth() + 1;  // Convert to 1-based month
        selectedMonth = String.format("%02d", month);
    }

    searchSalesReport(carId, model, selectedMonth);
    }//GEN-LAST:event_searchActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        // TODO add your handling code here:
        Admin_DashBoard ad = new Admin_DashBoard();
        ad.setVisible(true);
        dispose();
    }//GEN-LAST:event_backActionPerformed

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
            java.util.logging.Logger.getLogger(Reports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Reports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Reports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Reports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Reports().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Bookpanel;
    private javax.swing.JButton back;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private com.toedter.calendar.JMonthChooser monthChooser;
    private javax.swing.JTable salestable;
    private javax.swing.JButton search;
    private javax.swing.JTextField searchCarIdField;
    private javax.swing.JTextField searchModelField;
    private com.toedter.calendar.JYearChooser yearChooser;
    // End of variables declaration//GEN-END:variables
}
