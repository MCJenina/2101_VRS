package vrs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.logging.*;

public class Logs extends javax.swing.JFrame {

    // Create a Logger instance for the Logs class
    private static final Logger logger = Logger.getLogger(Logs.class.getName());

    // Table model to hold log data
    private DefaultTableModel logTableModel;

    public Logs() {
        initComponents();
        
        // Set up the table model to hold log data
        logTableModel = new DefaultTableModel(new Object[][] {}, new String[] {"Time", "Log Message"});
        jTable1.setModel(logTableModel);
        
        // Add a custom log handler to update the table
        logger.addHandler(new TableLogHandler());
        
        // Log when the Logs window is initialized
        logger.info("Logs window initialized.");
    }

    // Custom log handler to update the JTable with log messages
    private class TableLogHandler extends Handler {

        @Override
        public void publish(LogRecord record) {
            // Get the log message and timestamp
            String message = record.getMessage();
            String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(record.getMillis()));

            // Add the log message to the table model
            SwingUtilities.invokeLater(() -> {
                logTableModel.addRow(new Object[]{timestamp, message});
            });
        }

        @Override
        public void flush() {
            // Not used in this case
        }

        @Override
        public void close() throws SecurityException {
            // Not used in this case
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Time", "Log Message"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Logs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Logs().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
