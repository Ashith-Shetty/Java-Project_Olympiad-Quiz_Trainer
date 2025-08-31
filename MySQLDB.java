package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDB {
	
	    // Replace with your DB credentials
	    private static final String URL = "jdbc:mysql://localhost:3306/question_bank";
	    private static final String USER = "root";
	    private static final String PASSWORD = "Test@1234";

	    public static Connection getConnection() {
	        Connection connection = null;

	        try {
	            // Load the MySQL JDBC driver
	            Class.forName("com.mysql.cj.jdbc.Driver");

	            // Establish the connection
	            connection = DriverManager.getConnection(URL, USER, PASSWORD);
	            System.out.println("Connected to the database successfully!");
	            return connection;

	        } catch (ClassNotFoundException e) {
	            System.out.println("MySQL JDBC Driver not found.");
	            e.printStackTrace();
	        } catch (SQLException e) {
	            System.out.println("Failed to connect to the database.");
	            e.printStackTrace();
	        } 
	        return null;
	        
	    }
}
