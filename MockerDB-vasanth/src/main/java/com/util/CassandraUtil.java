package com.util;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.Logger.BaseClass;
import com.datastax.driver.core.Cluster;

import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.table.TableMetaData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CassandraUtil
{
	public static final Logger logger  = Logger.getLogger(CassandraUtil.class.getName());

	public static Cluster getCluster(JSONObject inputJson) throws UnknownHostException,IllegalArgumentException{
		
		String hostname =  inputJson.getString("hostname");
		Cluster cluster = null;

        cluster = 	Cluster.builder().addContactPoint(hostname).build();

        //			logger.log(Level.SEVERE,e.getMessage(),e);

		return cluster;

    }
	
}
