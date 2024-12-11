package com.table;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
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

import com.mocker.DefaultTable;
import com.util.MySqlUtil;
import com.util.StringUtils;

import net.sf.json.JSONObject;

@WebServlet(name = "PopulateMockTableData", urlPatterns = {"/populateMockData"})
public class PopulateMockTableData extends HttpServlet {
	public static final Logger logger  = Logger.getLogger(PopulateMockTableData.class.getName());

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String jsonStrObj = IOUtils.toString(request.getInputStream(), "UTF-8");
        JSONObject obj = JSONObject.fromObject(jsonStrObj);
//		String username = (String) request.getSession().getAttribute("username");


        JSONObject connectionObj = (JSONObject) obj.remove("connection");
//		logger.log(Level.INFO, "User {0} is trying to add a new entry to the table {1}", new Object[]{username, connectionObj.getString("database_tablename")});

		Map<String,String> resMap = null;
        try(Connection conn = MySqlUtil.getConnection(connectionObj))
        {
			logger.severe(String.format("connected to the database %s",connectionObj.getString("database_name")));

			addRows(conn, obj, connectionObj);

//			 ResponseContext.getThreadLocalResponse().put("success text","Entries added successfully in database table "+connectionObj.getString("database_tablename"));

			Dispatcher.dispatch(request,ResponseContext.getThreadLocalResponse());


        }
        catch(Exception e)
        {
			System.out.println(e.getMessage());
            logger.log(Level.SEVERE,"Error adding entry to the table "+connectionObj.getString("database_tablename"),e);
          Handler.responseMaker(e);
			Dispatcher.dispatch(request, ResponseContext.getThreadLocalResponse());

        }
    }
    
    public void addRows(Connection conn, JSONObject jsonReqObj, JSONObject connectionObj) throws IOException, SQLException
    {
    	String tableName = connectionObj.getString("database_tablename");
        List<Map<String, String>> columnsList=TableMetaData.getTableMetaData(connectionObj, tableName);
        // Create a prepared statement
        String sql = getPreparedStatementSQLString(tableName, columnsList);
        String primKeyRow = "";
        TableMetaData.getTableMetaData(jsonReqObj, tableName);
        
        PreparedStatement pstmt = null;
        try
        {
            pstmt = conn.prepareStatement(sql);
			logger.log(Level.INFO,"Prepared statement created successfully");


		}
        catch (SQLException e)
        {
			logger.log(Level.SEVERE,"Failed to create prepared statement for the user request",e.getMessage());


            return;
        }
        
      //  int minRowSize = getRowSize(columnsList, jsonReqObj);
        
        //Object[][] obj = new Object[minRowSize][columnsList.size()];
        
        DefaultTable df = new DefaultTable(columnsList, jsonReqObj);
        df.setData();
        List<Map<String, Object>> resultList = df.getResultList();
		// this iterates multiple times if its a range cuz the list have map with the values as object(we can also say nested map)
        for(int i=0; i<resultList.size(); i++)
        {
			//the diff why we we have this loop is cuz of range will have multiple result list
			//this is the map inside it with key as the column name , here we are getting the map with the column name and its value
        	Map<String, Object> resultMap = resultList.get(i);
			//iterating the columnList which is the meta data
	        for (int j = 0; j < columnsList.size(); j++)
	        {
				//getting the map with the key as the columname and the val
	            Map<String,String> column = columnsList.get(j);
	            
	            String dataType = column.get("data_type");
	            Map<String,String> colMetaData = columnsList.get(j);
				if (primKeyRow.isEmpty() && column.get("primary_key").equals("true")){
					primKeyRow = column.get("column_name");
				}
	            String columnName = colMetaData.get("column_name");
	            Object columnValue = resultMap.get(columnName);

	            switch (dataType)
	            {
	                case "tinyint":
	                case "boolean":
	                case "radio":
	                	pstmt.setByte(j+1, (Byte)columnValue);
	                    break;
	                case "int":
	                case "smallint":
	                case "mediumint":
	                case "bigint":
	                case "number":
	                case "timestamp":
	                case "double":
	                case "float":
	                case "decimal":
	                    pstmt.setObject(j+1, columnValue);
	                    break;
	                case "varchar":
	                case "char":
	                case "text":
	                	pstmt.setString(j+1, String.valueOf(columnValue));
	                    break;
	                default:
	                    pstmt.setString(j + 1, "default"); // Replace with actual value
	            }
	        }
	        pstmt.addBatch();

        }



			    int[] exeResult = pstmt.executeBatch();
//	         for(int i=0;i<exeResult.length;i++){
//				 if (exeResult[i]== Statement.EXECUTE_FAILED){
//					 System.out.println(i);
//				 }
//			 }
             long successfulEntryCount = Arrays.stream(exeResult)
							 .filter(num -> num==1)
									 .count();
			 if (successfulEntryCount<exeResult.length){
				 for(int i=0;i<exeResult.length;i++){
					 if (exeResult[i]==0) {
						 Map<String, Object> resultMap = resultList.get(i);

						 logger.log(Level.SEVERE, "duplicate "+primKeyRow+" entry for "+resultMap.get(primKeyRow));


						 ResponseContext.getThreadLocalResponse().put("error_text"+i, "Duplicate " + primKeyRow + " entry for " + resultMap.get(primKeyRow));


					 }
				 }

			 }

				 logger.log(Level.INFO, "executeBatch result: {0}, array: {1}", new Object[]{successfulEntryCount, Arrays.toString(exeResult)});//        System.out.println("executeBatch result "+exeResult.length+" array "+Arrays.toString(exeResult));

		         ResponseContext.getThreadLocalResponse().put("success text", successfulEntryCount + " entries inserted successfully");






        // Close the prepared statement and connection
        try {
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
			logger.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());//            System.out.println("Error closing prepared statement and connection: " + e.getMessage());
        }
    }
    
    private static String getPreparedStatementSQLString(String tableName, List<Map<String, String>> columns)
    {
    	String sql = "INSERT IGNORE INTO "+tableName +" (";
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
    }
    
    private static int getRowSize(List<Map<String, String>> columns, JSONObject inputJSON) 
    {
    	List<String> primaryKeys = new ArrayList<>();
    	
    	for(int i=0; i<columns.size(); i++) 
    	{
    		Map<String, String> map = columns.get(i);
    		String pk = map.get("primary_key");
    		if(Boolean.valueOf(pk)) 
    		{
    			primaryKeys.add(map.get("name"));
    		}
    	}
    	
    	int min = MySqlUtil.MAX_ROWS-1;
    	
    	for(int i=0; i<primaryKeys.size(); i++) 
    	{
    		String key = primaryKeys.get(i);
    		if(inputJSON.containsKey(key) && "range".equals(inputJSON.getJSONObject(key).getString("selected_type")))
			{
    			String str = inputJSON.getJSONObject(key).getString("value");
    			if(!str.contains("-")) {
    				continue;
    			}
    			String strArr[] = inputJSON.getJSONObject(key).getString("value").split("-");
    			
    			int incValue = StringUtils.parseInt(inputJSON.getJSONObject(key).get("incrementer"), 1);
    			
    			Long minVal = StringUtils.parseLong(strArr[0], 1l);
    			Long maxVal = StringUtils.parseLong(strArr[1], 1l);
    			if(minVal>maxVal) {
    				throw new IllegalArgumentException("range is not set properly");
    			}
    			int resVal = (maxVal.intValue()-minVal.intValue()) / incValue;
    			if(resVal < min )
    			{
    				min = resVal;
    			}
			}
    	}
    	return min;
    }
    private Byte getByte(int itrNum, JSONObject inputObj, Map<String,String> colMetaData)
    {
    	Object valObj = inputObj.get("value");
    	
    	String reqType = inputObj.getString("selected_type");
    	String orgType = colMetaData.get("data_type");
    	if( ("tinyint".equals(orgType) || "boolean".equals(orgType)) && "radio".equals(reqType)) 
    	{
    		if(valObj!=null) {
    			return (byte)valObj;
    		}
    	}
    	return 0;
    }
    
    private Number getNumber(int itrNum, JSONObject inputObj, Map<String,String> colMetaData)
    {
    	Object valObj = inputObj.get("value");
    	String reqType = inputObj.getString("selected_type");
    	String orgType = colMetaData.get("data_type").toLowerCase();
    	
    	if((orgType.matches("int|smallint|mediumint|bigint|number|timestamp|double|float|decimal")))
    	{
    		if("number".equals(reqType) || "radio".equals(reqType))
    		{
    			return (Number)valObj;
    		}
    		if("range".equals(reqType))
    		{
    			
    		}
    		if("textarea".equals(reqType) && "true".equals(colMetaData.get("primary_key") ))
    		{
    			
    		}
    		else if("textarea".equals(reqType))
    		{
    			
    		}
    	}
    	return 0l;
    }
    
    private String getText(int itrNum, JSONObject inputObj, Map<String,String> colMetaData)
    {
    	Object valObj = inputObj.get("value");
    	String reqType = inputObj.getString("selected_type");
    	String orgType = colMetaData.get("data_type").toLowerCase();
    	if((orgType.matches("char|varchar|text")))
    	{
    		if("text".equals(reqType)) 
    		{
    			
    		}
    		if("textarea".equals(reqType))
    		{
    			
    		}
    	}
    	return "defatul - text";
    }
}