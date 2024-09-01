package org.example.loger;

import org.apache.logging.log4j.LogManager;

public class Logger {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();
    public static org.apache.logging.log4j.Logger getLogger() {
        return logger;
    }
    public static void info(String message){
        logger.info(message);
    }
    public static  void debug(String message){
        logger.debug(message);
    }
    public static void error(String message){
        logger.error(message);
    }
    public static void verbose(String message){logger.trace(message);}
}
