/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import fun.rubicon.util.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.EventListener;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Rubicon-bot's main class. Initializes all components.
 *
 * @author tr808axm
 */
public class RubiconBot {
    private static final SimpleDateFormat timeStampFormatter = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    private static final String[] CONFIG_KEYS = {"token"};
    private static RubiconBot instance;
    private static ShardManager shardManager;
    private final Configuration configuration;
    private final Set<EventListener> eventListeners;

    /**
     * Constructs the RubiconBot.
     */
    private RubiconBot() {
        instance = this;

        new File("rubicon_logs").mkdirs();
        new File("data/").mkdirs();
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, "rubicon_logs/");

        configuration = new Configuration(new File(Info.CONFIG_FILE));
        for (String configKey : CONFIG_KEYS) {
            if (!configuration.has(configKey)) {
                String input = ConfigSetup.prompt(configKey);
                configuration.set(configKey, input);
            }
        }

        eventListeners = new HashSet<>();

        initShardManager();
    }

    /**
     * Initializes the bot.
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        if (instance != null)
            throw new RuntimeException("RubiconBot has already been initialized in this VM.");
        new RubiconBot();
    }

    /**
     * Initializes the JDA instance.
     */
    public static void initShardManager() {
        if (instance == null)
            throw new NullPointerException("RubiconBot has not been initialized yet.");

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder.setToken(instance.configuration.getString("token"));
        builder.setGame(Game.playing("Starting..."));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.addEventListeners(instance.eventListeners);
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            Logger.error(e);
        }
    }

    /**
     * Registers all jda event listeners
     */
    private static void registerListeners(DefaultShardManagerBuilder shardManagerBuilder) {
        shardManagerBuilder.addEventListeners(

        );
    }

    /**
     * @return the bot configuration.
     */
    public static Configuration getConfiguration() {
        return instance == null ? null : instance.configuration;
    }

    /**
     * Adds an EventListener to the event pipe. EventListeners registered here will be re-registered when the JDA
     * instance is initialized again.
     *
     * @param listener the EventListener to register.
     * @return false if the bot has never been initialized or if the EventListener is already registered.
     */
    public static boolean registerEventListener(EventListener listener) {
        if (instance != null && instance.eventListeners.add(listener)) {
            if (shardManager != null)
                shardManager.addEventListener(listener);
            return true;
        }
        return false;
    }

    /**
     * @return a freshly generated timestamp in the 'dd.MM.yyyy HH:mm:ss' format.
     */
    public static String getNewTimestamp() {
        return timeStampFormatter.format(new Date());
    }
}