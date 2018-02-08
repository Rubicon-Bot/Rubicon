/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Game;

public class GameAnimator {

    private static Thread t;
    private static boolean running = false;
    private static int currentGame = 0;

    private static final String[] gameAnimations = {
            Info.BOT_VERSION,
            "rubicon.fun"

    };

    public static synchronized void start() {
        if (!RubiconBot.getConfiguration().has("playingStatus")) {
            RubiconBot.getConfiguration().set("playingStatus", "0");
        }
        if (!running) {
            t = new Thread(() -> {
                long last = 0;
                while (running) {
                    if (System.currentTimeMillis() >= last + 30000) {
                        if (RubiconBot.getConfiguration().has("playingStatus")) {
                            String playStat = RubiconBot.getConfiguration().getString("playingStatus");
                            if (!playStat.equals("0") && !playStat.equals("")) {
                                RubiconBot.getJDA().getPresence().setGame(Game.playing(playStat));
                                last = System.currentTimeMillis();
                            } else {
                                RubiconBot.getJDA().getPresence().setGame(Game.playing("rc!help | " + gameAnimations[currentGame]));

                                if (currentGame == gameAnimations.length - 1)
                                    currentGame = 0;
                                else
                                    currentGame += 1;
                                last = System.currentTimeMillis();
                            }
                        }
                    }
                }
            });
            t.setName("GameAnimator");
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