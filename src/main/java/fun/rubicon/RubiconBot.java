/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import fun.rubicon.command.CommandManager;
import fun.rubicon.commands.admin.CommandPortal;
import fun.rubicon.commands.botowner.*;
import fun.rubicon.commands.fun.*;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.moderation.*;
import fun.rubicon.commands.music.*;
import fun.rubicon.commands.settings.*;
import fun.rubicon.commands.tools.*;
import fun.rubicon.core.EventManager;
import fun.rubicon.core.GameAnimator;
import fun.rubicon.core.ListenerAdapter;
import fun.rubicon.core.music.GuildMusicPlayerManager;
import fun.rubicon.core.music.LavalinkManager;
import fun.rubicon.core.translation.TranslationManager;
import fun.rubicon.features.poll.PollManager;
import fun.rubicon.features.poll.PunishmentManager;
import fun.rubicon.features.portal.PortalMessageListener;
import fun.rubicon.features.verification.VerificationCommandHandler;
import fun.rubicon.features.verification.VerificationLoader;
import fun.rubicon.io.Data;
import fun.rubicon.listener.*;
import fun.rubicon.listener.bot.*;
import fun.rubicon.listener.channel.TextChannelDeleteListener;
import fun.rubicon.listener.channel.VoiceChannelDeleteListener;
import fun.rubicon.listener.events.RubiconEventManager;
import fun.rubicon.listener.feature.LogListener;
import fun.rubicon.listener.feature.PunishmentListener;
import fun.rubicon.listener.feature.VerificationListener;
import fun.rubicon.listener.feature.VoteListener;
import fun.rubicon.listener.member.MemberJoinListener;
import fun.rubicon.listener.member.MemberLeaveListener;
import fun.rubicon.listener.role.RoleDeleteListener;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.io.deprecated_rethink.Rethink;
import fun.rubicon.io.deprecated_rethink.RethinkUtil;
import fun.rubicon.setup.SetupListener;
import fun.rubicon.setup.SetupManager;
import fun.rubicon.util.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.hooks.IEventManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rubicon-bot's main class. Initializes all components.
 *
 * @author tr808axm, ForYaSee
 */
public class RubiconBot {

    private static final SimpleDateFormat timeStampFormatter = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    private static RubiconBot instance;
    private static Rethink rethink;
    private final Configuration configuration;
    private final GameAnimator gameAnimator;
    private final CommandManager commandManager;
    private final PermissionManager permissionManager;
    private final TranslationManager translationManager;
    private PunishmentManager punishmentManager;
    private PollManager pollManager;
    private GuildMusicPlayerManager guildMusicPlayerManager;
    private ShardManager shardManager;
    private boolean allShardsInitialised;
    private BitlyAPI bitlyAPI;
    private VerificationLoader verificationLoader;
    private SetupManager setupManager;
    private static LavalinkManager lavalinkManager;
    @Deprecated
    private IEventManager iEventManager;
    private EventManager eventManager;
    private static final String[] CONFIG_KEYS = {
            "shard_count",
            "shard_id",
            "log_webhook",
            "token",
            "playingStatus",
            "dbl_token",
            "discord_pw_token",
            "gif_token",
            "google_token",
            "rethink_host",
            "rethink_port",
            "rethink_db",
            "rethink_user",
            "rethink_password",
            "rethink_host2",
            "rethink_port2",
            "rethink_host3",
            "rethink_port3",
            "fortnite_key",
            "supporthook",
            "rubiconfun_token"
    };
    /**
     * Constructs the RubiconBot.
     */
    @Deprecated
    private RubiconBot() {
        //System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

        instance = this;
        System.out.println(
                " ______        _     _                  \n" +
                        "(_____ \\      | |   (_)                 \n" +
                        " _____) )_   _| |__  _  ____ ___  ____  \n" +
                        "|  __  /| | | |  _ \\| |/ ___) _ \\|  _ \\ \n" +
                        "| |  \\ \\| |_| | |_) ) ( (__| |_| | | | |\n" +
                        "|_|   |_|____/|____/|_|\\____)___/|_| |_|\n" +
                        "                                        \n"
        );
        System.out.println("Version: " + Info.BOT_VERSION);
        System.out.println("Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        System.out.println("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        System.out.println("Java VM Version: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        System.out.println("JDA: " + JDAInfo.VERSION);
        System.out.println("Lavaplayer: " + PlayerLibrary.VERSION);
        System.out.println("\n");
        new File("rubicon_logs").mkdirs();
        new File("data/").mkdirs();
        new File("data/bot/settings").mkdirs();

        //Initialise Config and Database
        Data.init();
        eventManager = new EventManager();

        //OLD
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, "rubicon_logs/");

        //Init config
        configuration = new Configuration(new File(Info.CONFIG_FILE));
        for (String configKey : CONFIG_KEYS) {
            if (!configuration.has(configKey)) {
                String input = ConfigSetup.prompt(configKey);
                configuration.set(configKey, input);
            }
        }
        //Deactivate Beta if not active
        if (!configuration.has("beta"))
            configuration.set("beta", 0);
        Logger.enableWebhooks(configuration.getString("log_webhook"));
        connectRethink();
        RethinkUtil.createDefaults(rethink);

        //Init punishments
        punishmentManager = new PunishmentManager();

        commandManager = new CommandManager();
        lavalinkManager = new LavalinkManager();
        pollManager = new PollManager();
        guildMusicPlayerManager = new GuildMusicPlayerManager();
        registerCommands();
        permissionManager = new PermissionManager();
        translationManager = new TranslationManager();
        gameAnimator = new GameAnimator();
        iEventManager = new RubiconEventManager();
        //Init url shorter API
        bitlyAPI = new BitlyAPI(configuration.getString("bitly_token"));
        verificationLoader = new VerificationLoader();
        setupManager = new SetupManager();


        //Init Shard
        initShardManager();

        gameAnimator.start();
        shardManager.setStatus(OnlineStatus.ONLINE);

        Logger.info("Started!");
    }

    private void registerCommands() {
        //Bot Owner
        commandManager.registerCommandHandlers(
                new CommandEval(),
                new CommandBotstatus(),
                new CommandBotplay(),
                new CommandDisco(),
                new CommandTest(),
                new CommandBeta()
        );

        //Admin
        commandManager.registerCommandHandlers(
                new CommandPortal()
        );

        // Settings
        commandManager.registerCommandHandlers(
                new CommandJoinMessage(),
                new CommandLeaveMessage(),
                new CommandAutochannel(),
                new CommandJoinImage(),
                new CommandAutorole(),
                new CommandRanks(),
                new CommandLog()
        );

        // Fun
        commandManager.registerCommandHandlers(
                new CommandRandom(),
                new CommandLmgtfy(),
                new CommandAscii(),
                new CommandGiphy(),
                new CommandRip(),
                new CommandMedal(),
                new CommandRoadSign(),
                new CommandWeddingSign(),
                new CommandDice(),
                new CommandQR(),
                new CommandFortnite(),
                new CommandOverwatch(),
                new CommandMinecraft()
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
                new CommandUptime(),
                new CommandUserinfo(),
                new CommandMoney(),
                new CommandStatistics(),
                new CommandYTSearch(),
                new CommandPremium(),
                new CommandKey(),
                new CommandPing(),
                new CommandPermissionCheck(),
                new CommandProfile(),
                new CommandSupport(),
                new CommandBug()
        );

        //Moderation
        commandManager.registerCommandHandlers(
                new CommandUnmute(),
                new CommandUnban(),
                new CommandMoveall(),
                new CommandWarn(),
                new CommandClear()
        );

        //Punishments
        punishmentManager.registerPunishmentHandlers(
                new CommandMute(),
                new CommandBan()
        );

        //Tools
        commandManager.registerCommandHandlers(
                new CommandPoll(),
                new CommandShort(),
                new CommandYouTube(),
                new CommandNick(),
                new VerificationCommandHandler(),
                new CommandChoose(),
                new fun.rubicon.commands.tools.CommandSearch(),
                new CommandServerInfo(),
                new CommandRemindMe(),
                new CommandLeet(),
                new CommandGiveaway()
        );

        //Music
        commandManager.registerCommandHandlers(
                new CommandJoin(),
                new CommandLeave(),
                new CommandPlay(),
                new CommandForcePlay(),
                new CommandVolume(),
                new CommandSkip(),
                new CommandClearQueue(),
                new CommandQueue(),
                new CommandStop(),
                new CommandPause(),
                new CommandResume(),
                new CommandShuffle(),
                new CommandNow(),
                new CommandPlaylist()
        );

        //RPG
        commandManager.registerCommandHandlers(
        );
    }

    /**
     * Call all necessary methods
     */
    public void shutdown() {
        Data.db().closePool();
    }

    public void addListenerAdapter(ListenerAdapter listenerAdapter) {
        eventManager.addListenerAdapters(listenerAdapter);
    }

    public void addListenerAdapters(ListenerAdapter... listenerAdapters) {
        eventManager.addListenerAdapters(listenerAdapters);
    }

    public void removeListenerAdapter(ListenerAdapter listenerAdapter) {
        eventManager.removeListenerAdapter(listenerAdapter);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public static RubiconBot getInstance() {
        return instance;
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
    @Deprecated
    private void initShardManager() {
        if (instance == null)
            throw new NullPointerException("RubiconBot has not been initialized yet.");

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder.setToken(instance.configuration.getString("token"));
        builder.setGame(Game.playing("Starting..."));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setShardsTotal(Integer.parseInt(configuration.getString("shard_count")));
        builder.setShards(Integer.parseInt(configuration.getString("shard_id")));

        //Register Event Listeners
        builder.addEventListeners(
                new BotJoinListener(),
                new BotLeaveListener(),
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
                new PunishmentListener(),
                new GeneralMessageListener(),
                new RoleDeleteListener(),
                new LavalinkManager(),
                new VerificationListener(),
                new SetupListener(),
                new PortalMessageListener(),
                new AllShardsLoadedListener(),
                new LogListener()
        );
        builder.setEventManager(iEventManager);
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            Logger.error(e);
            throw new RuntimeException("Can't start bot!");
        }
        lavalinkManager.initialize();
        Info.lastRestart = new Date();
    }

    /**
     * @return the {@link ShardManager} that is used in the Rubicon project
     */
    @Deprecated
    public static ShardManager getShardManager() {
        return instance == null ? null : instance.shardManager;
    }

    /**
     * @return the Rubicon {@link User} instance
     */
    @Deprecated
    public static User getSelfUser() {
        return instance == null ? null : instance.shardManager.getApplicationInfo().getJDA().getSelfUser();
    }

    /**
     * @return the bot configuration.
     */
    @Deprecated
    public static Configuration getConfiguration() {
        return instance == null ? null : instance.configuration;
    }

    @Deprecated
    public static CommandManager getCommandManager() {
        return instance == null ? null : instance.commandManager;
    }

    @Deprecated
    public PermissionManager getPermissionManager() {
        return instance.permissionManager;
    }

    /**
     * @return the {@link PermissionManager} via a static reference.
     */
    @Deprecated
    public static PermissionManager sGetPermissionManager() {
        return instance == null ? null : instance.permissionManager;
    }

    /**
     * @return the rubicon instance
     */
    @Deprecated
    public static RubiconBot getRubiconBot() {
        return instance;
    }

    /**
     * @return a freshly generated timestamp in the 'dd.MM.yyyy HH:mm:ss' format.
     */
    @Deprecated
    public static String getNewTimestamp() {
        return timeStampFormatter.format(new Date());
    }

    /**
     * @param date A Date object
     * @return a generated timestamp in the 'dd.MM.yyyy HH:mm:ss' format.
     */
    @Deprecated
    public static String getTimestamp(Date date) {
        return timeStampFormatter.format(date);
    }

    /**
     * @return the translation manager.
     */
    @Deprecated
    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    /**
     * @return the punishment manager
     */
    @Deprecated
    public static PunishmentManager getPunishmentManager() {
        return instance == null ? null : instance.punishmentManager;
    }

    /**
     * @return the translation manager via a static reference.
     */
    @Deprecated
    public static TranslationManager sGetTranslations() {
        return instance == null ? null : instance.translationManager;
    }

    /**
     * @return List<Guild> of Guilds by name
     */
    @Deprecated
    public static List<Guild> getGuildsByName(String name, boolean ignoreCase) {
        return ignoreCase ? getShardManager().getGuilds().stream().filter(guild -> guild.getName().equalsIgnoreCase(name)).collect(Collectors.toList()) : getShardManager().getGuilds().stream().filter(guild -> guild.getName().equals(name)).collect(Collectors.toList());
    }
    @Deprecated
    public static boolean allShardsInitialised() {
        return instance.allShardsInitialised;
    }
    @Deprecated
    public static void setAllShardsInitialised(boolean allShardsInitialised) {
        instance.allShardsInitialised = allShardsInitialised;
    }
    @Deprecated
    public static GameAnimator getGameAnimator() {
        return instance.gameAnimator;
    }
    @Deprecated
    public static PollManager getPollManager() {
        return instance.pollManager;
    }
    @Deprecated
    public static BitlyAPI getBitlyAPI() {
        return instance.bitlyAPI;
    }
    @Deprecated
    public static LavalinkManager getLavalinkManager() {
        return lavalinkManager;
    }
    @Deprecated
    public static GuildMusicPlayerManager getGuildMusicPlayerManager() {
        return instance.guildMusicPlayerManager;
    }
    @Deprecated
    public static Rethink getRethink() {
        return instance == null ? null : rethink;
    }
    @Deprecated
    public static void connectRethink() {
        rethink = new Rethink(
                instance.configuration.getString("rethink_host"),
                instance.configuration.getInt("rethink_port"),
                instance.configuration.getString("rethink_db"),
                instance.configuration.getString("rethink_user"),
                instance.configuration.getString("rethink_password")
        );
        rethink.connect();
    }
    @Deprecated
    public static VerificationLoader getVerificationLoader() {
        return instance.verificationLoader;
    }
    @Deprecated
    public static SetupManager getSetupManager() {
        return instance.setupManager;
    }
    @Deprecated
    public static IEventManager getDEventManager(){
        return instance.iEventManager;
    }
}