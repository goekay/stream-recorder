package com.goekay.streamrecorder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
public final class Utils {
    private Utils() { }

    // -------------------------------------------------------------------------
    // Logging
    // -------------------------------------------------------------------------

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS")
                                                                        .withZone(ZoneId.systemDefault());

    public static void print(String message) {
        System.out.println(FORMATTER.format(Instant.now()) + " - " + message);
    }

    public static void print(String template, Object... args) {
        print(format(template, args));
    }

    public static void error(String message) {
        print("[Error] " + message);
    }

    public static void error(String template, Object... args) {
        error(format(template, args));
    }

    public static void warn(String message) {
        print("[Warn] " + message);
    }

    public static void warn(String template, Object... args) {
        warn(format(template, args));
    }

    // -------------------------------------------------------------------------
    // String
    // -------------------------------------------------------------------------

    public static String cleanUp(String str) {
        if (str == null) {
            return null;
        } else {
            return str.trim().replaceAll("\\s+", "_").toLowerCase();
        }
    }

    public static boolean hasValue(String str) {
        return str != null && !str.isEmpty();
    }
}
