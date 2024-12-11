package com.mocker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.Logger.BaseClass;
import com.github.javafaker.Faker;
import com.table.TableMetaData;
import com.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

public abstract class TableData
{
	public static final Logger logger  = Logger.getLogger(TableData.class.getName());

	public static long currentTime = System.currentTimeMillis() / 1000; // convert to seconds
	public long thirtyDaysAgo = currentTime - (30 * 24 * 60 * 60); // 30 days ago in seconds
	public long fifteenDaysAgo = currentTime - (15 * 24 * 60 * 60); // 30 days ago in seconds
	Faker faker = new Faker();
	
	protected JSONObject inputObj = null;
	protected List<Map<String,String>> columnMetaData = null;
	protected Map<String, Map<String, String>>  columnMetaDataMap = null;
	
	protected Map<String, Integer> incrementerMap = new HashMap<>();
	
	protected Map<String, JSONArray> jsonInputArrMap = new HashMap<>();
	protected Map<String, Long> rangeColumnLastValue = new HashMap<>();
	
	protected Map<String, Range> rangesMap = new HashMap<>();
	
	JSONNull jsonNull = JSONNull.getInstance(); 
	
	/**
	 * primaryKeysList variable
	 * In MYSQL all the primary keys and composite keys are included 
	 * in cassandra it includes partition key(rowKey) and Clustering keys(columnKeys)
	 */
	protected List<String> primaryKeysList = null; 
	
	protected List<Map<String, Object> >  resultList = new ArrayList<>();
	
	public Object getData(String columnName)
	{
		return getData(columnName, false);
	}

	public  Object getData(String columnName,boolean getDefaultOrMockData )
	{
		Map<String,String> colMetaData = columnMetaDataMap.get(columnName);
		String dataType = colMetaData.get("data_type").toString().toLowerCase();
		JSONObject valObj = inputObj.getJSONObject(columnName);
        switch (dataType)
        {
            case "tinyint":
            case "boolean":
            case "radio":
            	return getByte(columnName,valObj, colMetaData, getDefaultOrMockData);
            case "int":
            case "bigint":
            case "smallint":
            case "mediumint":
            case "number":
            case "timestamp":
            case "double":
            case "float":
            case "decimal":
            	return getNumber(columnName,valObj, colMetaData,getDefaultOrMockData);
            case "varchar":
            case "char":
            case "text":
            	return getText(dataType, columnName, valObj, colMetaData, getDefaultOrMockData);
            default:
        }
        return null;
	}
	
	private Object getByte(String columnName, JSONObject inputObj, Map<String,String> colMetaData, boolean getDefaultOrMockData)
    {
    	Object valObj = inputObj.get("value");
    	String reqType = inputObj.getString("selected_type");
    	String dataType = colMetaData.get("data_type").toLowerCase();
    	if(jsonNull.equals(valObj)) 
    	{
    		valObj = null;
    	}
    	if(valObj!=null)
    	{
	    	if("radio".equals(reqType)) 
	    	{
	    		boolean result = Boolean.valueOf(((String)(valObj)));
				return (byte)(result?1:0);
	    	}
	    	else if("tinyint".equals(dataType))
	    	{
	    		return (byte)getNumber(columnName, inputObj, colMetaData, getDefaultOrMockData);
	    	}
	    	else if("boolean".equals(dataType))
	    	{
	    		return valObj ==null?
	    				faker.bool().bool(): Boolean.valueOf(String.valueOf(valObj));
	    	}
    	}
    	else if(getDefaultOrMockData)
    	{
    		String defaultObj = colMetaData.get("default_value");
    		if(defaultObj!=null) {
    			return Byte.parseByte(defaultObj);
    		}
    		else {
    			return (byte)(faker.bool().bool()?1:0);
    		}
    	}
    	return null;
    }
    
    private Number getNumber(String columnName, JSONObject inputObj, Map<String,String> colMetaData, boolean getDefaultOrMockData)
    {
    	Object inputValueObj = inputObj.get("value");
    	Object result = null;
    	String reqType = inputObj.getString("selected_type");
    	String dataType = colMetaData.get("data_type").toLowerCase();

    	if(jsonNull.equals(inputValueObj)) 
    	{
    		inputValueObj = null;
    	}
    	if(inputValueObj==null &&  !getDefaultOrMockData){
    		return null;
    	}
    	if(inputValueObj!=null) {
	    	if(("number".equals(reqType) || "radio".equals(reqType)) && inputValueObj!=null && !jsonNull.equals(inputValueObj))
			{
				return (Number) formatDataType(dataType,inputValueObj);
			}
			else if("range".equals(reqType))
			{
				Range range = rangesMap.get(columnName);
				
				if(range==null) {
					int incValue = inputObj.getInt("incrementer");
					range = getRange(String.valueOf(inputValueObj), incValue);
					rangesMap.put(columnName, range);
				}
				result = formatDataType(dataType, range.getNextValue());
			}
			else if("textarea".equals(reqType))// && "true".equals(colMetaData.get("primary_key") ))
			{
				result = getInputArrayData(dataType, columnName, inputValueObj);
			}
			else if("text".equals(reqType)) 
			{
				result = formatDataType(dataType, inputValueObj);
			}
    	}
    	else if(getDefaultOrMockData) 
    	{
    		if(!"YES".equals(colMetaData.get("nullable"))){
    			result = faker.number().numberBetween(0, 1);
    		}
    	}
		
    	return (Number)result;
    }
    
    private Object formatDataType(String realDataType, Object value)
    {
    	switch (realDataType.toLowerCase())
        {
        	case "boolean":
        		return Boolean.parseBoolean(String.valueOf(value));
            case "tinyint":
            case "int":
            case "smallint":
            case "mediumint":
            case "number":
            case "timestamp":
            	return StringUtils.parseInt(value, 1);
            case "bigint":
            	 return StringUtils.parseLong(value, 1);
            case "double":
            case "float":
            case "decimal":
                return StringUtils.parseDouble(value, 1);
            case "varchar":
            case "char":
            case "text":
            default:
            	return String.valueOf(value);
        }
    	
    }
    
    private Object getText(String dataType, String columnName,  JSONObject inputObj, Map<String,String> colMetaData, boolean getDefaultOrMockData)
    {
    	Object valueObj = inputObj.get("value");
    	String reqType = inputObj.getString("selected_type");
    	if(jsonNull.equals(valueObj)) 
    	{
    		valueObj = null;
    	}
    	if((valueObj==null || StringUtils.isEmpty((String.valueOf(valueObj))) &&  !getDefaultOrMockData)) {
    		return null;
    	}
    	Object result = null;
    	String value = String.valueOf(valueObj);
    	if(!StringUtils.isEmpty(value))
    	{
    		if("text".equals(reqType)) 
    		{
    			result = value;
    		}
    		else if("textarea".equals(reqType))
    		{
    			if( value.trim().startsWith("[") && value.trim().endsWith("]"))
    			{
    				//List<Object> resultantArray = StringUtils.convertJSONStingToArray(value);
    				result = formatDataType(dataType, getInputArrayData(dataType, columnName, valueObj));
    			}
    			else
    			{
    				result = value;
    			}
    		}
			
    	}
    	else {
    		result = faker.name().firstName();
    	}
    	return result;
    } 
    
    private Object getInputArrayData(String dataType, String columnName, Object inputObj)
    {
		JSONArray inputArr = jsonInputArrMap.get(columnName) ;
		if(inputArr==null)
		{
			inputArr = JSONArray.fromObject(inputObj);
			jsonInputArrMap = new HashMap<>();
			jsonInputArrMap.put(columnName, inputArr);
		}
		Object result = formatDataType(dataType, inputArr.get(faker.number().numberBetween(0, inputArr.size())) );
    	return result;
    }
	
	public Map<String, Map<String, String>> converListToMap(List<Map<String,String>> list)
	{
		Map<String, Map<String, String>> map = new HashMap<>();
		if(list==null) 
		{
			return  map;
		}
        for (Map<String, String> obj : list) {
            map.put((String) obj.get("column_name"), obj);
        }
        return map;
	}

	public Map<String, Map<String, String>> getColumnMetaDataMap() {
		return columnMetaDataMap;
	}
	
	public Range getRange(String rangeValue, int incrementer)
	{
        String[] parts = rangeValue.split("-");
        long minValue = StringUtils.parseLong(parts[0],0);
        long maxValue = StringUtils.parseLong(parts[1],1);
        Range range = new Range(minValue, maxValue, minValue, incrementer);
        return range;
    }
	
	protected List<Object> getPartitionKeyData(String columnName, JSONObject inputJSON) 
    {
    	List<Object> resultantArray = new ArrayList<>();
    	Map<String,String> colMetaData = columnMetaDataMap.get(columnName);
		String dataType = colMetaData.get("data_type").toString().toLowerCase();
		
    	String selectedValue = inputJSON.getJSONObject(columnName).getString("selected_type");
    	String value = String.valueOf(inputJSON.getJSONObject(columnName).get("value"));
		if("range".equals(selectedValue))
		{
			int incValue = inputJSON.getJSONObject(columnName).getInt("incrementer");
			Range range = getRange(value, incValue);
			resultantArray = range.getListOfAllRangeValues();
		}
		else if("text".equals(selectedValue) || "textarea".equals(selectedValue))
		{
			if(!StringUtils.isEmpty(value) && value.trim().startsWith("[") && value.trim().endsWith("]"))
			{
				resultantArray = StringUtils.convertJSONStingToArray(value);
			}
			else {
				resultantArray.add(value);
			}
		}
		if(resultantArray.isEmpty())
		{
			resultantArray.add(value);
		}
		resultantArray = convertDataType(resultantArray, dataType);
    	return resultantArray;
    }
	
	protected void populateData(Map<String, List<Object>> values, int index, Object[] params)
	{
        if (index == values.size())
        {
        	Map<String, Object> resultMap = new HashMap<>();
        	for (int j = 0; j < columnMetaData.size(); j++)
   	     	{
   	    	 Map<String,String> colMData = columnMetaData.get(j);
   	    	 String columnName = (String) colMData.get("column_name");
   	    	 if(!primaryKeysList.contains(columnName)) {
   	    		 resultMap.put(columnName, getData(columnName, true));
   	    	 }
   	     	}
        	
        	for(int k=0; k<params.length; k++) {
        		resultMap.put(primaryKeysList.get(k), params[k]);
        	}
        	
        	resultList.add(resultMap);
//			String space10 = new String(new char[10]).replace('\0', ' ');

			logger.log(Level.INFO,resultMap.toString());
//            System.out.println(resultMap);
        }
        else
        {
            String columnName = (String) values.keySet().toArray()[index];
            List<Object> listData = values.get(columnName);
            for (int i = 0; i < listData.size(); i++) {
                params[index] = listData.get(i);
                populateData(values, index + 1, params);
            }
        }
	}
	
	protected Map<String, List<Object>> generateAllPartitionKeyData()
	{
		Map<String, List<Object>> resultMap = new LinkedHashMap<>();
		setRowColumnKeyList();
		for(String pKey : primaryKeysList) 
		{
			List<Object> data = getPartitionKeyData(pKey, inputObj);
			resultMap.put(pKey, data);
		}
		return resultMap;
	}
	
	private List<String> setRowColumnKeyList()
	{
		primaryKeysList = new ArrayList<>();

		for(Map<String, String> map : columnMetaData)
		{
			if("true".equals(map.get("primary_key")) ||
					"true".equals(map.get("is_partition_key")) || 
						"true".equals(map.get("is_clustering_key")))
			{
				primaryKeysList.add(map.get("column_name"));
			}
		}
		return primaryKeysList;
	}

    protected List<Object> convertDataType(List<Object> jsonArray, String dataType)
    {
    	List<Object> result = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++)
        {
            Object value = jsonArray.get(i);
            result.add(formatDataType(dataType, value));
        }
        return result;
    }
    private Object getParser(String dataType, Object value) {
        switch (dataType.toLowerCase()) {
            case "bigint":
                return StringUtils.parseLong(value, 1);
            case "int":
                return StringUtils.parseInt(value, 1);
            case "string":
            case "text":
                return String.valueOf(value);
            default:
                throw new IllegalArgumentException("Invalid data type");
        }
    }
}

class Range {
    long minValue;
    long maxValue;
    long currentValue;
    int incrementer;

    public Range(long minValue, long maxValue, long currentValue, int incrementer) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = currentValue;
        this.incrementer = incrementer;
    }
    public long getNextValue()
	{
	    if (currentValue > maxValue) {
	        currentValue = minValue;
	    }
	    return (currentValue+incrementer);
    }
    
    public List<Object> getListOfAllRangeValues() 
    {
    	List<Object> resultList = new ArrayList<>();
    	for(long i=currentValue; i<maxValue; i=(i+incrementer)) {
    		resultList.add(i);
    	}
    	return resultList;
    }
}

