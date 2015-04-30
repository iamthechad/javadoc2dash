package com.megatome.j2d.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

/**
 * Simple class that wraps all logging.
 */
public class LogUtility {
    private static Logger logger = LoggerFactory.getLogger("Javadoc2Dash");
    private static boolean verbose = false;

    public static void setLogger(final Logger logger) {
        if (null != logger) {
            LogUtility.logger = logger;
        }
    }

    public static void setVerbose(boolean verbose) {
        LogUtility.verbose = verbose;
    }

    public static void log(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    public static void logVerbose(String format, Object... arguments) {
        if (verbose) {
            logger.info(format, arguments);
        }
    }
}
