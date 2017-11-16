package de.rubicon.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static File logFile;
    private static String loggerText = "";

    public static void info(String text) {
        log(text, LoggerLevel.INFO);
    }

    public static void debug(String text) {
        log(text, LoggerLevel.DEBUG);
    }

    public static void error(String text) {
        log(text, LoggerLevel.ERROR);
    }

    public static void error(Throwable t) {
        log(t.getMessage(), LoggerLevel.ERROR);
    }

    public static void log(String text, LoggerLevel level) {
        switch (level.toString().toLowerCase()) {
            case "info":
                String lInfo = "[Info] (" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ") | " + text + "";
                System.out.println(lInfo);
                break;
            case "debug":
                String lDebug = "[Debug] (" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ") | " + text + "";
                System.out.println(lDebug);
                break;
            case "error":
                String lError = "[Error] (" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ") | " + text + "";
                System.err.println(lError);
                break;
            default:
                break;
        }
    }

    private static String getMemoryText() {
        Runtime var1 = Runtime.getRuntime();
        long var2 = var1.maxMemory();
        long var4 = var1.totalMemory();
        long var6 = var1.freeMemory();
        long var8 = var2 / 1024L / 1024L;
        long var10 = var4 / 1024L / 1024L;
        long var12 = var6 / 1024L / 1024L;

        return var6 + " bytes (" + var12 + " MB) / " + var4 + " bytes (" + var10 + " MB) up to " + var2 + " bytes (" + var8 + " MB)";
    }

    public static String getLoggerText() {
        return loggerText;
    }

    public enum LoggerLevel {
        INFO, DEBUG, ERROR
    }

}
