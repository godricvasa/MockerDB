package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.Logger.BaseClass;
import com.table.TableMetaData;
import net.sf.json.JSONObject;



public class MySqlUtil  {
	public static final Logger logger  = Logger.getLogger(MySqlUtil.class.getName());

	public static int MAX_ROWS = 2000; 
	public static int CASS_ROWS = 2000; 
	static
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch(Exception e) {
			logger.log(Level.SEVERE,"Driver issue " +e.getMessage());
//			System.out.println("Driver issue " +e.getMessage());
			e.printStackTrace();
		}
    }

	private static final String DB_URL = "localhost:3306";
    private static final String DB_PROTOCOL = "jdbc:mysql://";
    private static final String DEFAULT_DB_NAME = "testing";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection(JSONObject obj) 
    {
    	String hostUrl = (String)obj.getString("database_host");
    	String username = obj.getString("database_username");
    	String pwd = obj.getString("database_password");
    	String dbName = obj.getString("database_name");
    	if(DB_URL.equals(hostUrl) && DEFAULT_DB_NAME.equals(dbName) && DB_USERNAME.equals(username) && DB_PASSWORD.equals(pwd))
    	{
    		return getDefaultConnection(dbName);
    	}
    	else {
    		try {
				return DriverManager.getConnection(DB_PROTOCOL+hostUrl+"/"+dbName, username, pwd);

			} catch (SQLException e) {

				logger.log(Level.SEVERE, "problem in sql connectivity. Error: ", e);
//				System.out.println(e.getMessage());
			}

    	}
    	return null;
    }
    
    public static Connection getConnection() {
       return getDefaultConnection(DEFAULT_DB_NAME);
    }
    
    public static Connection getDefaultConnection(String dbName)
    {
	    try {
	        return DriverManager.getConnection(DB_PROTOCOL+DB_URL+"/"+dbName+"?useSSL=false", DB_USERNAME, DB_PASSWORD);
	    } catch (SQLException e) {
			logger.severe("Error connecting to database: " + e.getMessage());
//	        System.out.println("Error connecting to database: " + e.getMessage());
	        return null;
	    }
    }
    
    public JDBCType getJDBCType(String dataType) {
    	
    	return JDBCType.valueOf(getSqlType(dataType));
    }
    
    private int getSqlType(String dataType)
    {
    	int sqlType = -1;
    	dataType = dataType.toUpperCase();
    	switch (dataType)
    	{
    	    case "CHAR":
    	        sqlType = java.sql.Types.CHAR;
    	        break;
    	    case "TINYINT":
    	        sqlType = java.sql.Types.TINYINT;
    	        break;
    	    case "SMALLINT":
    	        sqlType = java.sql.Types.SMALLINT;
    	        break;
    	    case "INTEGER":
    	        sqlType = java.sql.Types.INTEGER;
    	        break;
    	    case "BIGINT":
    	        sqlType = java.sql.Types.BIGINT;
    	        break;
    	    case "FLOAT":
    	        sqlType = java.sql.Types.FLOAT;
    	        break;
    	    case "DOUBLE":
    	        sqlType = java.sql.Types.DOUBLE;
    	        break;
    	    case "VARCHAR":
    	        sqlType = java.sql.Types.VARCHAR;
    	        break;
    	    case "LONGVARCHAR":
    	        sqlType = java.sql.Types.LONGVARCHAR;
    	        break;
    	    // Add more cases as needed
    	    default:
				logger.log(Level.SEVERE,"Unsupported data type: " + dataType);
    	        throw new UnsupportedOperationException("Unsupported data type: " + dataType);
    	}
    	return sqlType;
    }
}