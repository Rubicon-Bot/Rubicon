/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import fun.rubicon.RubiconBot;

import java.util.Date;

/**
 * General data object.
 * @author ForYaSee, DerSchlaubi, LeeDJD, tr808axm
 */
public class Info {

    public final static String BOT_DEFAULT_PREFIX = "rc!";
    public final static String BOT_NAME = "Rubicon";
    public final static String BOT_VERSION = "0.1.8.5";
    public final static String BOT_WEBSITE = "https://rubicon.fun";
    public final static String BOT_GITHUB = "https://github.com/Rubicon-Bot/Rubicon";
    public final static String CONFIG_FILE = "config.json";
    public final static String BITLY_TOKEN = RubiconBot.getConfiguration().getString("bitlytoken");
    public final static String DBL_TOKEN = RubiconBot.getConfiguration().getString("dbl_token");
    public final static String GIPHY_TOKEN = RubiconBot.getConfiguration().getString("gip_token");
    public final static String LUCSOFT_TOKEN = RubiconBot.getConfiguration().getString("lucsoft_token");
    public final static String GOOGLE_TOKEN = RubiconBot.getConfiguration().getString("google_token");
    public static Date lastRestart;

    /**
     * Bot author long ids.
     */
    public final static Long[] BOT_AUTHOR_IDS = {
            227817074976751616L, // ForYaSee
            153507094933274624L, // Lee
            138014719582797824L, // Eiskeks
            264048760580079616L, // Schlaubi
            137253345336229889L, // lucsoft
            137263174675070976L  // tr808axm
    };

    /* MySQL login */
    public final static String MYSQL_HOST = RubiconBot.getConfiguration().getString("mysql_host");
    public final static String MYSQL_PORT = RubiconBot.getConfiguration().getString("mysql_port");
    public final static String MYSQL_USER = RubiconBot.getConfiguration().getString("mysql_user");
    public final static String MYSQL_PASSWORD = RubiconBot.getConfiguration().getString("mysql_password");
    public final static String MYSQL_DATABASE = RubiconBot.getConfiguration().getString("mysql_database");
}