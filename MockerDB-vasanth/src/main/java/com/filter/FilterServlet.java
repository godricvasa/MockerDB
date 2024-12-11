package com.filter;


import com.Logger.LoggingConfig;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

@WebFilter("/*")
public class FilterServlet implements Filter {
//    public static final Logger logger = Logger.getLogger(FilterServlet.class.getName());


    public void init(FilterConfig filterConfig) throws ServletException {
//        LoggingConfig.configure();
//        logger.info("Filter Servlet is initialized");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean isAjaxRequest = "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"));
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        boolean isPublicPage = path.equals("/login") || path.equals("/register");
        boolean loggedIn = (session != null && session.getAttribute("username") != null);

        if (loggedIn) {
            if (path.equals("/login")) {
                // Redirect logged-in users trying to access the login page back to the main page
                Dispatcher.requestDispatcher(httpRequest, httpResponse, "/");
            } else if (isAjaxRequest && path.startsWith("/main")) {
                // Allow AJAX requests within the main page to proceed
                System.out.println("Handling AJAX request within the main page");
                chain.doFilter(request, response);
            } else {
                // Other pages the logged-in user is allowed to access
                chain.doFilter(request, response);
            }
        } else {
            // Redirect unauthenticated users trying to access protected pages
            if (!isPublicPage) {
                if (isAjaxRequest) {
//                    System.out.println("hehe ajax ");
                    // Send 401 for AJAX requests without redirecting
//                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    chain.doFilter(request, response);
                } else {
                    // Redirect regular requests to the login page
//                    Dispatcher.requestDispatcher(httpRequest, httpResponse, "/login");
                    chain.doFilter(request, response);
                }
            } else {
                // Allow access to public pages (login, register)
                chain.doFilter(request, response);
            }
        }
    }

//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}

