package com.table;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import javax.servlet.http.HttpSession;

import com.ErrorHandler.Handler;
import com.Response.ResponseContext;
import com.util.CacheUtil;
import com.util.MySqlUtil;
import com.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import com.filter.Dispatcher;


@WebServlet(name = "FetchMetaData", urlPatterns = {"/fetchMetaData"})
public class TableMetaData extends HttpServlet {
	public static final Logger logger = Logger.getLogger(TableMetaData.class.getName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
//		String username = (String) session.getAttribute("username");
//		logger.info(String.format("User %s requesting for a table from SQL database", username));

		String jsonStrObj = IOUtils.toString(request.getInputStream(), "UTF-8");
		JSONObject obj = JSONObject.fromObject(jsonStrObj);
		String tableName = obj.getString("database_tablename");

		List<Map<String, String>> columnMetadataMap = getTableMetaData(obj, tableName);

		// Set response content type to JSON
		response.setContentType("application/json");

		// Send the response back
		Dispatcher.dispatch(request, columnMetadataMap);
	}

	public static String getHtmlTypeFromDBType(String type) {
		if (type == null) {
			return "text";
		}
		if (type.contains("(")) {
			type = type.substring(0, type.indexOf('('));
		}
		switch (type) {
			case "tinyint":
			case "boolean":
				return "radio";
			case "int":
			case "smallint":
			case "mediumint":
			case "bigint":
			case "datetime":
			case "timestamp":
				return "number";
			case "double":
			case "float":
			case "decimal":
				return "number";
			case "varchar":
			case "char":
			default:
				return "text";
		}
	}

	public static List<Map<String, String>> getTableMetaData(JSONObject obj, String tableName) throws IOException {
		List<Map<String, String>> columnMetadataMap = new ArrayList<>();
		Map<String, String> errorMap = null;

		Object cacheObj = CacheUtil.getCache(tableName);
		if (cacheObj != null) {
			logger.info(String.format("Requested table %s exists and fetch successful", tableName));
			return (List<Map<String, String>>) cacheObj;
		}

		try (Connection conn = MySqlUtil.getConnection(obj);
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("DESCRIBE " + tableName)) {

			while (rs.next()) {
				String columnName = rs.getString("Field");
				String type = getHtmlTypeFromDBType(rs.getString("Type"));
				String realType = rs.getString("Type");
				String nullable = rs.getString("Null");
				String defaultValue = rs.getString("Default");
				String primaryKey = rs.getString("Key");
				boolean isPrimaryKey = !StringUtils.isEmpty(primaryKey);

				if (realType != null && realType.contains("(")) {
					realType = realType.substring(0, realType.indexOf('('));
				}

				Map<String, String> columnMetadata = new HashMap<>();
				columnMetadata.put("column_name", columnName);
				columnMetadata.put("primary_key", String.valueOf(isPrimaryKey));
				columnMetadata.put("type", type);
				columnMetadata.put("data_type", realType);
				columnMetadata.put("nullable", nullable);
				columnMetadata.put("default_value", defaultValue);
				columnMetadataMap.add(columnMetadata);
			}

			logger.info(String.format("Requested table %s exists and fetch successful", tableName));
			CacheUtil.setCache(tableName, columnMetadataMap);

		} catch (SQLException e) {
			logger.log(Level.SEVERE, String.format("SQL error while fetching metadata for table %s", tableName), e);
			Handler.responseMaker(e);
			columnMetadataMap.add(ResponseContext.getThreadLocalResponse());
		} catch (Exception e) {

			logger.log(Level.SEVERE, "Unexpected error", e);
			Handler.responseMaker(e);
			columnMetadataMap.add(ResponseContext.getThreadLocalResponse());
		}

		return columnMetadataMap;
	}
}