package fun.rubicon.core;

import fun.rubicon.command.CommandHandler;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Game;
import java.util.stream.Collectors;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.core
 */

public class GameAnimator {

    private static Thread t;
    private static boolean running = false;

    private static int currentGame = 0;

    private static String authors(){
        String text ="";
        for(int i = 1; i < Info.BOT_AUTHORS.length; i++) {
            text += Info.BOT_AUTHORS[i];
        }

        return text;
    }

    private static String[] gameAnimations = {
            "Running on " + DiscordCore.getJDA().getGuilds().size() + " servers!",
            "Helping " + DiscordCore.getJDA().getUsers().stream().filter(u -> u.isBot() == false).collect(Collectors.toList()).size() + " users!",
            "JDA squad!",
            Info.BOT_NAME + " " + Info.BOT_VERSION,
            "Generating new features...",
            CommandHandler.getCommands().size() + " Commands loaded",
            "Blowing stuff up!",
    };

    public static synchronized void start() {
        if (!running) {
            t = new Thread(() -> {
                long last = 0;
                while (running) {
                    if (System.currentTimeMillis() >= last + 60000) {
                        DiscordCore.getJDA().getPresence().setGame(Game.of(gameAnimations[currentGame]));
                        last = System.currentTimeMillis();

                        if (currentGame == gameAnimations.length - 1)
                            currentGame = 0;
                        else
                            currentGame += 1;
                    }
                }
            });
            t.setName("GameAnimator Thread");
            running = true;
            t.start();
        }
    }

    public static synchronized void stop() {
        if (running) {
            try {
                running = false;
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
