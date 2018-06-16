/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.command.ClusterCommandManager;
import fun.rubicon.command.CommandManager;
import fun.rubicon.registries.CommandRegistry;
import fun.rubicon.core.GameAnimator;
import fun.rubicon.core.music.GuildMusicPlayerManager;
import fun.rubicon.core.music.LavalinkManager;
import fun.rubicon.core.translation.TranslationManager;
import fun.rubicon.features.poll.PollManager;
import fun.rubicon.features.poll.PunishmentManager;
import fun.rubicon.features.verification.VerificationLoader;
import fun.rubicon.io.Data;
import fun.rubicon.io.deprecated_rethink.Rethink;
import fun.rubicon.io.deprecated_rethink.RethinkUtil;
import fun.rubicon.listener.events.RubiconEventManager;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.registries.EventRegistry;
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
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.SimpleDateFormat;
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
    private static LavalinkManager lavalinkManager;
    private static RubiconBot instance;
    private static Rethink rethink;
    private final Configuration configuration;
    private final GameAnimator gameAnimator;
    private final CommandManager commandManager;
    private final PermissionManager permissionManager;
    private final TranslationManager translationManager;
    private final ClusterClient clusterClient;
    private final ClusterCommandManager clusterCommandManager;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private PunishmentManager punishmentManager;
    private PollManager pollManager;
    private GuildMusicPlayerManager guildMusicPlayerManager;
    private ShardManager shardManager;
    private boolean allShardsInitialised;
    private BitlyAPI bitlyAPI;
    private VerificationLoader verificationLoader;
    private SetupManager setupManager;
    @Deprecated
    private IEventManager iEventManager;
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
    public RubiconBot(ClusterClient clusterClient, ClusterCommandManager clusterCommandManager) {
        this.clusterClient = clusterClient;
        this.clusterCommandManager = clusterCommandManager;

        //System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        instance = this;
        new File("rubicon_logs").mkdirs();
        new File("data/").mkdirs();
        new File("data/bot/settings").mkdirs();

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
        connectRethink();
        logger.info("4");
        RethinkUtil.createDefaults(rethink);
        logger.info("3");

        //Init punishments
        punishmentManager = new PunishmentManager();

        commandManager = new CommandManager();
        lavalinkManager = new LavalinkManager();
        pollManager = new PollManager();
        guildMusicPlayerManager = new GuildMusicPlayerManager();
        logger.info("0");
        CommandRegistry commandRegistry = new CommandRegistry(commandManager, punishmentManager);
        logger.info("1");
        commandRegistry.register();
        logger.info("2");
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

        logger.info("Started!");
    }

    /**
     * Call all necessary shutdown methods
     */
    public void shutdown() {
        Data.db().closePool();
        shardManager.shutdown();
    }

    public ClusterClient getClusterClient() {
        return clusterClient;
    }

    public ClusterCommandManager getClusterCommandManager() {
        return clusterCommandManager;
    }

    public static RubiconBot getInstance() {
        return instance;
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
        EventRegistry eventRegistry = new EventRegistry(builder, commandManager);
        logger.info("1");
        eventRegistry.register();
        logger.info("2");
        builder.setEventManager(iEventManager);
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            logger.error("",e);
            throw new RuntimeException("Can't start bot!");
        }
        lavalinkManager.initialize();
        Info.lastRestart = new Date();
    }

    /**
     * @return an {@link org.slf4j.Logger} instance.
     */
    public static org.slf4j.Logger getLogger(Class theClass){
        return LoggerFactory.getLogger(theClass);
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
    public static IEventManager getDEventManager() {
        return instance.iEventManager;
    }
}