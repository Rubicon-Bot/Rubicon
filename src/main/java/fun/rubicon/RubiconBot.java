/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestHeader;
import de.foryasee.httprequest.RequestResponse;
import de.foryasee.httprequest.RequestType;
import fun.rubicon.command.CommandManager;
import fun.rubicon.commands.admin.CommandAutorole;
import fun.rubicon.commands.botowner.*;
import fun.rubicon.commands.fun.*;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.moderation.*;
import fun.rubicon.commands.music.*;
import fun.rubicon.commands.rpg.CommandInventory;
import fun.rubicon.commands.settings.*;
import fun.rubicon.commands.tools.*;
import fun.rubicon.core.GameAnimator;
import fun.rubicon.core.music.GuildMusicPlayerManager;
import fun.rubicon.core.music.LavalinkManager;
import fun.rubicon.core.rpg.RPGItemRegistry;
import fun.rubicon.core.translation.TranslationManager;
import fun.rubicon.features.poll.PollManager;
import fun.rubicon.features.poll.PunishmentManager;
import fun.rubicon.features.verification.VerificationCommandHandler;
import fun.rubicon.features.verification.VerificationLoader;
import fun.rubicon.listener.AutochannelListener;
import fun.rubicon.listener.GeneralMessageListener;
import fun.rubicon.listener.GeneralReactionListener;
import fun.rubicon.listener.UserMentionListener;
import fun.rubicon.listener.bot.BotJoinListener;
import fun.rubicon.listener.bot.BotLeaveListener;
import fun.rubicon.listener.bot.SelfMentionListener;
import fun.rubicon.listener.bot.ShardListener;
import fun.rubicon.listener.channel.TextChannelDeleteListener;
import fun.rubicon.listener.channel.VoiceChannelDeleteListener;
import fun.rubicon.listener.feature.PunishmentListener;
import fun.rubicon.listener.feature.VerificationListener;
import fun.rubicon.listener.feature.VoteListener;
import fun.rubicon.listener.member.MemberJoinListener;
import fun.rubicon.listener.member.MemberLeaveListener;
import fun.rubicon.listener.role.RoleDeleteListener;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.rethink.RethinkUtil;
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
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
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
    private static final String[] CONFIG_KEYS = {"log_webhook", "token", "playingStatus", "dbl_token", "discord_pw_token", "gif_token", "google_token", "rethink_host", "rethink_port", "rethink_db", "rethink_user", "rethink_password"};
    private static RubiconBot instance;
    private final Configuration configuration;
    private final Rethink rethink;
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
    private static int SHARD_COUNT;
    private static LavalinkManager lavalinkManager;
    private RPGItemRegistry rpgItemRegistry;

    /**
     * Constructs the RubiconBot.
     */
    private RubiconBot() {
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
        if(!configuration.has("beta"))
            configuration.set("beta",0);
        Logger.enableWebhooks(configuration.getString("log_webhook"));
        rethink = new Rethink(
                configuration.getString("rethink_host"),
                configuration.getInt("rethink_port"),
                configuration.getString("rethink_db"),
                configuration.getString("rethink_user"),
                configuration.getString("rethink_password")
        );
        rethink.connect();
        RethinkUtil.createDefaults(rethink);

        SHARD_COUNT = generateShardCount();
        Logger.info(String.format("Starting with %d shards...", SHARD_COUNT));

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
        rpgItemRegistry = new RPGItemRegistry();
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
                new CommandShardManage(),
                new CommandBotstatus(),
                new CommandBotplay(),
                new CommandDisco(),
                new CommandTest(),
                new CommandInvMod(),
                new CommandBeta()
        );

        //Admin
        commandManager.registerCommandHandlers(
                new CommandAutorole()
        );

        // Settings
        commandManager.registerCommandHandlers(
                new CommandJoinMessage(),
                new CommandLeaveMessage(),
                new CommandAutochannel(),
                new CommandJoinImage()
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
                new CommandOverwatch()
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
                new CommandMoney(),
                new CommandStatistics(),
                new CommandUptime(),
                new CommandYTSearch(),
                new CommandPremium(),
                new CommandKey(),
                new CommandPing(),
                new CommandPermissionCheck()
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
                new CommandSearch(),
                new CommandServerInfo()
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
                new CommandInventory()
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
        builder.setGame(Game.playing("Starting..."));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setShardsTotal(SHARD_COUNT);

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
                new LavalinkManager(),
                new VerificationListener(),
                new SetupListener()
        );
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            Logger.error(e);
            throw new RuntimeException("Can't start bot!");
        }
        lavalinkManager.initialize();
        Info.lastRestart = new Date();
    }

    private static int generateShardCount() {

        HttpRequestBuilder builder = new HttpRequestBuilder("https://discordapp.com/api/gateway/bot", RequestType.GET)
                .setRequestHeader(new RequestHeader().addField("Authorization", getConfiguration().getString("token")).addField("User-Agent", "Rubicon"));
        try {
            RequestResponse response = builder.sendRequest();
            return (int) (new JSONObject(response.getResponseMessage())).get("shards");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("The Discord API did not Respond with a Shard count!");
        }
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
    public static String getTimestamp(Date date) {
        return timeStampFormatter.format(date);
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

    /**
     * @return List<Guild> of Guilds by name
     */
    public static List<Guild> getGuildsByName(String name, boolean ignoreCase) {
        return ignoreCase ? getShardManager().getGuilds().stream().filter(guild -> guild.getName().equalsIgnoreCase(name)).collect(Collectors.toList()) : getShardManager().getGuilds().stream().filter(guild -> guild.getName().equals(name)).collect(Collectors.toList());
    }

    public static boolean allShardsInitialised() {
        return instance.allShardsInitialised;
    }

    public static void setAllShardsInitialised(boolean allShardsInitialised) {
        instance.allShardsInitialised = allShardsInitialised;
    }

    public static GameAnimator getGameAnimator() {
        return instance.gameAnimator;
    }

    public static PollManager getPollManager() {
        return instance.pollManager;
    }

    public static BitlyAPI getBitlyAPI() {
        return instance.bitlyAPI;
    }

    public static LavalinkManager getLavalinkManager() {
        return lavalinkManager;
    }

    public static GuildMusicPlayerManager getGuildMusicPlayerManager() {
        return instance.guildMusicPlayerManager;
    }

    public static Rethink getRethink() {
        return instance == null ? null : instance.rethink;
    }

    public static RPGItemRegistry getRPGItemRegistry() {
        return instance == null ? null : instance.rpgItemRegistry;
    }

    public static VerificationLoader getVerificationLoader() {
        return instance.verificationLoader;
    }

    public static SetupManager getSetupManager() {
        return instance.setupManager;
    }
}