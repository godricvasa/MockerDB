package com.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "vasa";
    static {
    	try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver ());
    	}
    	catch(Exception e) {
    		System.out.println("Driver issue " +e.getMessage());
    		e.printStackTrace();
    	}
    }

    public static Connection getConnection() {
        try {
        	
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getColumns(String tableName) {

        List<String> columns = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'users" + tableName + "'")) {
            while (rs.next()) {
                columns.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return columns;
    }

    public static void populateData(String tableName, List<String> columns, List<String[]> data) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String[] row : data) {
                StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
                for (int i = 0; i < columns.size(); i++) {
                    query.append(columns.get(i));
                    if (i < columns.size() - 1) {
                        query.append(", ");
                    }
                }
                query.append(") VALUES (");
                for (int i = 0; i < row.length; i++) {
                    query.append("'" + row[i] + "'");
                    if (i < row.length - 1) {
                        query.append(", ");
                    }
                }
                query.append(")");
                stmt.executeUpdate(query.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void populateTable(String tableName, int numRows, Map<String, String> columnData) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (int i = 0; i < numRows; i++) {
                StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
                for (String column : columnData.keySet()) {
                    query.append(column);
                    if (!column.equals(columnData.keySet().toArray()[columnData.size() - 1])) {
                        query.append(", ");
                    }
                }
                query.append(") VALUES (");
                for (String column : columnData.keySet()) {
                    String value = columnData.get(column);
                    if (value.contains("-")) {
                        // Range of values
                        String[] range = value.split("-");
                        for (int j = Integer.parseInt(range[0]); j <= Integer.parseInt(range[1]); j++) {
                            query.append("'" + j + "'");
                            if (j < Integer.parseInt(range[1])) {
                                query.append(", ");
                            }
                        }
                    } else if (value.contains(",")) {
                        // Comma-separated values
                        String[] values = value.split(",");
                        for (String val : values) {
                            query.append("'" + val + "'");
                            if (!val.equals(values[values.length - 1])) {
                                query.append(", ");
                            }
                        }
                    } else {
                        // Single value
                        query.append("'" + value + "'");
                    }
                    if (!column.equals(columnData.keySet().toArray()[columnData.size() - 1])) {
                        query.append(", ");
                    }
                }
                query.append(")");
                stmt.executeUpdate(query.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}