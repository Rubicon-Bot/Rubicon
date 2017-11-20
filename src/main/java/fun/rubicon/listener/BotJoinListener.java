package fun.rubicon.listener;

import fun.rubicon.core.Main;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
            Main.getMySQL().createGuildServer(e.getGuild());
        } catch (Exception ex) {

        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!e.getGuild().getMember(e.getJDA().getSelfUser()).getPermissions().contains(Permission.MANAGE_CHANNEL)) {
                    e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("The bot needs the MANAGE_CHANNEL permissions to work correctly!\nUse rc!rebuild when the bot has the permissions!").queue());
                    return;
                }
                Category category = null;
                TextChannel logChannel = null;
                TextChannel commandChannel = null;
                try {
                    category = e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0);
                } catch (Exception ex) {
                    e.getGuild().getController().createCategory(Info.BOT_NAME).complete();
                    category = e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0);
                    logChannel = (TextChannel) e.getGuild().getController().createTextChannel("r-log").setParent(category).complete();
                    commandChannel = (TextChannel) e.getGuild().getController().createTextChannel("r-commands").setParent(category).complete();
                }
                Main.getMySQL().updateGuildValue(e.getGuild(), "logchannel", e.getGuild().getTextChannelsByName("r-log", false).get(0).getId());
            }
        }, 500);
    }
}
