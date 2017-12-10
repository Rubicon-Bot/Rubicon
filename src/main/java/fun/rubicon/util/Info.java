/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Main;

import java.util.Date;

/**
 * General data object.
 * @author ForYaSee, DerSchlaubi, LeeDJD, tr808axm
 */
public class Info {
    /**
     * @deprecated Use Main.getConfiguration() directly instead.
     */
    @Deprecated
    static Configuration cfg = Main.getConfiguration();
    /**
     * Discord user ID of the bot application.
     *
     * @deprecated Bot should work on different applications, use RubiconBot.getJDA().getSelfUser().getIdLong() instead
     * to resolve such conflicts.
     */
    @Deprecated
    public final static String BOT_ID = "380713705073147915";
    public final static String BOT_DEFAULT_PREFIX = "rc!";
    public static int reconnectCount = 0;
    public final static String BOT_NAME = "Rubicon";
    public final static String BOT_VERSION = "0.1.5";
    public final static String BOT_WEBSITE = "https://rubicon.fun";
    public final static String BOT_GITHUB = "https://github.com/Rubicon-Bot/RubiCon";
    public final static String CONFIG_FILE = "config.json";
    public final static String EMBED_FOOTER = "© 2017 Rubicon Dev Team";
    public final static String ICON_URL = "https://images-ext-2.discordapp.net/external/Xae9oFQTIRhV7V21twWufcxtdcxhmW6NFNatN8cyxz8/https/cdn.discordapp.com/icons/380415148545802250/579ee17b8de4d027c98853606567d760.jpg?width=72&height=72";
    public final static String BITLY_TOKEN = RubiconBot.getConfiguration().getString("bitlytoken");
    public final static String DBL_TOKEN = RubiconBot.getConfiguration().getString("dbl_token");
    public static Date lastRestart;



    /**
     * Bot author long ids.
     */
    public final static Long[] BOT_AUTHOR_IDS = {
            227817074976751616L, // ForYaSee
            153507094933274624L, // Lee
            318773753796624394L, // Scryptex
            138014719582797824L, // Eiskeks
            264048760580079616L, // Schlaubi
            221905671296253953L, // Zekro
            224528662710452224L, // ForMoJa
            137263174675070976L  // tr808axm
    };

    /* MySQL login */
    public final static String MYSQL_HOST = RubiconBot.getConfiguration().getString("mysql_host");
    public final static String MYSQL_PORT = RubiconBot.getConfiguration().getString("mysql_port");
    public final static String MYSQL_USER = RubiconBot.getConfiguration().getString("mysql_user");
    public final static String MYSQL_PASSWORD = RubiconBot.getConfiguration().getString("mysql_password");
    public final static String MYSQL_DATABASE = RubiconBot.getConfiguration().getString("mysql_database");
}