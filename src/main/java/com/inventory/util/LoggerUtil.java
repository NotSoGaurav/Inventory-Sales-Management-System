package com.inventory.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lightweight, standard Core Java logging utility.
 * Formats timestamped log entries and safely outputs them to console and file storage.
 * Strictly avoids logging sensitive user credentials such as passwords.
 */
public class LoggerUtil {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_FILE = "data/app.log";
    private static boolean fileLoggingEnabled = true;

    public enum LogLevel {
        INFO, WARN, ERROR
    }

    public static void setFileLoggingEnabled(boolean enabled) {
        fileLoggingEnabled = enabled;
    }

    /**
     * Logs an informational message.
     *
     * @param message the log message
     */
    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    /**
     * Logs a warning message.
     *
     * @param message the log message
     */
    public static void warn(String message) {
        log(LogLevel.WARN, message);
    }

    /**
     * Logs an error message.
     *
     * @param message the log message
     */
    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }

    /**
     * Logs an error message accompanied by an exception.
     *
     * @param message   the log message
     * @param throwable the cause
     */
    public static void error(String message, Throwable throwable) {
        String detail = throwable != null ? message + " | Exception: " + throwable.getMessage() : message;
        log(LogLevel.ERROR, detail);
    }

    /**
     * Specialized logger for authentication attempts.
     * Deliberately excludes passwords to maintain security integrity.
     *
     * @param username the username attempting sign-in
     * @param success  whether login was successful
     */
    public static void logLoginAttempt(String username, boolean success) {
        String safeUser = (username != null && !username.trim().isEmpty()) ? username.trim() : "<anonymous>";
        if (success) {
            info("[AUTH] User '" + safeUser + "' successfully logged in.");
        } else {
            warn("[AUTH] Failed login attempt for username: '" + safeUser + "'.");
        }
    }

    /**
     * Specialized logger for business operations.
     *
     * @param operation the action (e.g., ADD_PRODUCT, PROCESS_SALE)
     * @param entityId  the target entity ID
     * @param details   descriptive notes
     */
    public static void logOperation(String operation, String entityId, String details) {
        info("[AUDIT] Action: " + operation + " | ID: " + entityId + (details != null ? " | " + details : ""));
    }

    /**
     * Formats and emits a log message with level and timestamp.
     */
    private static void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String formatted = String.format("[%s] [%-5s] %s", timestamp, level.name(), message);

        // Emit to system output
        if (level == LogLevel.ERROR) {
            System.err.println(formatted);
        } else {
            System.out.println(formatted);
        }

        // Persist to app.log if enabled
        if (fileLoggingEnabled) {
            FileUtil.appendLine(LOG_FILE, formatted);
        }
    }
}
