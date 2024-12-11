package com.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CassandraInsertTest {

	public static void main(String[] args) {
        // Create a Cassandra session
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        Session session = cluster.connect("site24x7");

        // Create a list of maps to represent the data
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", "4");
        row1.put("name", "John Doe");
        row1.put("age", 30);
        data.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", 2);
        row2.put("name", "Jane Doe");
        row2.put("age", 25);
        data.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("id", 3);
        row3.put("name", "Bob Smith");
        row3.put("age", 40);
        data.add(row3);

        // Call the insertDataIntoCassandra method
        CassandraInsertTest insert = new CassandraInsertTest();
        insert.insertDataIntoCassandra(data, session, "test");

        // Close the session and cluster
        session.close();
        cluster.close();
    }

    // The insertDataIntoCassandra method remains the same
	 public void insertDataIntoCassandra(List<Map<String, Object>> data, Session session, String tableName) {
	        // Create a prepared statement for the insert query
	        //String query = "INSERT INTO " + tableName + " (" + String.join(", ", data.get(0).keySet()) + ") VALUES (" + String.join(", ", new String[data.get(0).size()]).replaceAll("\\[|\\]", "") + ")";
		 String query = "INSERT INTO test (" + String.join(", ", data.get(0).keySet()) + ") VALUES (" + String.join(", ", Collections.nCopies(data.get(0).size(), "?")) + ")";
		 PreparedStatement ps = session.prepare(query);

	        // Create a batch statement
	        BatchStatement batch = new BatchStatement();

	        // Iterate over the data and add it to the batch statement
	        for (Map<String, Object> row : data) {
	            Object[] values = new Object[row.size()];
	            int i = 0;
	            for (Map.Entry<String, Object> entry : row.entrySet()) {
	                values[i++] = entry.getValue();
	            }
	            batch.add(ps.bind(values));
	        }

	        // Execute the batch statement
	        session.execute(batch);
	    }
}
