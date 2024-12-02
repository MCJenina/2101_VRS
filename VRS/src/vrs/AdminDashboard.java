package vrs;

//import com.mycompany.vrsystem.Cars;
//import com.mycompany.vrsystem.Customers;
//import com.mycompany.vrsystem.LogIn;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {

    
    public AdminDashboard() {
       
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

       
        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        
        setVisible(true);
    }

    
    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        
        JButton customerButton = new JButton("Manage Customers");
        customerButton.setBounds(50, 50, 200, 30);
        customerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Navigate to Customer Management page
                Customers customerForm = new Customers(); // Your existing Customer form
                customerForm.setVisible(true);
                dispose();  // Close the Admin Dashboard when navigating to Customer form
            }
        });
        panel.add(customerButton);

        
        JButton carButton = new JButton("Manage Cars");
        carButton.setBounds(50, 100, 200, 30);
        carButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Navigate to Car Management page
                Car carForm = new Car(); // Your existing Car form
                carForm.setVisible(true);
                dispose();  // Close the Admin Dashboard when navigating to Car form
            }
        });
        panel.add(carButton);

        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 150, 200, 30);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Log out and go back to the login form
                LogIn loginForm = new LogIn(); // Your login form
                loginForm.setVisible(true);
                dispose();  
            }
        });
        panel.add(logoutButton);
    }

    public static void main(String[] args) {
        new AdminDashboard(); 
    }
}
