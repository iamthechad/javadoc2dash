package com.megatome.d2d.util;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

/**
 * Simple class that wraps all logging.
 */
public class LogUtility {
    private static Logger logger;
    private static boolean verbose = false;

    /**
     * Specify a logger to use. Log messages will go to System.out if no logger is specified.
     * @param logger Logger instance to use
     */
    public static void setLogger(final Logger logger) {
        if (null != logger) {
            LogUtility.logger = logger;
        }
    }

    /**
     * Specify if messages that are more verbose should be shown.
     * @param verbose True to set a higher verbosity
     */
    public static void setVerbose(boolean verbose) {
        LogUtility.verbose = verbose;
    }

    /**
     * Log a message. If a logger has been specified, log the message at INFO level. Otherwise the message is logged
     * to System.out.
     * @param format Message, formatted per SLF4J requirements
     * @param arguments Arguments to substitute into the message before logging
     */
    public static void log(String format, Object... arguments) {
        if (null != logger) {
            logger.info(format, arguments);
        } else {
            System.out.println(MessageFormatter.arrayFormat(format, arguments).getMessage());
        }
    }

    /**
     * Log a message only if the verbose flag has been set. If a logger has been specified, log the message at INFO level. Otherwise the message is logged
     * to System.out.
     * @param format Message, formatted per SLF4J requirements
     * @param arguments Arguments to substitute into the message before logging
     */
    public static void logVerbose(String format, Object... arguments) {
        if (verbose) {
            log(format, arguments);
        }
    }
}
