package com.example;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PopulateTableServlet", urlPatterns = {"/PopulateTableRest"})
public class PopulateTableServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName");
        Map<String, String> columnValues = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String value = request.getParameter(parameterName);
            columnValues.put(parameterName, value);
        }
        DBUtil.populateTable(tableName, 1000, columnValues);
        RequestDispatcher dispatcher = request.getRequestDispatcher("populate.jsp");
        dispatcher.forward(request, response);
    }
}