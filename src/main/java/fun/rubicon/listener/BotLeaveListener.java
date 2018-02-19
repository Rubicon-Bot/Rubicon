/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.core.entities.RubiconUser;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotLeaveListener extends ListenerAdapter{

    public void onGuildLeave(GuildLeaveEvent event) {

        RubiconGuild.fromGuild(event.getGuild()).delete();

        for (Member member: event.getGuild().getMembers()) {
            new RubiconMember(member).delete();
        }

    }



}
