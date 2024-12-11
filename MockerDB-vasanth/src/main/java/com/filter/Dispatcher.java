package com.filter;


import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.ErrorHandler.Handler.getErrorString;

public class Dispatcher
{
	public static void dispatch(HttpServletRequest request,Object result)
	{
//		if (result==null){
//			System.out.println(getErrorString("table not found"));
//			JsonObject object = new JsonObject();
//
//			object.addProperty("error",getErrorString("table not found"));
//			result = object;
//			request.setAttribute("response", result);
////			request.setAttribute("failed",true);
////			request.setAttribute("response",getErrorString("table not found") );
//
//		}
//		else{

			request.setAttribute("response", result);
//		}

	}
	
	public static void requestDispatcher(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException
	{
		/*
		 * if(request.getRequestURI().startsWith("/app/")) {
		 * request.setAttribute("response", result); }
		 */
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        dispatcher.forward(request, response);
	}
}
