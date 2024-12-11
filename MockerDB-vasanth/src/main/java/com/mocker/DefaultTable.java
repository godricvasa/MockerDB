package com.mocker;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public class DefaultTable extends TableData
{
		public DefaultTable(List<Map<String,String>> columnMetaData, JSONObject inputObj) {
			super.columnMetaData = columnMetaData;
			super.inputObj = inputObj;
			super.columnMetaDataMap = converListToMap(columnMetaData);
		}
		
		public void setData()
		{
			Map<String, List<Object>> partitionKeyValues = generateAllPartitionKeyData();
			populateData(partitionKeyValues,0, new Object[partitionKeyValues.size()]);

		 /*Map<String, Object> resultMap = null;
		 for(int i=0; i<minRowSize; i++)
	     {
			 resultMap = new HashMap<>();
		     for (int j = 0; j < columnMetaData.size(); j++)
		     {
		    	 Map<String,String> colMData = columnMetaData.get(j);
		    	 String columnName = (String) colMData.get("column_name");
		    	 resultMap.put(columnName, getData(columnName, true));
		     }
		     resultList.add(resultMap);
		 }*/
		}

		public List<Map<String, Object>> getResultList() {
			return resultList;
		}

		public void setResultList(List<Map<String, Object>> resultList) {
			this.resultList = resultList;
		}
		
		
}
