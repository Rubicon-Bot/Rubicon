/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.FileUtil;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class GameAnimator {

    private final Timer timer;
    private final File gameFile;
    private final Game[] games = {
            Game.listening("rc!help"),
            Game.playing("rubicon.fun"),
            Game.watching("twitter.com/realRubicon"),
            Game.playing("Version: " + Info.BOT_VERSION),
            Game.watching("Rubiteam <3"),
            Game.watching("New Translations"),
            Game.playing("translate.rubicon.fun")
    };
    private int currentGame = 0;

    public GameAnimator() {
        timer = new Timer();
        gameFile = FileUtil.createFileIfNotExist(new File("data/bot/settings", "status.game"));
    }

    public void start() {
        Logger.info("Starting Game Animator....");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String gameFileContent = FileUtil.readFromFile(gameFile);
                if (gameFileContent.equals("")) {
                    if (currentGame == games.length)
                        currentGame = 0;
                    RubiconBot.getShardManager().setStatus(OnlineStatus.ONLINE);
                    RubiconBot.getShardManager().setGame(games[currentGame]);
                    currentGame++;
                } else {
                    RubiconBot.getShardManager().setGame(GameStatusFileParser.parse());
                }
            }
        }, 0, 1000 * 60);
    }

    public void stop() {
        Logger.info("Stopping Game Animator ....");
        timer.cancel();
    }
}
