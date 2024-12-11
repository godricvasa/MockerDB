package com.Response;

import java.util.HashMap;
import java.util.Map;

public class ResponseContext {
    private static final ThreadLocal<Map<String,String>> responseMap = ThreadLocal.withInitial(HashMap::new);

    public static Map<String,String> getThreadLocalResponse(){

        return responseMap.get();
    }
    public static void clearResponseData() {
        responseMap.remove();
    }
}
