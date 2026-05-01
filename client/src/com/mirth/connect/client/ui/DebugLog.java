package com.mirth.connect.client.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Lightweight file logger for diagnosing UI issues.
 */
public final class DebugLog {

    private static final File LOG_FILE = new File(System.getProperty("user.home"), "oie-admin-debug.log");
    private static final SimpleDateFormat TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private DebugLog() {
    }

    public static void info(String message) {
        write("INFO", message, null);
    }

    public static void error(String message, Throwable t) {
        write("ERROR", message, t);
    }

    private static synchronized void write(String level, String message, Throwable t) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(TS.format(new Date()) + " [" + level + "] " + message);
            if (t != null) {
                t.printStackTrace(out);
            }
        } catch (IOException ignored) {
            // As a last resort, do nothing. We don't want logging to crash the UI.
        }
    }
}

