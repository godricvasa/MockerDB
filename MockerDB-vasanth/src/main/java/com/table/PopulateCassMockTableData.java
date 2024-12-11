package com.table;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ErrorHandler.Handler;
import com.Response.ResponseContext;
import com.filter.Dispatcher;
import org.apache.commons.io.IOUtils;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.mocker.DefaultTable;
import com.util.CassandraUtil;

import net.sf.json.JSONObject;

@WebServlet(name = "PopulateCassMockTableData", urlPatterns = {"/populateCassMockData"})
public class PopulateCassMockTableData extends HttpServlet {
    public static final Logger logger  = Logger.getLogger(PopulateCassMockTableData.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String jsonStrObj = IOUtils.toString(request.getInputStream(), "UTF-8");
        JSONObject obj = JSONObject.fromObject(jsonStrObj);
        
        JSONObject connectionObj = (JSONObject) obj.remove("connection");
        Cluster cluster = null;
        try {
            cluster = CassandraUtil.getCluster(connectionObj);
            if (cluster == null) {
                throw new IOException("Cassandra DB Connection issue");
            }

//            String keySpace = connectionObj.getString("key_space");
//            String tableName = connectionObj.getString("table_name");
//            logger.log(Level.INFO, String.format("User %s requested for adding an entry to tableName %s in keyspace %s",
//                    request.getSession().getAttribute("username"), tableName, keySpace));

            addRows(cluster, obj, connectionObj);


        } catch (Exception e) {
            Handler.responseMaker(e);
            logger.log(Level.SEVERE, "Failed to add rows to the table. Error: ", e);
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
        Dispatcher.dispatch(request, ResponseContext.getThreadLocalResponse());
    }
    
    public void addRows(Cluster cluster, JSONObject jsonReqObj, JSONObject connectionObj) throws IOException
    {
    	String tableName = connectionObj.getString("table_name");
    	String keySpace = connectionObj.getString("key_space");
        List<Map<String, String>> columnsList=CassandraTableMetaData.getMetaData(cluster,keySpace, tableName);
        
        Session session = cluster.connect(keySpace);
        
        Insert queryBuilder = QueryBuilder.insertInto(tableName);

        
        DefaultTable df = new DefaultTable(columnsList, jsonReqObj);
        df.setData();
        List<Map<String, Object>> resultList = df.getResultList();
		String query = "INSERT INTO "+tableName+ "(" + String.join(", ", resultList.get(0).keySet()) + ") VALUES (" + String.join(", ", Collections.nCopies(resultList.get(0).size(), "?")) + ")";

		PreparedStatement ps = session.prepare(query);

        // Create a batch statement
        BatchStatement batch = new BatchStatement();
//        System.out.println(" size " +resultList.size());
        int batchCount =0;
        // Iterate over the data and add it to the batch statement
        for (Map<String, Object> row : resultList) {
            Object[] values = new Object[row.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                values[i++] = entry.getValue();
            }
            batch.add(ps.bind(values));
            batchCount++;
            if(batchCount>60000) {
            	 session.execute(batch);
            	 batchCount = 0;
            	 batch = new BatchStatement();
            }
        }

        ResultSet result = session.execute(batch);

        logger.log(Level.INFO,String.format("Resultset executed %s size %s" ,result,resultList.size()));
        ResponseContext.getThreadLocalResponse().put("success text", resultList.size()+  " Entries added to the table " + tableName + " in keyspace " + keySpace + " successfully");


        // Close the prepared statement and connection
        try {
        	 session.close();
        	 cluster.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error closing session and cluster statements: " , e.getMessage());

        }
    }
    
    /*private static String getCassandraSqlInsertStatement(String tableName, List<Map<String, String>> columns)
    {
    	String sql = "INSERT INTO "+tableName +" (";
        for (int i = 0; i < columns.size(); i++)
        {
        	Map<String, String> column = columns.get(i);
            sql += column.get("column_name")+",";
        }
        sql = sql.substring(0, sql.lastIndexOf(","));
        sql += ") VALUES (";
        for (int i = 0; i < columns.size(); i++)
        {
            sql += "?" +", ";
        }
        sql = sql.substring(0, sql.lastIndexOf(","));
        sql += ")";
        
        return sql;
    }*/
    
}