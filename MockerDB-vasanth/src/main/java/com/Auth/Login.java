package com.Auth;



import com.Logger.BaseClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mysql.cj.jdbc.Driver;

import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

import java.sql.*;
import java.util.logging.Level;

@WebServlet("/login")
public class Login extends BaseClass {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("Login.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        logger.info(String.format("User %s trying to login to the application from IP: %s", username, req.getRemoteAddr()));
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            DriverManager.registerDriver(new Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_db", "root", "vasa");

            ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();

            JsonObject responseJson = new JsonObject();

            if (rs.next()) {
                if (rs.getString("password").equals(password)) {


                    HttpSession session = req.getSession(true);
                    session.setAttribute("username", username);
                    // jsessionId is the default cookie handled by the servlet
                    Cookie sessionCookie = new Cookie("JSESSIONID",session.getId());
                    //we can also create our own cookie
//                    Cookie sessionCookie2 = new Cookie("owner",username);
//          resp.addCookie(sessionCookie2);
                    sessionCookie.setMaxAge(7*24*60*60);
                    sessionCookie.setHttpOnly(true);  // Prevent XSS attacks
                    sessionCookie.setSecure(true);    // Only send over HTTPS
                    sessionCookie.setPath("/");
                    resp.addCookie(sessionCookie);
                    responseJson.addProperty("status", "success");
                    responseJson.addProperty("redirect", req.getContextPath() + "/");
                    logger.info(String.format("User %s logged in successfully.", username));
                } else {
                    logger.warning(String.format("Failed login attempt for user %s: Incorrect password.", username));
                    responseJson.addProperty("status", "error");
                    responseJson.addProperty("message", "Invalid password");
                }
            } else {
                logger.warning(String.format("Failed login attempt for user %s: User not found.", username));
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "User not found");
            }

            resp.getWriter().write(new Gson().toJson(responseJson));

        } catch (SQLException e) {
            logger.log(Level.SEVERE,String.format("Error processing login for user %s: %s", username, e.getMessage()), e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}

