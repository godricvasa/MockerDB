package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

public class DynamicMySQLDataGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the MySQL table name: ");
        String tableName = scanner.nextLine();
        System.out.print("Enter the number of rows to generate: ");
        int numRows = scanner.nextInt();
        scanner.close();

        // Database connection settings
        String dbUrl = "jdbc:mysql://localhost:3306/testing";
        String dbUser = "root";
        String dbPassword = "";

        // Create a connection to the database
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // Get the table's columns and data types
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("DESCRIBE " + tableName);
            int numColumns = 0;
            String[] columnNames = new String[100];
            String[] columnTypes = new String[100];
            while (rs.next()) {
                columnNames[numColumns] = rs.getString(1);
                String dataType = rs.getString(2);
                columnTypes[numColumns] = rs.getString(2);
                if(columnTypes[numColumns].contains("(")) {
                	System.out.println(dataType);
                	columnTypes[numColumns] = dataType.substring(0, dataType.indexOf('('));
                }
                else {
                	columnTypes[numColumns] = dataType;
                }
                numColumns++;
            }

            // Generate random data for each column
            Random random = new Random();
            String[] columnValues = new String[numColumns];
            for (int i = 0; i < numColumns; i++) {
                switch (columnTypes[i])
                {
                    case "tinyint":
                    	columnValues[i] = String.valueOf(random.nextInt(1));
                        break;
                    case "int":
                    case "smallint":
                    case "mediumint":
                    case "bigint":
                        columnValues[i] = String.valueOf(random.nextInt(100));
                        break;
                    case "varchar":
                    case "char":
                        columnValues[i] = generateRandomString(10);
                        break;
                    case "datetime":
                    case "timestamp":
                        columnValues[i] = String.valueOf(System.currentTimeMillis());
                        break;
                    case "double":
                    case "float":
                    case "decimal":
                        columnValues[i] = String.valueOf(random.nextDouble());
                        break;
                    case "boolean":
                        columnValues[i] = String.valueOf(random.nextBoolean());
                        break;
                    default:
                        columnValues[i] = "NULL";
                }
            }

            // Create a prepared statement to insert data into the table
            String query = "INSERT INTO " + tableName + " (";
            for (int i = 0; i < numColumns; i++) {
                query += columnNames[i];
                if (i < numColumns - 1) {
                    query += ", ";
                }
            }
            query += ") VALUES (";
            for (int i = 0; i < numColumns; i++) {	
                query += "?";
                if (i < numColumns - 1) {
                    query += ", ";
                }
            }
            query += ")";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Insert data into the table
                for (int i = 0; i < numRows; i++) {
                    for (int j = 0; j < numColumns; j++) {
                        pstmt.setString(j + 1, columnValues[j]);
                    }
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error generating data for " + tableName + ": " + e.getMessage());
            e.printStackTrace();        }
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}