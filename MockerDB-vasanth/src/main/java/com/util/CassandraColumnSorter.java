package com.util;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ClusteringOrder;
import com.datastax.driver.core.Session;

import java.util.*;

public class CassandraColumnSorter
{
    public static void main(String[] args)
    {
        // Create a Cassandra cluster and session
        Cluster cluster = Cluster.builder().addContactPoint("localhost").build();
        Session session = cluster.connect();

        // Get the keyspace and table metadata
        KeyspaceMetadata keyspaceMetadata = cluster.getMetadata().getKeyspace("site24x7");
        //TableMetadata tableMetadata = keyspaceMetadata.getTable("wm_monitor_daily_data");
        TableMetadata tableMetadata = keyspaceMetadata.getTable("wm_current_status");

        // Get all the columns
        List<ColumnMetadata> columns = tableMetadata.getColumns();
        
        List<ColumnMetadata> partitionKeyColumns = tableMetadata.getPartitionKey();
        List<ColumnMetadata> clusteringColumns = tableMetadata.getClusteringColumns();
        List<ClusteringOrder> clusteringOrder = tableMetadata.getClusteringOrder();

        // Separate the columns into partition keys, clustering keys, and normal columns
        List<Map<String, Object>> partitionKeys = new ArrayList<>();
        List<Map<String, Object>> clusteringKeys = new ArrayList<>();
        List<Map<String, Object>> normalColumns = new ArrayList<>();

        for (ColumnMetadata column : columns) {
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("column_name", column.getName());
            columnMap.put("is_partition_key", partitionKeyColumns.contains(column));
            columnMap.put("is_clustering_key", clusteringColumns.contains(column));
            columnMap.put("column_data_type", column.getType().getName().name());

            if ((Boolean) columnMap.get("is_partition_key")) {
                partitionKeys.add(columnMap);
            } else if ((Boolean) columnMap.get("is_clustering_key")) {
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
        finalList.addAll(partitionKeys);
        finalList.addAll(clusteringKeys);
        finalList.addAll(normalColumns);

        // Print the final list
        for (Map<String, Object> column : finalList) {
            System.out.println(column);
        }

        // Close the session and cluster
        session.close();
        cluster.close();
    }
}