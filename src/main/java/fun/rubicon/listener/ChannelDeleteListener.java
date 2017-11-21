package fun.rubicon.listener;

import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class ChannelDeleteListener extends ListenerAdapter {

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        if (e.getChannel().getName().equals("rubicon-portal")) {
            String stat = Main.getMySQL().getGuildValue(e.getGuild(), "portal");
            if (stat.contains("waiting")) {
                Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
                TextChannel tc = null;
                try {
                    tc = e.getGuild().getTextChannelsByName("rubicon-portal", true).get(0);
                    tc.delete().queue();
                    e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal successfully closed!").queue());
                } catch (Exception ex) {

                }
            } else if (stat.contains("connected")) {
                Main.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
                TextChannel tcc = null;
                try {
                    tcc = e.getGuild().getTextChannelsByName("rubicon-portal", true).get(0);
                    tcc.delete().queue();
                    e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal successfully closed!").queue());
                } catch (Exception ex) {

                }
                Guild otherGuild = e.getJDA().getGuildById(stat.split(":")[1]);
                otherGuild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Portal was closed from the other owner!").queue());
                Main.getMySQL().updateGuildValue(otherGuild, "portal", "closed");
                TextChannel tc = null;
                try {
                    tc = otherGuild.getTextChannelsByName("rubicon-portal", true).get(0);
                    tc.delete().queue();
                } catch (Exception ex) {

                }
            }
        }
    }

    @Override
    public void onCategoryDelete(CategoryDeleteEvent e) {
        if (e.getCategory().getName().contains(Info.BOT_NAME)) {
            e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You deleted the rubicon category.\n" +
                    "Some features doesn't work anymore. Use the rc!rebuild command.").queue());
        }
    }
}
