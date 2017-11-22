package fun.rubicon.listener;

import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */

public class BotJoinListener extends ListenerAdapter {
    public void onGuildJoin(GuildJoinEvent e) {
        try {
            Guild g = e.getGuild();
            if (!Main.getMySQL().ifGuildExits(e.getGuild())) {
                Main.getMySQL().createGuildServer(g);
            }
        } catch (Exception ignored) {

        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!e.getGuild().getMember(e.getJDA().getSelfUser()).getPermissions().contains(Permission.MANAGE_CHANNEL)) {
                    e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("The bot needs the MANAGE_CHANNEL permissions to work correctly!\nUse rc!rebuild when the bot has the permissions!").queue());
                    return;
                }
                Category category = null;
                TextChannel logChannel;
                TextChannel channel;
                try {
                    channel = e.getGuild().getTextChannelsByName("r-messages", true).get(0);
                    e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0);
                    logChannel = e.getGuild().getTextChannelsByName("r-log", true).get(0);
                    e.getGuild().getTextChannelsByName("r-commands", true).get(0);
                } catch (Exception ex) {
                    e.getGuild().getController().createCategory(Info.BOT_NAME).complete();
                    category = e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0);
                    channel = (TextChannel) e.getGuild().getController().createTextChannel("r-messages").setParent(category).complete();
                    logChannel = (TextChannel) e.getGuild().getController().createTextChannel("r-log").setParent(category).complete();
                    e.getGuild().getController().createTextChannel("r-commands").setParent(category).complete();
                }
                Main.getMySQL().updateGuildValue(e.getGuild(), "logchannel", logChannel.getId());
                Main.getMySQL().updateGuildValue(e.getGuild(), "channel", channel.getId());
            }
        }, 200);
    }
}