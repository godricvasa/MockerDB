package com.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ClusteringOrder;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.TableMetadata;

import org.apache.commons.io.IOUtils;

import com.filter.Dispatcher;
import com.util.CacheUtil;
import com.util.CassandraUtil;

import net.sf.json.JSONObject;



@WebServlet(name = "CassandraTableMetaData", urlPatterns = {"/fetchCassandraMetaData"})
public class CassandraTableMetaData extends HttpServlet
{
	public static final Logger logger  = Logger.getLogger(CassandraTableMetaData.class.getName());

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String jsonStrObj = IOUtils.toString(request.getInputStream(), "UTF-8");
        JSONObject obj = JSONObject.fromObject(jsonStrObj);

        String keySpace = obj.getString("key_space");
        String tableName = obj.getString("table_name");

//		logger.log(Level.INFO,String.format("User %s requested for tableName %s in keyspace %s",request.getSession().getAttribute("username"),tableName,keySpace));

		List<Map<String, String>> columnMetadataMap = null;

		Cluster cluster = null;

		try{
			cluster = CassandraUtil.getCluster(obj);
			columnMetadataMap = getMetaData(cluster, keySpace, tableName);
		}
		catch (IllegalArgumentException e){

			columnMetadataMap = new ArrayList<>();
			Handler.responseMaker(e);
			columnMetadataMap.add(ResponseContext.getThreadLocalResponse());


		}

		Dispatcher.dispatch(request, columnMetadataMap);



    }

	public static List<Map<String, String>> getMetaData(Cluster cluster, String keySpace, String tableName)
	{

		List<Map<String, String>> tableMetadataList= null;
		try
		{
			Object cacheObj = CacheUtil.getCache(tableName);
	    	if(cacheObj!=null)
	    	{
	    		return (List<Map<String, String>>)cacheObj;
	    	}
		    Metadata metadata = cluster.getMetadata();
		    tableMetadataList = new ArrayList<>();

		    TableMetadata tableMetadata = metadata.getKeyspace(keySpace).getTable(tableName);

		    // Get all the columns
	        List<ColumnMetadata> columns = tableMetadata.getColumns();

	        List<ColumnMetadata> partitionKeyColumns = tableMetadata.getPartitionKey();
	        List<ColumnMetadata> clusteringColumns = tableMetadata.getClusteringColumns();
	        List<ClusteringOrder> clusteringOrder = tableMetadata.getClusteringOrder();

	        // Separate the columns into partition keys, clustering keys, and normal columns
	        List<Map<String, String>> partitionKeys = new ArrayList<>();
	        List<Map<String, String>> clusteringKeys = new ArrayList<>();
	        List<Map<String, String>> normalColumns = new ArrayList<>();

	        for (ColumnMetadata column : columns)
	        {
	        	boolean isPartitionKey = partitionKeyColumns.contains(column);
	        	boolean isClusteringKey = clusteringColumns.contains(column);

	            Map<String, String> columnMap = new HashMap<>();
	            columnMap.put("column_name", column.getName());
	            columnMap.put("is_partition_key", String.valueOf(isPartitionKey));
	            columnMap.put("is_clustering_key", String.valueOf(isClusteringKey));
	            columnMap.put("data_type", column.getType().getName().name());

	            if(isPartitionKey){
	                partitionKeys.add(columnMap);
	            } else if (isClusteringKey) {
	                clusteringKeys.add(columnMap);
	            } else {
	                normalColumns.add(columnMap);
	            }
	        }

	        // Sort the partition keys and clustering keys
	        Collections.sort(partitionKeys, Comparator.comparing(column ->  {
	            int index = partitionKeyColumns.indexOf(column.get("column_name"));
	            return index;
	        }));
	        Collections.sort(clusteringKeys, Comparator.comparing(column -> {
	        	for (int i = 0; i < clusteringOrder.size(); i++) {
	                ClusteringOrder order = clusteringOrder.get(i);
	                if (order.name().equals(column.get("column_name"))) {
	                    return i;
	                }
	            }
	            return -1;
	        }));
	        Collections.sort(normalColumns, Comparator.comparing(column -> (String) column.get("column_name")));
	        // Combine the lists
	        List<Map<String, Object>> finalList = new ArrayList<>();
	        tableMetadataList.addAll(partitionKeys);
	        tableMetadataList.addAll(clusteringKeys);
	        tableMetadataList.addAll(normalColumns);

	        CacheUtil.setCache(tableName, tableMetadataList);

	        // Print the final list
	        for (Map<String, Object> column : finalList) {
	            System.out.println(column);
	        }
			logger.log(Level.INFO,String.format("Meta data for the table %s exists in keyspace %s",tableName,keySpace));
		}
		catch(Exception e) {
			tableMetadataList = new ArrayList<>();



				logger.log(Level.SEVERE, String.format("Error fetching metadata for table %s from keyspace %s.", tableName, keySpace), e);
				String message = e.getMessage();
                Handler.responseMaker(e);

			tableMetadataList.add(ResponseContext.getThreadLocalResponse());
		}
		return tableMetadataList;
	}

}
