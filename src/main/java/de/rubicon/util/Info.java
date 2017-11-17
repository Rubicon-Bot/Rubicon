package de.rubicon.util;

import de.rubicon.core.Main;

import java.util.HashMap;

public class Info {

    static Configuration cfg = Main.getConfiguration();
    public final static String BOT_TOKEN = cfg.getString("token");
    public final static String BOT_ID = "380713705073147915";
    public final static String BOT_DEFAULT_PREFIX = "RC!";

    public final static String BOT_NAME = "Rubicon";
    public final static String BOT_VERSION = "0.1.0";
    public final static String CONFIG_FILE = "config.json";
    public final static String[] CONFIG_KEYS = {"token","mysql_host","mysql_port","mysql_database","mysql_password","mysql_user"};
    private static HashMap<String, Long> BOT_AUTHORS;
    /* MySQL login */
    public final static String MYSQL_HOST = cfg.getString("mysql_host");
    public final static String MYSQL_PORT = cfg.getString("mysql_port");
    public final static String MYSQL_USER = cfg.getString("mysql_user");
    public final static String MYSQL_PASSWORD = cfg.getString("mysql_password");
    public final static String MYSQL_DATABASE = cfg.getString("mysql_database");

    //TODO Namen richtig?
    public static void init() {
        BOT_AUTHORS = new HashMap<String, Long>();
        BOT_AUTHORS.put("ForYaSee", 227817074976751616L);
        BOT_AUTHORS.put("xEiiskeksx", 138014719582797824L);
        BOT_AUTHORS.put("Scryptex", 318773753796624394L);
        BOT_AUTHORS.put("Schlaubi", 264048760580079616L);
        BOT_AUTHORS.put("Robert", 148905646715043841L);
        BOT_AUTHORS.put("Lee", 153507094933274624L);
        BOT_AUTHORS.put("ForMoJa", 224528662710452224L);

        Configuration cfg = Main.getConfiguration();
        for (String configKey : CONFIG_KEYS) {
            if(!cfg.has(configKey)){
                String input = Setup.prompt("Please enter the the value of " + configKey);
                cfg.set(configKey, input);
            }

        }

    }

}
