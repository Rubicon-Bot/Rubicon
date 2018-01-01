package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee | Yannick Seeger
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package Listener
 */

public class AutoroleExecutor extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Guild guild = e.getGuild();
        String roleEntry = RubiconBot.getMySQL().getGuildValue(guild, "autorole");
        try {
            if (e.getMember().getUser().isBot()) return;
            if (roleEntry.equalsIgnoreCase("0")) return;
        } catch (Exception ignored) {

        }

        try {
            e.getGuild().getController().addRolesToMember(e.getMember(), e.getGuild().getRoleById(roleEntry)).queue();
        } catch (Exception ignored) {
            // Ignored because role can be deleted or higher than the bot role
            e.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Error!", "Your autotole is invalid. Please choose an other role.").build()).queue());
        }

    }
}