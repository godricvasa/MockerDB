package com.example;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "TableColumnsServlet", urlPatterns = {"/TableColumnsRest"})
public class TableColumnsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName");
        List<String> columns = DBUtil.getColumns(tableName);
        request.setAttribute("columns", columns);
        request.setAttribute("tableName", tableName);
        RequestDispatcher dispatcher = request.getRequestDispatcher("columns.jsp");
        dispatcher.forward(request, response);
    }
}
		