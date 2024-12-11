package com.ErrorHandler;

import com.Response.ResponseContext;
import com.datastax.driver.core.exceptions.NoHostAvailableException;

import java.sql.BatchUpdateException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Handler {
    public static final Logger logger  = Logger.getLogger(Handler.class.getName());

    public static Map<String,String> errorMap = new HashMap<>(){
        {
            put("table not found","The table entered is not found in the database");
            put("Cluster connection failed","Failed to connect to the cluster, check the cluster address and the keyspace name");
            put("KeyspaceNotFound","Keyspace does not exist in the cluster");
            put("TableNotFound","Table does not exist");
            put("SqlConnectionFailed","There is trouble connecting to the database, check the database name and password");
            put("NoHostAvailable","Cluster connection failed due to no host availability, Check the cluster name or try later");
            put("WrongHostname","Failed to connect to the cluster, Check the host name");
            put("CheckTableName","The table name contains invalid characters which is causing sql syntax error");
            put("InvalidPassword","Password is invalid, enter the correct password");
        }
    };


    public static String getErrorString(String key){
        if (errorMap.get(key)==null) return key;
        return errorMap.get(key);
    }

    public static void responseMaker(Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        String errorMessage = determineErrorMessage(e);
        Map<String,String> threadLocalResponse = ResponseContext.getThreadLocalResponse();
        threadLocalResponse.put("error text",getErrorString(errorMessage));


    }

    private static String determineErrorMessage(Exception e) {
        if (e instanceof NoHostAvailableException) {
            return "NoHostAvailable";
        } else if (e instanceof IllegalArgumentException) {
            return "WrongHostname";
        } else if (e instanceof BatchUpdateException && e.getMessage().contains("Duplicate entry")) {
            return e.getMessage();
        } else if (e instanceof NullPointerException) {
            return handleNullPointerException((NullPointerException) e);
        } else if (e instanceof SQLSyntaxErrorException) {
                return handleSQLSyntaxErrorException(e);
        } else {
            return e.getMessage();
        }
    }

    private static String handleSQLSyntaxErrorException(Exception e) {
        if (e.getMessage().contains("You have an error in your SQL syntax")) {
            return "CheckTableName";
        } else if (e.getMessage().contains("doesn't exist")) {
            return "TableNotFound";
        }
        return e.getMessage();
    }

    private static String handleNullPointerException(NullPointerException e) {
        if (e.getMessage().contains("getKeyspace(String)\" is null")) {
            return "KeyspaceNotFound";
        } else if (e.getMessage().contains("tableMetadata\" is null")) {
            return "TableNotFound";
        } else if (e.getMessage().contains("\"conn\" is null")) {
            return "SqlConnectionFailed";
        }
        return e.getMessage();
    }


}