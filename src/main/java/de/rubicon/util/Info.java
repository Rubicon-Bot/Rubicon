package de.rubicon.util;

import de.rubicon.core.DiscordCore;
import de.rubicon.core.Main;
import net.dv8tion.jda.core.entities.User;

public class Info {

    static Configuration cfg = Main.getConfiguration();
    public final static String BOT_ID = "380713705073147915";
    public final static String BOT_DEFAULT_PREFIX = "rc!";

    public final static String BOT_NAME = "Rubicon";
    public final static String BOT_VERSION = "0.1.0";
    public final static String CONFIG_FILE = "config.json";
    public final static String[] CONFIG_KEYS = {"token","mysql_host","mysql_port","mysql_database","mysql_password","mysql_user"};
    public final static User[] BOT_AUTHORS = {
            DiscordCore.getJDA().getUserById(227817074976751616L), //ForYaSee
            DiscordCore.getJDA().getUserById(318773753796624394L), //Scryptex
            DiscordCore.getJDA().getUserById(138014719582797824L), //Eiskeks
            DiscordCore.getJDA().getUserById(264048760580079616L), //Schlaubi
            DiscordCore.getJDA().getUserById(148905646715043841L), //Robert
            DiscordCore.getJDA().getUserById(153507094933274624L), //Lee
            DiscordCore.getJDA().getUserById(224528662710452224L) //ForMoJa
    };
    /* MySQL login */
    public final static String MYSQL_HOST = cfg.getString("mysql_host");
    public final static String MYSQL_PORT = cfg.getString("mysql_port");
    public final static String MYSQL_USER = cfg.getString("mysql_user");
    public final static String MYSQL_PASSWORD = cfg.getString("mysql_password");
    public final static String MYSQL_DATABASE = cfg.getString("mysql_database");

    //TODO Namen richtig?
    public static void init() {
        Configuration cfg = Main.getConfiguration();
        for (String configKey : CONFIG_KEYS) {
            if(!cfg.has(configKey)){
                String input = Setup.prompt("Please enter the the value of " + configKey);
                cfg.set(configKey, input);
            }
        }
    }
}