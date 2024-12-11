package com.mocker;


import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;


public class CassandraDefaultTable extends TableData
{
	public CassandraDefaultTable(List<Map<String,String>> columnMetaData, JSONObject inputObj)
	{
		super.columnMetaData = columnMetaData;
		super.inputObj = inputObj;
		super.columnMetaDataMap = converListToMap(columnMetaData);
	}
	

	public void setData()
	{
		Map<String, List<Object>> partitionKeyValues = generateAllPartitionKeyData();
		populateData(partitionKeyValues,0, new Object[partitionKeyValues.size()]);
	}
	
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	
	/*public static void main(String[] args) {
		String[] partitionKeys = {"a", "b", "c"};
        String query = "INSERT INTO mytable (" + String.join(", ", partitionKeys) + ") VALUES (" + String.join(", ", new String[partitionKeys.length]) + ")";
        
        
        System.out.println(query);
        LinkedHashMap<String, JSONArray> values = new LinkedHashMap<>();
        values.put("a", JSONArray.fromObject("[1]"));
        values.put("b", JSONArray.fromObject("[4, 5, 6]"));
        values.put("c", JSONArray.fromObject("[7, 8, 9]"));
        query = "INSERT INTO mytable (" + String.join(", ", values.keySet()) + ") VALUES (" + String.join(", ", new String[values.size()]).replaceAll("\\[|\\]", "") + ")";
        System.out.println(query);
        
        populateDataTest(values, 0 , new Object[values.size()]);
	}
	
	private static void populateDataTest( LinkedHashMap<String, JSONArray> values, int index, Object[] params)
	{
        if (index == values.size())
        {
            System.out.println(Arrays.toString(params));
        }
        else
        {
            String columnName = (String) values.keySet().toArray()[index];
            JSONArray jsonArray = values.get(columnName);
            for (int i = 0; i < jsonArray.size(); i++) {
                params[index] = jsonArray.get(i);
                populateDataTest(values, index + 1, params);
            }
        }
	}*/
	
}
