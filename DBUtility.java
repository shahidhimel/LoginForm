import java.sql.*;

import javax.sql.RowSet;

import com.sun.rowset.CachedRowSetImpl;

public class DBUtility {
	
	public static boolean connected =false;

	
	public static Connection connection;
	

    public static String login(String SSN_entry) throws ClassNotFoundException, SQLException{
   
   	 // Load the JDBC driver
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      System.out.println("Driver loaded");

      // Connect to a database
      connection = DriverManager.getConnection
      ("jdbc:sqlserver://s16988308.onlinehome-server.com:1433;databaseName=CUNY_DB;integratedSecurity=false;" 
      		, "cst3613", "password1");    
      System.out.println("Database connected");

      // Create a statement
      Statement statement = connection.createStatement(); 

			ResultSet resultSet = statement.executeQuery
				      ("select * from Enrollment where Enrollment.ssn =" + SSN_entry);

		      if (resultSet.next()) {
		    	  connected = true;
		          return "Connected";
		      }
		      else {  
		    	  connected = false;
		    	  return "Error: Student Not found";
		      }

    }
    
    public static String getStudentName(String SSN_entry) throws ClassNotFoundException, SQLException{

         // Create a statement
         Statement statement = connection.createStatement(); 

   			ResultSet resultSet = statement.executeQuery
   				      ("select concat(firstName,' ',lastName) from Students where ssn ="+ SSN_entry);
   		if (resultSet.next())
   			return resultSet.getString(1);
   		else
   			return "";
         //this will be used only if student could login
         //means it exist
       }

    public static RowSet getStudentRowset(String query) throws SQLException, ClassNotFoundException{

         
    	//Creating and Executing RowSet   
        RowSet rowSet = new CachedRowSetImpl();
        rowSet.setUrl("jdbc:sqlserver://s16988308.onlinehome-server.com:1433;databaseName=CUNY_DB;integratedSecurity=false");  
        rowSet.setUsername("cst3613");  
        rowSet.setPassword("password1");               
        rowSet.setCommand(query);  
        rowSet.execute(); 
        return rowSet;
    	
    }
    
    public static void updateEnroll(String query) throws SQLException, ClassNotFoundException{

        Statement statement = connection.createStatement(); 
        statement.executeUpdate(query);
   	
    }

}
