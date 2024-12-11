package com.Auth;

import com.Logger.BaseClass;
import com.mysql.cj.jdbc.Driver;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;

@WebServlet("/register")
public class Register extends BaseClass {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("Register.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean usernameExists = false;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        logger.info(String.format("User %s trying to register to the application from IP: %s", username, req.getRemoteAddr()));
        if (password.length()==0){
            resp.getWriter().write("password length must be greater than 0");
        }
        else{
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                DriverManager.registerDriver(new Driver());
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_db", "root", "vasa");

                PreparedStatement st = conn.prepareStatement("select username from users");
                ResultSet r1=st.executeQuery();
                String usernameCounter;

                while(r1.next())
                {

                    usernameCounter =  r1.getString("username");
//                    System.out.println(usernameCounter);
                    if(usernameCounter.equals(username)) //this part does not happen even if it should
                    {
                        logger.warning(String.format("Failed to register user %s: Username already exists.", username));
//                        System.out.println("It already exists");
                        resp.getWriter().write("username already exists try out another username");
                        usernameExists = true;
                    }


                }
                if (!usernameExists){

                    PreparedStatement st2 = conn.prepareStatement("INSERT INTO users(username,password) VALUES('"+username+"',"+"'"+password+"')");
                    st2.executeUpdate();

                    logger.info(String.format("User %s registered successfully.", username));
                    resp.getWriter().write("user created successfully");
                }


            } catch (SQLException e) {
                logger.log(Level.SEVERE, String.format("Error processing registration for user %s: %s", username, e.getMessage()), e);

            } finally {
                if (rs != null) try {
                    rs.close();
                } catch (SQLException ignored) {
                }
                if (ps != null) try {
                    ps.close();
                } catch (SQLException ignored) {
                }
                if (conn != null) try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }

    }
}
