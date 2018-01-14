/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;


import fun.rubicon.RubiconBot;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.WebSocketCode;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.RichPresence;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.stream.Collectors;

public class GameAnimator {

    private static Thread t;
    private static boolean running = false;
    private static int currentGame = 0;

    private static final String[] gameAnimations = {
            Info.BOT_VERSION,

    };

    public static synchronized void start() {
        if (!RubiconBot.getConfiguration().has("playingStatus")) {
            RubiconBot.getConfiguration().set("playingStatus", "0");
        }
        if (!running) {
            t = new Thread(() -> {
                long last = 0;
                while (running) {
                    if (System.currentTimeMillis() >= last + 60000) {
                        if (RubiconBot.getConfiguration().has("playingStatus")) {
                            String playStat = RubiconBot.getConfiguration().getString("playingStatus");
                            if (!playStat.equals("0") && !playStat.equals("")) {
                                //RubiconBot.getJDA().getPresence().setGame(Game.playing(playStat));
                                System.out.println("Hey");
                                updateRichPresence(RubiconBot.getJDA(), playStat);
                                last = System.currentTimeMillis();
                            } else {
                                //RubiconBot.getJDA().getPresence().setGame(Game.playing("rc!help | " + gameAnimations[currentGame]));
                                System.out.println("Hey");
                                updateRichPresence(RubiconBot.getJDA(), gameAnimations[currentGame]);

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

    public static void updateRichPresence(JDA jda, String gametext){
        JDAImpl jdaimpl = (JDAImpl) jda;
        /* General RPC object*/
        JSONObject rpcObject = new JSONObject();
        rpcObject.put("afk", false);
        rpcObject.put("status", "dnd");
        rpcObject.put("since", System.currentTimeMillis());
        /*Game object*/
        JSONObject gameObject = new JSONObject();
        gameObject.put("name", gametext);
        gameObject.put("type", 0);
        gameObject.put("state", "Loaded Commands: " + new HashSet<>(RubiconBot.getCommandManager().getCommandAssociations().values()).size());
        gameObject.put("details", "Running on " + RubiconBot.getJDA().getGuilds().size() + " servers and helping " + RubiconBot.getJDA().getUsers().stream().filter(u -> !u.isBot()).collect(Collectors.toList()).size() + " users");
        gameObject.put("application_id", "386841318372147202");

        /* Assets object*/
        JSONObject assetsObject = new JSONObject();
        assetsObject.put("large_image", "discord-small");
        assetsObject.put("small_image", "schlaubi-large");
        assetsObject.put("large_text", "Bot invite: http://inv.rucb.co");
        assetsObject.put("small_text", "Community discord: http://dc.rucb.co");

        //gameObject.put("assets", assetsObject);
        rpcObject.put("game", gameObject);

        System.out.println(gameObject);

        jdaimpl.getClient().send(new JSONObject().put("d", rpcObject).put("op", WebSocketCode.PRESENCE).toString());
    }


}
