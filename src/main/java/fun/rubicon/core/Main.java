package fun.rubicon.core;

import fun.rubicon.util.*;


import java.io.File;

public class Main {
    private static DiscordCore discordCore; //TODO remove redundant instance. DiscordCore only has static attributes and methods
    private static MySQL mySQL;
    public final static String[] CONFIG_KEYS = {"token","mysql_host","mysql_port","mysql_database","mysql_password","mysql_user","bitlytoken","darksky_token","mapstoken"};


    private static Configuration configuration;

    public static void main(String[] args) {
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, new File("latest.log"));
        configuration = new Configuration(new File(Info.CONFIG_FILE));
        for (String configKey : CONFIG_KEYS) {
            if(!configuration.has(configKey)){
                String input = Setup.prompt(configKey);
                configuration.set(configKey, input);
            }
        }
        discordCore = new DiscordCore();
        //noinspection AccessStaticViaInstance TODO see attribute discordCore
        discordCore.start();
        mySQL = new MySQL(Info.MYSQL_HOST, Info.MYSQL_PORT, Info.MYSQL_USER, Info.MYSQL_PASSWORD, Info.MYSQL_DATABASE);
        mySQL.connect();

    }

    public static DiscordCore getDiscordCore() {
        return discordCore;
    }

    public static MySQL getMySQL() {
        return mySQL;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }


    }


