/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import fun.rubicon.command.CommandManager;
import fun.rubicon.commands.botowner.CommandMaintenance;
import fun.rubicon.commands.botowner.CommandShardManage;
import fun.rubicon.commands.fun.CommandGiphy;
import fun.rubicon.commands.fun.CommandLmgtfy;
import fun.rubicon.commands.general.CommandAFK;
import fun.rubicon.commands.general.CommandHelp;
import fun.rubicon.commands.general.CommandInfo;
import fun.rubicon.commands.fun.CommandRandom;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.moderation.CommandBan;
import fun.rubicon.commands.moderation.CommandMute;
import fun.rubicon.commands.moderation.CommandUnban;
import fun.rubicon.commands.moderation.CommandUnmute;
import fun.rubicon.commands.settings.CommandAutochannel;
import fun.rubicon.commands.settings.CommandJoinMessage;
import fun.rubicon.commands.settings.CommandLeaveMessage;
import fun.rubicon.commands.tools.CommandPoll;
import fun.rubicon.commands.settings.CommandPrefix;
import fun.rubicon.commands.tools.CommandRandomColor;
import fun.rubicon.core.GameAnimator;
import fun.rubicon.core.translation.TranslationManager;
import fun.rubicon.commands.botowner.CommandEval;
import fun.rubicon.features.PunishmentManager;
import fun.rubicon.listener.*;
import fun.rubicon.listener.bot.BotJoinListener;
import fun.rubicon.listener.bot.SelfMentionListener;
import fun.rubicon.listener.bot.ShardListener;
import fun.rubicon.listener.channel.TextChannelDeleteListener;
import fun.rubicon.listener.channel.VoiceChannelDeleteListener;
import fun.rubicon.listener.feature.PunishmentListener;
import fun.rubicon.listener.feature.VoteListener;
import fun.rubicon.listener.member.MemberJoinListener;
import fun.rubicon.listener.member.MemberLeaveListener;
import fun.rubicon.mysql.DatabaseGenerator;
import fun.rubicon.mysql.MySQL;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.util.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Rubicon-bot's main class. Initializes all components.
 *
 * @author tr808axm, ForYaSee
 */
public class RubiconBot {
    private static final SimpleDateFormat timeStampFormatter = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    private static final String[] CONFIG_KEYS = {"token", "mysql_host", "mysql_database", "mysql_user", "mysql_password", "playingStatus", "dbl_token", "discord_pw_token","gif_token","google_token"};
    private static RubiconBot instance;
    private final Configuration configuration;
    private final MySQL mySQL;
    private final GameAnimator gameAnimator;
    private final CommandManager commandManager;
    private final PermissionManager permissionManager;
    private final TranslationManager translationManager;
    private PunishmentManager punishmentManager;
    private ShardManager shardManager;
    private boolean allShardsInited;
    private static final int SHARD_COUNT = 1;


    /**
     * Constructs the RubiconBot.
     */
    private RubiconBot() {
        instance = this;

        new File("rubicon_logs").mkdirs();
        new File("data/").mkdirs();
        new File("data/bot/settings").mkdirs();
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, "rubicon_logs/");

        //Init config
        configuration = new Configuration(new File(Info.CONFIG_FILE));
        for (String configKey : CONFIG_KEYS) {
            if (!configuration.has(configKey)) {
                String input = ConfigSetup.prompt(configKey);
                configuration.set(configKey, input);
            }
        }
        //Init MySQL Connection
        mySQL = new MySQL(
                configuration.getString("mysql_host"),
                "3306", configuration.getString("mysql_user"),
                configuration.getString("mysql_password"),
                configuration.getString("mysql_database"));
        mySQL.connect();

        DatabaseGenerator.createAllDatabasesIfNecessary();

        //Init punishments
        punishmentManager = new PunishmentManager();

        commandManager = new CommandManager();
        if(configuration.getString("maintenance") != null)
            if(Boolean.valueOf(configuration.getString("maintenance"))) commandManager.setMaintenance(true);
        registerCommands();
        permissionManager = new PermissionManager();
        translationManager = new TranslationManager();
        gameAnimator = new GameAnimator();

        //Init Shard
        initShardManager();

        gameAnimator.start();


    }

    private void registerCommands() {
        //Bot Owner
        commandManager.registerCommandHandlers(
                new CommandEval(),
                new CommandShardManage(),
                new CommandMaintenance()
        );

        // Settings
        commandManager.registerCommandHandlers(
                new CommandJoinMessage(),
                new CommandLeaveMessage(),
                new CommandAutochannel()
        );

        // Fun
        commandManager.registerCommandHandlers(
                new CommandRandom()
        );

        //General
        commandManager.registerCommandHandlers(
                new CommandHelp(),
                new CommandInfo(),
                new CommandAFK(),
                new CommandPrefix(),
                new CommandBio(),
                new CommandInvite(),
                new CommandSay(),
                new CommandUserinfo(),
                new CommandUptime(),
                new CommandYouTube(),
                new CommandSearch()
        );

        //Moderation
        commandManager.registerCommandHandlers(
            new CommandUnmute(),
            new CommandUnban()
        );

        //Punishments
        punishmentManager.registerPunishmentHandlers(
            new CommandMute(),
            new CommandBan()
        );

        //Tools
        commandManager.registerCommandHandlers(
                new CommandPoll()
        );

        //Fun
        commandManager.registerCommandHandlers(
                new CommandGiphy(),
                new CommandLmgtfy()
        );
    }

    /**
     * Initialises the bot.
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
    private void initShardManager() {
        if (instance == null)
            throw new NullPointerException("RubiconBot has not been initialized yet.");

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder.setToken(instance.configuration.getString("token"));
        if(commandManager.isMaintenanceEnabled())
            builder.setGame(Game.watching(configuration.getString("playingStatus")));
        else
            builder.setGame(Game.playing("Starting..."));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setShardsTotal(SHARD_COUNT);


        //Register Event Listeners
        builder.addEventListeners(
                new BotJoinListener(),
                commandManager,
                new UserMentionListener(),
                new ShardListener(),
                new SelfMentionListener(),
                new VoteListener(),
                new MemberJoinListener(),
                new MemberLeaveListener(),
                new TextChannelDeleteListener(),
                new VoiceChannelDeleteListener(),
                new GeneralReactionListener(),
                new AutochannelListener(),
                new PunishmentListener()
        );
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            Logger.error(e);
        }

        Info.lastRestart = new Date();
    }

    /**
     * @return the {@link ShardManager} that is used in the Rubicon project
     */
    public static ShardManager getShardManager() {
        return instance == null ? null : instance.shardManager;
    }

    /**
     * @return the Rubicon {@link User} instance
     */
    public static User getSelfUser() {
        return instance == null ? null : instance.shardManager.getApplicationInfo().getJDA().getSelfUser();
    }

    /**
     * @return the bot configuration.
     */
    public static Configuration getConfiguration() {
        return instance == null ? null : instance.configuration;
    }

    public static CommandManager getCommandManager() {
        return instance == null ? null : instance.commandManager;
    }

    public PermissionManager getPermissionManager() {
        return instance.permissionManager;
    }

    /**
     * @return the {@link PermissionManager} via a static reference.
     */
    public static PermissionManager sGetPermissionManager() {
        return instance == null ? null : instance.permissionManager;
    }

    /**
     * @return the rubicon instance
     */
    public static RubiconBot getRubiconBot() {
        return instance;
    }

    /**
     * @return the maximum shard count
     */
    public static int getMaximumShardCount() {
        return SHARD_COUNT;
    }

    /**
     * @return a freshly generated timestamp in the 'dd.MM.yyyy HH:mm:ss' format.
     */
    public static String getNewTimestamp() {
        return timeStampFormatter.format(new Date());
    }
    /**
     * @param date A Date object
     * @return a generated timestamp in the 'dd.MM.yyyy HH:mm:ss' format.
     */
    public static String getTimestamp(Date date){
        return timeStampFormatter.format(date);
    }
    /**
     * @return the {@link MySQL} instance
     */
    public static MySQL getMySQL() {
        return instance == null ? null : instance.mySQL;
    }

    /**
     * @return the translation manager.
     */
    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    /**
     * @return the punishment manager
     */
    public static PunishmentManager getPunishmentManager() {
        return instance == null ? null : instance.punishmentManager;
    }

    /**
     * @return the translation manager via a static reference.
     */
    public static TranslationManager sGetTranslations() {
        return instance == null ? null : instance.translationManager;
    }

    public static boolean isAllShardsInited() {
        return instance.allShardsInited;
    }

    public static void setAllShardsInited(boolean allShardsInited) {
        instance.allShardsInited = allShardsInited;
    }

    public static GameAnimator getGameAnimator(){ return instance.gameAnimator; }

}