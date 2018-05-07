/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.member;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.settings.CommandLeaveMessage;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.util.BotListHandler;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MemberLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());
        RubiconMember.fromMember(event.getMember()).delete();
        if (!RubiconBot.getConfiguration().getString("rubiconfun_token").isEmpty())
            BotListHandler.postRubiconFunUserCounts(false);

        if (rubiconGuild.hasLeaveMessagesEnabled()) {
            try {
                CommandLeaveMessage.LeaveMessage leaveMessage = rubiconGuild.getLeaveMessage();
                SafeMessage.sendMessage(event.getJDA().getTextChannelById(leaveMessage.getChannelId()), leaveMessage.getMessage()
                        .replace("%user%", event.getUser().getName())
                        .replace("%guild%", event.getGuild().getName())
                        .replace("%count%", event.getGuild().getMembers().size() + ""));
            } catch (Exception e) {
                rubiconGuild.deleteJoinMessage();
            }
        }
    }
}
