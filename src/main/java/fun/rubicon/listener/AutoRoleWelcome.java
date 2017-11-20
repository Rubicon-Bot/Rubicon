package fun.rubicon.listener;

import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.Main;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package Listener
 */

public class AutoRoleWelcome extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        MySQL SQL = Main.getMySQL();
        Guild guild = event.getGuild();
        if (event.getMember().getUser().isBot()) return;
        PrivateChannel pc = event.getMember().getUser().openPrivateChannel().complete();
        if (SQL.getGuildValue(event.getGuild(), "autorole").equals("0")) {
            pc.sendMessage(
                    "**Hey,** " + event.getMember().getAsMention() + " and welcome on " + event.getGuild().getName() + " :wave:\n\n" +
                            "Now, have a nice day and a lot of fun on the server! ;)"
            ).queue();
        } else {
            try {
                event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRolesByName(SQL.getGuildValue(event.getGuild(), "autorole"), true)).queue();
                pc.sendMessage(
                        "**Hey,** " + event.getMember().getAsMention() + " and welcome on " + event.getGuild().getName() + " :wave:\n\n" +
                                "You automatically got the Role `" + SQL.getGuildValue(event.getGuild(), "autorole") + "` \n" +
                                "Now, have a nice day and a lot of fun on the server! ;)"
                ).queue();
            } catch (Exception e) {
                e.printStackTrace();
                PrivateChannel ow = event.getGuild().getOwner().getUser().openPrivateChannel().complete();
                ow.sendMessage("Please enter a valid Autorole Role!").queue();
                ow.sendMessage(CommandHandler.commands.get("settings").getUsage() + "\n Only in Guild do not send commands at PM!");
            }

        }
        String enabled = SQL.getGuildValue(guild, "channel");
        String channelid = SQL.getGuildValue(guild, "channel");
        String joinmessage = SQL.getGuildValue(guild, "joinmsg").replace("%user%", event.getUser().getAsMention()).replace("%guild%", guild.getName());
        if (!enabled.equals("0")) {
            TextChannel channel = guild.getTextChannelById(channelid);
            channel.sendTyping().queue();
            channel.sendMessage(joinmessage).queue();
        }
    }
}
