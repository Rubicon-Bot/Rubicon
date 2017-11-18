package fun.rubicon.core;

import fun.rubicon.util.Info;

import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class DiscordCore {

    private static JDA jda;
    private static String token;

    public static void start() {
        token = Main.getConfiguration().getString("token");

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(token);
        builder.setGame(Game.of(Info.BOT_NAME + " " + Info.BOT_VERSION));

        new ListenerManager(builder);
        new CommandManager();

        try {
            jda = builder.buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            Logger.error(e.getMessage());
        }
        Info.init();
        GameAnimator.start();
    }

    public static JDA getJDA() {
        return jda;
    }
}
