package com.filter;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import com.Response.ResponseContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static java.lang.System.out;

public class ResponseFilter implements Filter
{
	  public static final Logger logger = Logger.getLogger(ResponseFilter.class.getName());
	private static final org.apache.log4j.Logger Audit_logger = org.apache.log4j.Logger.getLogger("AUDIT_LOGGER");

	public void init(FilterConfig filterConfig) throws ServletException {
//		LoggingConfig.configure();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		long before=System.currentTimeMillis();
    try{
		chain.doFilter(request,response);

		PrintWriter out=response.getWriter();
		Object respObj = request.getAttribute("response");
		if(respObj!=null) {
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			if(respObj instanceof Map)
			{
				JSONObject jsonObj = JSONObject.fromObject(respObj);
				out.print(jsonObj);

			}
			else if(respObj instanceof List) {
				JSONArray jsonArr = JSONArray.fromObject(respObj);
				out.print(jsonArr);
			}
			request.removeAttribute("response");

		}

//		long after=System.currentTimeMillis();
//		logger.info("Total time taken for this request "+(after - before));
		logAuditEntry((HttpServletRequest) request,true,before);

	}
	catch (Exception e){
		logAuditEntry((HttpServletRequest) request,false,before);
		Audit_logger.error("Request processing error", e);

	}
	finally {
		out.close();
		ResponseContext.clearResponseData();
	}


	}
	private void logAuditEntry(HttpServletRequest request, boolean success, long startTime) {
		long processingTime = System.currentTimeMillis() - startTime;

		Audit_logger.info(String.format(
				"IP=%s, User=%s, URL=%s, Method=%s, Status=%s, ProcessingTime=%dms",
				getClientIpAddress(request),
				getCurrentUsername(request),
				request.getRequestURL(),
				request.getMethod(),
				success ? "SUCCESS" : "FAILURE",
				processingTime
		));
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		return ipAddress != null ? ipAddress : request.getRemoteAddr();
	}

	private String getCurrentUsername(HttpServletRequest request) {
		Object user = request.getSession().getAttribute("username");
		return user != null ? user.toString() : "anonymous";
	}
	public void destroy() {}
}