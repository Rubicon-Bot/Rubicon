/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import fun.rubicon.commands.admin.CommandAutochannel;
import fun.rubicon.commands.admin.CommandPortal;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.commands.botowner.*;
import fun.rubicon.commands.fun.CommandGiveaway;
import fun.rubicon.commands.fun.CommandRip;
import fun.rubicon.commands.fun.CommandRoulette;
import fun.rubicon.commands.fun.CommandSlot;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.moderation.*;
import fun.rubicon.commands.settings.*;
import fun.rubicon.commands.tools.*;
import fun.rubicon.core.CommandManager;
import fun.rubicon.core.GameAnimator;
import fun.rubicon.core.ListenerManager;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Rubicon-bot's main class. Initializes all components.
 *
 * @author tr808axm
 */
public class RubiconBot {
    private static final SimpleDateFormat timeStampFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final String[] CONFIG_KEYS = {"token", "mysql_host", "mysql_port", "mysql_database", "mysql_password", "mysql_user", "bitlytoken", "dbl_token", "twitterConsumerKey", "twitterConsumerSecret", "twitterAccessToken", "twitterAccessTokenSecret"};
    private static final String dataFolder = "data/";
    private static RubiconBot instance;
    private final MySQL mySQL;
    private final Configuration configuration;
    private final fun.rubicon.command2.CommandManager commandManager;
    private JDA jda;

    /**
     * Constructs the RubiconBot.
     */
    private RubiconBot() {
        instance = this;
        // initialize logger
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, new File("latest.log"));
        // load configuration and obtain missing config values
        new File(dataFolder).mkdirs();

        configuration = new Configuration(new File(Info.CONFIG_FILE));
        for (String configKey : CONFIG_KEYS) {
            if (!configuration.has(configKey)) {
                String input = Setup.prompt(configKey);
                configuration.set(configKey, input);
            }
        }

        // load MySQL adapter
        mySQL = new MySQL(Info.MYSQL_HOST, Info.MYSQL_PORT, Info.MYSQL_USER, Info.MYSQL_PASSWORD, Info.MYSQL_DATABASE);
        mySQL.connect();


        commandManager = new fun.rubicon.command2.CommandManager();
        registerCommandHandlers();

        // init JDA
        initJDA();

        // post bot stats to discordbots.org and print warning
        DBLUtil.postStats(false);
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

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(instance.configuration.getString("token"));
        builder.setGame(Game.playing(Info.BOT_NAME + " " + Info.BOT_VERSION));

        // Register command manager (chat listener)
        builder.addEventListener(instance.commandManager);

        new ListenerManager(builder);

        try {
            instance.jda = builder.buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            Logger.error(e.getMessage());
        }
        GameAnimator.start();
        CommandVote.loadPolls(instance.jda);
        CommandGiveaway.startGiveawayManager(instance.jda);

        int memberCount = 0;
        for(Guild guild : getJDA().getGuilds())
            memberCount += guild.getMembers().size();

        StringBuilder infoOnStart = new StringBuilder();
        infoOnStart.append("\n");
        infoOnStart.append("---------- " + Info.BOT_NAME + " v." + Info.BOT_VERSION + " ---------- \n");
        infoOnStart.append("Running on " + getJDA().getGuilds().size() + " Guilds \n");
        infoOnStart.append("Supplying " + getJDA().getUsers().size() + " User \n");
        infoOnStart.append("Supplying " + memberCount + " Member \n");
        infoOnStart.append("---------------------------------------");
        infoOnStart.append("\n");

        System.out.println(infoOnStart.toString());
    }

    /**
     * Registers all command handlers used in this project.
     *
     * @see fun.rubicon.command2.CommandManager
     */
    private void registerCommandHandlers() {
        // Usage: commandManager.registerCommandHandler(yourCommandHandler...);

        // admin commands package
        commandManager.registerCommandHandlers(
                new CommandBan(),
                new CommandGetWarn(),
                new CommandKick(),
                new CommandMute(),
                new CommandUnmute(),
                new CommandUnWarn(),
                new CommandWarn(),
                new CommandPortal(),
                new CommandVerification(),
                new CommandAutochannel()
        );
        // botowner commands package
        commandManager.registerCommandHandlers(
                new CommandBroadcast(),
                new CommandPlay(),
                new CommandRestart(),
                new CommandStop(),
                new CommandGuilds(),
                new CommandCreateInvite(),
                new CommandEval(),
                new CommandTwitter()
        );
        // fun commands package
        commandManager.registerCommandHandlers(
                new CommandRip(),
                new CommandSlot(),
                new CommandRoulette()
        );
        // general commands package
        commandManager.registerCommandHandlers(
                new CommandHelp(),
                new CommandFeedback(),
                new CommandPing(),
                new CommandBug(),
                new CommandInfo(),
                new CommandInvite(),
                new CommandSpeedTest(),
                new CommandStatistics(),
                new CommandMoney(),
                new CommandLevel()
        );
        // settings commands package
        commandManager.registerCommandHandlers(
                new CommandAutorole(),
                new CommandJoinMessage(),
                new CommandLogChannel(),
                new CommandPrefix(),
                new CommandWelcomeChannel(),
                new CommandBlacklist()
        );
        // tools commands package
        commandManager.registerCommandHandlers(
                new CommandASCII(),
                new CommandChoose(),
                new CommandClear(),
                new CommandColor(),
                new CommandDice(),
                new CommandGoogle(),
                new CommandLmgtfy(),
                new CommandSay(),
                new CommandQRCode(),
                new CommandSearch(),
                new CommandServerInfo(),
                new CommandShorten(),
                new CommandUserInfo(),
                new CommandVote(),
                new CommandMoveAll()
        );

        // also register commands from the old framework
        //noinspection deprecation
        new CommandManager();
    }

    /**
     * @return the MySQL adapter.
     */
    public static MySQL getMySQL() {
        return instance == null ? null : instance.mySQL;
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
    public static fun.rubicon.command2.CommandManager getCommandManager() {
        return instance == null ? null : instance.commandManager;
    }

    /**
     * @return a freshly generated timestamp.
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
