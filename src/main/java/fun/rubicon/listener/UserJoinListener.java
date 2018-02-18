/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.general.CommandPremium;
import fun.rubicon.sql.UserSQL;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class UserJoinListener extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        try {
            String joinMessage = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "joinmsg");
            if (joinMessage.equals("0")) {
                return;
            } else {
                TextChannel messageChannel = e.getJDA().getTextChannelById(RubiconBot.getMySQL().getGuildValue(e.getGuild(), "channel"));
                if (messageChannel == null)
                    return;
                joinMessage = joinMessage.replace("%user%", e.getMember().getAsMention());
                joinMessage = joinMessage.replace("%guild%", e.getGuild().getName());
                messageChannel.sendMessage(joinMessage).queue();
            }
        } catch (NullPointerException ex) {
            //Channel does not exits
        }
        UserSQL userSQL = new UserSQL(e.getUser());
        CommandPremium.assignPremiumRole(userSQL);
    }
}
