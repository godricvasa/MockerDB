package com.util;

import java.util.HashMap;
import java.util.Map;

public class GenerateSetters {
    public static void main(String[] args) {
        Map<String, String> fields = new HashMap<>();
        fields.put("FAILURE_CHECKS", "Long");
        fields.put("LOCATION_CHECKS", "Long");
        fields.put("UPDATEDTIME", "Long");
        fields.put("ALERT_AFTER_EXE_ACTIONS", "Boolean");
        fields.put("MONITOR_POLL_ID", "Long");
        fields.put("DCSTARTTIME", "Long");
        fields.put("MONITOR_KEY", "String");
        fields.put("FREEMONITOR", "Integer");
        fields.put("UPTIMEMONITOR", "Integer");
        fields.put("HIDDEN_MONITOR", "Integer");
        fields.put("WEIGHTAGE", "Float");
        fields.put("ADDITIONAL_WEIGHTAGE", "Float");
        fields.put("LICENSE_TYPE_ID", "Integer");
        fields.put("MONITOR_TYPE_ID", "Integer");

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldType = entry.getValue();

            System.out.println("public void set" + fieldName + "() {");
            System.out.println("    Object obj = getData(\"" + fieldName + "\");");
            System.out.println("    if(obj!=null && obj instanceof " + fieldType + ")");
            System.out.println("    {");
            System.out.println("        " + fieldName + " = (" + fieldType + ")obj;");
            System.out.println("    }");
            System.out.println("    else {");
            System.out.println("        " + fieldName + " = " + getDefault(fieldType) + ";");
            System.out.println("    }");
            System.out.println("}");
            System.out.println();
        }
    }

    private static String getDefault(String fieldType) {
        switch (fieldType) {
            case "Long":
                return "1l";
            case "Integer":
                return "1";
            case "Float":
                return "1.0f";
            case "Boolean":
                return "true";
            case "String":
                return "\"\"";
            default:
                return "null";
        }
    }
}
