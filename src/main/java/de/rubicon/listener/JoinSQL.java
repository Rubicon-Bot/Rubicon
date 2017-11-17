package de.rubicon.listener;

import de.rubicon.core.Main;
import de.rubicon.util.MySQL;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Amme JDA BOT
 * <p>
 * By LordLee at 17.11.2017 20:12
 * <p>
 * Contributors for this class:
 * - github.com/zekrotja
 * - github.com/DRSchlaubi
 * <p>
 * © Coders Place 2017
 */
public class JoinSQL extends ListenerAdapter {
    public void onGuildJoin(GuildJoinEvent event) {
        Guild g = event.getGuild();
        Main.getMySQL().createGuildServer(g);
        System.out.println("System started on: " + g.getName());
        Guild guild = event.getGuild();
        GuildController controller = guild.getController();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                controller.createCategory("Rubicon | DON`T DELETE").queue(cat -> {

                    controller.modifyCategoryPositions()
                            .selectPosition(cat.getPosition())
                            .moveTo(0).queue();

                    String[] list = {"commands", "log", "randomstuff"};

                    Arrays.stream(list).forEach(s ->
                            controller.createTextChannel(s).queue(chan -> chan.getManager().setParent((Category) cat).queue())
                    );
                });

            }
        }, 5000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Main.getMySQL().updateGuildValue(guild, "logchannel", event.getGuild().getTextChannelsByName("log", true).get(0).getId());
                Main.getMySQL().updateGuildValue(guild, "channel", event.getGuild().getTextChannelsByName("randomstuff", true).get(0).getId());
            }
        }, 7000);



    }
}
