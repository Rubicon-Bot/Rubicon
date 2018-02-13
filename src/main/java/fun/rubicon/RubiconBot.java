/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import fun.rubicon.command.CommandManager;
import fun.rubicon.commands.admin.CommandAutochannel;
import fun.rubicon.commands.admin.CommandPortal;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.commands.botowner.*;
import fun.rubicon.commands.botowner.CommandPlay;
import fun.rubicon.commands.fun.*;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.moderation.*;
import fun.rubicon.commands.music.*;
import fun.rubicon.commands.settings.*;
import fun.rubicon.commands.tools.*;
import fun.rubicon.core.GameAnimator;
import fun.rubicon.core.ListenerManager;
import fun.rubicon.core.RubackReceiver;
import fun.rubicon.core.webpanel.WebpanelManager;
import fun.rubicon.core.webpanel.impl.*;
import fun.rubicon.features.GiveawayHandler;
import fun.rubicon.features.RemindHandler;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.sql.*;
import fun.rubicon.util.*;
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
    private static final String[] CONFIG_KEYS = {"token", "mysql_host", "mysql_port", "mysql_database", "mysql_password", "mysql_user", "bitlytoken", "dbl_token", "gip_token", "lucsoft_token", "twitterConsumerKey", "twitterConsumerSecret", "twitterAccessToken", "twitterAccessTokenSecret", "google_token", "musixmatch_key", "git_token", "maintenance", "discord_pw_token"};
    private static final String dataFolder = "data/";
    private static WebpanelManager webpanelManager;
    private static RubiconBot instance;
    private final MySQL mySQL;
    private final Configuration configuration;
    private final CommandManager commandManager;
    private JDA jda;
    private final Timer timer;
    private final Set<EventListener> eventListeners;
    private final PermissionManager permissionManager;
    private final RubackReceiver rubackReceiver;
    private final DatabaseManager databaseManager;

    /**
     * Constructs the RubiconBot.
     */
    private RubiconBot() {
        instance = this;
        // initialize logger
        new File("rubicon_logs").mkdirs();
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, "rubicon_logs/");

        timer = new Timer();
        eventListeners = new HashSet<>();
        databaseManager = new DatabaseManager();

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

        //Create databases if neccesary
        generateDatabases();


        commandManager = new CommandManager();
        registerCommandHandlers();
        permissionManager = new PermissionManager();
        webpanelManager = new WebpanelManager(getConfiguration().getString("lucsoft_token"));
        rubackReceiver = new RubackReceiver();
        rubackReceiver.start();

        registerWebpanelRequests();
        // init JDA
        initJDA();

        // init features
        new GiveawayHandler();
        new RemindHandler();
        //VerificationUserHandler.loadVerifyUser();
        //VerificationKickHandler.loadVerifyKicks();

        // post bot stats to discordbots.org and print warning
        DBLUtil.postStats(false);

        String maintenanceStatus = getConfiguration().getString("maintenance");
        if (maintenanceStatus.equalsIgnoreCase("1")) {
            CommandMaintenance.enable();
        }

        //ITERATING THROUGH MORE THAN 40K USERS
        /**
         *
         * Check if every user, that has the premium role has premium
         *
         * @see CommandPremium
         */
        CommandPremium.PremiumChecker.check();
        CommandPremium.PremiumChecker.startTask();
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
        builder.setGame(Game.playing("Starting...."));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        // add all EventListeners
        for (EventListener listener : instance.eventListeners)
            builder.addEventListener(listener);

        new ListenerManager(builder);

        try {
            instance.jda = builder.buildBlocking();
        } catch (LoginException | InterruptedException e) {
            Logger.error(e.getMessage());
        }

        getJDA().getPresence().setGame(Game.playing("Success."));
        getJDA().getPresence().setStatus(OnlineStatus.ONLINE);

        CommandVote.loadPolls(instance.jda);
        Info.lastRestart = new Date();
        getJDA().getPresence().setGame(Game.playing("Started."));
        GameAnimator.start();
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
                new CommandBan(),
                new CommandKick(),
                new CommandMute(),
                new CommandUnmute(),
                new CommandWarn(),
                new CommandPortal(),
                new CommandVerification(),
                new CommandAutochannel(),
                new CommandRole(),
                new CommandUnban()
        );
        // botowner commands package
        commandManager.registerCommandHandlers(
                new CommandBroadcast(),
                new CommandDBGuild(),
                new CommandPlay(),
                new CommandRestart(),
                new CommandStop(),
                new CommandGuilds(),
                new CommandCreateInvite(),
                new CommandEval(),
                new CommandTwitter(),
                new CommandGlobalBlacklist(),
                new CommandGenerateDocsJSON(),
                new CommandMaintenance(),
                new CommandGuildData(),
                new CommandAlarm()
        );
        // music commands package
        commandManager.registerCommandHandlers(
                new fun.rubicon.commands.music.CommandPlay(),
                new CommandSkip(),
                new CommandJoin(),
                new CommandLeave(),
                new CommandShuffle(),
                new CommandNow(),
                new CommandPause(),
                new CommandResume(),
                new CommandQueue(),
                new CommandVolume(),
                new CommandForceplay()
        );
        // fun commands package
        commandManager.registerCommandHandlers(
                new CommandRip(),
                new CommandSlot(),
                new CommandRoulette(),
                new CommandGiphy(),
                new CommandVideo(),
                new CommandUrban(),
                new CommandJoke(),
                new CommandMinecraft(),
                new CommandOWStats()

        );
        // general commands package
        commandManager.registerCommandHandlers(
                new CommandHelp(),
                new CommandFeedback(),
                new CommandPing(),
                new CommandInfo(),
                new CommandInvite(),
                new CommandSpeedTest(),
                new CommandStatistics(),
                new CommandMoney(),
                new CommandUptime(),
                new CommandProfile(),
                new CommandBio(),
                new CommandMiner(),
                new CommandPremium(),
                new CommandGitBug()
        );
        // settings commands package
        commandManager.registerCommandHandlers(
                new CommandAutorole(),
                new CommandJoinMessage(),
                new CommandPrefix(),
                new CommandWelcomeChannel(),
                new CommandWhitelist(),
                new CommandBlacklist(),
                new CommandLeaveMessage(),
                new CommandLog(),
                new CommandLevelMessage()
        );
        // tools commands package
        commandManager.registerCommandHandlers(
                new CommandChoose(),
                new CommandClear(),
                new CommandRandomColor(),
                new CommandDice(),
                new CommandLmgtfy(),
                new CommandSay(),
                new CommandQRCode(),
                new CommandSearch(),
                new CommandServerInfo(),
                new CommandShorten(),
                new CommandUserInfo(),
                new CommandVote(),
                new CommandMoveAll(),
                new CommandNick()
        );

        // also register commands from the old framework
        //noinspection deprecation
        new CommandManager();
    }

    private void generateDatabases() {
        databaseManager.addGenerators(new ServerLogSQL(),
                new UserMusicSQL(),
                new GuildMusicSQL(),
                new WarnSQL(),
                new MemberSQL(),
                new VerificationKickSQL(),
                new VerificationUserSQL(),
                new MinecraftSQL());

        databaseManager.generate();

    }

    private void registerWebpanelRequests() {
        webpanelManager.addRequest(new MessageStatisticsRequestImpl());
        webpanelManager.addRequest(new MemberJoinRequestImpl());
        webpanelManager.addRequest(new MemberLeaveRequestImpl());
        webpanelManager.addRequest(new MemberCountUpdateRequestImpl());
        webpanelManager.addRequest(new GuildNameUpdateRequestImpl());
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
     * @return a timer.
     */
    public static Timer getTimer() {
        return instance == null ? null : instance.timer;
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

    public static WebpanelManager getWebpanelManager() {
        return webpanelManager;
    }
}