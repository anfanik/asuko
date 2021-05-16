package me.anfanik.asuko;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.*;

public class AsukoLogger {

    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(DAY_OF_MONTH)
            .appendLiteral('/')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(YEAR)
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE)
            .toFormatter();

    public static void emptyLine() {
        info("");
    }

    public static void section(String section) {
        info("-=== Entering %s section ===-", section);
    }

    public static void info(String text) {
        System.out.println(format("INFO", text));
    }

    public static void info(String text, Object... arguments) {
        info(String.format(text, arguments));
    }

    public static void warning(String text) {
        System.out.println(format("WARN", text));
    }

    public static void warning(String text, Object... arguments) {
        warning(String.format(text, arguments));
    }

    public static void warning(String text, Throwable throwable, Object... arguments) {
        warning(String.format(text, arguments));
        throwable.printStackTrace();
    }

    public static void error(String text) {
        System.out.println(format("ERROR", text));
    }

    public static void error(String text, Object... arguments) {
        error(String.format(text, arguments));
    }

    public static void error(String text, Throwable throwable, Object... arguments) {
        error(String.format(text, arguments));
        throwable.printStackTrace();
    }

    private static String format(String level, String text) {
        LocalDateTime now = LocalDateTime.now();
        return "[" + level + " " + DATE_FORMAT.format(now)  + "] " + text;
    }

}
