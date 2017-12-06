package fun.rubicon.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.util
 */

public class Logger {
    private static File logFile;
    private static String loggerText = "";
    private static boolean fileLogging = false;
    private static SimpleDateFormat logDateFormatter = new SimpleDateFormat("HH:mm:ss");

    public static void logInFile(String appName, String appVersion, File file) {
        String date = new SimpleDateFormat("dd_MM_yyyy-HH:mm:ss").format(new Date());

        logFile = file;
        if (!logFile.exists())
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        fileLogging = true;
        String logHeader = ("---- " + appName + " " + appVersion + " Log ----\n") +
                "\n" +
                "Date: " + date + "\n" +
                "\n" +
                "-- System Details --\n" +
                "Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version") + "\n" +
                "Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor") + "\n" +
                "Java VM Version: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor") + "\n" +
                "Memory: " + getMemoryText() + "\n" +
                "\n" +
                "-- Log --\n";
        addLogEntry(logHeader);
    }

    private static void addLogEntry(String text) {
        if(!fileLogging)
            return;
        try {
            loggerText += text;
            FileWriter writer = new FileWriter(logFile);
            writer.write(loggerText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void info(String text) {
        log(text, LoggerLevel.INFO);
    }

    public static void debug(String text) {
        log(text, LoggerLevel.DEBUG);
    }

    public static void warning(String text) {
        log(text, LoggerLevel.WARNING);
    }

    public static void error(String text) {
        log(text, LoggerLevel.ERROR);
    }

    public static void error(Throwable t) {
        t.printStackTrace();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        t.printStackTrace(printWriter);
        log(stringWriter.getBuffer().toString(), LoggerLevel.THROWABLE);
    }

    public static void log(String text, LoggerLevel loggerLevel) {
        switch (loggerLevel) {
            case INFO:
                String infoMessage = formatLogMessage("Info", text);
                System.out.println(infoMessage);
                addLogEntry(infoMessage + "\n");
                break;
            case DEBUG:
                String debugMessage = formatLogMessage("Debug", text);
                System.out.println(debugMessage);
                addLogEntry(debugMessage + "\n");
                break;
            case ERROR:
                String errorMessage = formatLogMessage("Error", text);
                System.err.println(errorMessage);
                addLogEntry(errorMessage + "\n");
                break;
            case THROWABLE:
                addLogEntry(formatLogMessage("Error", text) + "\n");
                break;
            case WARNING:
                String warningMessage = formatLogMessage("Warning", text);
                System.err.print(warningMessage);
                addLogEntry(warningMessage + "\n");
                break;
            default:
                break;
        }
    }

    private static String formatLogMessage(String logType, String text) {
        return '[' + logType + "] (" + logDateFormatter.format(new Date()) + ") | " + text;
    }

    private static String getMemoryText() {
        Runtime var1 = Runtime.getRuntime();
        long maxMemory = var1.maxMemory();
        long totalMemory = var1.totalMemory();
        long freeMemory = var1.freeMemory();
        long maxMemoryInMB = maxMemory / 1024L / 1024L;
        long totalMemoryInMB = totalMemory / 1024L / 1024L;
        long freeMemoryInMB = freeMemory / 1024L / 1024L;

        return String.valueOf(freeMemory) + " bytes (" + freeMemoryInMB + " MB) / " +
                totalMemory + " bytes (" + totalMemoryInMB + " MB) up to " +
                maxMemory + " bytes (" + maxMemoryInMB + " MB)";
    }

    public static String getFullLog() {
        return loggerText;
    }

    public enum LoggerLevel {
        INFO, DEBUG, ERROR, THROWABLE, WARNING
    }
}