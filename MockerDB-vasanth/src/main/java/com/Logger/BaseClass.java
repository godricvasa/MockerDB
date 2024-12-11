package com.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.logging.Logger;

public class BaseClass extends HttpServlet {
    public static final Logger logger = Logger.getLogger(BaseClass.class.getName());

    @Override
    public void init() throws ServletException {
        super.init();
        LoggingConfig.configure();

    }
}