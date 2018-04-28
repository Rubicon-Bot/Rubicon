/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Logger {
    private static File logFile;
    private static String loggerText = "";
    private static boolean fileLogging = false;
    private static SimpleDateFormat logDateFormatter = new SimpleDateFormat("HH:mm:ss");
    private static WebhookClient webhookClient;

    public static void enableWebhooks(String url) {
        WebhookClientBuilder builder = new WebhookClientBuilder(url);
        webhookClient = builder.build();
    }

    public static void logInFile(String appName, String appVersion, String logDirectory) {
        String date = new SimpleDateFormat("dd_MM_yyyy-HH:mm:ss").format(new Date());
        String filename = new SimpleDateFormat("dd_MM_yyyy HH_mm").format(new Date());
        File newFile = new File(logDirectory + filename + ".log");
        try {
            if (!newFile.exists())
                newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logFile = newFile;
        if (!logFile.exists())
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        fileLogging = true;
        String logHeader = (" ______        _     _                  \n" +
                "(_____ \\      | |   (_)                 \n" +
                " _____) )_   _| |__  _  ____ ___  ____  \n" +
                "|  __  /| | | |  _ \\| |/ ___) _ \\|  _ \\ \n" +
                "| |  \\ \\| |_| | |_) ) ( (__| |_| | | | |\n" +
                "|_|   |_|____/|____/|_|\\____)___/|_| |_|\n" +
                "                                        \nVersion: " + appVersion + "\n") +
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
        if (!fileLogging)
            return;
        try {
            loggerText += text;
            FileWriter writer = new FileWriter(logFile);
            writer.write(loggerText);
            writer.close();
            //latest
            FileWriter writer2 = new FileWriter("latest.log");
            writer2.write(loggerText);
            writer2.close();
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
                webhookClient.send(new EmbedBuilder().setColor(Colors.COLOR_SECONDARY).setTitle("INFO").setDescription("```" + text + "```").setTimestamp(Instant.now()).build());
                break;
            case DEBUG:
                String debugMessage = formatLogMessage("Debug", text);
                System.out.println(debugMessage);
                addLogEntry(debugMessage + "\n");
                webhookClient.send(new EmbedBuilder().setColor(Colors.COLOR_NO_PERMISSION).setTitle("DEBUG").setDescription("```" + text + "```").setTimestamp(Instant.now()).build());
                break;
            case ERROR:
                String errorMessage = formatLogMessage("Error", text);
                System.err.println(errorMessage);
                addLogEntry(errorMessage + "\n");
                webhookClient.send(new EmbedBuilder().setColor(Colors.COLOR_ERROR).setTitle("ERROR").setDescription("```" + text + "```").setTimestamp(Instant.now()).build());
                break;
            case THROWABLE:
                addLogEntry(formatLogMessage("Error", text) + "\n");
                webhookClient.send(new EmbedBuilder().setColor(Colors.COLOR_ERROR).setTitle("THROWABLE").setDescription("```" + text + "```").setTimestamp(Instant.now()).build());
                break;
            case WARNING:
                String warningMessage = formatLogMessage("Warning", text);
                System.err.println(warningMessage);
                addLogEntry(warningMessage + "\n");
                webhookClient.send(new EmbedBuilder().setColor(Colors.COLOR_PREMIUM).setTitle("WARNING").setDescription("```" + text + "```").setTimestamp(Instant.now()).build());
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

    public static File getLogFile() {
        return logFile;
    }

    public enum LoggerLevel {
        INFO, DEBUG, ERROR, THROWABLE, WARNING
    }
}