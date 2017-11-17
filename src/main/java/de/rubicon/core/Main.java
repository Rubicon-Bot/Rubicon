package de.rubicon.core;

import de.rubicon.util.Configuration;
import de.rubicon.util.Info;
import de.rubicon.util.MySQL;

import java.io.File;

public class Main {

    private static DiscordCore discordCore;
    private static MySQL mySQL;

    public static Configuration getConfiguration() {
        return configuration;
    }

    private static Configuration configuration;

    public static void main(String[] args) {
        configuration = new Configuration(new File(Info.CONFIG_FILE));
        mySQL = new MySQL(Info.MYSQL_HOST, Info.MYSQL_PORT, Info.MYSQL_USER, Info.MYSQL_PASSWORD, Info.MYSQL_DATABASE);
        mySQL.connect();
        discordCore = new DiscordCore();
        discordCore.start();
    }

    public static DiscordCore getDiscordCore() {
        return discordCore;
    }

    public static MySQL getMySQL() {
        return mySQL;
    }
}
