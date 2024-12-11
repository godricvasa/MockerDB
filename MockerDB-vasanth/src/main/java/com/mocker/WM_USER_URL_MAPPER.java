package com.mocker;

import java.util.List;
import java.util.Map;

import com.github.javafaker.Faker;

import net.sf.json.JSONObject;

public class WM_USER_URL_MAPPER extends TableData
{
	public WM_USER_URL_MAPPER(List<Map<String,String>> columnMetaData, JSONObject inputObj)
	{
		super.columnMetaData = columnMetaData;
		super.inputObj = inputObj;
		super.columnMetaDataMap = converListToMap(columnMetaData);
	}
	
    private Long USER_ID;
    private Long URLID;
    private String DISPLAYNAME;
    private Long TYPE;
    private String MONITORTYPE;
    private Long FAILURE_CHECKS;
    private Long LOCATION_CHECKS;
    private Long UPDATEDTIME;
    private Boolean ALERT_AFTER_EXE_ACTIONS;
    private Long MONITOR_POLL_ID;
    private Long DCSTARTTIME;
    private String MONITOR_KEY;
    private Integer FREEMONITOR;
    private Integer UPTIMEMONITOR;
    private Integer HIDDEN_MONITOR;
    private Float WEIGHTAGE;
    private Float ADDITIONAL_WEIGHTAGE;
    private Integer LICENSE_TYPE_ID;
    private Integer MONITOR_TYPE_ID;

    Faker faker = new Faker();
    public WM_USER_URL_MAPPER(JSONObject obj)
    {
        setUSER_ID(1l);
        URLID = faker.number().numberBetween(1l, 1000l);
        setDISPLAYNAME();
        setTYPE();
        setMONITORTYPE();
        setFAILURE_CHECKS();
        setLOCATION_CHECKS();
        setUPDATEDTIME();
        setALERT_AFTER_EXE_ACTIONS();
        setMONITOR_POLL_ID();
        setDCSTARTTIME();
        setMONITOR_KEY();
        setFREEMONITOR();
        setUPTIMEMONITOR();
        setHIDDEN_MONITOR();
        setWEIGHTAGE();
        setADDITIONAL_WEIGHTAGE();
        setLICENSE_TYPE_ID();
        setMONITOR_TYPE_ID();
        
		/*
		 * setUSER_ID(1l); URLID = faker.number().numberBetween(1l, 1000l);
		 * setDISPLAYNAME(); TYPE = faker.number().numberBetween(1l, 10l); MONITORTYPE =
		 * faker.lorem().word(); FAILURE_CHECKS = faker.number().numberBetween(1l, 10l);
		 * LOCATION_CHECKS = faker.number().numberBetween(1l, 10l); UPDATEDTIME =
		 * faker.number().numberBetween(1l, 1000l); ALERT_AFTER_EXE_ACTIONS =
		 * faker.bool().bool(); MONITOR_POLL_ID = faker.number().numberBetween(1l,
		 * 1000l); DCSTARTTIME = faker.number().numberBetween(1l, 1000l); MONITOR_KEY =
		 * faker.lorem().word(); FREEMONITOR = faker.number().numberBetween(1, 10);
		 * UPTIMEMONITOR = faker.number().numberBetween(1, 10); HIDDEN_MONITOR =
		 * faker.number().numberBetween(1, 10); WEIGHTAGE = (float)
		 * faker.number().randomDouble(1,1, 10); ADDITIONAL_WEIGHTAGE = (float)
		 * faker.number().randomDouble(1,1, 10); LICENSE_TYPE_ID =
		 * faker.number().numberBetween(1, 10); MONITOR_TYPE_ID =
		 * faker.number().numberBetween(1, 10);
		 */
    }
    
    private void setPrimaryKey(Long USER_ID, Long URLID)
    {
    	this.USER_ID = USER_ID;
    	this.URLID = URLID;
    }

	public void setUSER_ID(Long uSER_ID) {
		USER_ID = uSER_ID;
	}

	public void setURLID() {
		Object obj = getData("URLID");
		if(obj==null)
		{
			faker.number().numberBetween(1l, 1000l);
		}
	}

	public void setDISPLAYNAME() {
		Object obj = getData("DISPLAYNAME");
		if(obj!=null && obj instanceof String)
		{
			DISPLAYNAME = (String) obj;
		}
		else 
		{
			DISPLAYNAME = faker.name().fullName();
		}
	}

	public void setTYPE() {
		Object obj = getData("TYPE");
		if(obj!=null && obj instanceof Integer)
		{
			TYPE = (Long)obj;
		}
		else {
			TYPE = 1l;
		}
	}

	public void setMONITORTYPE() {
		Object obj = getData("MONITORTYPE");
		if(obj!=null && obj instanceof String)
		{
			MONITORTYPE = (String)obj;
		}
		else {
			MONITORTYPE = "URL";
		}
	}

	public void setFAILURE_CHECKS() {
	    Object obj = getData("FAILURE_CHECKS");
	    if(obj!=null && obj instanceof Long) {
	        FAILURE_CHECKS = (Long)obj;
	    } else {
	        FAILURE_CHECKS = 0l;
	    }
	}

	public void setLOCATION_CHECKS() {
	    Object obj = getData("LOCATION_CHECKS");
	    if(obj!=null && obj instanceof Long) {
	        LOCATION_CHECKS = (Long)obj;
	    } else {
	        LOCATION_CHECKS = (long) faker.number().numberBetween(1, 3);
	    }
	}

	public void setUPDATEDTIME() {
	    Object obj = getData("UPDATEDTIME");
	    if(obj!=null && obj instanceof Long) {
	        UPDATEDTIME = (Long)obj;
	    } else {
	        UPDATEDTIME = faker.number().numberBetween(fifteenDaysAgo, currentTime);
	    }
	}

	public void setALERT_AFTER_EXE_ACTIONS() {
	    Object obj = getData("ALERT_AFTER_EXE_ACTIONS");
	    if(obj!=null && obj instanceof Boolean) {
	        ALERT_AFTER_EXE_ACTIONS = (Boolean)obj;
	    } else {
	        ALERT_AFTER_EXE_ACTIONS = false;
	    }
	}

	public void setMONITOR_POLL_ID() {
	    Object obj = getData("MONITOR_POLL_ID");
	    if(obj!=null && obj instanceof Long) {
	        MONITOR_POLL_ID = (Long)obj;
	    } else {
	        MONITOR_POLL_ID = 1l;
	    }
	}

	public void setDCSTARTTIME() {
	    Object obj = getData("DCSTARTTIME");
	    if(obj!=null && obj instanceof Long) {
	        DCSTARTTIME = (Long)obj;
	    } else {
	        DCSTARTTIME = faker.number().numberBetween(thirtyDaysAgo, fifteenDaysAgo);
	    }
	}

	public void setMONITOR_KEY() {
	    Object obj = getData("MONITOR_KEY");
	    if(obj!=null && obj instanceof String) {
	        MONITOR_KEY = (String)obj;
	    } else {
	        MONITOR_KEY = faker.lorem().word();
	    }
	}

	public void setFREEMONITOR() {
	    Object obj = getData("FREEMONITOR");
	    if(obj!=null && obj instanceof Integer) {
	        FREEMONITOR = (Integer)obj;
	    } else {
	        FREEMONITOR = 0;
	    }
	}

	public void setUPTIMEMONITOR() {
	    Object obj = getData("UPTIMEMONITOR");
	    if(obj!=null && obj instanceof Integer) {
	        UPTIMEMONITOR = (Integer)obj;
	    } else {
	        UPTIMEMONITOR = 0;
	    }
	}

	public void setHIDDEN_MONITOR() {
	    Object obj = getData("HIDDEN_MONITOR");
	    if(obj!=null && obj instanceof Integer) {
	        HIDDEN_MONITOR = (Integer)obj;
	    } else {
	        HIDDEN_MONITOR = 0;
	    }
	}

	public void setWEIGHTAGE() {
	    Object obj = getData("WEIGHTAGE");
	    if(obj!=null && obj instanceof Float) {
	        WEIGHTAGE = (Float)obj;
	    } else {
	        WEIGHTAGE = 1.0f;
	    }
	}

	public void setADDITIONAL_WEIGHTAGE() {
	    Object obj = getData("ADDITIONAL_WEIGHTAGE");
	    if(obj!=null && obj instanceof Float) {
	        ADDITIONAL_WEIGHTAGE = (Float)obj;
	    } else {
	        ADDITIONAL_WEIGHTAGE = null;
	    }
	}

	public void setLICENSE_TYPE_ID() {
	    Object obj = getData("LICENSE_TYPE_ID");
	    if(obj!=null && obj instanceof Integer) {
	        LICENSE_TYPE_ID = (Integer)obj;
	    } else {
	        LICENSE_TYPE_ID = 0;
	    }
	}

	public void setMONITOR_TYPE_ID() {
	    Object obj = getData("MONITOR_TYPE_ID");
	    if(obj!=null && obj instanceof Integer) {
	        MONITOR_TYPE_ID = (Integer)obj;
	    } else {
	        MONITOR_TYPE_ID = 0;
	    }
	}

	
	
	public Long getUSER_ID() {
		return USER_ID;
	}

	public Long getURLID() {
		return URLID;
	}

	public String getDISPLAYNAME() {
		return DISPLAYNAME;
	}

	public Long getTYPE() {
		return TYPE;
	}

	public String getMONITORTYPE() {
		return MONITORTYPE;
	}

	public Long getFAILURE_CHECKS() {
		return FAILURE_CHECKS;
	}

	public Long getLOCATION_CHECKS() {
		return LOCATION_CHECKS;
	}

	public Long getUPDATEDTIME() {
		return UPDATEDTIME;
	}

	public Boolean getALERT_AFTER_EXE_ACTIONS() {
		return ALERT_AFTER_EXE_ACTIONS;
	}

	public Long getMONITOR_POLL_ID() {
		return MONITOR_POLL_ID;
	}

	public Long getDCSTARTTIME() {
		return DCSTARTTIME;
	}

	public String getMONITOR_KEY() {
		return MONITOR_KEY;
	}

	public Integer getFREEMONITOR() {
		return FREEMONITOR;
	}

	public Integer getUPTIMEMONITOR() {
		return UPTIMEMONITOR;
	}

	public Integer getHIDDEN_MONITOR() {
		return HIDDEN_MONITOR;
	}

	public Float getWEIGHTAGE() {
		return WEIGHTAGE;
	}

	public Float getADDITIONAL_WEIGHTAGE() {
		return ADDITIONAL_WEIGHTAGE;
	}

	public Integer getLICENSE_TYPE_ID() {
		return LICENSE_TYPE_ID;
	}

	public Integer getMONITOR_TYPE_ID() {
		return MONITOR_TYPE_ID;
	}

	public Faker getFaker() {
		return faker;
	}

	
}
