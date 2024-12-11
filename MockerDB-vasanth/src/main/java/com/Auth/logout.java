package com.Auth;

import com.Logger.BaseClass;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;

@WebServlet("/logout")
public class logout extends BaseClass {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         HttpSession session = req.getSession(false);
           String username = (String) session.getAttribute("username");

           session.invalidate();
           Cookie[] cookies = req.getCookies();
           for(Cookie cookie:cookies){
               cookie.setPath("/");
               cookie.setMaxAge(0);
           }
        logger.log(Level.INFO,String.format("user %s logged out successfully.",username));

    }
}
