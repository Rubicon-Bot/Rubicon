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
import fun.rubicon.features.GiveawayHandler;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.sql.GuildMusicSQL;
import fun.rubicon.sql.MySQL;
import fun.rubicon.sql.ServerLogSQL;
import fun.rubicon.sql.UserMusicSQL;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.hooks.EventListener;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.Connection;
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
    private static final String[] CONFIG_KEYS = {"token", "mysql_host", "mysql_port", "mysql_database", "mysql_password", "mysql_user", "bitlytoken", "dbl_token", "twitterConsumerKey", "twitterConsumerSecret", "twitterAccessToken", "twitterAccessTokenSecret"};
    private static final String dataFolder = "data/";
    private static RubiconBot instance;
    private final MySQL mySQL;
    private final Configuration configuration;
    private final CommandManager commandManager;
    private JDA jda;
    private final Timer timer;
    private final Set<EventListener> eventListeners;
    private final PermissionManager permissionManager;

    /**
     * Constructs the RubiconBot.
     */
    private RubiconBot() {
        instance = this;
        // initialize logger
        new File("rubicon_logs").mkdirs();
        String filename = new SimpleDateFormat("dd_MM_yyyy HH_mm").format(new Date());
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, new File("rubicon_logs/" + filename + ".log"));

        timer = new Timer();
        eventListeners = new HashSet<>();

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
        generateDatabases();
        //Create databases if neccesary
        Connection connection = mySQL.getConnection();
        /*try{
        /*Connection connection = mySQL.getConnection();
        /*
        Connection connection = mySQL.getConnection();
        try{
            PreparedStatement guilds = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `guilds` ( `serverid` VARCHAR(100) NOT NULL AUTO_INCREMENT , `prefix` VARCHAR(25) NOT NULL , `joinmsg` TEXT NOT NULL , `leavemsg` TEXT NOT NULL , `channel` TEXT NOT NULL , `logchannel` TEXT NOT NULL , `autorole` TEXT NOT NULL , `portal` VARCHAR(250) NOT NULL , `welmsg` TEXT NOT NULL , `autochannels` VARCHAR(250) NOT NULL , `cases` INT(11) NOT NULL , `blacklist` INT NOT NULL , PRIMARY KEY (`serverid`)) ENGINE = InnoDB;");        
            PreparedStatement member = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `member` ( `id` INT(250) NOT NULL , `userid` VARCHAR(100) NOT NULL , `guildid` VARCHAR(100) NOT NULL , `permissionlevel` VARCHAR(2) NOT NULL , `permissions` VARCHAR(250) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            PreparedStatement mutes = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `mutes` ( `id` INT NOT NULL AUTO_INCREMENT , `serverid` BIGINT(18) NOT NULL , `userid` BIGINT(18) NOT NULL , `authorid` BIGINT(18) NOT NULL , `add_date` VARCHAR(10) NOT NULL , `del_date` VARCHAR(10) NOT NULL , `reason` VARCHAR(300) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            PreparedStatement portal = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `portal` ( `id` INT(250) NOT NULL AUTO_INCREMENT , `guildid` VARCHAR(250) NOT NULL , `partnerid` VARCHAR(250) NOT NULL , `channelid` VARCHAR(250) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            PreparedStatement roles = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `roles` ( `id` INT(250) NOT NULL AUTO_INCREMENT , `roleid` VARCHAR(100) NOT NULL , `permissions` VARCHAR(250) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            PreparedStatement user = connection.prepareStatement("CREATE TABLE IF NOT EXSITS `user` ( `id` INT(250) NOT NULL AUTO_INCREMENT , `userid` VARCHAR(250) NOT NULL , `bio` VARCHAR(250) NULL , `bday` VARCHAR NULL , `level` TEXT NULL DEFAULT NULL , `points` TEXT NULL DEFAULT NULL , `money` TEXT NULL DEFAULT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            PreparedStatement verifications = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `verifications_b` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `guildid` TEXT NOT NULL , `channelid` TEXT NOT NULL , `roleid` TEXT NOT NULL , `text` TEXT NOT NULL , `verifiedtext` TEXT NOT NULL , `kicktime` TEXT NOT NULL , `kicktext` TEXT NOT NULL , `emote` TEXT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            mySQL.executePreparedStatements(guilds, member, mutes, portal, roles, user, verifications);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        */

        commandManager = new CommandManager();
        registerCommandHandlers();
        permissionManager = new PermissionManager();

        // init JDA
        initJDA();

        // init features
        new GiveawayHandler();

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

        // add all EventListeners
        for (EventListener listener : instance.eventListeners)
            builder.addEventListener(listener);

        new ListenerManager(builder);

        try {
            instance.jda = builder.buildBlocking();
        } catch (LoginException | InterruptedException e) {
            Logger.error(e.getMessage());
        }
        GameAnimator.start();
        CommandVote.loadPolls(instance.jda);
//      CommandGiveaway.startGiveawayManager(instance.jda);

        int memberCount = 0;
        for (Guild guild : getJDA().getGuilds())
            memberCount += guild.getMembers().size();

        StringBuilder infoOnStart = new StringBuilder();
        infoOnStart.append("\n");
        infoOnStart.append("---------- " + Info.BOT_NAME + " v." + Info.BOT_VERSION + " ---------- \n");
        infoOnStart.append("Running on " + getJDA().getGuilds().size() + " Guilds \n");
        infoOnStart.append("Supplying " + getJDA().getUsers().size() + " Users \n");
        infoOnStart.append("Supplying " + memberCount + " Members \n");
        infoOnStart.append("---------------------------------------");
        infoOnStart.append("\n");

        System.out.println(infoOnStart.toString());
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
                new CommandGetWarn(),
                new CommandKick(),
                new CommandMute(),
                new CommandUnmute(),
                new CommandUnWarn(),
                new CommandWarn(),
                new CommandPortal(),
                new CommandVerification(),
                new CommandAutochannel(),
                new CommandRole()
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
                new CommandGlobalBlacklist()
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
                new CommandQueue()
        );
        // fun commands package
        commandManager.registerCommandHandlers(
                new CommandRip(),
                new CommandSlot(),
                new CommandRoulette(),
                new CommandOK()
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
                new CommandPrefix(),
                new CommandWelcomeChannel(),
                new CommandBlacklist(),
                new CommandLeaveMessage(),
                new CommandLog()
        );
        // tools commands package
        commandManager.registerCommandHandlers(
                new CommandASCII(),
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
        new ServerLogSQL().createTableIfNotExist();
        new UserMusicSQL().createTableIfNotExist();
        new GuildMusicSQL().createTableIfNotExist();
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
}
