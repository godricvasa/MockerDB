package com.Logger;

import java.util.logging.*;

public class LoggingConfig {
    public static void configure() {
        try {
            // Get the root logger to apply settings globally
            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.INFO); // Set the global logging level


            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.FINE);
            rootLogger.addHandler(consoleHandler);


            FileHandler fileHandler = new FileHandler("/home/vasanth-pt7742/Downloads/MockerDB-vasanth/src/main/java/com/Logger/application.log", true); // true for append mode            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO); // File will log everything
            rootLogger.addHandler(fileHandler);


        } catch (Exception e) {
            Logger.getLogger(LoggingConfig.class.getName()).log(Level.SEVERE, "Failed to configure logging", e);
        }
    }
}