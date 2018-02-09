/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.sql.MemberSQL;
import fun.rubicon.sql.UserSQL;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MemberLevelListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }
        if (Cooldown.has(event.getAuthor().getId())) {
            return;
        }
        MemberSQL memberSQL = new MemberSQL(event.getMember());
        //Point System
        int currentPoints = Integer.parseInt(memberSQL.get("points"));
        int pRandom = (int) ((Math.random() * 12 + 3));
        int nowPoints = currentPoints + pRandom;
        String sPoints = String.valueOf(nowPoints);
        memberSQL.set("points", sPoints);

        //Cooldown
        Cooldown.add(event.getAuthor().getId());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Cooldown.remove(event.getAuthor().getId());
            }
        }, 20000);

        int currentLevel = Integer.parseInt(memberSQL.get("level"));
        int requiredPoints = getRequiredPointsByLevel(currentLevel);

        if (nowPoints > requiredPoints) {
            currentLevel++;
            String fina = String.valueOf(currentLevel);
            memberSQL.set("level", fina);
            memberSQL.set("points", "0");
            UserSQL userSQL = new UserSQL(event.getAuthor());
            int oldMoney = Integer.parseInt(userSQL.get("money"));
            userSQL.set("money", (oldMoney + (currentLevel * 100)) + "");

            //Level Up
            /*if (event.getMessage().getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_READ) && event.getMessage().getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor(event.getAuthor().getName() + " leveled up!", null, event.getAuthor().getAvatarUrl())
                        .setDescription("You are now level **" + fina + "**")
                        .setFooter("rc!profile to see money, level, points and more", null)
                        .setColor(Colors.COLOR_SECONDARY)
                        .build()
                ).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            }*/
        }
    }

    public static int getRequiredPointsByLevel(int level) {
        return (5 * ((level * level / 48) * 49) + 50 * level + 100);
    }

    private static class Cooldown {
        /**
         * Cooldown for MemberLevelListener
         */
        public static ArrayList<String> ids = new ArrayList<>();

        public static boolean has(String id) {
            if (ids.contains(id)) {
                return true;
            } else {
                return false;
            }
        }

        public static void add(String id) {
            ids.add(id);
        }

        public static void remove(String id) {
            ids.remove(id);
        }
    }
}
