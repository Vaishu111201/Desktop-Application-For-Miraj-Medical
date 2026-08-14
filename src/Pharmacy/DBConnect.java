package Pharmacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    public static Connection connectDB() {
        Connection conn = null;
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // ✅ Update DB name (should match the one you created)
            String url = "jdbc:mysql://localhost:3306/miraj_medical_db";
            String user = "root";
            String password = "leon5cprytv8@"; // Your actual MySQL password

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Database Connected Successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Database Connection Failed: " + e.getMessage());
        }
        return conn;
    }

    // Optional: test connection directly
    public static void main(String[] args) {
        connectDB();
    }
}




