package fun.rubicon.listener;

import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package listener
 */

public class JoinSQL extends ListenerAdapter {
    public void onGuildJoin(GuildJoinEvent event) {
        Guild g = event.getGuild();
        if(Main.getMySQL().ifGuildExits(event.getGuild())) {
            Main.getMySQL().createGuildServer(g);
        }
        System.out.println("System started on: " + g.getName());
        Guild guild = event.getGuild();
        GuildController controller = guild.getController();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                controller.createCategory(Info.BOT_NAME).queue(cat -> {

                    controller.modifyCategoryPositions()
                            .selectPosition(cat.getPosition())
                            .moveTo(0).queue();

                    String[] list = {"commands", "log"};

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
