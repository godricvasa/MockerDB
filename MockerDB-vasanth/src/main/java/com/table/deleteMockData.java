package com.table;

import com.Response.ResponseContext;
import com.filter.Dispatcher;

import com.mocker.DefaultTable;
import com.util.MySqlUtil;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//This deletion logic deletes using the data collected from the column meta data , requested object passed to default table class to get result set to process accordingly
@WebServlet(name = "deleteMockData", urlPatterns = {"/deleteMockData"})

public class deleteMockData extends HttpServlet {
private static final Logger logger = Logger.getLogger(deleteMockData.class.getName());
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonRequest = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
        JSONObject jsonRequestObj = JSONObject.fromObject(jsonRequest);
        JSONObject connectionObject = (JSONObject) jsonRequestObj.remove("connection") ;
        Connection conn = MySqlUtil.getConnection(connectionObject);
        logger.log(Level.INFO,"User "+req.getSession().getAttribute("username")+" trying to delete records");
//        System.out.println(jsonRequestObj);
        String PrimaryKey = "";

        try{
            deleteRows(connectionObject,conn,jsonRequestObj);

        }
        catch (Exception e){

            logger.severe(e.getMessage());
            e.printStackTrace();
        }



        Dispatcher.dispatch(req,ResponseContext.getThreadLocalResponse());

    }

    private void deleteRows(JSONObject connectionObject, Connection conn, JSONObject jsonRequestObj) throws IOException, SQLException {

        String tableName = connectionObject.getString("database_tablename");
//
//        String primaryKeyQuery = "Select primary_key_labels from primaryKeys where tableName = '"+tableName+"'";
//        Statement statement = conn.createStatement();
//        ResultSet resultSet = statement.executeQuery(primaryKeyQuery);
//

List<Map<String,String>> columnsList = new ArrayList<>(TableMetaData.getTableMetaData(connectionObject,tableName));
                    List<String> primaryKeyColumns = new ArrayList<>();

        for(Map<String,String> columnMetaData:columnsList) {
            String columnName = columnMetaData.get("column_name");
            if ("true".equals(columnMetaData.get("primary_key"))) {

                JSONObject valueObj = (JSONObject) jsonRequestObj.get(columnName);

                if (!valueObj.getString("value").isEmpty()) {
                    primaryKeyColumns.add(columnName);
                }
                else{
                    columnMetaData.put("empty","true");
                    jsonRequestObj.remove(columnName);
                }
            }
            else{
//                columnsList.remove(columnMetaData);
                jsonRequestObj.remove(columnName);
            }
        }
        // collection api removal of non primary columns
        columnsList.removeIf((a) -> a.get("primary_key").equals("false"));
        columnsList.removeIf((a) -> a.containsKey("empty") && a.get("empty").equals("true"));

        String query = "Delete from "+tableName+" WHERE "+ String.join("=? AND ",primaryKeyColumns);
        query+="=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(query);
            System.out.println(jsonRequestObj);
            System.out.println(columnsList);
            DefaultTable df = new DefaultTable(columnsList,jsonRequestObj);
            df.setData();
            List<Map<String,Object>> resultList = df.getResultList();
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
            long successfulDeletionCount;
            if (exeResult.length>1){
                successfulDeletionCount = Arrays.stream(exeResult)
                        .filter(num -> num==1)
                        .count();
            }
            else{
                successfulDeletionCount = exeResult.length;
            }


                logger.info(Arrays.toString(exeResult));
                ResponseContext.getThreadLocalResponse().put("success text","Deleted "+ successfulDeletionCount+" record successfully");



        }
        catch (Exception e){
            logger.severe(e.getMessage());
            e.printStackTrace();
//            System.out.println(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
