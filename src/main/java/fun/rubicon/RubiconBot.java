/*
 * Copyright (c) 2017 Rubicon Dev Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import fun.rubicon.commands.tools.CommandVote;
import fun.rubicon.core.*;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.File;

/**
 * Rubicon-bot's main class. Initializes all components.
 * @author tr808axm
 */
public class RubiconBot {
    private static JDA jda;
    private static MySQL mySQL;
    private static Configuration configuration;
    public final static String[] CONFIG_KEYS = {"token","mysql_host","mysql_port","mysql_database","mysql_password","mysql_user","bitlytoken"};


    /**
     * Initializes the bot.
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        // initialize logger
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, new File("latest.log"));

        // load configuration and obtain missing config values
        configuration = new Configuration(new File(Info.CONFIG_FILE));
        for (String configKey : CONFIG_KEYS)
            if(!configuration.has(configKey)){
                String input = Setup.prompt(configKey);
                configuration.set(configKey, input);
            }

        // init JDA
        initJDA();

        // load MySQL adapter
        mySQL = new MySQL(Info.MYSQL_HOST, Info.MYSQL_PORT, Info.MYSQL_USER, Info.MYSQL_PASSWORD, Info.MYSQL_DATABASE);
        mySQL.connect();
    }

    /**
     * Initializes the JDA instance.
     */
    public static void initJDA() {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(configuration.getString("token"));
        builder.setGame(Game.of(Info.BOT_NAME + " " + Info.BOT_VERSION));

        new ListenerManager(builder);
        new CommandManager();

        try {
            jda = builder.buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            Logger.error(e.getMessage());
        }
        GameAnimator.start();
        CommandVote.loadPolls(jda);
    }

    public static MySQL getMySQL() {
        return mySQL;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @return the static JDA instance. May be null if DiscordCore.start() was not called before.
     */
    public static JDA getJDA() {
        return jda;
    }
}
