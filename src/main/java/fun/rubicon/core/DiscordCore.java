package fun.rubicon.core;

import fun.rubicon.commands.tools.CommandVote;
import fun.rubicon.util.Info;

import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

/**
 * Core class holding the JDA object statically
 */
public class DiscordCore {
    private static JDA jda;

    /**
     * Initializes the static JDA bot instance and starts listeners.
     */
    public static void start() {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(Main.getConfiguration().getString("token"));
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

    /**
     * @return the static JDA instance. May be null if DiscordCore.start() was not called before.
     */
    public static JDA getJDA() {
        return jda;
    }
}
