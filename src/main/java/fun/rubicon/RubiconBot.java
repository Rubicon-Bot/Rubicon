/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import fun.rubicon.command.CommandManager;
import fun.rubicon.commands.botowner.*;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.tools.*;
import fun.rubicon.core.GameAnimator;
import fun.rubicon.core.ListenerManager;
import fun.rubicon.core.webpanel.WebpanelManager;
import fun.rubicon.core.webpanel.impl.*;
import fun.rubicon.features.GiveawayHandler;
import fun.rubicon.features.RemindHandler;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.sql.*;
import fun.rubicon.util.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.EventListener;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

/**
 * Rubicon-bot's main class. Initializes all components.
 *
 * @author tr808axm
 */
public class RubiconBot {
    private static final SimpleDateFormat timeStampFormatter = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    private static final String[] CONFIG_KEYS = {"token"};
    private static RubiconBot instance;
    private final Configuration configuration;
    private final CommandManager commandManager;
    private final Set<EventListener> eventListeners;
    private final PermissionManager permissionManager;

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
        commandManager = new CommandManager();
        registerCommandHandlers();
        permissionManager = new PermissionManager();

        // init JDA
        initJDA();
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
    public static void initJDA() {
        if (instance == null)
            throw new NullPointerException("RubiconBot has not been initialized yet.");

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder.setToken(instance.configuration.getString("token"));
        builder.setGame(Game.playing("Starting..."));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        for(EventListener listener : instance.eventListeners) {
            
        }

        /*JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(instance.configuration.getString("token"));
        builder.setGame(Game.playing("Starting...."));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        // add all EventListeners
        for (EventListener listener : instance.eventListeners)
            builder.addEventListener(listener);

        registerListeners(builder);

        try {
            instance.jda = builder.buildBlocking();
        } catch (LoginException | InterruptedException e) {
            Logger.error(e.getMessage());
        }

        getJDA().getPresence().setGame(Game.playing("Success."));
        getJDA().getPresence().setStatus(OnlineStatus.ONLINE);

        Info.startedAt = new Date();
        getJDA().getPresence().setGame(Game.playing("Started."));*/
    }

    /**
     * Registers all jda event listeners
     */
    private static void registerListeners(DefaultShardManagerBuilder shardManagerBuilder) {
        shardManagerBuilder.addEventListeners(

        );
    }

    /**
     * Registers all command handlers used in this project.
     *
     * @see CommandManager
     */
    private void registerCommandHandlers() {
        // Usage: commandManager.registerCommandHandler(yourCommandHandler...);

        // admin commands package
        commandManager.registerCommandHandlers(

        );
        // botowner commands package
        commandManager.registerCommandHandlers(

        );
        // music commands package
        commandManager.registerCommandHandlers(

        );
        // fun commands package
        commandManager.registerCommandHandlers(


        );
        // general commands package
        commandManager.registerCommandHandlers(

        );
        // settings commands package
        commandManager.registerCommandHandlers(

        );
        // tools commands package
        commandManager.registerCommandHandlers(

        );

        // also register commands from the old framework
        //noinspection deprecation
        new CommandManager();
    }

    /**
     * @return the bot configuration.
     */
    public static Configuration getConfiguration() {
        return instance == null ? null : instance.configuration;
    }

    /**
     * @return the JDA instance.
     */
    public static JDA getJDA() {
        return instance == null ? null : instance.jda;
    }

    /**
     * @return the CommandManager.
     */
    public static CommandManager getCommandManager() {
        return instance == null ? null : instance.commandManager;
    }

    /**
     * @return the {@link PermissionManager}.
     */
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    /**
     * @return the {@link PermissionManager} via a static reference.
     */
    public static PermissionManager sGetPermissionManager() {
        return instance == null ? null : instance.permissionManager;
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
            if (instance.jda != null)
                instance.jda.addEventListener(listener);
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

    /**
     * @return the data folder path
     */
    public static String getDataFolder() {
        return dataFolder;
    }
}