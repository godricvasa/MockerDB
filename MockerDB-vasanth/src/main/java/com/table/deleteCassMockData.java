package com.table;

import com.ErrorHandler.Handler;
import com.Response.ResponseContext;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.filter.Dispatcher;
import com.mocker.DefaultTable;
import com.util.CassandraUtil;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import com.datastax.driver.core.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "deleteCassMockData", urlPatterns = {"/deleteCassMockData"})

public class deleteCassMockData extends HttpServlet {
    private static final Logger logger = Logger.getLogger(deleteCassMockData.class.getName());
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          String jsonData = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
        JSONObject requestObject = JSONObject.fromObject(jsonData);
        JSONObject connectionObject = requestObject.getJSONObject("connection");

        JSONObject connectionObj = (JSONObject) requestObject.remove("connection");
        Cluster cluster = null;
        try {
            cluster = CassandraUtil.getCluster(connectionObj);
            if (cluster == null) {
                throw new IOException("Cassandra DB Connection issue");
            }

//            String keySpace = connectionObj.getString("key_space");
//            String tableName = connectionObj.getString("table_name");
//            logger.log(Level.INFO, String.format("User %s requested for adding an entry to tableName %s in keyspace %s",
//                    req.getSession().getAttribute("username"), tableName, keySpace));

            deleteRows(cluster, requestObject, connectionObj);


        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to add rows to the table. Error: ", e);
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
        Dispatcher.dispatch(req, ResponseContext.getThreadLocalResponse());

    }

    private void deleteRows(Cluster cluster, JSONObject requestObject, JSONObject connectionObj) {
        String tableName = connectionObj.getString("table_name");
        String keySpace = connectionObj.getString("key_space");
        List<Map<String, String>> columnsList= new ArrayList<>(CassandraTableMetaData.getMetaData(cluster,keySpace, tableName));


        Session session = cluster.connect(keySpace);

        List<String> primaryKeyColumns = new ArrayList<>();

        for(Map<String,String> columnMetaData:columnsList) {
            String columnName = columnMetaData.get("column_name");
            if ("true".equals(columnMetaData.get("is_partition_key")) ||
                    "true".equals(columnMetaData.get("is_clustering_key"))) {

                JSONObject valueObj = (JSONObject) requestObject.get(columnName);

                if (!valueObj.getString("value").isEmpty()) {
                    primaryKeyColumns.add(columnName);
                }
                else{
                    columnMetaData.put("empty","true");
                    requestObject.remove(columnName);
                }
            }
            else{

                requestObject.remove(columnName);
            }
        }
        // collection api removal of non primary columns
        columnsList.removeIf((a) -> a.get("is_partition_key").equals("false")&&a.get("is_clustering_key").equals("false"));
        columnsList.removeIf((a) -> a.containsKey("empty") && a.get("empty").equals("true"));

        String query = "Delete from "+tableName+" WHERE "+ String.join("=? AND ",primaryKeyColumns);
        query+="=?";

        try {
            DefaultTable df = new DefaultTable(columnsList, requestObject);
            df.setData();
            List<Map<String, Object>> resultList = df.getResultList();
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
            ResponseContext.getThreadLocalResponse().put("success text", resultList.size()+  " matched record deleted from the table " + tableName + " in keyspace " + keySpace + " successfully");
//


    } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                session.close();
                cluster.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE,"Error closing session and cluster statements: " , e.getMessage());

            }
        }
    }
    }
