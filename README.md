import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LogInClass {

    public static void main(String[] args) {
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/vehiclerentalsystem?useSSL=false&serverTimezone=UTC&autoReconnect=true";
        String username = "admin"; //MySQL username for admin only
        String password = "admin123"; // MySQL password for admin only

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");

            // Test query to ensure the connection is fully established
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connected successfully!");
                // Close the connection
                conn.close();
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: Unable to connect to database. Check your connection details.");
            e.printStackTrace(); 
        } catch (ClassNotFoundException e) {
            System.err.println("Driver Error: MySQL JDBC Driver not found.");
            e.printStackTrace(); 
        }
    }
}

