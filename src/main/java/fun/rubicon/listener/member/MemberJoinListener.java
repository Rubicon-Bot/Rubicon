/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.member;

import fun.rubicon.commands.settings.CommandJoinMessage;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MemberJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());
        RubiconMember.fromMember(event.getMember());

        if (rubiconGuild.hasJoinMessagesEnabled()) {
            try {
                CommandJoinMessage.JoinMessage joinMessage = rubiconGuild.getJoinMessage();
                SafeMessage.sendMessage(event.getJDA().getTextChannelById(joinMessage.getChannelId()), joinMessage.getMessage()
                        .replace("%user%", event.getMember().getAsMention())
                        .replace("%server%", event.getGuild().getName())
                        .replace("%count%", event.getGuild().getMembers().size() + ""));
            } catch (Exception e) {
                rubiconGuild.deleteJoinMessage();
            }
        }

        if(rubiconGuild.hasAutoroleEnabled()) {
            Role role = event.getGuild().getRoleById(rubiconGuild.getAutorole());
            if(role == null || !event.getGuild().getSelfMember().canInteract(role)) {
                rubiconGuild.disableAutorole();
                return;
            }
            event.getGuild().getController().addSingleRoleToMember(event.getMember(), role).queue();
        }
    }
}
