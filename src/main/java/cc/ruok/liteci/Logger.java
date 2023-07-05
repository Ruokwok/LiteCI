package cc.ruok.liteci;

import org.apache.logging.log4j.LogManager;

public class Logger {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(LiteCI.class.getName());

    public static void info(String str) {
        LOGGER.info(str);
    }

    public static void warning(String str) {
        LOGGER.warn(str);
    }

    public static void error(String str) {
        LOGGER.error(str);
    }

}
