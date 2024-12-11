package com.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import org.slf4j.ILoggerFactory;

public class StringUtils
{
	
	public static String parseHttpRequestForJsonObj(HttpServletRequest req) 
	{
		String result = "";
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
		    result = reader.readLine();
		    while ((result = reader.readLine())!= null) {
		      result += result;
		    }
		    reader.close();
	
		    // Process the JSON string
		    System.out.println("Received JSON: " + result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(reader!=null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static boolean isEmpty(String str)
	{
		if (str == null || str.trim().equalsIgnoreCase("null") || str.trim().equalsIgnoreCase("undefined") || str.trim().equalsIgnoreCase(""))
		{
			return true;
		}
		return false;
	}
	
	public static int genRandomInteger(int maxvalue)
    {
    	Random random = new Random();
    	int num = random.nextInt(maxvalue);
        return num;        
    }
    
    public static String genRanNum()
    {
    	Random random = new Random();
        String ran = "123456789"; // total num 9
        int count = 0;
        int length=5;
        String ranWord = "";
        while( count < length )
        {            
            int i = random.nextInt();
            Integer val = new Integer(i);
            val = new Integer( Math.abs(val.intValue()));
            int  dummy = ((val.intValue())%9);
            ranWord += ran.charAt(dummy);
            count++;            
        }        
        return ranWord;        
    }
    
    public static int parseInt(String value,int defaultvalue)
	{
		try
		{
			value = value.trim();
			int val = Integer.parseInt(value);
			return val;
		}
		catch(Exception e)
		{
			return defaultvalue;
		}
	}
    
    public static int parseInt(Object value,int defaultvalue)
	{
		return parseInt(String.valueOf(value), defaultvalue);
	}
    
    public static long parseLong(Object value,long defaultvalue)
	{
    	return parseLong(String.valueOf(value), defaultvalue);
	}
    
    public static long parseLong(String value,long defaultvalue)
	{
		try
		{
			value = value.trim();
			long val = Long.parseLong(value);
			return val;
		}
		catch(Exception e)
		{
			try
			{
				return ((Double)parseDouble(value, ((Long)defaultvalue).doubleValue())).longValue();
			}
			catch(Exception e1)
			{
				return defaultvalue;
			}	
		}
	}
    
    public static Double parseDouble(Object value,double defaultvalue)
	{
    	return parseDouble(String.valueOf(value), defaultvalue);
	}
    
    
    public static double parseDouble(String value,double defaultvalue)
	{
		try
		{
			value = value.trim();
			double val = Double.parseDouble(value);
			return val;
		}
		catch(Exception e)
		{
			return defaultvalue;
		}
	}
    
    public static List<Object> convertJSONStingToArray(String array) {
    	
    	if(array==null || !array.trim().startsWith("[") || !array.trim().endsWith("]")) {
    		return null;
    	}

        // Convert unquoted JSON array to quoted array
        JSONArray jsonArray = null;
        try {
        	// Convert quoted JSON array to JSONArray
        	jsonArray = JSONArray.fromObject(array);
        }
        catch(JSONException e) {
        	if(e.getMessage().contains("Expected a")){
        		array = "[" + array.substring(1, array.length() - 1).replaceAll(",", "\",\"") + "]";
        		jsonArray = JSONArray.fromObject(array);
        	}
        	else {
        		throw new IllegalArgumentException("invalid array passed");
        	}
        }

        // Convert JSONArray to List
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getString(i));
        }
		return list;
    }
    
    
}